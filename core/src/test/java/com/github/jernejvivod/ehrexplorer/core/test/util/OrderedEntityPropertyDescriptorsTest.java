package com.github.jernejvivod.ehrexplorer.core.test.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.jernejvivod.ehrexplorer.annotation.PropertyOrder;
import com.github.jernejvivod.ehrexplorer.core.processing.util.OrderedEntityPropertyDescriptors;
import com.github.jernejvivod.ehrexplorer.core.test.ACoreTest;
import com.github.jernejvivod.ehrexplorer.mimiciii.entity.AEntity;
import com.github.jernejvivod.ehrexplorer.mimiciii.entity.AdmissionsEntity;

public class OrderedEntityPropertyDescriptorsTest extends ACoreTest
{
    @Inject
    private OrderedEntityPropertyDescriptors orderedEntityPropertyDescriptors;

    @Test
    void testForAdmissionsEntity()
    {
        PropertyDescriptor[] propertyDescriptors = orderedEntityPropertyDescriptors.getForEntity(AdmissionsEntity.class);
        Field[] fields = ArrayUtils.addAll(AEntity.class.getDeclaredFields(), AdmissionsEntity.class.getDeclaredFields());

        for (Field field : fields)
        {
            int propertyPosition = field.getAnnotation(PropertyOrder.class).value();
            Assertions.assertEquals(propertyPosition, indexOfPropertyWithNameInArrayOfPropertyDescriptors(propertyDescriptors, field.getName()) + 1);
        }
    }

    private int indexOfPropertyWithNameInArrayOfPropertyDescriptors(PropertyDescriptor[] propertyDescriptors, String propertyName)
    {
        for (int i = 0; i < propertyDescriptors.length; i++)
        {
            if (propertyDescriptors[i].getName().equals(propertyName))
            {
                return i;
            }
        }
        return -1;
    }
}
