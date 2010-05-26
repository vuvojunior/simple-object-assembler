package com.googlecode.simpleobjectassembler;

import com.googlecode.simpleobjectassembler.utils.PrimitiveTypeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.GenericCollectionTypeResolver;

import com.googlecode.simpleobjectassembler.converter.ConversionException;
import com.googlecode.simpleobjectassembler.converter.DefaultConverters;
import com.googlecode.simpleobjectassembler.converter.GenericConverter;
import com.googlecode.simpleobjectassembler.converter.ObjectConverter;
import com.googlecode.simpleobjectassembler.converter.mapping.Exclusions;
import com.googlecode.simpleobjectassembler.converter.mapping.MappingPaths;
import com.googlecode.simpleobjectassembler.converter.cache.CachingObjectAssembler;
import com.googlecode.simpleobjectassembler.converter.cache.ConversionCache;
import com.googlecode.simpleobjectassembler.converter.dao.EntityDao;
import com.googlecode.simpleobjectassembler.registry.ConverterRegistryImpl;
import com.googlecode.simpleobjectassembler.utils.CglibUtils;
import com.googlecode.simpleobjectassembler.utils.CollectionUtils;

import java.util.Collection;
import java.util.Iterator;

/**
 * Single implementation of the {@link ObjectAssembler} for assembling objects.
 * Uses an internal registry of converters supplied by the end developer that
 * are registered on app startup.
 */

public class SimpleObjectAssembler implements ObjectAssembler, CachingObjectAssembler {

   private static final Log LOG = LogFactory.getLog(SimpleObjectAssembler.class);

   private boolean automapWhenNoConverterFound = false;

   private EntityDao entityDao;

   private final ConverterRegistry converterRegistry = new ConverterRegistryImpl();


   public SimpleObjectAssembler() {
      for (ObjectConverter converter : new DefaultConverters(this).getConverters()) {
         registerConverter(converter);
      }
   }

   public <T> T assemble(Object sourceObject, Class<T> destinationClass) {
      return assemble(sourceObject, destinationClass, new ConversionCache(), new Exclusions());
   }


   public <T> T assemble(Object sourceObject, ConversionCache conversionCache, Class<T> destinationClass) {
      return assemble(sourceObject, destinationClass, conversionCache, new Exclusions());
   }

   public final <T> T assemble(Object sourceObject, Class<T> destinationClass, String... ignoreProperties) {
      return assemble(sourceObject, destinationClass, new ConversionCache(), MappingPaths.exclude(ignoreProperties));
   }

   public final <T> T assemble(Object sourceObject, Class<T> destinationClass, Exclusions exclusions) {
      return assemble(sourceObject, destinationClass, new ConversionCache(), exclusions);
   }


   private boolean equalPrimitiveEquivilentTypes(Class sourceType, Class destinationType) {
      if(sourceType.equals(destinationType) && PrimitiveTypeUtils.isPrimitiveEquivilent(sourceType)) {
         return true;
      }
      return false;
   }


   public final <T> T assemble(Object sourceObject, Class<T> destinationClass, ConversionCache conversionCache,
                               Exclusions exclusions) {
      if (sourceObject == null) {
         return null;
      }

      Class<?> sourceClass;
      try {
         sourceClass = CglibUtils.resolveTargetClassIfProxied(sourceObject);
      } catch (ClassNotFoundException e) {
         throw new ConversionException("Can't find class for source object: " + sourceObject.getClass().getName(), e);
      }

      if(equalPrimitiveEquivilentTypes(sourceClass, destinationClass)) {
         return (T) sourceObject;
      }


      ObjectConverter objectConverter = converterRegistry.getConverter(sourceObject, destinationClass);

      if (objectConverter == null && automapWhenNoConverterFound) {
         objectConverter = new GenericConverter((CachingObjectAssembler) this, sourceClass, destinationClass);
         this.registerConverter(objectConverter);
      } else if (objectConverter == null) {
         throw new ConversionException(sourceObject.getClass(), destinationClass);
      }

      return (T) objectConverter.convert(sourceObject, conversionCache, exclusions);
   }



   public final <T> T assemble(Object sourceObject, T destinationObject) {
      return this.assemble(sourceObject, destinationObject, new ConversionCache(), new Exclusions());
   }



   public final <T> T assemble(Object sourceObject, T destinationObject, String... ignoreProperties) {
      return this.assemble(sourceObject, destinationObject, new ConversionCache(), MappingPaths.exclude(ignoreProperties));
   }



   public <T> T assemble(Object sourceObject, T destinationObject, Exclusions exclusions) {
      return this.assemble(sourceObject, destinationObject, new ConversionCache(), exclusions);
   }



   public final <T> T assemble(Object sourceObject, T destinationObject, ConversionCache conversionCache,
                               Exclusions exclusions) {

      if (sourceObject == null) {
         return null;
      }

      if (CollectionUtils.isOrderedCollection(sourceObject) && CollectionUtils.isOrderedCollection(destinationObject)) {
         final Class destinationCollectionType = GenericCollectionTypeResolver.getCollectionType(((Collection)destinationObject).getClass());
         if (destinationCollectionType == null) {
            throw new ConversionException(new StringBuilder()
                  .append("You have attempted to convert between generic collections where the destination collection ")
                  .append("has lost it's generic type information due to erasure. It is possible to map to an instance of a generic collection using ")
                  .append("an anonymous class that contains the type information. For example instead of passing 'new ArrayList<Destination>()' ")
                  .append("as the destination object, pass 'new ArrayList<DestinationObject>() {} which creates an instance of an anonymous class ")
                  .append("while also preserving the generic type information.").toString());
         }


         final Collection destinationCollection = (Collection) destinationObject;
         final Collection sourceCollection = (Collection) sourceObject;

         for (Iterator it = sourceCollection.iterator(); it.hasNext();) {
            destinationCollection.add(assemble(it.next(), destinationCollectionType, exclusions));
         }

         return (T) destinationCollection;
      }


      try {
         final ObjectConverter objectConverter = converterRegistry.getConverter(sourceObject, CglibUtils
               .resolveTargetClassIfProxied(destinationObject));
         return (T) objectConverter.convert(sourceObject, destinationObject, conversionCache, exclusions);
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

   public boolean isAutomapWhenNoConverterFound() {
      return automapWhenNoConverterFound;
   }

   public void setAutomapWhenNoConverterFound(boolean automapWhenNoConverterFound) {
      this.automapWhenNoConverterFound = automapWhenNoConverterFound;
   }


   public EntityDao getEntityDao() {
      return entityDao;
   }


   public void setEntityDao(EntityDao entityDao) {
      this.entityDao = entityDao;
   }

}
