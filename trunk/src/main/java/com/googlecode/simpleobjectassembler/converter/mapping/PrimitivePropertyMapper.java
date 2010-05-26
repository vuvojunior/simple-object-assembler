package com.googlecode.simpleobjectassembler.converter.mapping;

import java.util.List;

import org.springframework.beans.PropertyAccessor;

import com.googlecode.simpleobjectassembler.converter.cache.CachingObjectAssembler;
import com.googlecode.simpleobjectassembler.converter.cache.ConversionCache;

public class PrimitivePropertyMapper extends AbstractPropertyMapper {

   public void mapProperties(List<PropertyDescriptorPair> conversionCandidates, Exclusions explicitExclusions,
         PropertyAccessor sourcePropertyAccessor, PropertyAccessor destinationPropertyAccessor,
         ConversionCache conversionCache, CachingObjectAssembler objectAssembler) {

      for (PropertyDescriptorPair pdp : conversionCandidates) {
         final String sourcePropertyName = pdp.getSource().getName();
         final String destinationPropertyName = pdp.getDestination().getName();
         if (!explicitExclusions.contains(destinationPropertyName)) {

            /*
            if (objectAssembler.converterExists(pdp.getSource().getPropertyType(), pdp.getDestination()
                  .getPropertyType())) {
               destinationPropertyAccessor.setPropertyValue(destinationPropertyName, objectAssembler.assemble(
                     sourcePropertyAccessor.getPropertyValue(sourcePropertyName), pdp.getDestination()
                           .getPropertyType()));
            }
            // TODO: Around here we really want to be checking whether the property type is a 'primitive', or
            // primitive wrapper. If it's not then we should really be creating a new instance of the source
            // and converting it

            else {
            */

               destinationPropertyAccessor.setPropertyValue(destinationPropertyName, sourcePropertyAccessor
                     .getPropertyValue(sourcePropertyName));

            //}
         }
      }

   }

}
