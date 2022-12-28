package si.jernej.mexplorer.core.test.processing.transform;

import static si.jernej.mexplorer.processorapi.v1.model.TransformDto.DateDiffRoundTypeEnum.FIFTEEN_YEARS;
import static si.jernej.mexplorer.processorapi.v1.model.TransformDto.DateDiffRoundTypeEnum.FIVE_YEARS;
import static si.jernej.mexplorer.processorapi.v1.model.TransformDto.DateDiffRoundTypeEnum.TEN_YEARS;
import static si.jernej.mexplorer.processorapi.v1.model.TransformDto.DateDiffRoundTypeEnum.TWENTY_YEARS;
import static si.jernej.mexplorer.processorapi.v1.model.TransformDto.DateDiffRoundTypeEnum.YEAR;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import si.jernej.mexplorer.core.exception.ValidationCoreException;
import si.jernej.mexplorer.core.processing.transform.ValueTransformer;
import si.jernej.mexplorer.core.util.DtoConverter;
import si.jernej.mexplorer.processorapi.v1.model.TransformDto;
import si.jernej.mexplorer.processorapi.v1.model.ValueTransformationSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.ValueTransformationSpecEntryDto;
import si.jernej.mexplorer.test.ATestBase;

class ValueTransformerTest extends ATestBase
{
    private static final Map<String, Map<TransformDto.DateDiffRoundTypeEnum, String>> dateDiffValuesToResults = Map.ofEntries(
            Map.entry("0 0 0", Map.of(YEAR, "0", FIVE_YEARS, "0", TEN_YEARS, "0", FIFTEEN_YEARS, "0", TWENTY_YEARS, "0")),
            Map.entry("1 0 0", Map.of(YEAR, "1", FIVE_YEARS, "0", TEN_YEARS, "0", FIFTEEN_YEARS, "0", TWENTY_YEARS, "0")),
            Map.entry("80 3 1", Map.of(YEAR, "80", FIVE_YEARS, "80", TEN_YEARS, "80", FIFTEEN_YEARS, "75", TWENTY_YEARS, "80")),
            Map.entry("77 12 3", Map.of(YEAR, "77", FIVE_YEARS, "75", TEN_YEARS, "80", FIFTEEN_YEARS, "75", TWENTY_YEARS, "80")),
            Map.entry("113 1 4", Map.of(YEAR, "113", FIVE_YEARS, "115", TEN_YEARS, "110", FIFTEEN_YEARS, "120", TWENTY_YEARS, "120"))
    );

    private static final List<String> dateDiffValues = List.of("0 0 0", "1 0 0", "80 3 1", "77 12 3", "113 1 4");

    @Test
    void testDateDiffSingleEntry()
    {
        ValueTransformationSpecDto valueTransformationSpecDto = new ValueTransformationSpecDto();

        ValueTransformationSpecEntryDto valueTransformationSpecEntryDto = new ValueTransformationSpecEntryDto();
        valueTransformationSpecEntryDto.setEntity("TestEntity");
        valueTransformationSpecEntryDto.setProperty("testProperty");
        TransformDto transformDto = new TransformDto();
        transformDto.setKind(TransformDto.KindEnum.DATE_DIFF_ROUND);
        transformDto.setDateDiffRoundType(YEAR);
        valueTransformationSpecEntryDto.setTransform(transformDto);

        valueTransformationSpecDto.addEntriesItem(valueTransformationSpecEntryDto);

        ValueTransformer valueTransformer = DtoConverter.toValueTransformer(valueTransformationSpecDto);

        String res1 = (String) valueTransformer.applyTransform("SomethingElse", "alsoSomethingElse", dateDiffValues.get(4));
        String res2 = (String) valueTransformer.applyTransform("TestEntity", "somethingElse", dateDiffValues.get(4));
        String res3 = (String) valueTransformer.applyTransform("SomethingElse", "testProperty", dateDiffValues.get(4));

        Assertions.assertEquals(dateDiffValues.get(4), res1);
        Assertions.assertEquals(dateDiffValues.get(4), res2);
        Assertions.assertEquals(dateDiffValues.get(4), res3);

        String res4 = (String) valueTransformer.applyTransform("TestEntity", "testProperty", dateDiffValues.get(0));
        String res5 = (String) valueTransformer.applyTransform("TestEntity", "testProperty", dateDiffValues.get(1));
        String res6 = (String) valueTransformer.applyTransform("TestEntity", "testProperty", dateDiffValues.get(2));
        String res7 = (String) valueTransformer.applyTransform("TestEntity", "testProperty", dateDiffValues.get(3));
        String res8 = (String) valueTransformer.applyTransform("TestEntity", "testProperty", dateDiffValues.get(4));

        Assertions.assertEquals(dateDiffValuesToResults.get(dateDiffValues.get(0)).get(YEAR), res4);
        Assertions.assertEquals(dateDiffValuesToResults.get(dateDiffValues.get(1)).get(YEAR), res5);
        Assertions.assertEquals(dateDiffValuesToResults.get(dateDiffValues.get(2)).get(YEAR), res6);
        Assertions.assertEquals(dateDiffValuesToResults.get(dateDiffValues.get(3)).get(YEAR), res7);
        Assertions.assertEquals(dateDiffValuesToResults.get(dateDiffValues.get(4)).get(YEAR), res8);
    }

    @Test
    void testDateDiffMultipleEntries()
    {
        ValueTransformationSpecDto valueTransformationSpecDto = new ValueTransformationSpecDto();

        List<ImmutablePair<String, String>> entityPropertyPairs = IntStream.range(0, 6)
                .mapToObj(i -> ImmutablePair.of(String.format("SomeEntity%s", i), String.format("someProperty%s", i)))
                .toList();

        ValueTransformationSpecEntryDto valueTransformationSpecEntryDto1 = new ValueTransformationSpecEntryDto();
        ValueTransformationSpecEntryDto valueTransformationSpecEntryDto2 = new ValueTransformationSpecEntryDto();
        ValueTransformationSpecEntryDto valueTransformationSpecEntryDto3 = new ValueTransformationSpecEntryDto();
        ValueTransformationSpecEntryDto valueTransformationSpecEntryDto4 = new ValueTransformationSpecEntryDto();
        ValueTransformationSpecEntryDto valueTransformationSpecEntryDto5 = new ValueTransformationSpecEntryDto();
        ValueTransformationSpecEntryDto valueTransformationSpecEntryDto6 = new ValueTransformationSpecEntryDto();

        valueTransformationSpecEntryDto1.setEntity(entityPropertyPairs.get(0).getLeft());
        valueTransformationSpecEntryDto1.setProperty(entityPropertyPairs.get(0).getRight());

        valueTransformationSpecEntryDto2.setEntity(entityPropertyPairs.get(1).getLeft());
        valueTransformationSpecEntryDto2.setProperty(entityPropertyPairs.get(1).getRight());

        valueTransformationSpecEntryDto3.setEntity(entityPropertyPairs.get(2).getLeft());
        valueTransformationSpecEntryDto3.setProperty(entityPropertyPairs.get(2).getRight());

        valueTransformationSpecEntryDto4.setEntity(entityPropertyPairs.get(3).getLeft());
        valueTransformationSpecEntryDto4.setProperty(entityPropertyPairs.get(3).getRight());

        valueTransformationSpecEntryDto5.setEntity(entityPropertyPairs.get(4).getLeft());
        valueTransformationSpecEntryDto5.setProperty(entityPropertyPairs.get(4).getRight());

        valueTransformationSpecEntryDto6.setEntity(entityPropertyPairs.get(5).getLeft());
        valueTransformationSpecEntryDto6.setProperty(entityPropertyPairs.get(5).getRight());

        TransformDto transformDto1 = new TransformDto();
        transformDto1.setKind(TransformDto.KindEnum.DATE_DIFF_ROUND);
        transformDto1.setDateDiffRoundType(YEAR);

        TransformDto transformDto2 = new TransformDto();
        transformDto2.setKind(TransformDto.KindEnum.DATE_DIFF_ROUND);
        transformDto2.setDateDiffRoundType(FIVE_YEARS);

        TransformDto transformDto3 = new TransformDto();
        transformDto3.setKind(TransformDto.KindEnum.DATE_DIFF_ROUND);
        transformDto3.setDateDiffRoundType(TEN_YEARS);

        TransformDto transformDto4 = new TransformDto();
        transformDto4.setKind(TransformDto.KindEnum.DATE_DIFF_ROUND);
        transformDto4.setDateDiffRoundType(FIFTEEN_YEARS);

        TransformDto transformDto5 = new TransformDto();
        transformDto5.setKind(TransformDto.KindEnum.DATE_DIFF_ROUND);
        transformDto5.setDateDiffRoundType(TWENTY_YEARS);

        TransformDto transformDto6 = new TransformDto();
        transformDto6.setKind(TransformDto.KindEnum.DATE_DIFF_ROUND);
        transformDto6.setDateDiffRoundType(YEAR);

        valueTransformationSpecEntryDto1.setTransform(transformDto1);
        valueTransformationSpecEntryDto2.setTransform(transformDto2);
        valueTransformationSpecEntryDto3.setTransform(transformDto3);
        valueTransformationSpecEntryDto4.setTransform(transformDto4);
        valueTransformationSpecEntryDto5.setTransform(transformDto5);
        valueTransformationSpecEntryDto6.setTransform(transformDto6);

        valueTransformationSpecDto.addEntriesItem(valueTransformationSpecEntryDto1);
        valueTransformationSpecDto.addEntriesItem(valueTransformationSpecEntryDto2);
        valueTransformationSpecDto.addEntriesItem(valueTransformationSpecEntryDto3);
        valueTransformationSpecDto.addEntriesItem(valueTransformationSpecEntryDto4);
        valueTransformationSpecDto.addEntriesItem(valueTransformationSpecEntryDto5);
        valueTransformationSpecDto.addEntriesItem(valueTransformationSpecEntryDto6);

        ValueTransformer valueTransformer = DtoConverter.toValueTransformer(valueTransformationSpecDto);

        String res1 = (String) valueTransformer.applyTransform("SomethingElse", "alsoSomethingElse", dateDiffValues.get(4));
        String res2 = (String) valueTransformer.applyTransform("TestEntity", "somethingElse", dateDiffValues.get(4));
        String res3 = (String) valueTransformer.applyTransform("SomethingElse", "testProperty", dateDiffValues.get(4));

        Assertions.assertEquals(dateDiffValues.get(4), res1);
        Assertions.assertEquals(dateDiffValues.get(4), res2);
        Assertions.assertEquals(dateDiffValues.get(4), res3);

        TransformDto.DateDiffRoundTypeEnum[] dateDiffRoundTypeEnums = TransformDto.DateDiffRoundTypeEnum.values();

        List<ImmutablePair<ImmutablePair<String, String>, TransformDto.DateDiffRoundTypeEnum>> immutablePairs = IntStream.range(0, entityPropertyPairs.size())
                .mapToObj(i -> ImmutablePair.of(entityPropertyPairs.get(i), dateDiffRoundTypeEnums[i >= dateDiffRoundTypeEnums.length ? 0 : i]))
                .toList();

        for (ImmutablePair<ImmutablePair<String, String>, TransformDto.DateDiffRoundTypeEnum> immutablePair : immutablePairs)
        {
            for (String dateDiffValue : dateDiffValues)
            {
                String resNxt = (String) valueTransformer.applyTransform(immutablePair.getLeft().getLeft(), immutablePair.getLeft().getRight(), dateDiffValue);
                String expected = dateDiffValuesToResults.get(dateDiffValue).get(immutablePair.getRight());
                Assertions.assertEquals(expected, resNxt);
            }
        }
    }

    @Test
    void testValueTransformerValidationValid()
    {
        ValueTransformer valueTransformer = new ValueTransformer();
        valueTransformer.addTransform("AdmissionsEntity", "language", x -> x);
        valueTransformer.addTransform("PatientsEntity", "gender", x -> x);
        Assertions.assertDoesNotThrow(() -> valueTransformer.assertValid(em.getMetamodel()));
    }

    @Test
    void testValueTransformerValidationEmpty()
    {
        ValueTransformer valueTransformer = new ValueTransformer();
        Assertions.assertDoesNotThrow(() -> valueTransformer.assertValid(em.getMetamodel()));
    }

    @Test
    void testValueTransformerValidationWrongEntity()
    {
        ValueTransformer valueTransformer = new ValueTransformer();
        valueTransformer.addTransform("AdmissionsEntity", "language", x -> x);
        valueTransformer.addTransform("PatientsEntity", "gender", x -> x);
        valueTransformer.addTransform("Wrong", "dbSource", x -> x);
        Assertions.assertThrows(ValidationCoreException.class, () -> valueTransformer.assertValid(em.getMetamodel()));
    }

    @Test
    void testValueTransformerValidationWrongProperty()
    {
        ValueTransformer valueTransformer = new ValueTransformer();
        valueTransformer.addTransform("AdmissionsEntity", "language", x -> x);
        valueTransformer.addTransform("PatientsEntity", "wrong", x -> x);
        Assertions.assertThrows(ValidationCoreException.class, () -> valueTransformer.assertValid(em.getMetamodel()));
    }

}
