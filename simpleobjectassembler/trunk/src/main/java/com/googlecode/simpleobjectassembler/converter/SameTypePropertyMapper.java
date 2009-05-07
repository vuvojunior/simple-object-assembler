package com.googlecode.simpleobjectassembler.converter;

import java.util.List;
import java.util.Set;

import org.springframework.beans.PropertyAccessor;

public class SameTypePropertyMapper extends AbstractPropertyMapper {

   
   public void mapProperties(List<PropertyDescriptorPair> conversionCandidates,
         Set<String> explicitIgnoreSet, PropertyAccessor sourcePropertyAccessor,
         PropertyAccessor destinationPropertyAccessor, ConversionCache conversionCache, 
         CachingObjectAssembler objectAssembler) {

      for (PropertyDescriptorPair pdp : conversionCandidates) {
         final String sourcePropertyName = pdp.getSource().getName();
         final String destinationPropertyName = pdp.getDestination().getName();
         if (!explicitIgnoreSet.contains(destinationPropertyName)) {
            
            if(objectAssembler.converterExists(pdp.getSource().getPropertyType(), pdp.getDestination().getPropertyType())) {
               destinationPropertyAccessor.setPropertyValue(destinationPropertyName, objectAssembler.assemble(sourcePropertyAccessor
                  .getPropertyValue(sourcePropertyName), pdp.getDestination().getPropertyType()));
            } else {
            
            destinationPropertyAccessor.setPropertyValue(destinationPropertyName, sourcePropertyAccessor
                  .getPropertyValue(sourcePropertyName));
            }
         }
      }

   }

}
