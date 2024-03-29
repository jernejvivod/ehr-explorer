package com.github.jernejvivod.ehrexplorer.core.test.util;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.jernejvivod.ehrexplorer.core.processing.Wordification;
import com.github.jernejvivod.ehrexplorer.core.processing.spec.PropertySpec;
import com.github.jernejvivod.ehrexplorer.core.processing.transform.CompositeColumnCreator;
import com.github.jernejvivod.ehrexplorer.core.processing.transform.ValueTransformer;
import com.github.jernejvivod.ehrexplorer.core.processing.util.WordificationUtil;
import com.github.jernejvivod.ehrexplorer.mimiciii.entity.PatientsEntity;

class WordificationUtilTest
{
    @Test
    void testAddConcatenationsZero()
    {
        List<String> words = List.of("a", "b", "c");
        List<String> res = WordificationUtil.addConcatenations(words, Wordification.ConcatenationScheme.ZERO);
        Assertions.assertEquals(words, res);
    }

    @Test
    void testAddConcatenationsOne()
    {
        List<String> words = List.of("a", "b", "c");
        List<String> res = WordificationUtil.addConcatenations(words, Wordification.ConcatenationScheme.ONE);
        Assertions.assertEquals(List.of("a", "b", "c", "a@@b", "a@@c", "b@@c"), res);
    }

    public static class TestClass
    {
        private Integer a;

        public TestClass(Integer a)
        {
            this.a = a;
        }

        public Integer getA()
        {
            return a;
        }
    }

    public static class TestClassWithDateTimeProperty
    {
        private LocalDateTime a;

        public TestClassWithDateTimeProperty(LocalDateTime a)
        {
            this.a = a;
        }

        public LocalDateTime getA()
        {
            return a;
        }
    }

    @Test
    void testAddConcatenationsTwo()
    {
        List<String> words = List.of("a", "b", "c", "d");
        List<String> res = WordificationUtil.addConcatenations(words, Wordification.ConcatenationScheme.TWO);
        Assertions.assertEquals(List.of("a", "b", "c", "d", "a@@b", "a@@c", "a@@d", "b@@c", "b@@d", "c@@d", "a@@b@@c", "a@@b@@d", "a@@c@@d", "b@@c@@d"), res);
    }

    @Test
    void testAddLinkedCollectionToStack()
    {
        LinkedList<Object> dfsStack = new LinkedList<>();
        PropertySpec propertySpec = new PropertySpec();
        Class<?> linkedEntityClass = PatientsEntity.class;

        Collection<?> collection = Set.of(
                new TestClass(4),
                new TestClass(3),
                new TestClass(1),
                new TestClass(2)
        );

        WordificationUtil.pushLinkedCollectionToStack(dfsStack, propertySpec, collection, linkedEntityClass, null);

        Assertions.assertFalse(dfsStack.isEmpty());
        dfsStack.forEach(e -> Assertions.assertTrue(collection.contains(e)));
    }

    @Test
    void testAddLinkedCollectionToStackWithSorting()
    {
        LinkedList<Object> dfsStack = new LinkedList<>();

        Class<?> linkedEntityClass = TestClass.class;

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addSort("TestClass", "a");

        Collection<?> collection = Set.of(
                new TestClass(4),
                new TestClass(3),
                new TestClass(1),
                new TestClass(6),
                new TestClass(7),
                new TestClass(2),
                new TestClass(5)
        );

        WordificationUtil.pushLinkedCollectionToStack(dfsStack, propertySpec, collection, linkedEntityClass, null);

        Assertions.assertFalse(dfsStack.isEmpty());
        Assertions.assertEquals(List.of(1, 2, 3, 4, 5, 6, 7), dfsStack.stream().map(e -> ((TestClass) e).getA()).toList());
    }

    @Test
    void testApplyDurationLimitIfSpecifiedNotSpecified()
    {
        Random random = new Random();
        List<?> linkedEntitiesList = List.of(
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(random.nextInt())),
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(random.nextInt())),
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(random.nextInt())),
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(random.nextInt())),
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(random.nextInt())),
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(random.nextInt())),
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(random.nextInt()))
        );

        List<?> res = WordificationUtil.applyDurationLimitIfSpecified(linkedEntitiesList, TestClass.class, new PropertySpec(), null);
        Assertions.assertEquals(linkedEntitiesList, res);
    }

    @Test
    void testApplyDurationLimitIfSpecified()
    {
        List<TestClassWithDateTimeProperty> linkedEntitiesList = List.of(
                new TestClassWithDateTimeProperty(LocalDateTime.now()),
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(1)),
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(2)),
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(3)),
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(4)),
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(5)),
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(6))
        );

        PropertySpec propertySpec1 = new PropertySpec();
        propertySpec1.addEntityAndPropertyForDurationLimit("TestClassWithDateTimeProperty", "a");

        PropertySpec propertySpec2 = new PropertySpec();
        propertySpec2.addEntityAndPropertyForDurationLimit("TestClassWithDateTimeProperty", "a");

        PropertySpec propertySpec3 = new PropertySpec();
        propertySpec3.addEntityAndPropertyForDurationLimit("TestClassWithDateTimeProperty", "a");

        List<?> res1 = WordificationUtil.applyDurationLimitIfSpecified(linkedEntitiesList, TestClassWithDateTimeProperty.class, propertySpec1, linkedEntitiesList.get(1).getA().plusSeconds(1));
        List<?> res2 = WordificationUtil.applyDurationLimitIfSpecified(linkedEntitiesList, TestClassWithDateTimeProperty.class, propertySpec2, linkedEntitiesList.get(3).getA().plusSeconds(1));
        List<?> res3 = WordificationUtil.applyDurationLimitIfSpecified(linkedEntitiesList, TestClassWithDateTimeProperty.class, propertySpec3, linkedEntitiesList.get(5).getA().plusSeconds(1));
        Assertions.assertEquals(linkedEntitiesList.subList(0, 2), res1);
        Assertions.assertEquals(linkedEntitiesList.subList(0, 4), res2);
        Assertions.assertEquals(linkedEntitiesList.subList(0, 6), res3);
    }

    @Test
    void testAddLinkedCollectionToStackWithSortingAndDurationLimit()
    {
        List<TestClassWithDateTimeProperty> linkedEntitiesList = List.of(
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(3)),
                new TestClassWithDateTimeProperty(LocalDateTime.now()),
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(5)),
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(6)),
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(1)),
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(2)),
                new TestClassWithDateTimeProperty(LocalDateTime.now().plusHours(4))
        );

        PropertySpec propertySpec1 = new PropertySpec();
        propertySpec1.addSort("TestClassWithDateTimeProperty", "a");
        propertySpec1.addEntityAndPropertyForDurationLimit("TestClassWithDateTimeProperty", "a");

        PropertySpec propertySpec2 = new PropertySpec();
        propertySpec2.addSort("TestClassWithDateTimeProperty", "a");
        propertySpec2.addEntityAndPropertyForDurationLimit("TestClassWithDateTimeProperty", "a");

        PropertySpec propertySpec3 = new PropertySpec();
        propertySpec3.addSort("TestClassWithDateTimeProperty", "a");
        propertySpec3.addEntityAndPropertyForDurationLimit("TestClassWithDateTimeProperty", "a");

        LinkedList<Object> dfsStack1 = new LinkedList<>();
        WordificationUtil.pushLinkedCollectionToStack(dfsStack1, propertySpec1, linkedEntitiesList, TestClassWithDateTimeProperty.class, linkedEntitiesList.get(4).getA().plusSeconds(1));

        LinkedList<Object> dfsStack2 = new LinkedList<>();
        WordificationUtil.pushLinkedCollectionToStack(dfsStack2, propertySpec2, linkedEntitiesList, TestClassWithDateTimeProperty.class, linkedEntitiesList.get(0).getA().plusSeconds(1));

        LinkedList<Object> dfsStack3 = new LinkedList<>();
        WordificationUtil.pushLinkedCollectionToStack(dfsStack3, propertySpec3, linkedEntitiesList, TestClassWithDateTimeProperty.class, linkedEntitiesList.get(2).getA().plusSeconds(1));

        Assertions.assertEquals(2, dfsStack1.size());
        Assertions.assertEquals(4, dfsStack2.size());
        Assertions.assertEquals(6, dfsStack3.size());
    }

    public static class A
    {
        private int a;
        private B b;
        private C c;

        public int getA()
        {
            return a;
        }

        public void setA(int a)
        {
            this.a = a;
        }

        public B getB()
        {
            return b;
        }

        public void setB(B b)
        {
            this.b = b;
        }

        public C getC()
        {
            return c;
        }

        public void setC(C c)
        {
            this.c = c;
        }
    }

    public static class B
    {
        private int b;

        public B(int b)
        {
            this.b = b;
        }

        public int getB()
        {
            return b;
        }
    }

    public static class C
    {
        private int c;

        public C(int c)
        {
            this.c = c;
        }

        public int getC()
        {
            return c;
        }
    }

    @Test
    void testGetWordsForCompositeColumns()
    {
        A a = new A();
        a.setB(new B(1));
        a.setC(new C(2));

        CompositeColumnCreator compositeColumnCreator = new CompositeColumnCreator();
        compositeColumnCreator.addEntry(
                List.of("A", "B"),
                "b",
                List.of("A", "C"),
                "c",
                "composite_column_name",
                (o1, o2) -> ((int) o1) - ((int) o2)
        );

        List<String> res1 = WordificationUtil.getWordsForCompositeColumns(compositeColumnCreator, new ValueTransformer(), a);
        Assertions.assertEquals(List.of("composite@composite_column_name@-1"), res1);

        ValueTransformer valueTransformer = new ValueTransformer();
        valueTransformer.addTransform("composite", "composite_column_name", new ValueTransformer.Transform(o -> ((int) o) + 8));
        List<String> res2 = WordificationUtil.getWordsForCompositeColumns(compositeColumnCreator, valueTransformer, a);
        Assertions.assertEquals(List.of("composite@composite_column_name@7"), res2);
    }

    @Test
    void testGetCompositePropertiesForEntity()
    {
        A a = new A();
        a.setA(3);
        a.setB(new B(4));

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.addEntry("A", List.of("a"), Set.of(
                new PropertySpec.CompositePropertySpec(
                        "a",
                        "b",
                        List.of("A", "B"),
                        "compositeProperty",
                        (o1, o2) -> ((int) o1) + ((int) o2)
                )
        ));

        List<String> res1 = WordificationUtil.getCompositePropertiesForEntity(
                a,
                "A",
                propertySpec,
                new ValueTransformer()
        );

        Assertions.assertEquals(List.of("a@compositeproperty@7"), res1);

        ValueTransformer valueTransformer = new ValueTransformer();
        valueTransformer.addTransform("A", "compositeProperty", new ValueTransformer.Transform(o -> ((int) o) + 8));
        List<String> res2 = WordificationUtil.getCompositePropertiesForEntity(
                a,
                "A",
                propertySpec,
                valueTransformer
        );

        Assertions.assertEquals(List.of("a@compositeproperty@15"), res2);
    }
}
