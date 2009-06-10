package com.googlecode.simpleobjectassembler.converter;

public class NestedSourceObject {

   private String string;

   private String otherString;
   
   private SourceObject parent;

   public NestedSourceObject(String string) {
      super();
      this.string = string;
   }



   public String getString() {
      return string;
   }



   public String getOtherString() {
      return otherString;
   }

   public void setOtherString(String otherString) {
      this.otherString = otherString;
   }

   
   public SourceObject getParent() {
      return parent;
   }

   
   public void setParent(SourceObject parent) {
      this.parent = parent;
   }

}
