package com.googlecode.simpleobjectassembler.registry;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.simpleobjectassembler.ConverterRegistry;
import com.googlecode.simpleobjectassembler.converter.ConversionException;
import com.googlecode.simpleobjectassembler.converter.ObjectConverter;
import com.googlecode.simpleobjectassembler.converter.mapping.ConverterMappingKey;
import com.googlecode.simpleobjectassembler.utils.CglibUtils;

public class ConverterRegistryImpl implements ConverterRegistry {

   private final Map<ConverterMappingKey, ObjectConverter<?, ?>> converterRegistry =
         new HashMap<ConverterMappingKey, ObjectConverter<?, ?>>();

   public ConverterRegistryImpl() {
      // default
   }

   public ObjectConverter<?, ?> getConverter(final Class<?> sourceClass, final Class<?> destinationClass) {

      /*
      Class<?> sourceObjectClass;
      try {
         sourceObjectClass = CglibUtils.resolveTargetClassIfProxied(sourceObject);
      } catch (ClassNotFoundException e) {
         throw new ConversionException("Can't find class for source object: " + sourceObject.getClass().getName(), e);
      }
*/
      return lookupConverterUsingSourceObjectHierarchy(sourceClass, destinationClass);

   }

   private ObjectConverter<?, ?> lookupConverterUsingSourceObjectHierarchy(Class<?> sourceObjectOrSuperClass,
         final Class<?> destinationClass) {

      final ConverterMappingKey converterMappingKey = new TypeBasedTransformerMappingKey(sourceObjectOrSuperClass, destinationClass);

      if (converterRegistry.containsKey(converterMappingKey)) {
         return converterRegistry.get(converterMappingKey);
      } else if (sourceObjectOrSuperClass.getSuperclass() == null || sourceObjectOrSuperClass.getSuperclass().equals(Object.class)) {
         return null;
      }

      return lookupConverterUsingSourceObjectHierarchy(sourceObjectOrSuperClass.getSuperclass(), destinationClass);

   }

   public void registerConverter(ObjectConverter<?, ?> objectConverter) {

      final ConverterMappingKey transformerMappingKey =
            new TypeBasedTransformerMappingKey(objectConverter.getSourceObjectClass(), objectConverter
                  .getDestinationObjectClass());

      if (converterRegistry.containsKey(transformerMappingKey)) {
         throw new ConverterRegistryException(transformerMappingKey);
      }

      converterRegistry.put(transformerMappingKey, objectConverter);

   }

   public boolean converterExists(Class<?> sourceClass, Class<?> destinationClass) {
      return lookupConverterUsingSourceObjectHierarchy(sourceClass, destinationClass) != null;
   }

}
