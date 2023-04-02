package com.github.jernejvivod.ehrexplorer.core.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jernejvivod.ehrexplorer.common.exception.ValidationCoreException;
import com.github.jernejvivod.ehrexplorer.core.manager.DbEntityManager;
import com.github.jernejvivod.ehrexplorer.core.processing.Wordification;
import com.github.jernejvivod.ehrexplorer.core.processing.spec.PropertySpec;
import com.github.jernejvivod.ehrexplorer.core.processing.transform.CompositeColumnCreator;
import com.github.jernejvivod.ehrexplorer.core.processing.transform.ValueTransformer;
import com.github.jernejvivod.ehrexplorer.core.util.DtoConverter;
import com.github.jernejvivod.ehrexplorer.core.util.EntityUtils;
import com.github.jernejvivod.ehrexplorerprocessorapi.v1.model.ConcatenationSpecDto;
import com.github.jernejvivod.ehrexplorerprocessorapi.v1.model.WordificationConfigDto;
import com.github.jernejvivod.ehrexplorerprocessorapi.v1.model.WordificationResultDto;

@Stateless
public class PropositionalizationService
{
    private static final Logger logger = LoggerFactory.getLogger(PropositionalizationService.class);

    @Inject
    private DbEntityManager dbEntityManager;
    @Inject
    private Wordification wordification;

    // mapping of concatenation scheme specification enums
    private static final Map<ConcatenationSpecDto.ConcatenationSchemeEnum, Wordification.ConcatenationScheme> concatenationSchemeEnumMapping =
            new EnumMap<>(Map.ofEntries(
                    Map.entry(ConcatenationSpecDto.ConcatenationSchemeEnum.ZERO, Wordification.ConcatenationScheme.ZERO),
                    Map.entry(ConcatenationSpecDto.ConcatenationSchemeEnum.ONE, Wordification.ConcatenationScheme.ONE),
                    Map.entry(ConcatenationSpecDto.ConcatenationSchemeEnum.TWO, Wordification.ConcatenationScheme.TWO)
            ));

    private Map<String, Set<String>> entityNameToAttributes;

    @PostConstruct
    public void init()
    {
        entityNameToAttributes = EntityUtils.computeEntityNameToAttributes(dbEntityManager.getMetamodel());
    }

    /**
     * Compute Wordification results with the specified configuration.
     *
     * @param wordificationConfigDto configuration for the Wordification algorithm
     * @return Wordification algorithm results in the specified format
     */
    public List<WordificationResultDto> computeWordification(WordificationConfigDto wordificationConfigDto)
    {
        logger.info("Computing Wordification.");

        // check specified root entity and id property names
        String rootEntityName = wordificationConfigDto.getRootEntitiesSpec().getRootEntity();
        String idPropertyName = wordificationConfigDto.getRootEntitiesSpec().getIdProperty();

        if (!entityNameToAttributes.containsKey(rootEntityName))
            throw new ValidationCoreException("Unknown entity '%s'".formatted(rootEntityName));

        if (entityNameToAttributes.containsKey(rootEntityName) && !entityNameToAttributes.get(rootEntityName).contains(idPropertyName))
            throw new ValidationCoreException("Unknown property name '%s' of entity '%s'".formatted(idPropertyName, rootEntityName));

        // get PropertySpec, ValueTransformer and CompositeColumnCreator instances
        PropertySpec propertySpec = DtoConverter.toPropertySpec(wordificationConfigDto.getPropertySpec());
        propertySpec.assertValid(dbEntityManager.getMetamodel());

        CompositeColumnCreator compositeColumnCreator = Optional.ofNullable(wordificationConfigDto.getCompositeColumnsSpec())
                .map(DtoConverter::toCompositeColumnCreator)
                .orElse(new CompositeColumnCreator());
        compositeColumnCreator.assertValid(dbEntityManager.getMetamodel());

        ValueTransformer valueTransformer = Optional.ofNullable(wordificationConfigDto.getValueTransformationSpec())
                .map(DtoConverter::toValueTransformer)
                .orElse(new ValueTransformer());
        valueTransformer.assertValid(dbEntityManager.getMetamodel(), compositeColumnCreator, propertySpec);

        List<List<String>> foreignKeyPaths = EntityUtils.getForeignKeyPathsFromPropertySpec(rootEntityName, propertySpec, dbEntityManager.getMetamodel());

        // get list of root entities
        List<Object[]> rootEntities = dbEntityManager.fetchRootEntitiesAndIdsForForeignKeyPaths(
                rootEntityName,
                foreignKeyPaths,
                idPropertyName,
                new HashSet<>(wordificationConfigDto.getRootEntitiesSpec().getIds())
        ).toList();

        // initialize list for storing Wordification results
        List<WordificationResultDto> wordificationResults = new ArrayList<>(wordificationConfigDto.getRootEntitiesSpec().getIds().size());

        // go over entity IDs and compute Wordification results
        for (Object[] rootEntityWithId : rootEntities)
        {
            Object rootEntity = rootEntityWithId[0];
            long rootEntityId = (long) rootEntityWithId[1];

            List<LocalDateTime> timeLimsForRootEntity = propertySpec.getRootEntityIdToTimeLims().get(rootEntityId);

            if (timeLimsForRootEntity != null)
            {
                for (final LocalDateTime timeLim : timeLimsForRootEntity)
                {
                    WordificationResultDto wordificationResultDtoNxt = new WordificationResultDto()
                            .rootEntityId(rootEntityId);

                    wordificationResultDtoNxt.setTimeLim(timeLim);
                    wordificationResultDtoNxt.setWords(
                            wordification.wordify(
                                    rootEntity,
                                    propertySpec,
                                    valueTransformer,
                                    compositeColumnCreator,
                                    concatenationSchemeEnumMapping.get(wordificationConfigDto.getConcatenationSpec().getConcatenationScheme()),
                                    EntityUtils.getTransitionPairsFromForeignKeyPath(foreignKeyPaths),
                                    timeLim,
                                    false
                            )
                    );
                    wordificationResults.add(wordificationResultDtoNxt);
                }
            }
            else
            {
                WordificationResultDto wordificationResultDtoNxt = new WordificationResultDto()
                        .rootEntityId(rootEntityId);

                wordificationResultDtoNxt.setWords(
                        wordification.wordify(
                                rootEntity,
                                propertySpec,
                                valueTransformer,
                                compositeColumnCreator,
                                concatenationSchemeEnumMapping.get(wordificationConfigDto.getConcatenationSpec().getConcatenationScheme()),
                                EntityUtils.getTransitionPairsFromForeignKeyPath(foreignKeyPaths),
                                null,
                                false
                        )
                );
                wordificationResults.add(wordificationResultDtoNxt);
            }

        }

        return wordificationResults;
    }
}
