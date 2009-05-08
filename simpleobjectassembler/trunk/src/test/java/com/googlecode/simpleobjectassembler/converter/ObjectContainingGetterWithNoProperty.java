package com.googlecode.simpleobjectassembler.converter;

import java.util.ArrayList;
import java.util.List;

public class ObjectContainingGetterWithNoProperty {

   private String string;

   public String getString() {
      return string;
   }

   public void setString(String string) {
      this.string = string;
   }

   public String getAugmentedString() {
      return "augmented" + string;
   }

   public String getNumber() {
      return "22";
   }

   public List<String> getList() {
      List<String> list = new ArrayList<String>();
      list.add("string");
      return list;
   }

}
