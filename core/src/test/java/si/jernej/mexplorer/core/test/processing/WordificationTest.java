package si.jernej.mexplorer.core.test.processing;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import si.jernej.mexplorer.core.processing.Wordification;
import si.jernej.mexplorer.core.processing.spec.PropertySpec;
import si.jernej.mexplorer.core.processing.transform.CompositeColumnCreator;
import si.jernej.mexplorer.core.processing.transform.ValueTransformer;
import si.jernej.mexplorer.core.test.ACoreTest;
import si.jernej.mexplorer.core.util.DtoConverter;
import si.jernej.mexplorer.core.util.EntityUtils;
import si.jernej.mexplorer.entity.AdmissionsEntity;
import si.jernej.mexplorer.entity.IcuStaysEntity;
import si.jernej.mexplorer.entity.PatientsEntity;

class WordificationTest extends ACoreTest
{
    @Inject
    private Wordification wordification;

    private static AdmissionsEntity rootAdmissionsEntity;

    @BeforeAll
    static void constructEntities()
    {
        rootAdmissionsEntity = new AdmissionsEntity();
        rootAdmissionsEntity.setAdmissionType("admissionTypeString");
        rootAdmissionsEntity.setInsurance("insuranceString");
        rootAdmissionsEntity.setAdmitTime(LocalDateTime.of(LocalDate.of(2012, 2, 13), LocalTime.of(8, 13)));

        PatientsEntity patientsEntity = new PatientsEntity();
        patientsEntity.setGender("genderString");
        patientsEntity.setDob(LocalDateTime.of(LocalDate.of(1955, 8, 3), LocalTime.of(0, 0)));

        int year = 2022;
        int month = 4;
        int dayOfMonth = 18;
        int hour = 8;
        int minute = 0;
        patientsEntity.setDod(LocalDateTime.of(year, month, dayOfMonth, hour, minute));
        rootAdmissionsEntity.setPatientsEntity(patientsEntity);
    }

    @Test
    void blank()
    {
        PropertySpec propertySpec = new PropertySpec();
        ValueTransformer valueTransformer = new ValueTransformer();
        CompositeColumnCreator compositeColumnCreator = new CompositeColumnCreator();

        List<String> res = wordification.wordify(
                rootAdmissionsEntity,
                propertySpec,
                valueTransformer,
                compositeColumnCreator,
                Wordification.ConcatenationScheme.ZERO,
                EntityUtils.getTransitionPairsFromForeignKeyPath(EntityUtils.getForeignKeyPathsFromPropertySpec("AdmissionsEntity", propertySpec, em.getMetamodel())),
                null,
                false
        );

        Assertions.assertTrue(res.isEmpty());
    }

    @Test
    void basic()
    {
        List<String> expectedWords = List.of(
                "admissionsentity@admissiontype@admissiontypestring",
                "admissionsentity@insurance@insurancestring",
                "patientsentity@gender@genderstring",
                "patientsentity@dod@2022-04-18t08:00"
        );

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addEntry("AdmissionsEntity", List.of("admissionType", "insurance", "patientsEntity"));
        propertySpec.addEntry("PatientsEntity", List.of("gender", "dod"));

        ValueTransformer valueTransformer = new ValueTransformer();
        CompositeColumnCreator compositeColumnCreator = new CompositeColumnCreator();

        List<String> res = wordification.wordify(rootAdmissionsEntity,
                propertySpec,
                valueTransformer,
                compositeColumnCreator,
                Wordification.ConcatenationScheme.ZERO,
                EntityUtils.getTransitionPairsFromForeignKeyPath(EntityUtils.getForeignKeyPathsFromPropertySpec("AdmissionsEntity", propertySpec, em.getMetamodel())),
                null,
                false
        );

        Assertions.assertEquals(expectedWords, res);
    }

    @Test
    void basicOneConcatenation()
    {
        List<String> expectedWords = List.of(
                "admissionsentity@admissiontype@admissiontypestring",
                "admissionsentity@insurance@insurancestring",
                "admissionsentity@admissiontype@admissiontypestring@@admissionsentity@insurance@insurancestring",
                "patientsentity@gender@genderstring",
                "patientsentity@dod@2022-04-18t08:00",
                "patientsentity@gender@genderstring@@patientsentity@dod@2022-04-18t08:00"
        );

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addEntry("AdmissionsEntity", List.of("admissionType", "insurance", "patientsEntity"));
        propertySpec.addEntry("PatientsEntity", List.of("gender", "dod"));

        ValueTransformer valueTransformer = new ValueTransformer();
        CompositeColumnCreator compositeColumnCreator = new CompositeColumnCreator();

        List<String> res = wordification.wordify(
                rootAdmissionsEntity,
                propertySpec,
                valueTransformer,
                compositeColumnCreator,
                Wordification.ConcatenationScheme.ONE,
                EntityUtils.getTransitionPairsFromForeignKeyPath(EntityUtils.getForeignKeyPathsFromPropertySpec("AdmissionsEntity", propertySpec, em.getMetamodel())),
                null,
                false
        );

        Assertions.assertEquals(expectedWords, res);
    }

    @Test
    void basicTwoConcatenation()
    {
        List<String> expectedWords = List.of(
                "admissionsentity@admissiontype@admissiontypestring",
                "admissionsentity@insurance@insurancestring",
                "admissionsentity@admissiontype@admissiontypestring@@admissionsentity@insurance@insurancestring",
                "patientsentity@gender@genderstring",
                "patientsentity@dod@2022-04-18t08:00",
                "patientsentity@gender@genderstring@@patientsentity@dod@2022-04-18t08:00"
        );

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addEntry("AdmissionsEntity", List.of("admissionType", "insurance", "patientsEntity"));
        propertySpec.addEntry("PatientsEntity", List.of("gender", "dod"));

        ValueTransformer valueTransformer = new ValueTransformer();
        CompositeColumnCreator compositeColumnCreator = new CompositeColumnCreator();

        List<String> res = wordification.wordify(rootAdmissionsEntity,
                propertySpec,
                valueTransformer,
                compositeColumnCreator,
                Wordification.ConcatenationScheme.TWO,
                EntityUtils.getTransitionPairsFromForeignKeyPath(EntityUtils.getForeignKeyPathsFromPropertySpec("AdmissionsEntity", propertySpec, em.getMetamodel())),
                null,
                false
        );

        Assertions.assertEquals(expectedWords, res);
    }

    @Test
    void basicWithCompositeColumnCreator()
    {
        List<String> expectedWords = List.of(
                "admissionsentity@admissiontype@admissiontypestring",
                "admissionsentity@insurance@insurancestring",
                "admissionsentity@admissiontype@admissiontypestring@@admissionsentity@insurance@insurancestring",
                "patientsentity@gender@genderstring",
                "patientsentity@dod@2022-04-18t08:00",
                "patientsentity@gender@genderstring@@patientsentity@dod@2022-04-18t08:00",
                "composite@ageatadmission@56"
        );

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addEntry("AdmissionsEntity", List.of("admissionType", "insurance", "patientsEntity"));
        propertySpec.addEntry("PatientsEntity", List.of("gender", "dod"));

        ValueTransformer valueTransformer = new ValueTransformer();
        CompositeColumnCreator compositeColumnCreator = new CompositeColumnCreator();
        compositeColumnCreator.addEntry(
                List.of("AdmissionsEntity"),
                "admitTime",
                List.of("AdmissionsEntity", "PatientsEntity"),
                "dob",
                "ageAtAdmission",
                (dateAdmission, dateBirth) -> ChronoUnit.YEARS.between((LocalDateTime) dateBirth, (LocalDateTime) dateAdmission)
        );

        List<String> res = wordification.wordify(
                rootAdmissionsEntity,
                propertySpec,
                valueTransformer,
                compositeColumnCreator,
                Wordification.ConcatenationScheme.TWO,
                EntityUtils.getTransitionPairsFromForeignKeyPath(EntityUtils.getForeignKeyPathsFromPropertySpec("AdmissionsEntity", propertySpec, em.getMetamodel())),
                null,
                false
        );

        Assertions.assertEquals(expectedWords, res);
    }

    @Test
    void basicWithCompositeColumnCreatorAndValueTransformerDateDiff()
    {
        List<String> expectedWords = List.of(
                "admissionsentity@admissiontype@admissiontypestring",
                "admissionsentity@insurance@insurancestring",
                "admissionsentity@admissiontype@admissiontypestring@@admissionsentity@insurance@insurancestring",
                "patientsentity@gender@genderstring",
                "patientsentity@dod@2022-04-18t08:00",
                "patientsentity@gender@genderstring@@patientsentity@dod@2022-04-18t08:00",
                "composite@ageatadmission@60"
        );

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addEntry("AdmissionsEntity", List.of("admissionType", "insurance", "patientsEntity"));
        propertySpec.addEntry("PatientsEntity", List.of("gender", "dod"));

        ValueTransformer valueTransformer = new ValueTransformer();
        valueTransformer.addTransform(
                "composite",
                "ageAtAdmission",
                new ValueTransformer.Transform(x -> String.valueOf((int) 20.0 * Math.round(Double.parseDouble(((String) x).split(" ")[0]) / 20.0)))
        );

        CompositeColumnCreator compositeColumnCreator = new CompositeColumnCreator();
        compositeColumnCreator.addEntry(
                List.of("AdmissionsEntity", "PatientsEntity"),
                "dob",
                List.of("AdmissionsEntity"),
                "admitTime",
                "ageAtAdmission",
                DtoConverter.CombinerEnum.DATE_DIFF.getBinaryOperator()
        );

        List<String> res = wordification.wordify(rootAdmissionsEntity,
                propertySpec,
                valueTransformer,
                compositeColumnCreator,
                Wordification.ConcatenationScheme.TWO,
                EntityUtils.getTransitionPairsFromForeignKeyPath(EntityUtils.getForeignKeyPathsFromPropertySpec("AdmissionsEntity", propertySpec, em.getMetamodel())),
                null,
                false
        );

        Assertions.assertEquals(expectedWords, res);
    }

    @Test
    void basicWithCompositeColumnCreatorAndValueTransformerRounding()
    {
        IcuStaysEntity icuStaysEntity = new IcuStaysEntity();
        icuStaysEntity.setFirstCareUnit("firstCareUnitString");
        icuStaysEntity.setLastCareUnit("lastCareUnitString");
        icuStaysEntity.setLos(4.25);
        rootAdmissionsEntity.setIcuStaysEntitys(new HashSet<>(Set.of(icuStaysEntity)));

        List<String> expectedWords = List.of(
                "admissionsentity@admissiontype@admissiontypestring",
                "admissionsentity@insurance@insurancestring",
                "admissionsentity@admissiontype@admissiontypestring@@admissionsentity@insurance@insurancestring",
                "icustaysentity@firstcareunit@firstcareunitstring",
                "icustaysentity@lastcareunit@lastcareunitstring",
                "icustaysentity@los@4.0",
                "icustaysentity@firstcareunit@firstcareunitstring@@icustaysentity@lastcareunit@lastcareunitstring",
                "icustaysentity@firstcareunit@firstcareunitstring@@icustaysentity@los@4.0",
                "icustaysentity@lastcareunit@lastcareunitstring@@icustaysentity@los@4.0",
                "icustaysentity@firstcareunit@firstcareunitstring@@icustaysentity@lastcareunit@lastcareunitstring@@icustaysentity@los@4.0",
                "patientsentity@gender@genderstring",
                "patientsentity@dod@2022-04-18t08:00",
                "patientsentity@gender@genderstring@@patientsentity@dod@2022-04-18t08:00",
                "composite@ageatadmission@60"
        );

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addEntry("AdmissionsEntity", List.of("admissionType", "insurance", "patientsEntity", "icuStaysEntitys"));
        propertySpec.addEntry("PatientsEntity", List.of("gender", "dod"));
        propertySpec.addEntry("IcuStaysEntity", List.of("firstCareUnit", "lastCareUnit", "los"));

        ValueTransformer valueTransformer = new ValueTransformer();
        valueTransformer.addTransform(
                "composite",
                "ageAtAdmission",
                new ValueTransformer.Transform(x -> String.valueOf((int) 20.0 * Math.round(Double.parseDouble(((String) x).split(" ")[0]) / 20.0)))
        );
        valueTransformer.addTransform(
                "IcuStaysEntity",
                "los",
                new ValueTransformer.Transform(x -> 1.0 * Math.round(((double) x) / 1.0))
        );

        CompositeColumnCreator compositeColumnCreator = new CompositeColumnCreator();
        compositeColumnCreator.addEntry(
                List.of("AdmissionsEntity", "PatientsEntity"),
                "dob",
                List.of("AdmissionsEntity"),
                "admitTime",
                "ageAtAdmission",
                DtoConverter.CombinerEnum.DATE_DIFF.getBinaryOperator()
        );

        List<String> res = wordification.wordify(rootAdmissionsEntity,
                propertySpec,
                valueTransformer,
                compositeColumnCreator,
                Wordification.ConcatenationScheme.TWO,
                EntityUtils.getTransitionPairsFromForeignKeyPath(EntityUtils.getForeignKeyPathsFromPropertySpec("AdmissionsEntity", propertySpec, em.getMetamodel())),
                null,
                false
        );

        Assertions.assertEquals(expectedWords, res);
    }

    @Test
    void testWordificationNullValuesIgnoredOrNotIgnored()
    {
        PatientsEntity patientsEntity = new PatientsEntity();
        patientsEntity.setGender("M");

        IcuStaysEntity icuStaysEntity = new IcuStaysEntity();
        icuStaysEntity.setFirstCareUnit(null);
        icuStaysEntity.setLastCareUnit("lastCareUnit");
        icuStaysEntity.setPatientsEntity(patientsEntity);
        patientsEntity.setIcuStaysEntitys(Set.of(icuStaysEntity));

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addEntry("PatientsEntity", List.of("gender", "icuStaysEntitys"));
        propertySpec.addEntry("IcuStaysEntity", List.of("firstCareUnit", "lastCareUnit"));

        List<String> expectedWordsNullValuesNotIgnored = List.of(
                "patientsentity@gender@m",
                "icustaysentity@firstcareunit@null",
                "icustaysentity@lastcareunit@lastcareunit"
        );

        List<String> expectedWordsNullValuesIgnored = List.of(
                "patientsentity@gender@m",
                "icustaysentity@lastcareunit@lastcareunit"
        );

        List<String> resNullValuesNotIgnored = wordification.wordify(
                patientsEntity,
                propertySpec,
                new ValueTransformer(),
                new CompositeColumnCreator(),
                Wordification.ConcatenationScheme.ZERO,
                EntityUtils.getTransitionPairsFromForeignKeyPath(EntityUtils.getForeignKeyPathsFromPropertySpec("PatientsEntity", propertySpec, em.getMetamodel())),
                null,
                false
        );

        Assertions.assertEquals(expectedWordsNullValuesNotIgnored, resNullValuesNotIgnored);

        List<String> resNullValuesIgnored = wordification.wordify(
                patientsEntity,
                propertySpec,
                new ValueTransformer(),
                new CompositeColumnCreator(),
                Wordification.ConcatenationScheme.ZERO,
                EntityUtils.getTransitionPairsFromForeignKeyPath(EntityUtils.getForeignKeyPathsFromPropertySpec("PatientsEntity", propertySpec, em.getMetamodel())),
                null,
                true
        );

        Assertions.assertEquals(expectedWordsNullValuesIgnored, resNullValuesIgnored);
    }
}
