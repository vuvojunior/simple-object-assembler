package com.googlecode.simpleobjectassembler.utils;


import org.springframework.core.ResolvableType;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class CollectionUtils {

   private CollectionUtils() {
      // Don't want instances of this class
   }

   /*@SuppressWarnings("unchecked")
   public static <T> List<T> createFrom(final List sourceList) {
      final List<T> destinationList = new ArrayList<T>(sourceList.size());
      for (final Iterator iter = sourceList.iterator(); iter.hasNext();) {
         destinationList.add((T) iter.next());
      }
      return destinationList;
   } */

   @SuppressWarnings("unchecked")
   public static <T> Collection<T> createFrom(final Collection sourceCollection) {
      final Class collectionType = ResolvableType.forClass(sourceCollection.getClass()).asCollection().resolve();
      try {
         final Collection<T> destinationCollection = sourceCollection.getClass().newInstance();

         while (sourceCollection.iterator().hasNext()) {
            destinationCollection.add((T) sourceCollection.iterator().next());
         }
         return destinationCollection;
      } catch (InstantiationException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }

      return null;
   }


   public static boolean hasSameGenericCollectionType(PropertyDescriptor sourcePropertyDescriptor,
                                                      PropertyDescriptor destinationPropertyDescriptor) {

      final Class<?> genericSourceType = ResolvableType.forMethodReturnType(sourcePropertyDescriptor
            .getReadMethod()).resolve();
      final Class<?> genericDestinationType = ResolvableType.forMethodReturnType(destinationPropertyDescriptor.getReadMethod()).resolve();
      if (genericSourceType == null && genericDestinationType == null) {
         return true;
      } else if (genericSourceType == null || genericDestinationType == null) {
         return false;
      } else {
         return genericSourceType.equals(genericDestinationType);
      }
   }

   /**
    * Will retrieve an object by it's index regardless of collection type.
    * Beware that non indexed collections such as a hashset will not retrieve
    * values reliably by index .
    * <p/>
    * TODO: May want to throw an exception if attempting to retrieve a value by
    * index from an unordered collection.
    *
    * @param collection
    * @param index
    * @return
    */
   public static <T> T retrieveIndexedValueFromCollection(Collection<T> collection, int index) {

      if (collection instanceof List) {
         return ((List<T>) collection).get(index);
      } else {
         final Iterator<T> it = collection.iterator();
         for (int j = 0; it.hasNext(); j++) {
            if (j == index) {
               return it.next();
            }
         }
         return null;
      }
   }


   public static boolean isOrderedCollection(Object collection) {
      return List.class.isAssignableFrom(collection.getClass());
   }

   public static boolean isOrderedCollectionClass(Class clazz) {
      return List.class.isAssignableFrom(clazz);
   }
}
