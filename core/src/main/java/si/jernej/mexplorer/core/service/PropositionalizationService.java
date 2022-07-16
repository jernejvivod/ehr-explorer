package si.jernej.mexplorer.core.service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import si.jernej.mexplorer.core.exception.ValidationCoreException;
import si.jernej.mexplorer.core.manager.MimicEntityManager;
import si.jernej.mexplorer.core.processing.Wordification;
import si.jernej.mexplorer.core.processing.spec.PropertySpec;
import si.jernej.mexplorer.core.processing.transform.CompositeColumnCreator;
import si.jernej.mexplorer.core.processing.transform.ValueTransformer;
import si.jernej.mexplorer.core.util.DtoConverter;
import si.jernej.mexplorer.core.util.EntityUtils;
import si.jernej.mexplorer.processorapi.v1.model.ConcatenationSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.WordificationConfigDto;
import si.jernej.mexplorer.processorapi.v1.model.WordificationResultDto;

@Dependent
public class PropositionalizationService
{
    @Inject
    private MimicEntityManager mimicEntityManager;
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
        entityNameToAttributes = EntityUtils.computeEntityNameToAttributes(mimicEntityManager.getMetamodel());
    }

    /**
     * Compute Wordification results with the specified configuration.
     *
     * @param wordificationConfigDto configuration for the Wordification algorithm
     * @return Wordification algorithm results in the specified format
     */
    public List<WordificationResultDto> computeWordification(WordificationConfigDto wordificationConfigDto)
    {
        // check specified root entity and id property names
        String rootEntityName = wordificationConfigDto.getRootEntitiesSpec().getRootEntity();
        String idPropertyName = wordificationConfigDto.getRootEntitiesSpec().getIdProperty();

        if (!entityNameToAttributes.containsKey(rootEntityName))
        {
            throw new ValidationCoreException(String.format("Unknown entity '%s'", rootEntityName));
        }
        if (entityNameToAttributes.containsKey(rootEntityName) && !entityNameToAttributes.get(rootEntityName).contains(idPropertyName))
        {
            throw new ValidationCoreException(String.format("Unknown property name '%s'", idPropertyName));
        }

        // get list of root entities and their IDfaun fulls
        List<Object[]> rootEntitiesWithIds = mimicEntityManager.getEntitiesAndIds(
                idPropertyName,
                rootEntityName,
                wordificationConfigDto.getRootEntitiesSpec().getIds()
        );

        // get PropertySpec, ValueTransformer and CompositeColumnCreator instances
        PropertySpec propertySpec = DtoConverter.toPropertySpec(wordificationConfigDto.getPropertySpec());

        ValueTransformer valueTransformer = Optional.ofNullable(wordificationConfigDto.getValueTransformationSpec())
                .map(DtoConverter::toValueTransformer)
                .orElse(new ValueTransformer());

        CompositeColumnCreator compositeColumnCreator = Optional.ofNullable(wordificationConfigDto.getCompositeColumnsSpec())
                .map(s -> DtoConverter.toCompositeColumnCreator(rootEntityName, s, mimicEntityManager.getMetamodel()))
                .orElse(new CompositeColumnCreator());

        // initialize list for storing wordification results
        List<WordificationResultDto> wordificationResults = new ArrayList<>(wordificationConfigDto.getRootEntitiesSpec().getIds().size());

        // go over entity IDs and compute Wordification results
        for (Object[] rootEntityWithId : rootEntitiesWithIds)
        {
            Object rootEntity = rootEntityWithId[0];
            long rootEntityId = (long) rootEntityWithId[1];

            WordificationResultDto wordificationResultDtoNxt = new WordificationResultDto();wordificationResultDtoNxt.setRootEntityId(rootEntityId);

            // compute wordification
            wordificationResultDtoNxt.setWords(
                    wordification.wordify(
                            rootEntity,
                            propertySpec,
                            valueTransformer,
                            compositeColumnCreator,
                            concatenationSchemeEnumMapping.get(wordificationConfigDto.getConcatenationSpec().getConcatenationScheme())
                    )
            );

            wordificationResults.add(wordificationResultDtoNxt);
        }

        return wordificationResults;
    }
}
