package com.github.jernejvivod.ehrexplorer.core.test.util;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.jernejvivod.ehrexplorer.core.processing.spec.PropertySpec;
import com.github.jernejvivod.ehrexplorer.core.util.DtoConverter;
import com.github.jernejvivod.ehrexplorer.processorapi.v1.model.CompositePropertySpecEntryDto;
import com.github.jernejvivod.ehrexplorer.processorapi.v1.model.PropertySpecDto;
import com.github.jernejvivod.ehrexplorer.processorapi.v1.model.PropertySpecEntryDto;

class DtoConverterTest
{
    @Test
    void testToPropertySpecEmpty()
    {
        PropertySpecDto propertySpecDto = new PropertySpecDto();
        PropertySpec propertySpec = DtoConverter.toPropertySpec(propertySpecDto);
        Assertions.assertFalse(propertySpec.containsEntry("AdmissionsEntity", "admissionType"));
    }

    @Test
    void testToPropertySpec()
    {
        final String ENTITY1 = "AdmissionsEntity";
        final String ENTITY2 = "PatientsEntity";
        final List<String> PROPERTIES_LIST1 = List.of("admissionType", "insurance");
        final List<String> PROPERTIES_LIST2 = List.of("gender", "dod");

        PropertySpecDto propertySpecDto = new PropertySpecDto();

        PropertySpecEntryDto propertySpecEntryDto1 = new PropertySpecEntryDto();
        propertySpecEntryDto1.setEntity(ENTITY1);
        propertySpecEntryDto1.setProperties(PROPERTIES_LIST1);
        propertySpecEntryDto1.setCompositePropertySpecEntries(
                List.of(
                        new CompositePropertySpecEntryDto()
                                .propertyOnThisEntity("dateAtAdmission")
                                .propertyOnOtherEntity("dob")
                                .foreignKeyPath(List.of("AdmissionsEntity", "PatientsEntity"))
                                .compositePropertyName("ageAtAdmission")
                                .combiner(CompositePropertySpecEntryDto.CombinerEnum.DATE_DIFF)
                )
        );

        PropertySpecEntryDto propertySpecEntryDto2 = new PropertySpecEntryDto();
        propertySpecEntryDto2.setEntity(ENTITY2);
        propertySpecEntryDto2.setProperties(PROPERTIES_LIST2);

        propertySpecDto.setEntries(List.of(propertySpecEntryDto1, propertySpecEntryDto2));

        PropertySpec propertySpec = DtoConverter.toPropertySpec(propertySpecDto);

        assertToPropertySpecConversion(propertySpecDto, propertySpec);
    }

    void assertToPropertySpecConversion(PropertySpecDto propertySpecDto, PropertySpec propertySpec)
    {
        for (PropertySpecEntryDto entry : propertySpecDto.getEntries())
        {
            Optional<Set<PropertySpec.CompositePropertySpec>> compositePropertySpecsForEntity = propertySpec.getCompositePropertySpecsForEntity(entry.getEntity());

            if (entry.getCompositePropertySpecEntries() != null)
            {
                Assertions.assertTrue(compositePropertySpecsForEntity.isPresent());

                Set<PropertySpec.CompositePropertySpec> compositePropertySpecs = compositePropertySpecsForEntity.get();
                Assertions.assertEquals(entry.getCompositePropertySpecEntries().size(), compositePropertySpecs.size());
            }
            else
            {
                Assertions.assertTrue(compositePropertySpecsForEntity.isEmpty());
            }

            for (String property : entry.getProperties())
            {
                Assertions.assertTrue(propertySpec.containsEntry(entry.getEntity(), property));
                Assertions.assertFalse(propertySpec.containsEntry("DOES_NOT_EXIST", property));
                Assertions.assertFalse(propertySpec.containsEntry(entry.getEntity(), "DOES_NOT_EXIST"));
            }
        }
        Assertions.assertFalse(propertySpec.containsEntry("DOES_NOT_EXIST", "DOES_NOT_EXIST"));
    }
}
