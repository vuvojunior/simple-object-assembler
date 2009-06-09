package com.googlecode.simpleobjectassembler.converter.mapping;

import java.beans.PropertyDescriptor;


/**
 * Models a pair of property descriptors
 * 
 * @author robmonie
 * 
 */
public class PropertyDescriptorPair {

   private final PropertyDescriptor source;

   private final PropertyDescriptor destination;

   private final Class<?> genericDestinationCollectionType;

   public PropertyDescriptorPair(PropertyDescriptor source, PropertyDescriptor destination) {
      super();
      this.source = source;
      this.destination = destination;
      this.genericDestinationCollectionType = null;
   }

   public PropertyDescriptorPair(PropertyDescriptor source, PropertyDescriptor destination,
         Class<?> genericDestinationClass) {
      super();
      this.source = source;
      this.destination = destination;
      this.genericDestinationCollectionType = genericDestinationClass;

   }

   public Class<?> getGenericDestinationCollectionType() {
      return genericDestinationCollectionType;
   }

   public PropertyDescriptor getSource() {
      return source;
   }

   public PropertyDescriptor getDestination() {
      return destination;
   }

   public boolean destinationTypeIsGeneric() {
      return genericDestinationCollectionType != null;
   }

}
