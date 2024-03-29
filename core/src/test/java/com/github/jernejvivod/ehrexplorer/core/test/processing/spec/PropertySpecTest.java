package com.github.jernejvivod.ehrexplorer.core.test.processing.spec;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.jernejvivod.ehrexplorer.common.exception.ValidationCoreException;
import com.github.jernejvivod.ehrexplorer.core.processing.spec.PropertySpec;
import com.github.jernejvivod.ehrexplorer.core.test.ACoreTest;
import com.github.jernejvivod.ehrexplorer.core.util.DtoConverter;

class PropertySpecTest extends ACoreTest
{
    @Test
    void testPropertySpec()
    {
        String ENTITY1 = "AdmissionsEntity";
        String ENTITY1_PROPERTY1 = "admissionType";
        String ENTITY1_PROPERTY2 = "insurance";
        String ENTITY2 = "PatientsEntity";
        String ENTITY2_PROPERTY1 = "gender";
        String ENTITY2_PROPERTY2 = "dod";

        PropertySpec propertySpec1 = new PropertySpec();
        propertySpec1.addEntry(ENTITY1, ENTITY1_PROPERTY1);
        propertySpec1.addEntry(ENTITY1, ENTITY1_PROPERTY2);
        propertySpec1.addEntry(ENTITY2, ENTITY2_PROPERTY1);
        propertySpec1.addEntry(ENTITY2, ENTITY2_PROPERTY2);

        Assertions.assertTrue(propertySpec1.containsEntry(ENTITY1, ENTITY1_PROPERTY1));
        Assertions.assertTrue(propertySpec1.containsEntry(ENTITY1, ENTITY1_PROPERTY2));
        Assertions.assertTrue(propertySpec1.containsEntry(ENTITY2, ENTITY2_PROPERTY1));
        Assertions.assertTrue(propertySpec1.containsEntry(ENTITY2, ENTITY2_PROPERTY2));

        PropertySpec propertySpec2 = new PropertySpec();
        propertySpec2.addEntry(ENTITY1, Set.of(ENTITY1_PROPERTY1, ENTITY1_PROPERTY2), null);
        propertySpec2.addEntry(ENTITY2, Set.of(ENTITY2_PROPERTY1, ENTITY2_PROPERTY2), null);

        Assertions.assertTrue(propertySpec2.containsEntry(ENTITY1, ENTITY1_PROPERTY1));
        Assertions.assertTrue(propertySpec2.containsEntry(ENTITY1, ENTITY1_PROPERTY2));
        Assertions.assertTrue(propertySpec2.containsEntry(ENTITY2, ENTITY2_PROPERTY1));
        Assertions.assertTrue(propertySpec2.containsEntry(ENTITY2, ENTITY2_PROPERTY2));

    }

    @Test
    void testPropertySpecValidationValid()
    {
        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addEntry("AdmissionsEntity", "patientsEntity");
        propertySpec.addEntry("AdmissionsEntity", "rowId");
        propertySpec.addEntry("PatientsEntity", "gender");
        propertySpec.addSort("PatientsEntity", "dod");
        propertySpec.addEntityAndPropertyForDurationLimit("MicrobiologyEventsEntity", "chartTime");

        Assertions.assertDoesNotThrow(() -> propertySpec.assertValid(em.getMetamodel()));
    }

    @Test
    void testPropertySpecValidationEmpty()
    {
        PropertySpec propertySpec = new PropertySpec();
        Assertions.assertDoesNotThrow(() -> propertySpec.assertValid(em.getMetamodel()));
    }

    @Test
    void testPropertySpecValidationInvalidSpecEntity()
    {
        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addEntry("AdmissionsEntity", "patientsEntity");
        propertySpec.addEntry("AdmissionsEntity", "rowId");
        propertySpec.addEntry("Wrong", "expireFlag");

        Assertions.assertThrows(ValidationCoreException.class, () -> propertySpec.assertValid(em.getMetamodel()));
    }

    @Test
    void testPropertySpecValidationInvalidSpecProperty()
    {
        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addEntry("AdmissionsEntity", "patientsEntity");
        propertySpec.addEntry("AdmissionsEntity", "rowId");
        propertySpec.addEntry("PatientsEntity", "gender");
        propertySpec.addEntry("PatientsEntity", "wrong");

        Assertions.assertThrows(ValidationCoreException.class, () -> propertySpec.assertValid(em.getMetamodel()));
    }

    @Test
    void testPropertySpecValidationInvalidSortProperty()
    {
        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addEntry("AdmissionsEntity", "patientsEntity");
        propertySpec.addEntry("AdmissionsEntity", "rowId");
        propertySpec.addEntry("PatientsEntity", "gender");
        propertySpec.addSort("PatientsEntity", "wrong");

        Assertions.assertThrows(ValidationCoreException.class, () -> propertySpec.assertValid(em.getMetamodel()));
    }

    @Test
    void testPropertySpecValidationInvalidDurationLimitProperty()
    {
        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addEntry("AdmissionsEntity", "patientsEntity");
        propertySpec.addEntry("AdmissionsEntity", "rowId");
        propertySpec.addEntry("PatientsEntity", "gender");
        propertySpec.addEntityAndPropertyForDurationLimit("MicrobiologyEventsEntity", "wrong");

        Assertions.assertThrows(ValidationCoreException.class, () -> propertySpec.assertValid(em.getMetamodel()));
    }

    @Test
    void testPropertySpecValidationInvalidCompositePropertySpecProperty()
    {
        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addEntry("PatientsEntity", List.of("gender", "icuStaysEntitys"), null);
        propertySpec.addEntry("IcuStaysEntity", List.of("dbSource"), List.of(
                        new PropertySpec.CompositePropertySpec(
                                "inTime",
                                "wrong",
                                List.of("IcuStaysEntity", "PatientsEntity"),
                                "ageAtAdmission",
                                DtoConverter.CombinerEnum.DATE_DIFF.getBinaryOperator()
                        )
                )
        );
        Assertions.assertThrows(ValidationCoreException.class, () -> propertySpec.assertValid(em.getMetamodel()));
    }
}
