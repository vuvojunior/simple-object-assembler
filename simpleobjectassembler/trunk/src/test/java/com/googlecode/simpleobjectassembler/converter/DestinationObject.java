package com.googlecode.simpleobjectassembler.converter;

import java.util.List;
import java.util.Set;

public class DestinationObject {

   private String string;

   private String differentNameDestination;

   private NestedDestinationObject nestedObjectDifferentNameDestination;

   private NestedDestinationObject nestedObject;

   private List<NestedDestinationObject> nestedObjectList;

   private Set<NestedDestinationObject> nestedObjectSet;

   private List<NestedDestinationObject> nestedObjectCollection;

   private List<NestedDestinationObject> nestedObjectListDifferentNameDestination;

   public String getString() {
      return string;
   }

   public void setString(String string) {
      this.string = string;
   }

   public NestedDestinationObject getNestedObject() {
      return nestedObject;
   }

   public void setNestedObject(NestedDestinationObject nestedObject) {
      this.nestedObject = nestedObject;
   }

   public List<NestedDestinationObject> getNestedObjectList() {
      return nestedObjectList;
   }

   public void setNestedObjectList(List<NestedDestinationObject> nestedObjectList) {
      this.nestedObjectList = nestedObjectList;
   }

   public Set<NestedDestinationObject> getNestedObjectSet() {
      return nestedObjectSet;
   }

   public void setNestedObjectSet(Set<NestedDestinationObject> nestedObjectSet) {
      this.nestedObjectSet = nestedObjectSet;
   }

   public String getDifferentNameDestination() {
      return differentNameDestination;
   }

   public void setDifferentNameDestination(String differentNameDestination) {
      this.differentNameDestination = differentNameDestination;
   }

   public NestedDestinationObject getNestedObjectDifferentNameDestination() {
      return nestedObjectDifferentNameDestination;
   }

   public void setNestedObjectDifferentNameDestination(NestedDestinationObject nestedObjectDifferentNameDestination) {
      this.nestedObjectDifferentNameDestination = nestedObjectDifferentNameDestination;
   }

   public List<NestedDestinationObject> getNestedObjectListDifferentNameDestination() {
      return nestedObjectListDifferentNameDestination;
   }

   public void setNestedObjectListDifferentNameDestination(
         List<NestedDestinationObject> nestedObjectListDifferentNameDestination) {
      this.nestedObjectListDifferentNameDestination = nestedObjectListDifferentNameDestination;
   }

   public List<NestedDestinationObject> getNestedObjectCollection() {
      return nestedObjectCollection;
   }

   public void setNestedObjectCollection(List<NestedDestinationObject> nestedObjectCollection) {
      this.nestedObjectCollection = nestedObjectCollection;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((string == null) ? 0 : string.hashCode());
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
      DestinationObject other = (DestinationObject) obj;
      if (string == null) {
         if (other.string != null)
            return false;
      }
      else if (!string.equals(other.string))
         return false;
      return true;
   }

}
