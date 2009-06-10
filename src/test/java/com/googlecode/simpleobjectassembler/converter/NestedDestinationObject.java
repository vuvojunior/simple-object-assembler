package com.googlecode.simpleobjectassembler.converter;

public class NestedDestinationObject {

   private String string;

   private String otherString;
   
   private DestinationObject parent;

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

}
