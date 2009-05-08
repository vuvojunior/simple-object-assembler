package com.googlecode.simpleobjectassembler.converter.mapping;

import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.PropertyAccessor;

import com.googlecode.simpleobjectassembler.converter.cache.CachingObjectAssembler;
import com.googlecode.simpleobjectassembler.converter.cache.ConversionCache;

public class DifferentTypePropertyMapper extends AbstractPropertyMapper {

   public void mapProperties(List<PropertyDescriptorPair> conversionCandidates, Set<String> explicitIgnoreSet,
         PropertyAccessor sourcePropertyAccessor, PropertyAccessor destinationPropertyAccessor,
         ConversionCache conversionCache, CachingObjectAssembler objectAssembler) {

      // check each conversion candidate and convert if not explicitly set to
      // ignore
      for (PropertyDescriptorPair pdp : conversionCandidates) {
         final String destinationPropertyName = pdp.getDestination().getName();
         final String sourcePropertyName = pdp.getSource().getName();
         if (!explicitIgnoreSet.contains(destinationPropertyName)) {
            
            final Class<?> destinationType = destinationPropertyAccessor.getPropertyType(destinationPropertyName);
            final Object nestedSourceObject= sourcePropertyAccessor.getPropertyValue(sourcePropertyName);

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
