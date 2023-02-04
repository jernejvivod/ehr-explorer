package si.jernej.mexplorer.core.test.service;

import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import si.jernej.mexplorer.core.exception.ValidationCoreException;
import si.jernej.mexplorer.core.service.PropositionalizationService;
import si.jernej.mexplorer.core.service.TargetExtractionService;
import si.jernej.mexplorer.core.test.ACoreTest;
import si.jernej.mexplorer.processorapi.v1.model.CompositeColumnsSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.CompositeColumnsSpecEntryDto;
import si.jernej.mexplorer.processorapi.v1.model.ConcatenationSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.ExtractedTargetDto;
import si.jernej.mexplorer.processorapi.v1.model.PropertySpecDto;
import si.jernej.mexplorer.processorapi.v1.model.PropertySpecEntryDto;
import si.jernej.mexplorer.processorapi.v1.model.RootEntitiesSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.TargetExtractionSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.TransformDto;
import si.jernej.mexplorer.processorapi.v1.model.ValueTransformationSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.ValueTransformationSpecEntryDto;
import si.jernej.mexplorer.processorapi.v1.model.WordificationConfigDto;
import si.jernej.mexplorer.processorapi.v1.model.WordificationResultDto;

public class PropositionalizationServiceTest extends ACoreTest
{
    @Inject
    private PropositionalizationService propositionalizationService;
    @Inject
    private TargetExtractionService targetExtractionService;

    @Test
    public void testComputeWordificationWrongEntity()
    {
        WordificationConfigDto wordificationConfigDto = new WordificationConfigDto();
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("Wrong");
        rootEntitiesSpecDto.setIdProperty("wrong");
        rootEntitiesSpecDto.setIds(List.of(100001L));
        wordificationConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);
        Assertions.assertThrows(ValidationCoreException.class, () -> propositionalizationService.computeWordification(wordificationConfigDto));
    }

    @Test
    public void testComputeWordificationWrongIdProperty()
    {
        WordificationConfigDto wordificationConfigDto = new WordificationConfigDto();
        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("wrong");
        rootEntitiesSpecDto.setIds(List.of(100001L));
        wordificationConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);
        Assertions.assertThrows(ValidationCoreException.class, () -> propositionalizationService.computeWordification(wordificationConfigDto));
    }

    @Test
    public void testComputeWordificationEmptyPropertySpec()
    {
        WordificationConfigDto wordificationConfigDto = new WordificationConfigDto();

        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(100001L));

        wordificationConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        PropertySpecDto propertySpecDto = new PropertySpecDto();
        wordificationConfigDto.setPropertySpec(propertySpecDto);

        ConcatenationSpecDto concatenationSpecDto = new ConcatenationSpecDto();
        concatenationSpecDto.setConcatenationScheme(ConcatenationSpecDto.ConcatenationSchemeEnum.ZERO);

        wordificationConfigDto.setConcatenationSpec(concatenationSpecDto);

        List<WordificationResultDto> res = propositionalizationService.computeWordification(wordificationConfigDto);
        res.forEach(r -> Assertions.assertTrue(r.getWords().isEmpty()));
    }

    @Test
    public void testComputeWordificationSimpleSingleEntity()
    {
        WordificationConfigDto wordificationConfigDto = new WordificationConfigDto();

        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(100001L));

        wordificationConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        PropertySpecDto propertySpecDto = new PropertySpecDto();

        PropertySpecEntryDto propertySpecEntryDto = new PropertySpecEntryDto();
        propertySpecEntryDto.setEntity("AdmissionsEntity");
        propertySpecEntryDto.setProperties(List.of("insurance", "language", "religion"));
        propertySpecDto.addEntriesItem(propertySpecEntryDto);

        wordificationConfigDto.setPropertySpec(propertySpecDto);

        ConcatenationSpecDto concatenationSpecDto = new ConcatenationSpecDto();
        concatenationSpecDto.setConcatenationScheme(ConcatenationSpecDto.ConcatenationSchemeEnum.ZERO);

        wordificationConfigDto.setConcatenationSpec(concatenationSpecDto);

        List<WordificationResultDto> res = propositionalizationService.computeWordification(wordificationConfigDto);

        List<String> expectedWords = List.of(
                "admissionsentity@insurance@private",
                "admissionsentity@language@engl",
                "admissionsentity@religion@protestant_quaker"
        );

        Assertions.assertEquals(1, res.size());
        Assertions.assertEquals(expectedWords, res.get(0).getWords());
    }

    @Test
    public void testComputeWordificationSimpleTwoLinkedEntities()
    {
        WordificationConfigDto wordificationConfigDto = new WordificationConfigDto();

        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(100001L));

        wordificationConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        PropertySpecDto propertySpecDto = new PropertySpecDto();

        PropertySpecEntryDto propertySpecEntryDto1 = new PropertySpecEntryDto();
        propertySpecEntryDto1.setEntity("AdmissionsEntity");
        propertySpecEntryDto1.setProperties(List.of("insurance", "language", "religion", "patientsEntity"));
        propertySpecDto.addEntriesItem(propertySpecEntryDto1);

        PropertySpecEntryDto propertySpecEntryDto2 = new PropertySpecEntryDto();
        propertySpecEntryDto2.setEntity("PatientsEntity");
        propertySpecEntryDto2.setProperties(List.of("gender", "expireFlag"));
        propertySpecDto.addEntriesItem(propertySpecEntryDto2);

        wordificationConfigDto.setPropertySpec(propertySpecDto);

        ConcatenationSpecDto concatenationSpecDto = new ConcatenationSpecDto();
        concatenationSpecDto.setConcatenationScheme(ConcatenationSpecDto.ConcatenationSchemeEnum.ZERO);

        wordificationConfigDto.setConcatenationSpec(concatenationSpecDto);

        List<WordificationResultDto> res = propositionalizationService.computeWordification(wordificationConfigDto);

        List<String> expectedWords = List.of(
                "admissionsentity@insurance@private",
                "admissionsentity@language@engl",
                "admissionsentity@religion@protestant_quaker",
                "patientsentity@gender@f",
                "patientsentity@expireflag@0"
        );

        Assertions.assertEquals(1, res.size());
        Assertions.assertEquals(expectedWords, res.get(0).getWords());
    }

    @Test
    public void testComputeWordificationSimpleTwoLinkedEntitiesWithCompositeColumns()
    {
        WordificationConfigDto wordificationConfigDto = new WordificationConfigDto();

        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(100001L));

        wordificationConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        PropertySpecDto propertySpecDto = new PropertySpecDto();

        PropertySpecEntryDto propertySpecEntryDto1 = new PropertySpecEntryDto();
        propertySpecEntryDto1.setEntity("AdmissionsEntity");
        propertySpecEntryDto1.setProperties(List.of("insurance", "language", "religion", "patientsEntity"));
        propertySpecDto.addEntriesItem(propertySpecEntryDto1);

        PropertySpecEntryDto propertySpecEntryDto2 = new PropertySpecEntryDto();
        propertySpecEntryDto2.setEntity("PatientsEntity");
        propertySpecEntryDto2.setProperties(List.of("gender", "expireFlag"));
        propertySpecDto.addEntriesItem(propertySpecEntryDto2);

        wordificationConfigDto.setPropertySpec(propertySpecDto);

        ConcatenationSpecDto concatenationSpecDto = new ConcatenationSpecDto();
        concatenationSpecDto.setConcatenationScheme(ConcatenationSpecDto.ConcatenationSchemeEnum.ZERO);

        wordificationConfigDto.setConcatenationSpec(concatenationSpecDto);

        CompositeColumnsSpecDto compositeColumnsSpecDto = new CompositeColumnsSpecDto();

        CompositeColumnsSpecEntryDto compositeColumnsSpecEntryDto = new CompositeColumnsSpecEntryDto();
        compositeColumnsSpecEntryDto.setForeignKeyPath1(List.of("AdmissionsEntity", "PatientsEntity"));
        compositeColumnsSpecEntryDto.setForeignKeyPath2(List.of("AdmissionsEntity"));
        compositeColumnsSpecEntryDto.setProperty1("dob");
        compositeColumnsSpecEntryDto.setProperty2("admitTime");
        compositeColumnsSpecEntryDto.setCompositeName("ageAtAdmission");
        compositeColumnsSpecEntryDto.setCombiner(CompositeColumnsSpecEntryDto.CombinerEnum.DATE_DIFF);

        compositeColumnsSpecDto.addEntriesItem(compositeColumnsSpecEntryDto);

        wordificationConfigDto.setCompositeColumnsSpec(compositeColumnsSpecDto);

        List<WordificationResultDto> res = propositionalizationService.computeWordification(wordificationConfigDto);

        List<String> expectedWords = List.of(
                "admissionsentity@insurance@private",
                "admissionsentity@language@engl",
                "admissionsentity@religion@protestant_quaker",
                "patientsentity@gender@f",
                "patientsentity@expireflag@0",
                "composite@ageatadmission@35_5_21"
        );

        Assertions.assertEquals(1, res.size());
        Assertions.assertEquals(expectedWords, res.get(0).getWords());
    }

    @Test
    public void testComputeWordificationSimpleTwoLinkedEntitiesWithCompositeColumnsAndValueTransformer()
    {
        WordificationConfigDto wordificationConfigDto = new WordificationConfigDto();

        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(100001L));

        wordificationConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        PropertySpecDto propertySpecDto = new PropertySpecDto();

        PropertySpecEntryDto propertySpecEntryDto1 = new PropertySpecEntryDto();
        propertySpecEntryDto1.setEntity("AdmissionsEntity");
        propertySpecEntryDto1.setProperties(List.of("insurance", "language", "religion", "patientsEntity"));
        propertySpecDto.addEntriesItem(propertySpecEntryDto1);

        PropertySpecEntryDto propertySpecEntryDto2 = new PropertySpecEntryDto();
        propertySpecEntryDto2.setEntity("PatientsEntity");
        propertySpecEntryDto2.setProperties(List.of("gender", "expireFlag"));
        propertySpecDto.addEntriesItem(propertySpecEntryDto2);

        wordificationConfigDto.setPropertySpec(propertySpecDto);

        ConcatenationSpecDto concatenationSpecDto = new ConcatenationSpecDto();
        concatenationSpecDto.setConcatenationScheme(ConcatenationSpecDto.ConcatenationSchemeEnum.ZERO);

        wordificationConfigDto.setConcatenationSpec(concatenationSpecDto);

        CompositeColumnsSpecDto compositeColumnsSpecDto = new CompositeColumnsSpecDto();

        CompositeColumnsSpecEntryDto compositeColumnsSpecEntryDto = new CompositeColumnsSpecEntryDto();
        compositeColumnsSpecEntryDto.setForeignKeyPath1(List.of("AdmissionsEntity", "PatientsEntity"));
        compositeColumnsSpecEntryDto.setForeignKeyPath2(List.of("AdmissionsEntity"));
        compositeColumnsSpecEntryDto.setProperty1("dob");
        compositeColumnsSpecEntryDto.setProperty2("admitTime");
        compositeColumnsSpecEntryDto.setCompositeName("ageAtAdmission");
        compositeColumnsSpecEntryDto.setCombiner(CompositeColumnsSpecEntryDto.CombinerEnum.DATE_DIFF);

        compositeColumnsSpecDto.addEntriesItem(compositeColumnsSpecEntryDto);

        wordificationConfigDto.setCompositeColumnsSpec(compositeColumnsSpecDto);

        ValueTransformationSpecDto valueTransformationSpecDto = new ValueTransformationSpecDto();
        ValueTransformationSpecEntryDto valueTransformationSpecEntryDto = new ValueTransformationSpecEntryDto();
        valueTransformationSpecEntryDto.setEntity("composite");
        valueTransformationSpecEntryDto.setProperty("ageAtAdmission");

        TransformDto transformDto = new TransformDto();
        transformDto.setDateDiffRoundType(TransformDto.DateDiffRoundTypeEnum.TWENTY_YEARS);
        transformDto.setKind(TransformDto.KindEnum.DATE_DIFF_ROUND);
        valueTransformationSpecEntryDto.setTransform(transformDto);

        valueTransformationSpecDto.addEntriesItem(valueTransformationSpecEntryDto);

        wordificationConfigDto.setValueTransformationSpec(valueTransformationSpecDto);

        List<WordificationResultDto> res = propositionalizationService.computeWordification(wordificationConfigDto);

        List<String> expectedWords = List.of(
                "admissionsentity@insurance@private",
                "admissionsentity@language@engl",
                "admissionsentity@religion@protestant_quaker",
                "patientsentity@gender@f",
                "patientsentity@expireflag@0",
                "composite@ageatadmission@40"
        );

        Assertions.assertEquals(1, res.size());
        Assertions.assertEquals(expectedWords, res.get(0).getWords());
    }

    @Test
    public void testComputeWordificationSimpleTwoLinkedEntitiesWithValueTransformerRounding()
    {
        WordificationConfigDto wordificationConfigDto = new WordificationConfigDto();

        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");
        rootEntitiesSpecDto.setIds(List.of(100001L));

        wordificationConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        PropertySpecDto propertySpecDto = new PropertySpecDto();

        PropertySpecEntryDto propertySpecEntryDto1 = new PropertySpecEntryDto();
        propertySpecEntryDto1.setEntity("AdmissionsEntity");
        propertySpecEntryDto1.setProperties(List.of("insurance", "language", "religion", "patientsEntity", "icuStaysEntitys"));
        propertySpecDto.addEntriesItem(propertySpecEntryDto1);

        PropertySpecEntryDto propertySpecEntryDto2 = new PropertySpecEntryDto();
        propertySpecEntryDto2.setEntity("PatientsEntity");
        propertySpecEntryDto2.setProperties(List.of("gender", "expireFlag"));
        propertySpecDto.addEntriesItem(propertySpecEntryDto2);

        PropertySpecEntryDto propertySpecEntryDto3 = new PropertySpecEntryDto();
        propertySpecEntryDto3.setEntity("IcuStaysEntity");
        propertySpecEntryDto3.setProperties(List.of("firstCareUnit", "lastCareUnit", "los"));
        propertySpecDto.addEntriesItem(propertySpecEntryDto3);

        wordificationConfigDto.setPropertySpec(propertySpecDto);

        ConcatenationSpecDto concatenationSpecDto = new ConcatenationSpecDto();
        concatenationSpecDto.setConcatenationScheme(ConcatenationSpecDto.ConcatenationSchemeEnum.ZERO);

        wordificationConfigDto.setConcatenationSpec(concatenationSpecDto);

        CompositeColumnsSpecDto compositeColumnsSpecDto = new CompositeColumnsSpecDto();

        CompositeColumnsSpecEntryDto compositeColumnsSpecEntryDto = new CompositeColumnsSpecEntryDto();
        compositeColumnsSpecEntryDto.setForeignKeyPath1(List.of("AdmissionsEntity", "PatientsEntity"));
        compositeColumnsSpecEntryDto.setForeignKeyPath2(List.of("AdmissionsEntity"));
        compositeColumnsSpecEntryDto.setProperty1("dob");
        compositeColumnsSpecEntryDto.setProperty2("admitTime");
        compositeColumnsSpecEntryDto.setCompositeName("ageAtAdmission");
        compositeColumnsSpecEntryDto.setCombiner(CompositeColumnsSpecEntryDto.CombinerEnum.DATE_DIFF);

        compositeColumnsSpecDto.addEntriesItem(compositeColumnsSpecEntryDto);

        wordificationConfigDto.setCompositeColumnsSpec(compositeColumnsSpecDto);

        ValueTransformationSpecDto valueTransformationSpecDto = new ValueTransformationSpecDto();

        ValueTransformationSpecEntryDto valueTransformationSpecEntryDto1 = new ValueTransformationSpecEntryDto();
        valueTransformationSpecEntryDto1.setEntity("composite");
        valueTransformationSpecEntryDto1.setProperty("ageAtAdmission");

        TransformDto transformDto1 = new TransformDto();
        transformDto1.setDateDiffRoundType(TransformDto.DateDiffRoundTypeEnum.TWENTY_YEARS);
        transformDto1.setKind(TransformDto.KindEnum.DATE_DIFF_ROUND);
        valueTransformationSpecEntryDto1.setTransform(transformDto1);

        valueTransformationSpecDto.addEntriesItem(valueTransformationSpecEntryDto1);

        ValueTransformationSpecEntryDto valueTransformationSpecEntryDto2 = new ValueTransformationSpecEntryDto();
        valueTransformationSpecEntryDto2.setEntity("IcuStaysEntity");
        valueTransformationSpecEntryDto2.setProperty("los");

        TransformDto transformDto2 = new TransformDto();
        transformDto2.setRoundingMultiple(1.0);
        transformDto2.setKind(TransformDto.KindEnum.ROUNDING);
        valueTransformationSpecEntryDto2.setTransform(transformDto2);

        valueTransformationSpecDto.addEntriesItem(valueTransformationSpecEntryDto2);

        wordificationConfigDto.setValueTransformationSpec(valueTransformationSpecDto);

        List<WordificationResultDto> res = propositionalizationService.computeWordification(wordificationConfigDto);

        List<String> expectedWords = List.of(
                "admissionsentity@insurance@private",
                "admissionsentity@language@engl",
                "admissionsentity@religion@protestant_quaker",
                "icustaysentity@firstcareunit@micu",
                "icustaysentity@lastcareunit@micu",
                "icustaysentity@los@4.0",
                "patientsentity@gender@f",
                "patientsentity@expireflag@0",
                "composite@ageatadmission@40"
        );

        Assertions.assertEquals(1, res.size());
        Assertions.assertEquals(expectedWords, res.get(0).getWords());
    }

    @Test
    @Timeout(value = 180)
    public void testComputeWordificationSimpleTwoLinkedEntities50PercentAdmissionsEntries()
    {
        WordificationConfigDto wordificationConfigDto = new WordificationConfigDto();

        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("AdmissionsEntity");
        rootEntitiesSpecDto.setIdProperty("hadmId");

        // select 50% of IDs
        List<Long> ids = em.createQuery("SELECT a.hadmId FROM AdmissionsEntity a", Long.class)
                .setMaxResults(29488)
                .getResultList();
        rootEntitiesSpecDto.setIds(ids);

        wordificationConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        PropertySpecDto propertySpecDto = new PropertySpecDto();

        PropertySpecEntryDto propertySpecEntryDto1 = new PropertySpecEntryDto();
        propertySpecEntryDto1.setEntity("AdmissionsEntity");
        propertySpecEntryDto1.setProperties(List.of("insurance", "language", "religion", "patientsEntity", "icuStaysEntitys", "calloutEntitys"));
        propertySpecDto.addEntriesItem(propertySpecEntryDto1);

        PropertySpecEntryDto propertySpecEntryDto2 = new PropertySpecEntryDto();
        propertySpecEntryDto2.setEntity("PatientsEntity");
        propertySpecEntryDto2.setProperties(List.of("gender", "expireFlag"));
        propertySpecDto.addEntriesItem(propertySpecEntryDto2);

        PropertySpecEntryDto propertySpecEntryDto3 = new PropertySpecEntryDto();
        propertySpecEntryDto3.setEntity("IcuStaysEntity");
        propertySpecEntryDto3.setProperties(List.of("firstCareUnit", "lastCareUnit"));
        propertySpecDto.addEntriesItem(propertySpecEntryDto3);

        PropertySpecEntryDto propertySpecEntryDto4 = new PropertySpecEntryDto();
        propertySpecEntryDto4.setEntity("CalloutEntity");
        propertySpecEntryDto4.setProperties(
                List.of("submitWardId",
                        "submitCareUnit",
                        "currWardId",
                        "currCareUnit",
                        "calloutWardId",
                        "calloutService",
                        "requestTele",
                        "requestResp",
                        "requestCdiff",
                        "requestMrsa",
                        "requestVre",
                        "calloutStatus",
                        "calloutOutcome",
                        "dischargeWardId",
                        "acknowledgeStatus"
                )
        );
        propertySpecDto.addEntriesItem(propertySpecEntryDto4);

        wordificationConfigDto.setPropertySpec(propertySpecDto);

        ConcatenationSpecDto concatenationSpecDto = new ConcatenationSpecDto();
        concatenationSpecDto.setConcatenationScheme(ConcatenationSpecDto.ConcatenationSchemeEnum.ONE);

        wordificationConfigDto.setConcatenationSpec(concatenationSpecDto);

        List<WordificationResultDto> res = propositionalizationService.computeWordification(wordificationConfigDto);

        Assertions.assertNotNull(res);
        Assertions.assertFalse(res.isEmpty());
    }

    @Test
    void testWordificationPatientsEntityWithDurationLimitFromTarget()
    {
        long patientSubjectId = 291L;

        TargetExtractionSpecDto targetExtractionSpecDto = new TargetExtractionSpecDto();
        targetExtractionSpecDto.setTargetType(TargetExtractionSpecDto.TargetTypeEnum.ICU_STAY_READMISSION_HAPPENED);
        targetExtractionSpecDto.setIds(List.of(patientSubjectId));

        List<ExtractedTargetDto> extractedTargetDtos = targetExtractionService.computeTarget(targetExtractionSpecDto);

        WordificationConfigDto wordificationConfigDto = new WordificationConfigDto();

        ConcatenationSpecDto concatenationSpecDto = new ConcatenationSpecDto();
        concatenationSpecDto.setConcatenationScheme(ConcatenationSpecDto.ConcatenationSchemeEnum.ZERO);
        wordificationConfigDto.setConcatenationSpec(concatenationSpecDto);

        RootEntitiesSpecDto rootEntitiesSpecDto = new RootEntitiesSpecDto();
        rootEntitiesSpecDto.setRootEntity("PatientsEntity");
        rootEntitiesSpecDto.setIdProperty("subjectId");
        rootEntitiesSpecDto.setIds(List.of(patientSubjectId));

        wordificationConfigDto.setRootEntitiesSpec(rootEntitiesSpecDto);

        PropertySpecDto propertySpecDto = new PropertySpecDto();

        PropertySpecEntryDto propertySpecEntryDto1 = new PropertySpecEntryDto();
        propertySpecEntryDto1.setEntity("PatientsEntity");
        propertySpecEntryDto1.setProperties(List.of("gender", "icuStaysEntitys"));
        propertySpecDto.addEntriesItem(propertySpecEntryDto1);

        PropertySpecEntryDto propertySpecEntryDto2 = new PropertySpecEntryDto();
        propertySpecEntryDto2.setEntity("IcuStaysEntity");
        propertySpecEntryDto2.setProperties(List.of("dbSource"));
        propertySpecEntryDto2.setPropertyForLimit("outTime");
        propertySpecDto.addEntriesItem(propertySpecEntryDto2);

        propertySpecDto.setDurationLim(extractedTargetDtos.get(0).getDateTimeLimit());
        wordificationConfigDto.setPropertySpec(propertySpecDto);

        List<WordificationResultDto> res1 = propositionalizationService.computeWordification(wordificationConfigDto);
        Assertions.assertEquals(1, res1.size());
        Assertions.assertEquals(2, res1.get(0).getWords().size());

        propertySpecDto.setDurationLim(extractedTargetDtos.get(1).getDateTimeLimit());
        wordificationConfigDto.setPropertySpec(propertySpecDto);

        List<WordificationResultDto> res2 = propositionalizationService.computeWordification(wordificationConfigDto);
        Assertions.assertEquals(1, res2.size());
        Assertions.assertEquals(3, res2.get(0).getWords().size());

        propertySpecDto.setDurationLim(extractedTargetDtos.get(2).getDateTimeLimit());
        wordificationConfigDto.setPropertySpec(propertySpecDto);

        List<WordificationResultDto> res3 = propositionalizationService.computeWordification(wordificationConfigDto);
        Assertions.assertEquals(1, res3.size());
        Assertions.assertEquals(4, res3.get(0).getWords().size());
    }
}
