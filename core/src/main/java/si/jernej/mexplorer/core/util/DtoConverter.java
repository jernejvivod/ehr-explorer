package si.jernej.mexplorer.core.util;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import si.jernej.mexplorer.core.exception.ValidationCoreException;
import si.jernej.mexplorer.core.processing.spec.PropertySpec;
import si.jernej.mexplorer.core.processing.transform.CompositeColumnCreator;
import si.jernej.mexplorer.core.processing.transform.ValueTransformer;
import si.jernej.mexplorer.processorapi.v1.model.CompositeColumnsSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.CompositeColumnsSpecEntryDto;
import si.jernej.mexplorer.processorapi.v1.model.CompositePropertySpecEntryDto;
import si.jernej.mexplorer.processorapi.v1.model.PropertySpecDto;
import si.jernej.mexplorer.processorapi.v1.model.PropertySpecEntryDto;
import si.jernej.mexplorer.processorapi.v1.model.TransformDto;
import si.jernej.mexplorer.processorapi.v1.model.ValueTransformationSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.ValueTransformationSpecEntryDto;

public final class DtoConverter
{
    private DtoConverter()
    {
        throw new IllegalStateException("This class should not be instantiated");
    }

    private static final Map<TransformDto.DateDiffRoundTypeEnum, Function<Object, ?>> dateDiffRoundTypeKindToTransformFunction = Map.ofEntries(
            Map.entry(TransformDto.DateDiffRoundTypeEnum.YEAR, x -> ((String) x).split(" ")[0]),
            Map.entry(TransformDto.DateDiffRoundTypeEnum.FIVE_YEARS, x -> String.valueOf((int) 5.0 * Math.round(Double.parseDouble(((String) x).split(" ")[0]) / 5.0))),
            Map.entry(TransformDto.DateDiffRoundTypeEnum.TEN_YEARS, x -> String.valueOf((int) 10.0 * Math.round(Double.parseDouble(((String) x).split(" ")[0]) / 10.0))),
            Map.entry(TransformDto.DateDiffRoundTypeEnum.FIFTEEN_YEARS, x -> String.valueOf((int) 15.0 * Math.round(Double.parseDouble(((String) x).split(" ")[0]) / 15.0))),
            Map.entry(TransformDto.DateDiffRoundTypeEnum.TWENTY_YEARS, x -> String.valueOf((int) 20.0 * Math.round(Double.parseDouble(((String) x).split(" ")[0]) / 20.0)))
    );

    /**
     * Enums for operations for combining columns when creating composite columns
     */
    public enum CombinerEnum
    {
        DATE_DIFF((x, y) -> {
            Period period = Period.between(((LocalDateTime) x).toLocalDate(), ((LocalDateTime) y).toLocalDate());
            return "%s %s %s".formatted(Math.abs(period.getYears()), Math.abs(period.getMonths()), Math.abs(period.getDays()));
        });

        private final BinaryOperator<Object> binaryOperator;

        CombinerEnum(BinaryOperator<Object> binaryOperator)
        {
            this.binaryOperator = binaryOperator;
        }

        public BinaryOperator<Object> getBinaryOperator()
        {
            return binaryOperator;
        }
    }

    // mapping of concatenation scheme specification enums
    private static final Map<CompositeColumnsSpecEntryDto.CombinerEnum, CombinerEnum> compositeColumnsCombinerEnumMapping = new EnumMap<>(Map.ofEntries(
            Map.entry(CompositeColumnsSpecEntryDto.CombinerEnum.DATE_DIFF, CombinerEnum.DATE_DIFF)
    ));

    private static final Map<CompositePropertySpecEntryDto.CombinerEnum, CombinerEnum> compositePropertiesCombinerEnumMapping = new EnumMap<>(Map.ofEntries(
            Map.entry(CompositePropertySpecEntryDto.CombinerEnum.DATE_DIFF, CombinerEnum.DATE_DIFF)
    ));

    /**
     * Get transformation function from {@link TransformDto} instance.
     *
     * @param transformDto model for the function
     * @return function used in {@link ValueTransformer}
     */
    private static ValueTransformer.Transform transformDtoToTransformFunction(TransformDto transformDto)
    {
        ValueTransformer.Transform res = null;

        if (transformDto.getKind() == TransformDto.KindEnum.ROUNDING)
        {
            res = new ValueTransformer.Transform(x -> transformDto.getRoundingMultiple() * Math.round(((double) x) / transformDto.getRoundingMultiple()));
        }
        else if (transformDto.getKind() == TransformDto.KindEnum.DATE_DIFF_ROUND)
        {
            res = new ValueTransformer.Transform(dateDiffRoundTypeKindToTransformFunction.get(transformDto.getDateDiffRoundType()));
        }

        return Optional.ofNullable(res).orElseThrow(() -> new ValidationCoreException("Error setting value transformations"));
    }

    /**
     * Construct {@link PropertySpec} instance from model.
     *
     * @param propertySpecDto model for the instance
     * @return initialized {@link PropertySpec} instance that can be used in {@link si.jernej.mexplorer.core.processing.Wordification}
     */
    public static PropertySpec toPropertySpec(PropertySpecDto propertySpecDto)
    {
        PropertySpec propertySpec = new PropertySpec();

        propertySpecDto.getRootEntityAndLimeLimit()
                .forEach(r -> propertySpec.getRootEntityIdToTimeLims()
                        .computeIfAbsent(r.getRootEntityId(), id -> new ArrayList<>())
                        .add(r.getTimeLim())
                );

        for (PropertySpecEntryDto entry : propertySpecDto.getEntries())
        {
            List<PropertySpec.CompositePropertySpec> compositePropertySpecs = Optional.ofNullable(entry.getCompositePropertySpecEntries())
                    .map(cs -> cs.stream()
                            .map(c -> new PropertySpec.CompositePropertySpec(
                                            c.getPropertyOnThisEntity(),
                                            c.getPropertyOnOtherEntity(),
                                            c.getForeignKeyPath(),
                                            c.getCompositePropertyName(),
                                            compositePropertiesCombinerEnumMapping.get(c.getCombiner()).getBinaryOperator()
                                    )
                            ).toList()
                    )
                    .orElse(null);

            propertySpec.addEntry(entry.getEntity(), entry.getProperties(), compositePropertySpecs);

            if (entry.getPropertyForLimit() != null)
            {
                propertySpec.addEntityAndPropertyForDurationLimit(entry.getEntity(), entry.getPropertyForLimit());
            }
        }
        return propertySpec;
    }

    /**
     * Construct {@link ValueTransformer} instance from model
     *
     * @param valueTransformationSpecDto model for the instance
     * @return initialized {@link ValueTransformer} instance that can be used in {@link si.jernej.mexplorer.core.processing.Wordification}
     */
    public static ValueTransformer toValueTransformer(ValueTransformationSpecDto valueTransformationSpecDto)
    {
        ValueTransformer valueTransformer = new ValueTransformer();

        for (ValueTransformationSpecEntryDto entry : valueTransformationSpecDto.getEntries())
        {
            valueTransformer.addTransform(entry.getEntity(), entry.getProperty(), transformDtoToTransformFunction(entry.getTransform()));
        }

        return valueTransformer;
    }

    /**
     * Construct {@link CompositeColumnCreator} instance from model.
     *
     * @param compositeColumnsSpecDto model for the instance
     * @return initialized {@link CompositeColumnCreator} instance that can be used in {@link si.jernej.mexplorer.core.processing.Wordification}
     */
    public static CompositeColumnCreator toCompositeColumnCreator(CompositeColumnsSpecDto compositeColumnsSpecDto)
    {
        CompositeColumnCreator compositeColumnCreator = new CompositeColumnCreator();
        for (CompositeColumnsSpecEntryDto entry : compositeColumnsSpecDto.getEntries())
        {
            compositeColumnCreator.addEntry(entry.getForeignKeyPath1(), entry.getProperty1(), entry.getForeignKeyPath2(), entry.getProperty2(), entry.getCompositeName(), compositeColumnsCombinerEnumMapping.get(entry.getCombiner()).getBinaryOperator());
        }
        return compositeColumnCreator;
    }
}
