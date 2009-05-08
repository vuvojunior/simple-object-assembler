package com.googlecode.simpleobjectassembler.converter;

import java.util.List;

public class ObjectWithSetterForAugmentedGetter {

   private String string;

   private String augmentedString;

   private Integer number;

   private List<String> list;

   public String getString() {
      return string;
   }

   public String getAugmentedString() {
      return augmentedString;
   }

   public Integer getNumber() {
      return number;
   }

   public List<String> getList() {
      return list;
   }

   public void setNumber(Integer number) {
      this.number = number;
   }

}
