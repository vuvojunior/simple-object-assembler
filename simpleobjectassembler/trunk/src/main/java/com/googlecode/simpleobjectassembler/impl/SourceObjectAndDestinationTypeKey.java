package com.googlecode.simpleobjectassembler.impl;

/**
 * Used for caching sourceObject and destination type during conversion. Can be used to lookup previously converted objects to
 * prevent circular dependency induced infinite loops
 * 
 * @author robmonie
 * 
 */
public class SourceObjectAndDestinationTypeKey {

   public Object sourceObject;

   public Class<?> destinationType;

   public SourceObjectAndDestinationTypeKey(Object sourceObject, Class<?> destinationType) {
      super();
      this.sourceObject = sourceObject;
      this.destinationType = destinationType;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((destinationType == null) ? 0 : destinationType.getName().hashCode());
      result = prime * result + ((sourceObject == null) ? 0 : sourceObject.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      SourceObjectAndDestinationTypeKey other = (SourceObjectAndDestinationTypeKey) obj;
      if (destinationType == null) {
         if (other.destinationType != null) return false;
      } else if (!destinationType.getName().equals(other.destinationType.getName())) return false;
      if (sourceObject == null) {
         if (other.sourceObject != null) return false;
      } else if (!sourceObject.equals(other.sourceObject)) return false;
      return true;
   }

}
