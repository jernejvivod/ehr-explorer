package si.jernej.mexplorer.core.test.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import si.jernej.mexplorer.core.processing.util.OrderedEntityPropertyDescriptors;
import si.jernej.mexplorer.core.test.ACoreTest;
import si.jernej.mexplorer.entity.AEntity;
import si.jernej.mexplorer.entity.AdmissionsEntity;
import si.jernej.mexplorer.entity.annotation.PropertyOrder;

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
