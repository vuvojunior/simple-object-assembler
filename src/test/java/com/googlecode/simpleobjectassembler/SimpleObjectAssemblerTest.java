package com.googlecode.simpleobjectassembler;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.*;

import com.googlecode.simpleobjectassembler.converter.*;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import static com.googlecode.simpleobjectassembler.converter.mapping.MappingPaths.*;
import com.googlecode.simpleobjectassembler.converter.dao.EntityDao;
import com.googlecode.simpleobjectassembler.registry.ConverterRegistryException;
import com.googlecode.simpleobjectassembler.utils.GenericTypeResolver;

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
      Assert.assertTrue(destinationObject.getBooleanVal());

   }

   @Test
   public void shouldAutomaticallyConvertCollectionElementsToAnAnonymousClassInstanceOfDestinationCollection() {
      final List<SourceObject> sourceList = Arrays.asList(new SourceObject("stringz"), new SourceObject("stringx"));

      final List<DestinationObject> destinationList = objectAssembler.assemble(sourceList, new ArrayList<DestinationObject>() {});

      assertThat(destinationList.get(0).getString(), is("stringz"));
      assertThat(destinationList.get(1).getString(), is("stringx"));

   }

   @Test
   public void shouldThrowExceptionIfMappingBetweenCollectionsWithDestinationThatHasNoGenericTypeInformation() {
      final List<SourceObject> sourceList = Arrays.asList(new SourceObject("string"));

      try {
         objectAssembler.assemble(sourceList, new ArrayList<DestinationObject>());
         fail("should have thrown a conversion exception");
      } catch(ConversionException e) {
         //expected
      }

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
      final DestinationObject destinationObject = objectAssembler.assemble(sourceObject, DestinationObject.class, exclude("nestedObject.*"));

      Assert.assertNull(destinationObject.getNestedObject().getString());
      Assert.assertNull(destinationObject.getNestedObject().getOtherString());
   }

   @Test
   public void shouldIgnoreNestedCollectionPropertiesWhenDefined() {

      final SourceObject sourceObject = createFullyPopulatedSourceObject();
      final DestinationObject destinationObject = objectAssembler.assemble(sourceObject, DestinationObject.class,
            exclude("nestedObjectList.string"));

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
            exclude("nestedObjectList.string"));

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
            exclude("nestedObjectList.string"));

      Assert.assertEquals("a", destinationObject.getNestedObjectList().get(0).getString());
      Assert.assertEquals("a", destinationObject.getNestedObjectList().get(1).getString());

      Assert.assertEquals(OTHER_STRING, destinationObject.getNestedObjectList().get(0).getOtherString());
      Assert.assertEquals(OTHER_STRING, destinationObject.getNestedObjectList().get(1).getOtherString());

   }


   public void shouldMapValuesInNestedOrderedCollectionOfDifferentCollectionTypes() {

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
            exclude("nestedObjectCollection.string"));

      Assert.assertEquals("a", destinationObject.getNestedObjectCollection().get(0).getString());
      Assert.assertEquals("a", destinationObject.getNestedObjectCollection().get(1).getString());

      Assert.assertEquals(OTHER_STRING, destinationObject.getNestedObjectCollection().get(0).getOtherString());
      Assert.assertEquals(OTHER_STRING, destinationObject.getNestedObjectCollection().get(1).getOtherString());

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
            exclude("nestedObjectSet.string"));

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
         Assert.fail("Expected an Exception to have been thrown due to invalid exclude properties");
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
      Assert.assertEquals(sourceObject.getNestedObjectDifferentNameSource().getString(), destinationObject
            .getNestedObjectDifferentNameDestination().getString());
      Assert.assertEquals(sourceObject.getNestedObjectListDifferentNameSource().get(0).getString(), destinationObject
            .getNestedObjectListDifferentNameDestination().get(0).getString());

   }

   @Test
   public void shouldHandleCircularRelationships() {

      final SourceObject sourceObject = createFullyPopulatedSourceObject();
      for (NestedSourceObject nested : sourceObject.getNestedObjectList()) {
         nested.setParent(sourceObject);
      }

      final DestinationObject destinationObject = objectAssembler.assemble(sourceObject, DestinationObject.class);

      Assert.assertEquals(sourceObject.getString(), destinationObject.getNestedObjectList().get(0).getParent()
            .getString());

   }

   @Test
   public void testTypeConverterTypeInference() {
      Assert.assertEquals(SourceObject.class, GenericTypeResolver.getParameterizedTypeByName("Source",
            DestinationObjectProvidingObjectConverter.class));
      Assert.assertEquals(DestinationObject.class, GenericTypeResolver.getParameterizedTypeByName(
            "Destination", DestinationObjectProvidingObjectConverter.class));
   }

   @Test
   public void shouldConvertAllPropertiesOfSameNameWhenUsingGenericConverter() {

      // reset with no converters
      objectAssembler = new SimpleObjectAssembler();
      objectAssembler.setAutomapWhenNoConverterFound(true);

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
   public void shouldCallEntityDaoToFindEntityWhenNoConverterProvided() {

      final EntityDao mockEntityDao = EasyMock.createMock(EntityDao.class);

      // reset with no converters
      objectAssembler = new SimpleObjectAssembler();
      objectAssembler.setAutomapWhenNoConverterFound(true);
      objectAssembler.setEntityDao(mockEntityDao);

      final StubEntityDto dto = new StubEntityDto();
      dto.setId(1L);
      dto.setName("name");

      expect(mockEntityDao.findById(StubEntity.class, 1L)).andReturn(new StubEntity());

      EasyMock.replay(mockEntityDao);

      final StubEntity stubEntity = objectAssembler.assemble(dto, StubEntity.class);

      EasyMock.verify(mockEntityDao);

      assertThat(dto.getId(), is(stubEntity.getId()));
      assertThat(dto.getName(), is(stubEntity.getName()));

   }

   @Test
   public void shouldNotCallEntityDaoToFindEntityWhenNoConverterProvidedAndDtoHasNullId() {

      final EntityDao mockEntityDao = EasyMock.createMock(EntityDao.class);

      // reset with no converters
      objectAssembler = new SimpleObjectAssembler();
      objectAssembler.setAutomapWhenNoConverterFound(true);
      objectAssembler.setEntityDao(mockEntityDao);

      final StubEntityDto dto = new StubEntityDto();
      dto.setName("name");

      EasyMock.replay(mockEntityDao); // no expectations

      final StubEntity stubEntity = objectAssembler.assemble(dto, StubEntity.class);

      EasyMock.verify(mockEntityDao);

      assertThat(dto.getId(), is(stubEntity.getId()));
      assertThat(dto.getName(), is(stubEntity.getName()));

   }

   @Test
   public void shouldThrowExceptionWhenTryingToMapNonDtoToEntityWhenNoConverterProvided() {

      final EntityDao mockEntityDao = EasyMock.createMock(EntityDao.class);

      // reset with no converters
      objectAssembler = new SimpleObjectAssembler();
      objectAssembler.setAutomapWhenNoConverterFound(true);
      objectAssembler.setEntityDao(mockEntityDao);

      final SourceObject dto = new SourceObject();

      try {
         final StubEntity stubEntity = objectAssembler.assemble(dto, StubEntity.class);
         fail("Expected exception");
      }
      catch (ConversionException e) {
         ;// Expected exception
      }

   }

   @Test
   public void shouldFallBackToMethodAccessWhenGetterHasNoProperty() {

      final ObjectContainingGetterWithNoProperty source = new ObjectContainingGetterWithNoProperty();
      source.getString();

      objectAssembler = new SimpleObjectAssembler();
      objectAssembler.setAutomapWhenNoConverterFound(true);

      ObjectWithSetterForAugmentedGetter destination = objectAssembler.assemble(source,
            ObjectWithSetterForAugmentedGetter.class);

      assertEquals(source.getAugmentedString(), destination.getAugmentedString());
      assertEquals(Integer.valueOf(source.getNumber()), destination.getNumber());
      assertEquals("string", destination.getList().get(0));

   }

   @Test
   public void shouldMapToDestinationPropertyWithoutSetter() {

      SourceObjectWithNoSetterForProperty source = new SourceObjectWithNoSetterForProperty("string");

      objectAssembler = new SimpleObjectAssembler();
      objectAssembler.setAutomapWhenNoConverterFound(true);

      DestinationObjectWithNoSetterForProperty destination = objectAssembler.assemble(source,
            DestinationObjectWithNoSetterForProperty.class);

      assertEquals("string", destination.getPropertyWithoutSetter());

   }

   @Test
   public void shouldConvertDateToCalendar() {
      DateToCalendarConverter converter = new DateToCalendarConverter();
      converter.setObjectAssembler(objectAssembler);
      converter.postConstruct();

      assertNotNull(objectAssembler.assemble(new Date(), Calendar.class));
   }


   @Test
   public void shouldConvertBetweenPrimitivesAndAutoboxedTypes() {

      objectAssembler.setAutomapWhenNoConverterFound(true);

      final SourceObjectWithPrimitives sourceObject = new SourceObjectWithPrimitives();

      final DestinationObjectWithPrimitives destinationObject = objectAssembler.assemble(sourceObject, DestinationObjectWithPrimitives.class);

      Assert.assertEquals(sourceObject.getByteA(), destinationObject.getByteA().byteValue());
      Assert.assertEquals(sourceObject.getByteB().byteValue(), destinationObject.getByteB());

      Assert.assertEquals(sourceObject.getShortA(), destinationObject.getShortA().shortValue());
      Assert.assertEquals(sourceObject.getShortB().shortValue(), destinationObject.getShortB());

      Assert.assertEquals(sourceObject.getIntA(), destinationObject.getIntA().intValue());
      Assert.assertEquals(sourceObject.getIntB().intValue(), destinationObject.getIntB());

      Assert.assertEquals(sourceObject.getLongA(), destinationObject.getLongA().longValue());
      Assert.assertEquals(sourceObject.getLongB().longValue(), destinationObject.getLongB());

   }


   public class DateToCalendarConverter extends AbstractObjectConverter<Date, Calendar> {

      @Override
      public Calendar createDestinationObject(Date date) {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(date);
         return calendar;
      }

      @Override
      protected boolean disableAutoMapping() {
         return true;
      }
   }


   private SourceObject createFullyPopulatedSourceObject() {
      final SourceObject sourceObject = new SourceObject();
      sourceObject.setString("string");
      sourceObject.setDifferentNameSource("differentName");
      sourceObject.setNestedObject(createFullyPopulatedNestedSourceObject("string1"));
      sourceObject
            .setNestedObjectDifferentNameSource(createFullyPopulatedNestedSourceObject("nestedObjectDifferentName"));

      final List<NestedSourceObject> list = new ArrayList<NestedSourceObject>();
      list.add(createFullyPopulatedNestedSourceObject("string2"));
      list.add(createFullyPopulatedNestedSourceObject("string3"));
      sourceObject.setNestedObjectList(list);

      final Set<NestedSourceObject> set = new LinkedHashSet<NestedSourceObject>();
      set.add(createFullyPopulatedNestedSourceObject("string4"));
      set.add(createFullyPopulatedNestedSourceObject("string5"));

      sourceObject.setNestedObjectCollection(set);
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
