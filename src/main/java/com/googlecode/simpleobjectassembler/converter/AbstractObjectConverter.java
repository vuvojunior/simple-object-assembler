package com.googlecode.simpleobjectassembler.converter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.GenericCollectionTypeResolver;
import org.springframework.util.ReflectionUtils;

import com.googlecode.simpleobjectassembler.ObjectAssembler;
import com.googlecode.simpleobjectassembler.beans.FallbackPropertyAccessor;
import com.googlecode.simpleobjectassembler.converter.cache.CachingObjectAssembler;
import com.googlecode.simpleobjectassembler.converter.cache.ConversionCache;
import com.googlecode.simpleobjectassembler.converter.mapping.CollectionPropertyMapper;
import com.googlecode.simpleobjectassembler.converter.mapping.ConverterFieldMapping;
import com.googlecode.simpleobjectassembler.converter.mapping.DifferentTypePropertyMapper;
import com.googlecode.simpleobjectassembler.converter.mapping.IgnoreSet;
import com.googlecode.simpleobjectassembler.converter.mapping.PropertyDescriptorPair;
import com.googlecode.simpleobjectassembler.converter.mapping.PropertyMapper;
import com.googlecode.simpleobjectassembler.converter.mapping.SameTypePropertyMapper;
import com.googlecode.simpleobjectassembler.utils.CollectionUtils;
import com.googlecode.simpleobjectassembler.utils.GenericTypeResolver;

public abstract class AbstractObjectConverter<SourceObjectClass, DestinationObjectClass> implements
      ObjectConverter<SourceObjectClass, DestinationObjectClass> {

   private static final String DESTINATION_OBJECT_CLASS_PARAM_TYPE_NAME = "DestinationObjectClass";

   private static final String SOURCE_OBJECT_CLASS_PARAM_TYPE_NAME = "SourceObjectClass";

   private static final String[] DEFAULT_PROPERTIES_TO_IGNORE = new String[] { "class" };

   private static final String PROPERTY_EXCLUSION_WILDCARD_CHARACTER = "*";

   private CachingObjectAssembler objectAssembler;

   private boolean initialised = false;

   private Set<ConverterFieldMapping> sourceToDestinationFieldMappings;

   private final List<PropertyDescriptorPair> conversionCandidatesOfSameType = new ArrayList<PropertyDescriptorPair>();

   private final List<PropertyDescriptorPair> conversionCandidatesOfDifferentType = new ArrayList<PropertyDescriptorPair>();

   private final List<PropertyDescriptorPair> collectionConversionCandidates = new ArrayList<PropertyDescriptorPair>();

   private PropertyMapper sameTypePropertyMapper = new SameTypePropertyMapper();

   private PropertyMapper differentTypePropertyMapper = new DifferentTypePropertyMapper();

   private PropertyMapper collectionPropertyMapper = new CollectionPropertyMapper();
   
  

   /**
    * Creates an instance of the destination object reflectively. Override this
    * method if special object construction is required. The sourceObject passed
    * into this method is guaranteed not to be null to save redundant null
    * checking of the sourceObject.
    * 
    * @param sourceObject
    * 
    * @return destinationObject
    */
   public DestinationObjectClass createDestinationObject(SourceObjectClass sourceObject) {

      try {
         return getDestinationObjectClass().newInstance();
      }
      catch (InstantiationException e) {
         throw new ConversionException("Could not instantiate new instance of " + getDestinationObjectClass()
               + ". Ensure there is a no arg constructor available.", e);
      }
      catch (IllegalAccessException e) {
         throw new ConversionException("Could not instantiate new instance of " + getDestinationObjectClass(), e);
      }
   }

   public final DestinationObjectClass convert(SourceObjectClass sourceObject, ConversionCache conversionCache,
         String[] ignoreProperties) {

      return convert(sourceObject, createDestinationObject(sourceObject), conversionCache, ignoreProperties);

   }

   public final DestinationObjectClass convert(SourceObjectClass sourceObject,
         DestinationObjectClass destinationObject, ConversionCache conversionCache, String[] ignoreProperties) {

      final DestinationObjectClass previouslyConvertedDestinationObject = (DestinationObjectClass) conversionCache
            .getConvertedObjectBySourceObjectAndDestinationType(sourceObject, getDestinationObjectClass());

      if (previouslyConvertedDestinationObject == null) {
         conversionCache.cacheConvertedObjectBySourceObject(sourceObject, destinationObject,
               getDestinationObjectClass());
      }
      else {
         return previouslyConvertedDestinationObject;
      }

      initialiseFieldMappingIfRequired();
      final IgnoreSet explicitIgnoreSet = alwaysIgnoreProperties();
      Collections.addAll(explicitIgnoreSet.getSet(), DEFAULT_PROPERTIES_TO_IGNORE);
      Collections.addAll(explicitIgnoreSet.getSet(), ignoreProperties);

      validatePropertiesToIgnore(destinationObject, ignoreProperties);

      final Set<String> fullIgnoreSet = new HashSet<String>();
      fullIgnoreSet.addAll(explicitIgnoreSet.getSet());

      if (!disableAutoMapping() && !fullIgnoreSet.contains(PROPERTY_EXCLUSION_WILDCARD_CHARACTER)) {

         final PropertyAccessor sourcePropertyAccessor = new FallbackPropertyAccessor(sourceObject);
         final PropertyAccessor destinationPropertyAccessor = new FallbackPropertyAccessor(destinationObject);

         sameTypePropertyMapper.mapProperties(conversionCandidatesOfSameType, explicitIgnoreSet,
               sourcePropertyAccessor, destinationPropertyAccessor, conversionCache, objectAssembler);
         differentTypePropertyMapper.mapProperties(conversionCandidatesOfDifferentType, explicitIgnoreSet,
               sourcePropertyAccessor, destinationPropertyAccessor, conversionCache, objectAssembler);
         collectionPropertyMapper.mapProperties(collectionConversionCandidates, explicitIgnoreSet,
               sourcePropertyAccessor, destinationPropertyAccessor, conversionCache, objectAssembler);

      }

      if (!fullIgnoreSet.contains(PROPERTY_EXCLUSION_WILDCARD_CHARACTER)) {
         // call user defined conversions.
         convert(sourceObject, destinationObject);
      }
         
      return destinationObject;

   }

   /**
    * Override to provide any custom conversion logic required for the
    * converter. Default implementation does nothing. Override if there is
    * special conversion logic required by
    * 
    * @param sourceObject
    * @param destinationObject
    * @return
    */
   public void convert(SourceObjectClass sourceObject, DestinationObjectClass destinationObject) {
      //Override to implement explicit custom coversion logic
   }

   /**
    * Return the main object assembler
    * 
    * @return
    */
   public ObjectAssembler getObjectAssembler() {
      return objectAssembler;
   }

   /**
    * Runs post construction to inject the converter back into the assembler for
    * use at runtime.
    * 
    */
   @PostConstruct
   public void postConstruct() {
      objectAssembler.registerConverter(this);
   }

   @Autowired
   @Required
   public void setObjectAssembler(CachingObjectAssembler objectAssembler) {
      this.objectAssembler = objectAssembler;
   }

   /**
    * Override to define any custom field mappings that should be applied where
    * the source and destination field names do not match
    * 
    * @return
    */
   protected Set<ConverterFieldMapping> customConverterFieldMappings() {
      return new HashSet<ConverterFieldMapping>();
   }

   /**
    * Override and return true if a particular converter should disable auto
    * field mapping. Default value is false
    * 
    * @return
    */
   protected boolean disableAutoMapping() {
      return false;
   }

   /**
    * Specify properties that should always be ignored during mapping with this
    * converted. The runtime ignore set is added to this before any property
    * mapping is carried out
    * 
    * @return
    */
   protected IgnoreSet alwaysIgnoreProperties() {
      return new IgnoreSet();
   }

   /**
    * Validates that the properties in the ignore list are actually valid
    * fields.
    * 
    * @param sourceObject
    * @param ignoreProperties
    */
   private void validatePropertiesToIgnore(DestinationObjectClass destinationObject, String[] ignoreProperties) {

      final PropertyAccessor beanPropertyAccessor = new DirectFieldAccessor(destinationObject);
      final List<String> invalidProperties = new ArrayList<String>();
      for (String property : ignoreProperties) {
         String localProperty = property;
         if (property.indexOf(".") > 0) {
            localProperty = property.substring(0, property.indexOf("."));
         }

         if (!PROPERTY_EXCLUSION_WILDCARD_CHARACTER.equals(localProperty)) {
            try {
               beanPropertyAccessor.getPropertyValue(localProperty);
            }
            catch (InvalidPropertyException ipe) {
               invalidProperties.add(destinationObject.getClass() + "#" + localProperty);
            }
            catch (PropertyAccessException pac) {
               invalidProperties.add(destinationObject.getClass() + "#" + localProperty);
            }
         }
      }

      if (!invalidProperties.isEmpty()) {
         throw new ConversionException(
               "The following properties defined as properties to exclude from conversion in converter "
                     + this.getClass() + " do not exist on their respective types. "
                     + "Please check that they have not been misspelled or have changed due to refactoring.\n"
                     + invalidProperties.toString());
      }

   }

   /**
    * Initialises list of incompatible fields and registers converter candidates
    * based on other registered converters. This must execute after all
    * converters are registered which is why it's not run on bean
    * initialisation. Results are cached so that it's only run once.
    * 
    * @throws NoSuchFieldException
    * @throws SecurityException
    */
   private void initialiseFieldMappingIfRequired() {

      if (!initialised && !disableAutoMapping()) {

         final PropertyDescriptor[] sourcePds = BeanUtils.getPropertyDescriptors(getSourceObjectClass());
         final PropertyDescriptor[] destinationPds = BeanUtils.getPropertyDescriptors(getDestinationObjectClass());
         final Map<String, Field> writableDestinationFields = new HashMap<String, Field>();

         ReflectionUtils.doWithFields(getDestinationObjectClass(), new ReflectionUtils.FieldCallback() {

            public void doWith(Field field) {
               writableDestinationFields.put(field.getName(), field);
            }
         }, ReflectionUtils.COPYABLE_FIELDS);

         for (int i = 0; i < sourcePds.length; i++) {

            final String sourceName = sourcePds[i].getName();
            final Class<?> sourceType = sourcePds[i].getPropertyType();

            for (int j = 0; j < destinationPds.length; j++) {

               final String destinationName = destinationPds[j].getName();
               final Class<?> destinationType = destinationPds[j].getPropertyType();

               if (shouldMapFieldNames(sourceName, destinationName) 
                     && isSupportedCollection(sourceType)
                     && isSupportedCollection(destinationType)
                     && !CollectionUtils.hasSameGenericCollectionType(sourcePds[i], destinationPds[j])
                     && writableDestinationFields.containsKey(destinationName)) {

                  final Class<?> genericDestinationCollectionType = GenericCollectionTypeResolver
                        .getCollectionReturnType(destinationPds[j].getReadMethod());

                  collectionConversionCandidates.add(new PropertyDescriptorPair(sourcePds[i], destinationPds[j],
                        genericDestinationCollectionType));

               }
               else if (shouldMapFieldNames(sourceName, destinationName) 
                     && !sourceType.equals(destinationType)
                     && writableDestinationFields.containsKey(destinationName)) {

                  if (objectAssembler.converterExists(sourceType, destinationType)) {
                     conversionCandidatesOfDifferentType.add(new PropertyDescriptorPair(sourcePds[i], destinationPds[j]));
                  }
                  else if( objectAssembler.isAutomapWhenNoConverterFound()) {
                     objectAssembler.registerConverter(new GenericConverter(objectAssembler, sourceType, destinationType));
                     conversionCandidatesOfDifferentType.add(new PropertyDescriptorPair(sourcePds[i], destinationPds[j]));
                  }
                  else {
                     throw new ConversionException(sourceType, destinationType);
                  }
               }
               else if (shouldMapFieldNames(sourceName, destinationName)
                     && writableDestinationFields.containsKey(destinationName)) {
                  conversionCandidatesOfSameType.add(new PropertyDescriptorPair(sourcePds[i], destinationPds[j]));
               }
            }
         }
         // cache custom field mappings
         this.sourceToDestinationFieldMappings = customConverterFieldMappings();
         initialised = true;
      }

   }

   private boolean shouldMapFieldNames(final String sourceName, final String destinationName) {

      return ((sourceName.equals(destinationName) || conversionMappingExists(sourceName, destinationName)) && !alwaysIgnoreProperties()
            .contains(destinationName));
   }

   private boolean conversionMappingExists(String sourceName, String destinationName) {
      final ConverterFieldMapping converterFieldMapping = new ConverterFieldMapping(sourceName, destinationName);
      // cache first time
      if (sourceToDestinationFieldMappings == null) {
         sourceToDestinationFieldMappings = customConverterFieldMappings();
      }
      return sourceToDestinationFieldMappings.contains(converterFieldMapping);
   }

   private boolean isSupportedCollection(Class<?> sourceType) {
      return Collection.class.isAssignableFrom(sourceType);
   }

   /**
    * Default implementation that infers the source object class from the
    * class's parameterized generic types
    * 
    * @see com.googlecode.simpleobjectassembler.converter.ObjectConverter#getSourceObjectClass()
    */
   public Class<SourceObjectClass> getSourceObjectClass() {
      return GenericTypeResolver.getParameterizedTypeByName(SOURCE_OBJECT_CLASS_PARAM_TYPE_NAME, this.getClass());
   }

   /**
    * Default implementation that infers the destination object class from the
    * class's parameterized generic types
    * 
    * @see com.googlecode.simpleobjectassembler.converter.ObjectConverter#getDestinationObjectClass()
    */
   public Class<DestinationObjectClass> getDestinationObjectClass() {
      return GenericTypeResolver.getParameterizedTypeByName(DESTINATION_OBJECT_CLASS_PARAM_TYPE_NAME, this.getClass());
   }

}
