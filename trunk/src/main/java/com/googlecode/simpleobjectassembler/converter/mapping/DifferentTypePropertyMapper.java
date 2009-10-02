package com.googlecode.simpleobjectassembler.converter.mapping;

import java.util.List;
import java.util.Set;

import org.springframework.beans.PropertyAccessor;

import com.googlecode.simpleobjectassembler.converter.cache.CachingObjectAssembler;
import com.googlecode.simpleobjectassembler.converter.cache.ConversionCache;

public class DifferentTypePropertyMapper extends AbstractPropertyMapper {

   public void mapProperties(List<PropertyDescriptorPair> conversionCandidates, Exclusions explicitExclusions,
         PropertyAccessor sourcePropertyAccessor, PropertyAccessor destinationPropertyAccessor,
         ConversionCache conversionCache, CachingObjectAssembler objectAssembler) {

      // check each conversion candidate and convert if not explicitly set to
      // exclude
      for (PropertyDescriptorPair pdp : conversionCandidates) {
         final String destinationPropertyName = pdp.getDestination().getName();
         final String sourcePropertyName = pdp.getSource().getName();
         if (!explicitExclusions.contains(destinationPropertyName)) {
            
            final Class<?> destinationType = destinationPropertyAccessor.getPropertyType(destinationPropertyName);
            final Object nestedSourceObject= sourcePropertyAccessor.getPropertyValue(sourcePropertyName);

            Object convertedValue = null;
            if (nestedSourceObject != null && convertedValue == null) {
               final Set<String> nestedExclusions = getNestedPropertyExclusions(destinationPropertyName, explicitExclusions);
               convertedValue = objectAssembler.assemble(nestedSourceObject, destinationType, conversionCache,
                     new Exclusions(nestedExclusions));
            }

            destinationPropertyAccessor.setPropertyValue(destinationPropertyName, convertedValue);

         }
      }
   }
}
