package com.googlecode.simpleobjectassembler;

import com.googlecode.simpleobjectassembler.converter.*;
import com.googlecode.simpleobjectassembler.converter.dao.EntityDao;
import com.googlecode.simpleobjectassembler.registry.ConverterRegistryException;
import com.googlecode.simpleobjectassembler.utils.GenericTypeResolver;
import junit.framework.Assert;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static com.googlecode.simpleobjectassembler.converter.mapping.MappingPaths.exclude;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/*
 * Not a serious performance test, just allows some basic tests of larger numbers of
 * objects / properties being converted.
 */
public class SimpleObjectAssemblerPerformanceTest {


   private SimpleObjectAssembler objectAssembler;

   @Before
   public void setUp() {
      objectAssembler = new SimpleObjectAssembler();
      objectAssembler.setAutomapWhenNoConverterFound(true);

   }

   @Test
   public void shouldConvertAllPropertiesOfSameNameByDefaultWithListAndSet() {

      final SourceObject sourceObject = createFullyPopulatedSourceObject();

      long startMillis = System.currentTimeMillis();
      objectAssembler.assemble(sourceObject, DestinationObject.class);
      long executionTimeInMillis = System.currentTimeMillis() - startMillis;

      System.out.println("Perf test execution time: " + executionTimeInMillis);

   }


   private SourceObject createFullyPopulatedSourceObject() {
      final SourceObject sourceObject = new SourceObject();
      sourceObject.setString("string");
      sourceObject.setDifferentNameSource("differentName");
      sourceObject.setNestedObject(createFullyPopulatedNestedSourceObject("string1"));
      sourceObject
            .setNestedObjectDifferentNameSource(createFullyPopulatedNestedSourceObject("nestedObjectDifferentName"));

      final List<NestedSourceObject> list = new ArrayList<NestedSourceObject>();
      for (int i = 0; i < 1000; i++) {

         NestedSourceObject nested = createFullyPopulatedNestedSourceObject("string2");

         for (int j = 0; j < 100; j++) {
            nested.addNested(createFullyPopulatedNestedSourceObject("string3"));
         }

         list.add(nested);
      }
      sourceObject.setNestedObjectList(list);

      final Set<NestedSourceObject> set = new LinkedHashSet<NestedSourceObject>();
      for (int i = 0; i < 1000; i++) {

         NestedSourceObject nested = createFullyPopulatedNestedSourceObject("string2");

         for (int j = 0; j < 100; j++) {
            nested.addNested(createFullyPopulatedNestedSourceObject("string3"));
         }

         set.add(nested);
      }

      sourceObject.setNestedObjectCollection(set);
      sourceObject.setNestedObjectListDifferentNameSource(list);
      sourceObject.setNestedObjectSet(Collections.singleton(createFullyPopulatedNestedSourceObject("string4")));
      return sourceObject;
   }

   private NestedSourceObject createFullyPopulatedNestedSourceObject(String string) {
      final NestedSourceObject nestedSourceObject = new NestedSourceObject(string);
      nestedSourceObject.setOtherString("...");
      return nestedSourceObject;
   }


}