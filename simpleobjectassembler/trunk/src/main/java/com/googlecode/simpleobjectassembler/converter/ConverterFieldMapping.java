package com.googlecode.simpleobjectassembler.converter;

/**
 * Represents a mapping of a source field name to a destination field name. Use
 * when automapping should be applied to two fields of different names on source
 * and destination objects
 * 
 * @author robmonie
 * 
 */
public class ConverterFieldMapping {

   private final String sourcePropertyName;

   private final String destinationPropertyName;

   public String getSourcePropertyName() {
      return sourcePropertyName;
   }

   public String getDestinationPropertyName() {
      return destinationPropertyName;
   }

   public ConverterFieldMapping(String destinationPropertyName, String sourcePropertyName) {
      super();
      this.destinationPropertyName = destinationPropertyName;
      this.sourcePropertyName = sourcePropertyName;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((destinationPropertyName == null) ? 0 : destinationPropertyName.hashCode());
      result = prime * result + ((sourcePropertyName == null) ? 0 : sourcePropertyName.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ConverterFieldMapping other = (ConverterFieldMapping) obj;
      if (destinationPropertyName == null) {
         if (other.destinationPropertyName != null)
            return false;
      }
      else if (!destinationPropertyName.equals(other.destinationPropertyName))
         return false;
      if (sourcePropertyName == null) {
         if (other.sourcePropertyName != null)
            return false;
      }
      else if (!sourcePropertyName.equals(other.sourcePropertyName))
         return false;
      return true;
   }

}
