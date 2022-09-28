package com.googlecode.simpleobjectassembler.converter;

import com.googlecode.simpleobjectassembler.ObjectAssembler;
import com.googlecode.simpleobjectassembler.beans.FallbackPropertyAccessor;
import com.googlecode.simpleobjectassembler.converter.cache.CachingObjectAssembler;
import com.googlecode.simpleobjectassembler.converter.cache.ConversionCache;
import com.googlecode.simpleobjectassembler.converter.mapping.*;
import com.googlecode.simpleobjectassembler.utils.GenericTypeResolver;
import com.googlecode.simpleobjectassembler.utils.PrimitiveTypeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;

public abstract class AbstractObjectConverter<Source, Destination> implements
      ObjectConverter<Source, Destination> {

   private static final String DESTINATION_OBJECT_CLASS_PARAM_TYPE_NAME = "Destination";

   private static final String SOURCE_OBJECT_CLASS_PARAM_TYPE_NAME = "Source";

   private static final String[] DEFAULT_PROPERTIES_TO_IGNORE = new String[] { "class" };

   private static final String PROPERTY_EXCLUSION_WILDCARD_CHARACTER = "*";

   private CachingObjectAssembler objectAssembler;

   private boolean initialised = false;

   private Set<ConverterFieldMapping> sourceToDestinationFieldMappings;

   private final List<PropertyDescriptorPair> primitiveConversionCandidates = new ArrayList<PropertyDescriptorPair>();

   private final List<PropertyDescriptorPair> defaultConversionCandidates = new ArrayList<PropertyDescriptorPair>();

   private final List<PropertyDescriptorPair> collectionConversionCandidates = new ArrayList<PropertyDescriptorPair>();

   private PropertyMapper primitivePropertyMapper = new PrimitivePropertyMapper();

   private PropertyMapper defaultPropertyMapper = new DefaultPropertyMapper();

   private PropertyMapper collectionPropertyMapper = new CollectionPropertyMapper();

   private final Set<Exclusions> validExclusions = new HashSet<Exclusions>();


   /**
    * Creates an instance of the destination object reflectively. Override this
    * method if special object construction is required. The source passed
    * into this method is guaranteed not to be null to save redundant null
    * checking of the source.
    *
    * @param source
    *
    * @return destinationObject
    */
   public Destination createDestinationObject(Source source) {

      try {
         return getDestinationClass().newInstance();
      }
      catch (InstantiationException e) {
         throw new ConversionException("Could not instantiate new instance of " +
               getDestinationClass() + " when trying to convert from " +
               source.getClass() + ". Ensure there is a no arg constructor " +
               "available or create an explicit converter for this source to destination combination.", e);
      }
      catch (IllegalAccessException e) {
         throw new ConversionException("Could not instantiate new instance of " + getDestinationClass(), e);
      }
   }



   public final Destination convert(Source source,
                                               ConversionCache conversionCache,
                                               Exclusions exclusions) {


      final Destination previouslyConvertedDestinationObject = (Destination) conversionCache
            .getConvertedObjectBySourceObjectAndDestinationType(source, getDestinationClass());

      if (previouslyConvertedDestinationObject == null) {

         Destination destination = createDestinationObject(source);

         if(destination == null) {
            return null;
         }

         conversionCache.cacheConvertedObjectBySourceObject(source, destination,
               getDestinationClass());

         return convert(source, destination, conversionCache, exclusions);
      }
      else {
         return previouslyConvertedDestinationObject;
      }




   }


   public final Destination convert(Source source,
                                               Destination destination,
                                               ConversionCache conversionCache,
                                               Exclusions exclusions) {



      initialiseFieldMappingIfRequired();

      final Exclusions explicitExclusions = alwaysExcludeProperties();
      Collections.addAll(explicitExclusions.getSet(), DEFAULT_PROPERTIES_TO_IGNORE);
      explicitExclusions.getSet().addAll(exclusions.getSet());

      if(!validExclusions.contains(exclusions)) {
         validatePropertiesToIgnore(destination, exclusions);
         validExclusions.add(exclusions);
      }



      final Set<String> fullIgnoreSet = new HashSet<String>();
      fullIgnoreSet.addAll(explicitExclusions.getSet());

      if (!disableAutoMapping() && !fullIgnoreSet.contains(PROPERTY_EXCLUSION_WILDCARD_CHARACTER)) {

         final PropertyAccessor sourcePropertyAccessor = new FallbackPropertyAccessor(source);
         final PropertyAccessor destinationPropertyAccessor = new FallbackPropertyAccessor(destination);

         primitivePropertyMapper.mapProperties(primitiveConversionCandidates, explicitExclusions,
               sourcePropertyAccessor, destinationPropertyAccessor, conversionCache, objectAssembler);

         defaultPropertyMapper.mapProperties(defaultConversionCandidates, explicitExclusions,
               sourcePropertyAccessor, destinationPropertyAccessor, conversionCache, objectAssembler);

         collectionPropertyMapper.mapProperties(collectionConversionCandidates, explicitExclusions,
               sourcePropertyAccessor, destinationPropertyAccessor, conversionCache, objectAssembler);

      }

      if (!fullIgnoreSet.contains(PROPERTY_EXCLUSION_WILDCARD_CHARACTER)) {
         // call user defined conversions.
         convert(source, destination);
      }

      return destination;

   }

   /**
    * Override to provide any custom conversion logic required for the
    * converter. Default implementation does nothing. Override if there is
    * special conversion logic required by
    *
    * @param source
    * @param destination
    * @return
    */
   public void convert(Source source, Destination destination) {
      //Override to implement explicit custom coversion logic
   }

   /**
    *
    * @return the object assembler this converter is bound to
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
   protected Exclusions alwaysExcludeProperties() {
      return new Exclusions();
   }

   /**
    * Validates that the properties in the ignore list are actually valid
    * fields.
    *
    * @param destination
    * @param exclusions
    */
   private void validatePropertiesToIgnore(Destination destination, Exclusions exclusions) {

      final PropertyAccessor beanPropertyAccessor = new DirectFieldAccessor(destination);
      final List<String> invalidProperties = new ArrayList<String>();


      for (String property : exclusions.getSet()) {

         String localProperty = property;

         int indexOfPropertyDelimiter = property.indexOf(".");

         if (indexOfPropertyDelimiter > 0) {
            localProperty = property.substring(0, indexOfPropertyDelimiter);
         }

         if (!PROPERTY_EXCLUSION_WILDCARD_CHARACTER.equals(localProperty)
               && !beanPropertyAccessor.isWritableProperty(localProperty)) {

               invalidProperties.add(destination.getClass() + "#" + localProperty);
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

         synchronized (this) {

            if(initialised) {
               return;
            }

            final PropertyDescriptor[] sourcePds = BeanUtils.getPropertyDescriptors(getSourceClass());
            final PropertyDescriptor[] destinationPds = BeanUtils.getPropertyDescriptors(getDestinationClass());
            final Map<String, Field> writableDestinationFields = new HashMap<String, Field>();

            ReflectionUtils.doWithFields(getDestinationClass(), new ReflectionUtils.FieldCallback() {

               public void doWith(Field field) {
                  writableDestinationFields.put(field.getName(), field);
               }
            }, ReflectionUtils.COPYABLE_FIELDS);

            for (int i = 0; i < sourcePds.length; i++) {

               final String sourceName = sourcePds[i].getName();
               Class<?> sourceType = sourcePds[i].getPropertyType();

               if (sourceType == null) {
                  continue;
               } else if(sourceType.isPrimitive()) {
                  sourceType = PrimitiveTypeUtils.getAutoboxedTypeForPrimitive(sourceType);
               }

               for (int j = 0; j < destinationPds.length; j++) {

                  final String destinationName = destinationPds[j].getName();
                  Class<?> destinationType = destinationPds[j].getPropertyType();

                  if(destinationType == null) {
                     continue;
                  } else if(destinationType.isPrimitive()) {
                     destinationType = PrimitiveTypeUtils.getAutoboxedTypeForPrimitive(destinationType);
                  }

                  if (shouldMapFieldNames(sourceName, destinationName)
                        && isSupportedCollection(sourceType)
                        && isSupportedCollection(destinationType)
                        //&& !CollectionUtils.hasSameGenericCollectionType(sourcePds[i], destinationPds[j])
                        && writableDestinationFields.containsKey(destinationName)) {

                     final Class<?> genericDestinationCollectionType = ResolvableType.forMethodReturnType(destinationPds[j]
                             .getReadMethod()).asCollection().resolveGeneric(0);

                     collectionConversionCandidates.add(new PropertyDescriptorPair(sourcePds[i], destinationPds[j],
                           genericDestinationCollectionType));

                  }
                  else if (shouldMapFieldNames(sourceName, destinationName)
                        && !equalPrimitiveEquivilentTypes(sourceType, destinationType)
                        && writableDestinationFields.containsKey(destinationName)) {

                     if (objectAssembler.converterExists(sourceType, destinationType)) {
                        defaultConversionCandidates.add(new PropertyDescriptorPair(sourcePds[i], destinationPds[j]));
                     }
                     else if( objectAssembler.isAutomapWhenNoConverterFound()) {
                        objectAssembler.registerConverter(new GenericConverter(objectAssembler, sourceType, destinationType));
                        defaultConversionCandidates.add(new PropertyDescriptorPair(sourcePds[i], destinationPds[j]));
                     }
                     else {
                        throw new ConversionException(sourceType, destinationType);
                     }
                  }
                  else if (shouldMapFieldNames(sourceName, destinationName)
                        && writableDestinationFields.containsKey(destinationName)) {
                     primitiveConversionCandidates.add(new PropertyDescriptorPair(sourcePds[i], destinationPds[j]));
                  }
               }
            }
            // cache custom field mappings
            this.sourceToDestinationFieldMappings = customConverterFieldMappings();
            initialised = true;
         }
      }


   }

   private boolean equalPrimitiveEquivilentTypes(Class sourceType, Class destinationType) {
      if(sourceType.equals(destinationType) && PrimitiveTypeUtils.isPrimitiveEquivilent(sourceType)) {
         return true;
      }
      return false;
   }



   private boolean shouldMapFieldNames(final String sourceName, final String destinationName) {

      return ((sourceName.equals(destinationName) || conversionMappingExists(sourceName, destinationName)) && !alwaysExcludeProperties()
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
    * @see com.googlecode.simpleobjectassembler.converter.ObjectConverter#getSourceClass()
    */
   public Class<Source> getSourceClass() {
      return GenericTypeResolver.getParameterizedTypeByName(SOURCE_OBJECT_CLASS_PARAM_TYPE_NAME, this.getClass());
   }

   /**
    * Default implementation that infers the destination object class from the
    * class's parameterized generic types
    *
    * @see com.googlecode.simpleobjectassembler.converter.ObjectConverter#getDestinationClass()
    */
   public Class<Destination> getDestinationClass() {
      return GenericTypeResolver.getParameterizedTypeByName(DESTINATION_OBJECT_CLASS_PARAM_TYPE_NAME, this.getClass());
   }

}
