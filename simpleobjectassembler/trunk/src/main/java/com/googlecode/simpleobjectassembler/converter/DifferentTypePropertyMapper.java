package com.googlecode.simpleobjectassembler.converter;

import java.util.List;
import java.util.Set;

import org.springframework.beans.PropertyAccessor;

public class DifferentTypePropertyMapper extends AbstractPropertyMapper {

   private final CachingObjectAssembler objectAssembler;
   
   
   public DifferentTypePropertyMapper(CachingObjectAssembler objectAssembler) {
      super();
      this.objectAssembler = objectAssembler;
   }


   public void mapProperties(List<PropertyDescriptorPair> conversionCandidates,
         Set<String> explicitIgnoreSet, PropertyAccessor sourcePropertyAccessor,
         PropertyAccessor destinationPropertyAccessor, ConversionCache conversionCache) {
      // check each conversion candidate and convert if not explicitly set to
      // ignore
      for (PropertyDescriptorPair pdp : conversionCandidates) {
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
}
