package com.googlecode.simpleobjectassembler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.simpleobjectassembler.SimpleObjectAssembler;
import com.googlecode.simpleobjectassembler.converter.ConversionException;
import com.googlecode.simpleobjectassembler.converter.ConverterRegistryException;
import com.googlecode.simpleobjectassembler.converter.DestinationObject;
import com.googlecode.simpleobjectassembler.converter.DestinationObjectProvidingObjectConverter;
import com.googlecode.simpleobjectassembler.converter.NestedObjectConverter;
import com.googlecode.simpleobjectassembler.converter.NestedSourceObject;
import com.googlecode.simpleobjectassembler.converter.SourceObject;
import com.googlecode.simpleobjectassembler.converter.SourceToDestinationTestObjectConverter;
import com.googlecode.simpleobjectassembler.converter.SourceToDestinationWithDifferentFieldNameObjectConverter;

public class SimpleObjectAssemblerTest {

   private static final String OTHER_STRING = "otherString";

   private SimpleObjectAssembler objectAssembler;

   private final SourceToDestinationTestObjectConverter sourceToDestinationTestObjectConverter = new SourceToDestinationTestObjectConverter();;

   private NestedObjectConverter nestedObjectConverter;

   @Before
   public void setUp() {
      objectAssembler = new SimpleObjectAssembler();

      sourceToDestinationTestObjectConverter.setObjectAssembler(objectAssembler);
      sourceToDestinationTestObjectConverter.postConstruct();

      nestedObjectConverter = new NestedObjectConverter();
      nestedObjectConverter.setObjectAssembler(objectAssembler);
      nestedObjectConverter.postConstruct();

   }

   @Test
   public void shouldConvertAllPropertiesOfSameNameByDefaultWithListAndSet() {

      final SourceObject sourceObject = createFullyPopulatedSourceObject();
      final DestinationObject destinationObject = objectAssembler.assemble(sourceObject, DestinationObject.class);

      Assert.assertEquals(sourceObject.getString(), destinationObject.getString());
      Assert.assertEquals(sourceObject.getNestedObject().getString(), destinationObject.getNestedObject().getString());
      Assert.assertEquals(sourceObject.getNestedObjectList().get(0).getString(), destinationObject
            .getNestedObjectList().get(0).getString());
      Assert.assertEquals(sourceObject.getNestedObjectList().get(1).getString(), destinationObject
            .getNestedObjectList().get(1).getString());
      Assert.assertEquals(sourceObject.getNestedObjectSet().iterator().next().getString(), destinationObject
            .getNestedObjectSet().iterator().next().getString());
      
      Assert.assertNull(destinationObject.getDifferentNameDestination());
      Assert.assertNull(destinationObject.getNestedObjectDifferentNameDestination());
      Assert.assertNull(destinationObject.getNestedObjectListDifferentNameDestination());
      

   }

   @Test
   public void shouldIgnoreBasicPropertiesWhenDefined() {
      final SourceObject sourceObject = createFullyPopulatedSourceObject();
      final DestinationObject destinationObject = objectAssembler.assemble(sourceObject, DestinationObject.class,
            "string");

      Assert.assertNull(destinationObject.getString());
      Assert.assertEquals(2, destinationObject.getNestedObjectList().size());
   }
   
   @Test
   public void shouldIgnorePropertiesWhenWildcardProvided() {
      final SourceObject sourceObject = createFullyPopulatedSourceObject();
      final DestinationObject destinationObject = objectAssembler.assemble(sourceObject, DestinationObject.class,
            "nestedObject.*");

      Assert.assertNull(destinationObject.getNestedObject().getString());
      Assert.assertNull(destinationObject.getNestedObject().getOtherString());
   }

   @Test
   public void shouldIgnoreNestedCollectionPropertiesWhenDefined() {

      final SourceObject sourceObject = createFullyPopulatedSourceObject();
      final DestinationObject destinationObject = objectAssembler.assemble(sourceObject, DestinationObject.class,
            "nestedObjectList.string");

      Assert.assertNull(destinationObject.getNestedObjectList().get(0).getString());
      Assert.assertNull(destinationObject.getNestedObjectList().get(1).getString());

   }

   @Test
   public void shouldNotAllowMoreThanOneConverterForGivenSourceAndDestinationClass() {

      try {
         final SourceToDestinationTestObjectConverter duplicateObjectConverter = new SourceToDestinationTestObjectConverter();
         duplicateObjectConverter.setObjectAssembler(objectAssembler);
         duplicateObjectConverter.postConstruct();
         Assert.fail("Expected exception to be thrown due to duplicate converter registration");
      }
      catch (ConverterRegistryException e) {
         ;// expected exception
      }
   }

   @Test
   public void shouldHandleNullValuesForNestedObjects() {

      final SourceObject sourceObject = createFullyPopulatedSourceObject();
      sourceObject.setNestedObject(null);

      final DestinationObject destinationObject = objectAssembler.assemble(sourceObject, DestinationObject.class,
            "nestedObjectList.string");

      Assert.assertNull(destinationObject.getNestedObject());
   }

   @Test
   public void shouldMapValuesInNestedOrderedCollectionOverExistingValuesIfPresent() {

      // reset with different assembler and converters - mainly the
      // destinationObject providing one
      objectAssembler = new SimpleObjectAssembler();

      final DestinationObjectProvidingObjectConverter testObjectConverter = new DestinationObjectProvidingObjectConverter();
      testObjectConverter.setObjectAssembler(objectAssembler);
      testObjectConverter.postConstruct();

      nestedObjectConverter = new NestedObjectConverter();
      nestedObjectConverter.setObjectAssembler(objectAssembler);
      nestedObjectConverter.postConstruct();

      final SourceObject sourceObject = createFullyPopulatedSourceObject();
      final DestinationObject destinationObject = objectAssembler.assemble(sourceObject, DestinationObject.class,
            "nestedObjectList.string");

      Assert.assertEquals("a", destinationObject.getNestedObjectList().get(0).getString());
      Assert.assertEquals("a", destinationObject.getNestedObjectList().get(1).getString());

      Assert.assertEquals(OTHER_STRING, destinationObject.getNestedObjectList().get(0).getOtherString());
      Assert.assertEquals(OTHER_STRING, destinationObject.getNestedObjectList().get(1).getOtherString());

   }

   @Test
   public void shouldNotMapValuesInNestedNonOrderedCollectionOverExistingValuesIfPresent() {

      // reset with different assembler and converters - mainly the
      // destinationObject providing one
      objectAssembler = new SimpleObjectAssembler();

      final DestinationObjectProvidingObjectConverter testObjectConverter = new DestinationObjectProvidingObjectConverter();
      testObjectConverter.setObjectAssembler(objectAssembler);
      testObjectConverter.postConstruct();

      nestedObjectConverter = new NestedObjectConverter();
      nestedObjectConverter.setObjectAssembler(objectAssembler);
      nestedObjectConverter.postConstruct();

      final SourceObject sourceObject = createFullyPopulatedSourceObject();
      final DestinationObject destinationObject = objectAssembler.assemble(sourceObject, DestinationObject.class,
            "nestedObjectSet.string");

      Assert.assertNull(destinationObject.getNestedObjectSet().iterator().next().getString());

      Assert.assertEquals(OTHER_STRING, destinationObject.getNestedObjectList().get(0).getOtherString());

   }

   @Test
   public void shouldThrowConversionExceptionIfNoConverterRegisteredForSourceAndDestinationTypes() {

      try {
         objectAssembler.assemble("string", Object.class);
         Assert.fail("Expected an Exception to have been thrown due to no converter being registered");
      }
      catch (ConversionException e) {
         ;// Expected Exception.
      }
   }

   @Test
   public void shouldThrowConversionExceptionIfInvalidIgnorePropertiesAreDefined() {

      try {
         final SourceObject sourceObject = createFullyPopulatedSourceObject();
         objectAssembler.assemble(sourceObject, DestinationObject.class, "invalidProperty");
         Assert.fail("Expected an Exception to have been thrown due to invalid ignore properties");
      }
      catch (ConversionException e) {
         ;// Expected Exception.
      }
   }
   
   
   @Test
   public void shouldConvertAllPropertiesIncludingSpecifiedFieldsWithDifferentNames() {

      
      // reset with different assembler and converters
      objectAssembler = new SimpleObjectAssembler();

      final SourceToDestinationWithDifferentFieldNameObjectConverter testObjectConverter = new SourceToDestinationWithDifferentFieldNameObjectConverter();
      testObjectConverter.setObjectAssembler(objectAssembler);
      testObjectConverter.postConstruct();

      nestedObjectConverter = new NestedObjectConverter();
      nestedObjectConverter.setObjectAssembler(objectAssembler);
      nestedObjectConverter.postConstruct();
      
      final SourceObject sourceObject = createFullyPopulatedSourceObject();
      final DestinationObject destinationObject = objectAssembler.assemble(sourceObject, DestinationObject.class);

      Assert.assertEquals(sourceObject.getString(), destinationObject.getString());
      Assert.assertEquals(sourceObject.getNestedObject().getString(), destinationObject.getNestedObject().getString());
      Assert.assertEquals(sourceObject.getNestedObjectList().get(0).getString(), destinationObject
            .getNestedObjectList().get(0).getString());
      Assert.assertEquals(sourceObject.getNestedObjectList().get(1).getString(), destinationObject
            .getNestedObjectList().get(1).getString());
      Assert.assertEquals(sourceObject.getNestedObjectSet().iterator().next().getString(), destinationObject
            .getNestedObjectSet().iterator().next().getString());
      
      Assert.assertEquals(sourceObject.getDifferentNameSource(), destinationObject.getDifferentNameDestination());
      Assert.assertEquals(sourceObject.getNestedObjectDifferentNameSource().getString(), destinationObject.getNestedObjectDifferentNameDestination().getString());
      Assert.assertEquals(sourceObject.getNestedObjectListDifferentNameSource().get(0).getString(), destinationObject.getNestedObjectListDifferentNameDestination().get(0).getString());

   }
   
   @Test
   public void shouldHandleCircularRelationships() {

      final SourceObject sourceObject = createFullyPopulatedSourceObject();
      for(NestedSourceObject nested : sourceObject.getNestedObjectList()) {
         nested.setParent(sourceObject);
      }
      
      final DestinationObject destinationObject = objectAssembler.assemble(sourceObject, DestinationObject.class);
     
      Assert.assertEquals(sourceObject.getString(), destinationObject.getNestedObjectList().get(0).getParent().getString());
      
   }

   private SourceObject createFullyPopulatedSourceObject() {
      final SourceObject sourceObject = new SourceObject();
      sourceObject.setString("string");
      sourceObject.setDifferentNameSource("differentName");
      sourceObject.setNestedObject(createFullyPopulatedNestedSourceObject("string1"));
      sourceObject.setNestedObjectDifferentNameSource(createFullyPopulatedNestedSourceObject("nestedObjectDifferentName"));
      final List<NestedSourceObject> list = new ArrayList<NestedSourceObject>();
      list.add(createFullyPopulatedNestedSourceObject("string2"));
      list.add(createFullyPopulatedNestedSourceObject("string3"));
      sourceObject.setNestedObjectList(list);
      sourceObject.setNestedObjectListDifferentNameSource(list);
      sourceObject.setNestedObjectSet(Collections.singleton(createFullyPopulatedNestedSourceObject("string4")));
      return sourceObject;
   }

   private NestedSourceObject createFullyPopulatedNestedSourceObject(String string) {
      final NestedSourceObject nestedSourceObject = new NestedSourceObject(string);
      nestedSourceObject.setOtherString(OTHER_STRING);
      return nestedSourceObject;
   }

}
