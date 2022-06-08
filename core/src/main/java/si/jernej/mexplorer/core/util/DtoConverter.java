package si.jernej.mexplorer.core.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.ws.rs.BadRequestException;

import si.jernej.mexplorer.core.processing.spec.PropertySpec;
import si.jernej.mexplorer.core.processing.transform.CompositeColumnCreator;
import si.jernej.mexplorer.core.processing.transform.ValueTransformer;
import si.jernej.mexplorer.processorapi.v1.model.CompositeColumnsSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.CompositeColumnsSpecEntryDto;
import si.jernej.mexplorer.processorapi.v1.model.PropertySpecDto;
import si.jernej.mexplorer.processorapi.v1.model.PropertySpecEntryDto;
import si.jernej.mexplorer.processorapi.v1.model.TransformDto;
import si.jernej.mexplorer.processorapi.v1.model.ValueTransformationSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.ValueTransformationSpecEntryDto;

@Stateless
public final class DtoConverter
{
    @PersistenceContext
    private static EntityManager em;

    private DtoConverter()
    {
        throw new IllegalStateException("This class should not be instantiated");
    }

    private static final Map<TransformDto.DiscretizationTypeEnum, Function<Object, ?>> discretizationKindToTransformFunction = Map.ofEntries(
            Map.entry(TransformDto.DiscretizationTypeEnum.HISTOGRAM_ANALYSIS, x -> x)  // TODO implement
    );

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
            final int SECONDS_IN_HOUR = 3600;
            Period period = Period.between(((LocalDateTime) x).toLocalDate(), ((LocalDateTime) y).toLocalDate());
            Duration duration = Duration.between((LocalDateTime) x, (LocalDateTime) y);
            return String.format("%s %s %s %s", period.getYears(), period.getMonths(), period.getDays(), duration.getSeconds() / SECONDS_IN_HOUR);
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
    private static final Map<CompositeColumnsSpecEntryDto.CombinerEnum, CombinerEnum> combinerEnumMapping = new EnumMap<>(Map.ofEntries(
            Map.entry(CompositeColumnsSpecEntryDto.CombinerEnum.DATE_DIFF, CombinerEnum.DATE_DIFF)
    ));

    /**
     * Get transformation function from {@link TransformDto} instance.
     *
     * @param transformDto model for the function
     * @return function used in {@link ValueTransformer}
     */
    private static Function<Object, ?> transformDtoToTransformFunction(TransformDto transformDto)
    {
        Function<Object, ?> res = null;

        if (transformDto.getKind() == TransformDto.KindEnum.DISCRETIZATION)
        {
            res = discretizationKindToTransformFunction.get(transformDto.getDiscretizationType());
        }
        else if (transformDto.getKind() == TransformDto.KindEnum.DATE_DIFF_ROUND)
        {
            res = dateDiffRoundTypeKindToTransformFunction.get(transformDto.getDateDiffRoundType());
        }

        return Optional.ofNullable(res).orElseThrow(() -> new BadRequestException("Error setting value transformations"));
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

        for (PropertySpecEntryDto entry : propertySpecDto.getEntries())
        {
            propertySpec.addEntry(entry.getEntity(), entry.getProperties());
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
     * @param rootEntityName name of root entity
     * @param compositeColumnsSpecDto model for the instance
     * @return initialized {@link CompositeColumnCreator} instance that can be used in {@link si.jernej.mexplorer.core.processing.Wordification}
     */
    public static CompositeColumnCreator toCompositeColumnCreator(String rootEntityName, CompositeColumnsSpecDto compositeColumnsSpecDto)
    {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("primary");

        Map<String, Set<String>> entityToLinkedEntities = EntityUtils.computeEntityToLinkedEntitiesMap(emf.getMetamodel());

        CompositeColumnCreator compositeColumnCreator = new CompositeColumnCreator();
        for (CompositeColumnsSpecEntryDto entry : compositeColumnsSpecDto.getEntries())
        {
            List<String> foreignKeyPath1 = EntityUtils.computeForeignKeyPath(rootEntityName, entry.getTable1(), entityToLinkedEntities);
            List<String> foreignKeyPath2 = EntityUtils.computeForeignKeyPath(rootEntityName, entry.getTable2(), entityToLinkedEntities);
            compositeColumnCreator.addEntry(foreignKeyPath1, entry.getProperty1(), foreignKeyPath2, entry.getProperty2(), entry.getCompositeName(), combinerEnumMapping.get(entry.getCombiner()).getBinaryOperator());
        }
        return compositeColumnCreator;
    }
}
