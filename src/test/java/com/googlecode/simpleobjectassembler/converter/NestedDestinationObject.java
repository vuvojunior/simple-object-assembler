package com.googlecode.simpleobjectassembler.converter;

import java.util.ArrayList;
import java.util.List;

public class NestedDestinationObject {

   private String string;

   private String otherString;
   
   private DestinationObject parent;
   private List<NestedDestinationObject> nested = new ArrayList<NestedDestinationObject>();


   public String getString() {
      return string;
   }

   public void setString(String string) {
      this.string = string;
   }

   public String getOtherString() {
      return otherString;
   }

   public void setOtherString(String otherString) {
      this.otherString = otherString;
   }

   
   public DestinationObject getParent() {
      return parent;
   }

   
   public void setParent(DestinationObject parent) {
      this.parent = parent;
   }

   public List<NestedDestinationObject> getNested() {
      return nested;
   }

   public void setNested(List<NestedDestinationObject> nested) {
      this.nested = nested;
   }
}
