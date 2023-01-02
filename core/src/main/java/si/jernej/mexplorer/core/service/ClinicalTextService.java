package si.jernej.mexplorer.core.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.jernej.mexplorer.core.manager.MimicEntityManager;
import si.jernej.mexplorer.core.util.EntityUtils;
import si.jernej.mexplorer.processorapi.v1.model.ClinicalTextConfigDto;
import si.jernej.mexplorer.processorapi.v1.model.ClinicalTextResultDto;
import si.jernej.mexplorer.processorapi.v1.model.DataRangeSpecDto;

@Stateless
public class ClinicalTextService
{
    private static final Logger logger = LoggerFactory.getLogger(ClinicalTextService.class);

    @Inject
    private MimicEntityManager mimicEntityManager;

    /**
     * Extract clinical text given specified configuration.
     *
     * @param clinicalTextConfigDto configuration for extracting clinical text
     * @return extracted clinical text for each specified root entity
     */
    public Set<ClinicalTextResultDto> extractClinicalText(ClinicalTextConfigDto clinicalTextConfigDto)
    {
        logger.info(".extractClinicalText extracting clinical text");
        logger.info(".extractClinicalText root entity name: {}, root entity ID property {}, number of IDs {}",
                clinicalTextConfigDto.getRootEntitiesSpec().getRootEntity(),
                clinicalTextConfigDto.getRootEntitiesSpec().getIdProperty(),
                clinicalTextConfigDto.getRootEntitiesSpec().getIds().size()
        );

        // if no root entity IDs specified, return empty map
        if (clinicalTextConfigDto.getRootEntitiesSpec().getIds().isEmpty())
        {
            return Collections.emptySet();
        }

        EntityUtils.assertForeignKeyPathValid(clinicalTextConfigDto.getForeignKeyPath(), mimicEntityManager.getMetamodel());
        EntityUtils.assertDateTimeLimitSpecValidForClinicalTextExtraction(clinicalTextConfigDto, mimicEntityManager.getMetamodel());

        DataRangeSpecDto dataRangeSpec = clinicalTextConfigDto.getDataRangeSpec();
        String idPropertyName = clinicalTextConfigDto.getRootEntitiesSpec().getIdProperty();

        // map ids of root entities to clinical text
        Map<Long, List<MimicEntityManager.ClinicalTextExtractionQueryResult<Long>>> rootEntityIdToClinicalTextData = mimicEntityManager.mapRootEntityIdsToClinicalText(
                new HashSet<>(clinicalTextConfigDto.getRootEntitiesSpec().getIds()),
                clinicalTextConfigDto.getForeignKeyPath(),
                idPropertyName,
                clinicalTextConfigDto.getClinicalTextEntityIdPropertyName(),
                clinicalTextConfigDto.getTextPropertyName(),
                clinicalTextConfigDto.getDateTimePropertiesNames()
        );

        // if range of data limited, filter
        if (dataRangeSpec != null)
        {
            rootEntityIdToClinicalTextData.replaceAll((id, values) -> {

                // filter out clinical text where all date/time values are null
                var valuesWithDateTime = values.stream().filter(v -> !v.dateTimeColumnValues().stream().allMatch(Objects::isNull)).toList();

                if (valuesWithDateTime.isEmpty())
                {
                    return List.of();
                }

                // initial date/time
                LocalDateTime initialDateTime = valuesWithDateTime.get(0).dateTimeColumnValues().stream()
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElseThrow(() -> new InternalServerErrorException("No associated LocalDateTime found"));

                // filter to specified range
                return valuesWithDateTime.stream().filter(d -> {
                    LocalDateTime dateTimeNxt = d.dateTimeColumnValues().stream()
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElseThrow(() -> new InternalServerErrorException("No associated LocalDateTime found"));
                    return Duration.between(initialDateTime, dateTimeNxt).toMinutes() < dataRangeSpec.getFirstMinutes();
                }).toList();
            });
        }

        // convert results to a set of ClinicalTextResultDto instances
        Set<ClinicalTextResultDto> res = new HashSet<>();
        rootEntityIdToClinicalTextData.forEach((id, values) -> res.add(
                        new ClinicalTextResultDto()
                                .rootEntityId(id)
                                .text(
                                        values.stream()
                                                .map(MimicEntityManager.ClinicalTextExtractionQueryResult::clinicalText)
                                                .collect(Collectors.joining(" "))
                                )
                )
        );

        return res;
    }
}
