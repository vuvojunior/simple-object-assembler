package com.googlecode.simpleobjectassembler.converter.mapping;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.PropertyAccessor;
import org.springframework.util.StringUtils;

import com.googlecode.simpleobjectassembler.converter.cache.CachingObjectAssembler;
import com.googlecode.simpleobjectassembler.converter.cache.ConversionCache;


public abstract class AbstractPropertyMapper implements PropertyMapper {

   
   /* (non-Javadoc)
    * @see com.googlecode.simpleobjectassembler.converter.PropertyMapper#mapProperties(java.util.List, java.util.Set, org.springframework.beans.PropertyAccessor, org.springframework.beans.PropertyAccessor, com.googlecode.simpleobjectassembler.converter.ConversionCache)
    */
   public abstract void mapProperties(List<PropertyDescriptorPair> conversionCandidates,
         IgnoreSet explicitIgnoreSet, 
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
    * @param explicitIgnoreSet
    * @return
    */
   protected String[] getNestedPropertyExclusions(String propertyBase, IgnoreSet explicitIgnoreSet) {
      final List<String> nestedProperties = new ArrayList<String>();
      for (String property : explicitIgnoreSet.getSet()) {

         final String nestedPropertyPrefix = propertyBase + ".";

         if (property.startsWith(nestedPropertyPrefix)) {
            nestedProperties.add(StringUtils.replace(property, nestedPropertyPrefix, ""));
         }

      }
      return nestedProperties.toArray(new String[] {});
   }
   
}
