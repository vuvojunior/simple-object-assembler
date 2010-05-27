package com.googlecode.simpleobjectassembler.converter;

import java.util.ArrayList;
import java.util.List;

public class NestedSourceObject {

   private String string;

   private String otherString;
   
   private SourceObject parent;

   private List<NestedSourceObject> nested = new ArrayList<NestedSourceObject>();

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

   public List<NestedSourceObject> getNested() {
      return nested;
   }

   public void setNested(List<NestedSourceObject> nested) {
      this.nested = nested;
   }

   public void addNested(NestedSourceObject nested) {
      this.nested.add(nested);
   }
}
