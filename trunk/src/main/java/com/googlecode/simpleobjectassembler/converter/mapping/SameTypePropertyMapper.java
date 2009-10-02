package com.googlecode.simpleobjectassembler.converter.mapping;

import java.util.List;

import org.springframework.beans.PropertyAccessor;

import com.googlecode.simpleobjectassembler.converter.cache.CachingObjectAssembler;
import com.googlecode.simpleobjectassembler.converter.cache.ConversionCache;

public class SameTypePropertyMapper extends AbstractPropertyMapper {

   public void mapProperties(List<PropertyDescriptorPair> conversionCandidates, Exclusions explicitExclusions,
         PropertyAccessor sourcePropertyAccessor, PropertyAccessor destinationPropertyAccessor,
         ConversionCache conversionCache, CachingObjectAssembler objectAssembler) {

      for (PropertyDescriptorPair pdp : conversionCandidates) {
         final String sourcePropertyName = pdp.getSource().getName();
         final String destinationPropertyName = pdp.getDestination().getName();
         if (!explicitExclusions.contains(destinationPropertyName)) {

            if (objectAssembler.converterExists(pdp.getSource().getPropertyType(), pdp.getDestination()
                  .getPropertyType())) {
               destinationPropertyAccessor.setPropertyValue(destinationPropertyName, objectAssembler.assemble(
                     sourcePropertyAccessor.getPropertyValue(sourcePropertyName), pdp.getDestination()
                           .getPropertyType()));
            }
            else {

               destinationPropertyAccessor.setPropertyValue(destinationPropertyName, sourcePropertyAccessor
                     .getPropertyValue(sourcePropertyName));

            }
         }
      }

   }

}
