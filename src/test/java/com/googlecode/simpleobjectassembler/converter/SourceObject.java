package com.googlecode.simpleobjectassembler.converter;

import java.util.List;
import java.util.Set;

public class SourceObject {

   private String string;
   
   private String differentNameSource;

   private NestedSourceObject nestedObject;
   
   private NestedSourceObject nestedObjectDifferentNameSource;

   private List<NestedSourceObject> nestedObjectList;
   
   private Set<NestedSourceObject> nestedObjectCollection;
   
   private List<NestedSourceObject> nestedObjectListDifferentNameSource;

   private Set<NestedSourceObject> nestedObjectSet;

   public SourceObject() {
      
   }

   public SourceObject(String string) {
      this.string = string;
   }

   public String getString() {
      return string;
   }

   public void setString(String string) {
      this.string = string;
   }

   public NestedSourceObject getNestedObject() {
      return nestedObject;
   }

   public void setNestedObject(NestedSourceObject nestedObject) {
      this.nestedObject = nestedObject;
   }

   public List<NestedSourceObject> getNestedObjectList() {
      return nestedObjectList;
   }

   public void setNestedObjectList(List<NestedSourceObject> nestedObjectList) {
      this.nestedObjectList = nestedObjectList;
   }

   public Set<NestedSourceObject> getNestedObjectSet() {
      return nestedObjectSet;
   }

   public void setNestedObjectSet(Set<NestedSourceObject> nestedObjectSet) {
      this.nestedObjectSet = nestedObjectSet;
   }

   
   public String getDifferentNameSource() {
      return differentNameSource;
   }

   
   public void setDifferentNameSource(String differentNameSource) {
      this.differentNameSource = differentNameSource;
   }

   
   public NestedSourceObject getNestedObjectDifferentNameSource() {
      return nestedObjectDifferentNameSource;
   }

   
   public void setNestedObjectDifferentNameSource(NestedSourceObject nestedObjectDifferentNameSource) {
      this.nestedObjectDifferentNameSource = nestedObjectDifferentNameSource;
   }

   
   public List<NestedSourceObject> getNestedObjectListDifferentNameSource() {
      return nestedObjectListDifferentNameSource;
   }

   
   public void setNestedObjectListDifferentNameSource(List<NestedSourceObject> nestedObjectListDifferentNameSource) {
      this.nestedObjectListDifferentNameSource = nestedObjectListDifferentNameSource;
   }

   
   public Set<NestedSourceObject> getNestedObjectCollection() {
      return nestedObjectCollection;
   }

   
   public void setNestedObjectCollection(Set<NestedSourceObject> nestedObjectCollection) {
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
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      SourceObject other = (SourceObject) obj;
      if (string == null) {
         if (other.string != null) return false;
      } else if (!string.equals(other.string)) return false;
      return true;
   }

   
   
   

}
