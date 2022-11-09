package si.jernej.mexplorer.core.test.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.weld.environment.se.Weld;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import si.jernej.mexplorer.core.exception.ValidationCoreException;
import si.jernej.mexplorer.core.manager.MimicEntityManager;
import si.jernej.mexplorer.core.processing.TargetExtraction;
import si.jernej.mexplorer.core.service.PropositionalizationService;
import si.jernej.mexplorer.processorapi.v1.model.CompositeColumnsSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.CompositeColumnsSpecEntryDto;
import si.jernej.mexplorer.processorapi.v1.model.ConcatenationSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.PropertySpecDto;
import si.jernej.mexplorer.processorapi.v1.model.PropertySpecEntryDto;
import si.jernej.mexplorer.processorapi.v1.model.RootEntitiesSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.TransformDto;
import si.jernej.mexplorer.processorapi.v1.model.ValueTransformationSpecDto;
import si.jernej.mexplorer.processorapi.v1.model.ValueTransformationSpecEntryDto;
import si.jernej.mexplorer.processorapi.v1.model.WordificationConfigDto;
import si.jernej.mexplorer.processorapi.v1.model.WordificationResultDto;
import si.jernej.mexplorer.test.ATestBase;

public class PropositionalizationServiceTest extends ATestBase
{
    @Override
    protected Weld loadWeld(Weld weld)
    {
        return weld.addPackages(
                true,
                getClass(),
                PropositionalizationService.class,
                TargetExtraction.class,
                MimicEntityManager.class
        );
    }

    @Inject
    private PropositionalizationService propositionalizationService;

    @Test
    public void testComputeWoridificationWrongEntity()
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
    public void testComputeWoridificationWrongIdProperty()
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

        List<WordificationResultDto> results = propositionalizationService.computeWordification(wordificationConfigDto);
        results.forEach(r -> Assertions.assertTrue(r.getWords().isEmpty()));
    }

    @Test
    public void testComputeWoridificationSimpleSingleEntity()
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

        List<WordificationResultDto> results = propositionalizationService.computeWordification(wordificationConfigDto);

        Set<String> expectedWords = Set.of(
                "admissionsentity@insurance@private",
                "admissionsentity@language@engl",
                "admissionsentity@religion@protestant_quaker"
        );

        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals(expectedWords, new HashSet<>(results.get(0).getWords()));
    }

    @Test
    public void testComputeWoridificationSimpleTwoLinkedEntities()
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
        propertySpecEntryDto1.setProperties(List.of("insurance", "language", "religion"));
        propertySpecDto.addEntriesItem(propertySpecEntryDto1);

        PropertySpecEntryDto propertySpecEntryDto2 = new PropertySpecEntryDto();
        propertySpecEntryDto2.setEntity("PatientsEntity");
        propertySpecEntryDto2.setProperties(List.of("gender", "expireFlag"));
        propertySpecDto.addEntriesItem(propertySpecEntryDto2);

        wordificationConfigDto.setPropertySpec(propertySpecDto);

        ConcatenationSpecDto concatenationSpecDto = new ConcatenationSpecDto();
        concatenationSpecDto.setConcatenationScheme(ConcatenationSpecDto.ConcatenationSchemeEnum.ZERO);

        wordificationConfigDto.setConcatenationSpec(concatenationSpecDto);

        List<WordificationResultDto> results = propositionalizationService.computeWordification(wordificationConfigDto);

        Set<String> expectedWords = Set.of(
                "admissionsentity@insurance@private",
                "admissionsentity@language@engl",
                "admissionsentity@religion@protestant_quaker",
                "patientsentity@expireflag@0",
                "patientsentity@gender@f"
        );

        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals(expectedWords, new HashSet<>(results.get(0).getWords()));
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
        propertySpecEntryDto1.setProperties(List.of("insurance", "language", "religion"));
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
        compositeColumnsSpecEntryDto.setTable1("PatientsEntity");
        compositeColumnsSpecEntryDto.setTable2("AdmissionsEntity");
        compositeColumnsSpecEntryDto.setProperty1("dob");
        compositeColumnsSpecEntryDto.setProperty2("admitTime");
        compositeColumnsSpecEntryDto.setCompositeName("ageAtAdmission");
        compositeColumnsSpecEntryDto.setCombiner(CompositeColumnsSpecEntryDto.CombinerEnum.DATE_DIFF);

        compositeColumnsSpecDto.addEntriesItem(compositeColumnsSpecEntryDto);

        wordificationConfigDto.setCompositeColumnsSpec(compositeColumnsSpecDto);

        List<WordificationResultDto> results = propositionalizationService.computeWordification(wordificationConfigDto);

        Set<String> expectedWords = Set.of(
                "admissionsentity@insurance@private",
                "admissionsentity@language@engl",
                "admissionsentity@religion@protestant_quaker",
                "patientsentity@expireflag@0",
                "patientsentity@gender@f",
                "composite@ageatadmission@35_5_21"
        );

        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals(expectedWords, new HashSet<>(results.get(0).getWords()));
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
        propertySpecEntryDto1.setProperties(List.of("insurance", "language", "religion"));
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
        compositeColumnsSpecEntryDto.setTable1("PatientsEntity");
        compositeColumnsSpecEntryDto.setTable2("AdmissionsEntity");
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

        List<WordificationResultDto> results = propositionalizationService.computeWordification(wordificationConfigDto);

        Set<String> expectedWords = Set.of(
                "admissionsentity@insurance@private",
                "admissionsentity@language@engl",
                "admissionsentity@religion@protestant_quaker",
                "patientsentity@expireflag@0",
                "patientsentity@gender@f",
                "composite@ageatadmission@40"
        );

        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals(expectedWords, new HashSet<>(results.get(0).getWords()));
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
        propertySpecEntryDto1.setProperties(List.of("insurance", "language", "religion"));
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
        compositeColumnsSpecEntryDto.setTable1("PatientsEntity");
        compositeColumnsSpecEntryDto.setTable2("AdmissionsEntity");
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

        List<WordificationResultDto> results = propositionalizationService.computeWordification(wordificationConfigDto);

        Set<String> expectedWords = Set.of(
                "admissionsentity@insurance@private",
                "admissionsentity@language@engl",
                "admissionsentity@religion@protestant_quaker",
                "icustaysentity@firstcareunit@micu",
                "icustaysentity@lastcareunit@micu",
                "icustaysentity@los@4.0",
                "patientsentity@expireflag@0",
                "patientsentity@gender@f",
                "composite@ageatadmission@40"
        );

        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals(expectedWords, new HashSet<>(results.get(0).getWords()));
    }

}
