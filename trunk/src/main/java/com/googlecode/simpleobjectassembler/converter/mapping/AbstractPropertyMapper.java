package com.googlecode.simpleobjectassembler.converter.mapping;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.springframework.beans.PropertyAccessor;
import org.springframework.util.StringUtils;

import com.googlecode.simpleobjectassembler.converter.cache.CachingObjectAssembler;
import com.googlecode.simpleobjectassembler.converter.cache.ConversionCache;


public abstract class AbstractPropertyMapper implements PropertyMapper {

   
   /* (non-Javadoc)
    * @see com.googlecode.simpleobjectassembler.converter.PropertyMapper#mapProperties(java.util.List, java.util.Set, org.springframework.beans.PropertyAccessor, org.springframework.beans.PropertyAccessor, com.googlecode.simpleobjectassembler.converter.ConversionCache)
    */
   public abstract void mapProperties(List<PropertyDescriptorPair> conversionCandidates,
         Exclusions explicitExclusions,
         PropertyAccessor sourcePropertyAccessor,
         PropertyAccessor destinationPropertyAccessor, 
         ConversionCache conversionCache,
         CachingObjectAssembler objectAssembler);
   
   
   /**
    * Returns any nested properties rooted at the property base path. For
    * example, if the propertyBase is set to "address", exclusions such as
    * "address.postcode" would match, as would "address.state.name". These would
    * return the values of "postcode" & "state.name" respectively
    * 
    * @param explicitExclusions
    * @return
    */
   protected Set<String> getNestedPropertyExclusions(String propertyBase, Exclusions explicitExclusions) {
      final Set<String> nestedProperties = new HashSet<String>();
      for (String property : explicitExclusions.getSet()) {

         final String nestedPropertyPrefix = propertyBase + ".";

         if (property.startsWith(nestedPropertyPrefix)) {
            nestedProperties.add(StringUtils.replace(property, nestedPropertyPrefix, ""));
         }

      }
      
      return nestedProperties;
   }
   
}
