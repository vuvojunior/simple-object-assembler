package com.googlecode.simpleobjectassembler.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.PropertyAccessor;

import com.googlecode.simpleobjectassembler.utils.CollectionUtils;

public class CollectionPropertyMapper extends AbstractPropertyMapper {

   private final CachingObjectAssembler objectAssembler;
   
   
   public CollectionPropertyMapper(CachingObjectAssembler objectAssembler) {
      super();
      this.objectAssembler = objectAssembler;
   }


   /**
    * Convert collection properties using mapped converters where registered.
    * Destination collections of the same size as source collections will map
    * directly against the collection item of the same index. Null or empty
    * destination collections will be created.
    * 
    * TODO: RM Need to handle case where the destination collection size is
    * different to the source - add new or remove deleted items.
    * 
    * @param explicitIgnoreSet
    * @param sourcePropertyAccessor
    * @param destinationPropertyAccessor
    */
   public void mapProperties(List<PropertyDescriptorPair> conversionCandidates, final Set<String> explicitIgnoreSet,
         final PropertyAccessor sourcePropertyAccessor, final PropertyAccessor destinationPropertyAccessor,
         ConversionCache conversionCache) {
      for (PropertyDescriptorPair pdp : conversionCandidates) {
         final String sourcePropertyName = pdp.getSource().getName();
         final String destinationPropertyName = pdp.getDestination().getName();
         if (!explicitIgnoreSet.contains(destinationPropertyName)) {

            final Class<?> destinationCollectionType = destinationPropertyAccessor
                  .getPropertyType(destinationPropertyName);
            final Class<?> genericDestinationCollectionType = pdp.getGenericDestinationCollectionType();
            final Collection<?> sourceCollection = (Collection<?>) sourcePropertyAccessor
                  .getPropertyValue(sourcePropertyName);

            Collection destinationCollection = (Collection) destinationPropertyAccessor.getPropertyValue(pdp
                  .getDestination().getName());
            
            if (sourceCollection != null) {

               final String[] nestedExclusions = getNestedPropertyExclusions(destinationPropertyName, explicitIgnoreSet);

               if (destinationCollection != null && CollectionUtils.isOrderedCollection(destinationCollection)
                     && CollectionUtils.isOrderedCollection(sourceCollection)
                     && destinationCollection.size() == sourceCollection.size()) {

                  int i = 0;
                  for (final Iterator it = sourceCollection.iterator(); it.hasNext(); i++) {
                     final Object sourceObject = it.next();
                     final Object destinationObject = CollectionUtils.retrieveIndexedValueFromCollection(destinationCollection, i);
                     objectAssembler.assemble(sourceObject, destinationObject, conversionCache, nestedExclusions);
                  }
               }
               else {

                  // This is limiting but only expect lists and sets for now.
                  if (List.class.isAssignableFrom(destinationCollectionType)) {
                     destinationCollection = new ArrayList();
                  }
                  else if (Set.class.isAssignableFrom(destinationCollectionType)) {
                     destinationCollection = new HashSet();
                  }
                  else {
                     throw new IllegalArgumentException(
                           "Only support conversion of set and list collection types for now.");
                  }

                  for (final Iterator it = sourceCollection.iterator(); it.hasNext();) {
                     final Object convertedObject = objectAssembler.assemble(it.next(),
                           genericDestinationCollectionType, conversionCache, nestedExclusions);
                     destinationCollection.add(convertedObject);
                  }

               }
            }
            destinationPropertyAccessor.setPropertyValue(destinationPropertyName, destinationCollection);
         }
      }
   }
}
