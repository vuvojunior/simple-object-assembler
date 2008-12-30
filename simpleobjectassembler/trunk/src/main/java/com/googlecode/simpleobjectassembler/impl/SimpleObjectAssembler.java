package com.googlecode.simpleobjectassembler.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.googlecode.simpleobjectassembler.ConverterRegistry;
import com.googlecode.simpleobjectassembler.ObjectAssembler;
import com.googlecode.simpleobjectassembler.ObjectConverter;
import com.googlecode.simpleobjectassembler.utils.CglibUtils;

/**
 * Single implementation of the {@link ObjectAssembler} for assembling objects.
 * Uses an internal registry of converters supplied by the end developer that
 * are registered on app startup. 
 * 
 */
@Component("objectAssembler")
public class SimpleObjectAssembler implements ObjectAssembler, CachingObjectAssembler {

   private static final Log LOG = LogFactory.getLog(SimpleObjectAssembler.class);

   private final ConverterRegistry converterRegistry = new ConverterRegistryImpl();

   public <T> T assemble(Object sourceObject, Class<T> destinationClass) {
      return assemble(sourceObject, destinationClass, new ConversionCache(), new String[] {});
   }

   public <T> T assemble(Object sourceObject, ConversionCache conversionCache, Class<T> destinationClass) {
      return assemble(sourceObject, destinationClass, conversionCache, new String[] {});
   }

   public final <T> T assemble(Object sourceObject, Class<T> destinationClass, String... ignoreProperties) {
      return assemble(sourceObject, destinationClass, new ConversionCache(), ignoreProperties);
   }

   public final <T> T assemble(Object sourceObject, Class<T> destinationClass, ConversionCache conversionCache,
         String... ignoreProperties) {
      if (sourceObject == null) {
         return null;
      }
      final ObjectConverter objectConverter = converterRegistry.getConverter(sourceObject, destinationClass);
      return (T) objectConverter.convert(sourceObject, conversionCache, ignoreProperties);
   }

   public final <T> T assemble(Object sourceObject, T destinationObject, String... ignoreProperties) {
      return this.assemble(sourceObject, destinationObject, new ConversionCache(), ignoreProperties);
   }

   public final <T> T assemble(Object sourceObject, T destinationObject, ConversionCache conversionCache,
         String... ignoreProperties) {
      if (sourceObject == null) {
         return null;
      }

      try {
         final ObjectConverter objectConverter = converterRegistry.getConverter(sourceObject, CglibUtils
               .resolveTargetClassIfProxied(destinationObject));
         return (T) objectConverter.convert(sourceObject, destinationObject, conversionCache, ignoreProperties);
      }
      catch (ClassNotFoundException e) {
         // This shouldn't be possible
         LOG.error(e.getMessage());
         throw new ConversionException("Could not load class", e);
      }
   }

   public void registerConverter(ObjectConverter<?, ?> objectConverter) {
      this.converterRegistry.registerConverter(objectConverter);
   }

   public boolean converterExists(Class<?> sourceClass, Class<?> destinationClass) {

      return this.converterRegistry.converterExists(sourceClass, destinationClass);
   }

   public ObjectConverter<?, ?> getConverter(Object sourceObject, Class<?> destinationClass) {
      return this.converterRegistry.getConverter(sourceObject, destinationClass);
   }

}
