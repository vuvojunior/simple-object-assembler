package com.googlecode.simpleobjectassembler.converter;

import javax.persistence.Entity;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.PropertyAccessor;
import org.springframework.core.annotation.AnnotationUtils;

import com.googlecode.simpleobjectassembler.annotation.EntityDto;
import com.googlecode.simpleobjectassembler.converter.cache.CachingObjectAssembler;
import com.googlecode.simpleobjectassembler.converter.dao.EntityDao;

public class GenericConverter extends AbstractObjectConverter<Object, Object> {

   private final EntityDao entityDao;  

   private final Class sourceObjectClass;

   private final Class destinationObjectClass;

   public GenericConverter(CachingObjectAssembler objectAssembler, Class sourceObjectClass, Class destinationObjectClass) {
      super();
      setObjectAssembler(objectAssembler);
      this.sourceObjectClass = sourceObjectClass;
      this.destinationObjectClass = destinationObjectClass;
      this.entityDao = objectAssembler.getEntityDao();
   }

   @Override
   public Object createDestinationObject(Object sourceObject) {

      if (entityDao != null) {

         final Entity entityAnnotation = (Entity) AnnotationUtils.findAnnotation(getDestinationObjectClass(),
               Entity.class);
         final EntityDto dtoAnnotation = (EntityDto) AnnotationUtils.findAnnotation(getSourceObjectClass(),
               EntityDto.class);

         if (entityAnnotation != null && dtoAnnotation != null) {
            final PropertyAccessor propertyAccessor = new DirectFieldAccessor(sourceObject);
            final Long destinationObjectId = (Long) propertyAccessor.getPropertyValue(dtoAnnotation.id());
            
            if(destinationObjectId != null) {
               return  entityDao.findById(getDestinationObjectClass(), destinationObjectId);
            }
         } else if (entityAnnotation != null && dtoAnnotation == null) {
               throw new ConversionException(
                     "Attempt to automatically convert an instance of "
                           + getSourceObjectClass()
                           + " to Entity of type "
                           + getDestinationObjectClass()
                           + " failed because the source object is not annotated with the EntityDto annotation."
                           + " Without this, the identifier for retrieving an instance of the destination "
                           + "entity cannot be resolved. Please annotate " + getSourceObjectClass()
                           + " with the EntityDto annotation or write a custom converter for this pair.");
            

         }

      }
      
      return super.createDestinationObject(sourceObject);
   }

   public Class getSourceObjectClass() {
      return sourceObjectClass;
   }

   public Class getDestinationObjectClass() {
      return destinationObjectClass;
   }

}
