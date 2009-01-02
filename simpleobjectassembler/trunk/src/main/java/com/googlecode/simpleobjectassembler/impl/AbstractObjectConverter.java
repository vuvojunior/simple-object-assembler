package com.googlecode.simpleobjectassembler.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.springframework.util.StringUtils;

import com.googlecode.simpleobjectassembler.ObjectAssembler;
import com.googlecode.simpleobjectassembler.ObjectConverter;

public abstract class AbstractObjectConverter<SourceObjectClass, DestinationObjectClass> implements
      ObjectConverter<SourceObjectClass, DestinationObjectClass> {

   private static final String[] DEFAULT_PROPERTIES_TO_IGNORE = new String[] { "class" };

   private static final String PROPERTY_EXCLUSION_WILDCARD_CHARACTER = "*";

   private CachingObjectAssembler objectAssembler;

   private List<String> incompatibleFields;

   private Set<ConverterFieldMapping> converterFieldMappings;

   private final List<PropertyDescriptorPair> conversionCandidatesOfSameType = new ArrayList<PropertyDescriptorPair>();

   private final List<PropertyDescriptorPair> conversionCandidatesOfDifferentType = new ArrayList<PropertyDescriptorPair>();

   private final List<PropertyDescriptorPair> collectionConversionCandidates = new ArrayList<PropertyDescriptorPair>();

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

   /*
    * (non-Javadoc)
    * 
    * @see
    * au.com.australiapost.postzone.core.service.assembler.ObjectConverter#convert
    * (java.lang.Object, java.lang.String[])
    */
   public final DestinationObjectClass convert(SourceObjectClass sourceObject, ConversionCache conversionCache,
         String[] ignoreProperties) {

      return convert(sourceObject, createDestinationObject(sourceObject), conversionCache, ignoreProperties);

   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * au.com.australiapost.postzone.core.service.assembler.ObjectConverter#convert
    * (java.lang.Object)
    */
   // public DestinationObjectClass convert(SourceObjectClass sourceObject) {
   // return this.convert(sourceObject, conversionCache, new String[] {});
   // }
   /*
    * (non-Javadoc)
    * 
    * @see
    * au.com.australiapost.postzone.core.service.assembler.ObjectConverter#convert
    * (java.lang.Object, java.lang.Object, java.lang.String[])
    */
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
      final Set<String> explicitIgnoreSet = alwaysIgnoreProperties();
      Collections.addAll(explicitIgnoreSet, DEFAULT_PROPERTIES_TO_IGNORE);
      Collections.addAll(explicitIgnoreSet, ignoreProperties);

      validatePropertiesToIgnore(destinationObject, ignoreProperties);

      final Set<String> fullIgnoreSet = new HashSet<String>();
      fullIgnoreSet.addAll(explicitIgnoreSet);
      fullIgnoreSet.addAll(this.incompatibleFields);

      if (!disableAutoMapping()) {

         if (!fullIgnoreSet.contains(PROPERTY_EXCLUSION_WILDCARD_CHARACTER)) {
            // Copy basic properties
            // BeanUtils.copyProperties(sourceObject, destinationObject,
            // fullIgnoreSet.toArray(new String[] {}));

            final PropertyAccessor sourcePropertyAccessor = new DirectFieldAccessor(sourceObject);
            final PropertyAccessor destinationPropertyAccessor = new DirectFieldAccessor(destinationObject);
            convertPropertiesOfSameType(explicitIgnoreSet, sourcePropertyAccessor, destinationPropertyAccessor);
            convertNonCollectionPropertiesOfDifferentType(explicitIgnoreSet, sourcePropertyAccessor,
                  destinationPropertyAccessor, conversionCache);
            convertCollectionProperties(explicitIgnoreSet, sourcePropertyAccessor, destinationPropertyAccessor,
                  conversionCache);
         }
      }

      if (!fullIgnoreSet.contains(PROPERTY_EXCLUSION_WILDCARD_CHARACTER)) {
         // call user defined conversions.
         return convert(sourceObject, destinationObject);
      }
      else {
         return destinationObject;
      }

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
   public DestinationObjectClass convert(SourceObjectClass sourceObject, DestinationObjectClass destinationObject) {
      return destinationObject;
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
   protected Set<String> alwaysIgnoreProperties() {
      return new HashSet<String>();
   }

   private void convertPropertiesOfSameType(Set<String> explicitIgnoreSet, PropertyAccessor sourcePropertyAccessor,
         PropertyAccessor destinationPropertyAccessor) {

      for (PropertyDescriptorPair pdp : conversionCandidatesOfSameType) {
         final String sourcePropertyName = pdp.getSource().getName();
         final String destinationPropertyName = pdp.getDestination().getName();
         if (!explicitIgnoreSet.contains(destinationPropertyName)) {
            destinationPropertyAccessor.setPropertyValue(destinationPropertyName, sourcePropertyAccessor
                  .getPropertyValue(sourcePropertyName));
         }
      }

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
    * Convert collection properties using mapped converters where registered.
    * Destination collections of the same size as source collections will map
    * directly against the collection item of the same index. Null or empty
    * destination collections will be created.
    * 
    * TODO: RM Need to handle case where the destination collection size is
    * different to the source - add new or remove deleted items.
    * 
    * @param explicitIgnoreSet
    * @param sourcePropertyAccessor
    * @param destinationPropertyAccessor
    */
   private void convertCollectionProperties(final Set<String> explicitIgnoreSet,
         final PropertyAccessor sourcePropertyAccessor, final PropertyAccessor destinationPropertyAccessor,
         ConversionCache conversionCache) {
      for (PropertyDescriptorPair pdp : collectionConversionCandidates) {
         final String sourcePropertyName = pdp.getSource().getName();
         final String destinationPropertyName = pdp.getDestination().getName();
         if (!explicitIgnoreSet.contains(destinationPropertyName)) {

            final Class<?> destinationCollectionType = destinationPropertyAccessor
                  .getPropertyType(destinationPropertyName);
            final Class<?> genericDestinationCollectionType = pdp.getGenericDestinationCollectionType();
            final Collection<?> sourceCollection = (Collection<?>) sourcePropertyAccessor
                  .getPropertyValue(sourcePropertyName);

            Collection destinationCollection = (Collection) destinationPropertyAccessor.getPropertyValue(pdp
                  .getDestination().getName());
            if (sourceCollection != null) {

               final String[] nestedExclusions = getNestedPropertyExclusions(destinationPropertyName, explicitIgnoreSet);

               if (destinationCollection != null && isOrderedCollection(destinationCollection)
                     && isOrderedCollection(sourceCollection)
                     && destinationCollection.size() == sourceCollection.size()) {

                  int i = 0;
                  for (final Iterator it = sourceCollection.iterator(); it.hasNext(); i++) {
                     final Object sourceObject = it.next();
                     final Object destinationObject = retrieveIndexedValueFromCollection(destinationCollection, i);
                     objectAssembler.assemble(sourceObject, destinationObject, conversionCache, nestedExclusions);
                  }
               }
               else {

                  // This is limiting but only expect lists and sets for now.
                  if (List.class.isAssignableFrom(destinationCollectionType)) {
                     destinationCollection = new ArrayList();
                  }
                  else if (Set.class.isAssignableFrom(destinationCollectionType)) {
                     destinationCollection = new HashSet();
                  }
                  else {
                     throw new IllegalArgumentException(
                           "Only support conversion of set and list collection types for now.");
                  }

                  for (final Iterator it = sourceCollection.iterator(); it.hasNext();) {
                     final Object convertedObject = objectAssembler.assemble(it.next(),
                           genericDestinationCollectionType, conversionCache, nestedExclusions);
                     destinationCollection.add(convertedObject);
                  }

               }
            }
            destinationPropertyAccessor.setPropertyValue(destinationPropertyName, destinationCollection);
         }
      }
   }

   private boolean isOrderedCollection(Collection<?> collection) {
      return List.class.isAssignableFrom(collection.getClass());
   }

   /**
    * Will retrieve an object by it's index regardless of collection type.
    * Beware that non indexed collections such as a hashset will not retrieve
    * values reliably by index .
    * 
    * TODO: May want to throw an exception if attempting to retrieve a value by
    * index from an unordered collection.
    * 
    * @param collection
    * @param index
    * @return
    */
   private Object retrieveIndexedValueFromCollection(Collection<?> collection, int index) {

      if (collection instanceof List) {
         return ((List<?>) collection).get(index);
      }
      else {
         final Iterator<?> it = collection.iterator();
         for (int j = 0; it.hasNext(); j++) {
            final Object elem = it.next();
            if (j == index) {
               return elem;
            }
         }
         return null;
      }
   }

   private void convertNonCollectionPropertiesOfDifferentType(final Set<String> explicitIgnoreSet,
         final PropertyAccessor sourcePropertyAccessor, final PropertyAccessor destinationPropertyAccessor,
         ConversionCache conversionCache) {
      // check each conversion candidate and convert if not explicitly set to
      // ignore
      for (PropertyDescriptorPair pdp : conversionCandidatesOfDifferentType) {
         final String destinationPropertyName = pdp.getDestination().getName();
         final String sourcePropertyName = pdp.getSource().getName();
         if (!explicitIgnoreSet.contains(destinationPropertyName)) {
            final Class<?> destinationType = destinationPropertyAccessor.getPropertyType(destinationPropertyName);
            final Object nestedSourceObject = sourcePropertyAccessor.getPropertyValue(sourcePropertyName);

            Object convertedValue = null;
            if (nestedSourceObject != null && convertedValue == null) {
               final String[] nestedExclusions = getNestedPropertyExclusions(destinationPropertyName, explicitIgnoreSet);
               convertedValue = objectAssembler.assemble(nestedSourceObject, destinationType, conversionCache,
                     nestedExclusions);
            }

            destinationPropertyAccessor.setPropertyValue(destinationPropertyName, convertedValue);

         }
      }
   }

   /**
    * Returns any nested properties rooted at the property base path. For
    * example, if the propertyBase is set to "address", exclusions such as
    * "address.postcode" would match, as would "address.state.name". These would
    * return the values of "postcode" & "state.name" respectively
    * 
    * @param explicitIgnoreSet
    * @return
    */
   private String[] getNestedPropertyExclusions(String propertyBase, Set<String> explicitIgnoreSet) {
      final List<String> nestedProperties = new ArrayList<String>();
      for (String property : explicitIgnoreSet) {

         final String nestedPropertyPrefix = propertyBase + ".";

         if (property.startsWith(nestedPropertyPrefix)) {
            nestedProperties.add(StringUtils.replace(property, nestedPropertyPrefix, ""));
         }

      }
      return nestedProperties.toArray(new String[] {});
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

      if (this.incompatibleFields == null) {
         this.incompatibleFields = new ArrayList<String>();
         final PropertyDescriptor[] sourcePds = BeanUtils.getPropertyDescriptors(getSourceObjectClass());
         final PropertyDescriptor[] destinationPds = BeanUtils.getPropertyDescriptors(getDestinationObjectClass());
         final Map<String, Field> writableDestinationFields = new HashMap<String, Field>();
         ReflectionUtils.doWithFields(getDestinationObjectClass(), new ReflectionUtils.FieldCallback() {

            public void doWith(Field field) {
               writableDestinationFields.put(field.getName(), field);
            }
         }, ReflectionUtils.COPYABLE_FIELDS);

         for (int i = 0; i < sourcePds.length; i++) {
            for (int j = 0; j < destinationPds.length; j++) {

               final String sourceName = sourcePds[i].getName();
               final Class<?> sourceType = sourcePds[i].getPropertyType();
               final String destinationName = destinationPds[j].getName();
               final Class<?> destinationType = destinationPds[j].getPropertyType();

               if (shouldMapFieldNames(sourceName, destinationName) && !sourceType.equals(destinationType)
                     && writableDestinationFields.containsKey(destinationName)) {

                  // add fields that are incompatible for a direct mapping
                  incompatibleFields.add(sourceName);

                  if (objectAssembler.converterExists(sourceType, destinationType)) {
                     conversionCandidatesOfDifferentType
                           .add(new PropertyDescriptorPair(sourcePds[i], destinationPds[j]));
                  }
               }
               else if (shouldMapFieldNames(sourceName, destinationName) && isSupportedCollection(sourceType)
                     && !hasSameGenericCollectionType(sourcePds[i], destinationPds[j])
                     && writableDestinationFields.containsKey(destinationName)) {

                  // add fields that are incompatible for a direct mapping
                  incompatibleFields.add(sourceName);

                  final Class<?> genericDestinationCollectionType = GenericCollectionTypeResolver
                        .getCollectionReturnType(destinationPds[j].getReadMethod());

                  collectionConversionCandidates.add(new PropertyDescriptorPair(sourcePds[i], destinationPds[j],
                        genericDestinationCollectionType));

               }
               else if (shouldMapFieldNames(sourceName, destinationName)
                     && writableDestinationFields.containsKey(destinationName)) {
                  conversionCandidatesOfSameType.add(new PropertyDescriptorPair(sourcePds[i], destinationPds[j]));
               }
            }
         }
         // cache custom field mappings
         this.converterFieldMappings = customConverterFieldMappings();
      }

   }

   private boolean shouldMapFieldNames(final String sourceName, final String destinationName) {
      return sourceName.equals(destinationName) || conversionMappingExists(sourceName, destinationName);
   }

   private boolean conversionMappingExists(String sourceName, String destinationName) {
      final ConverterFieldMapping converterFieldMapping = new ConverterFieldMapping(sourceName, destinationName);
      // cache first time
      if (converterFieldMappings == null) {
         converterFieldMappings = customConverterFieldMappings();
      }
      return converterFieldMappings.contains(converterFieldMapping);
   }

   private boolean hasSameGenericCollectionType(PropertyDescriptor sourcePropertyDescriptor,
         PropertyDescriptor destinationPropertyDescriptor) {

      final Class<?> genericSourceType = GenericCollectionTypeResolver.getCollectionReturnType(sourcePropertyDescriptor
            .getReadMethod());
      final Class<?> genericDestinationType = GenericCollectionTypeResolver
            .getCollectionReturnType(destinationPropertyDescriptor.getReadMethod());
      if (genericSourceType == null && genericDestinationType == null) {
         return true;
      }
      else if (genericSourceType == null || genericDestinationType == null) {
         return false;
      }
      else {
         return genericSourceType.equals(genericDestinationType);
      }
   }

   private boolean isSupportedCollection(Class<?> sourceType) {
      return Collection.class.isAssignableFrom(sourceType);
   }

   /**
    * Models a pair of fields with some relationship in two classes.
    * 
    * @author robmonie
    * 
    */
   private class PropertyDescriptorPair {

      private final PropertyDescriptor source;

      private final PropertyDescriptor destination;

      private final Class<?> genericDestinationCollectionType;

      public PropertyDescriptorPair(PropertyDescriptor source, PropertyDescriptor destination) {
         super();
         this.source = source;
         this.destination = destination;
         this.genericDestinationCollectionType = null;
      }

      public PropertyDescriptorPair(PropertyDescriptor source, PropertyDescriptor destination,
            Class<?> genericDestinationClass) {
         super();
         this.source = source;
         this.destination = destination;
         this.genericDestinationCollectionType = genericDestinationClass;

      }

      public Class<?> getGenericDestinationCollectionType() {
         return genericDestinationCollectionType;
      }

      public PropertyDescriptor getSource() {
         return source;
      }

      public PropertyDescriptor getDestination() {
         return destination;
      }

      public boolean destinationTypeIsGeneric() {
         return genericDestinationCollectionType != null;
      }

   }

}
