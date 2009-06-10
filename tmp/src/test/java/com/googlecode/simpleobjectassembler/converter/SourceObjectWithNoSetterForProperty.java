package com.googlecode.simpleobjectassembler.converter;


public class SourceObjectWithNoSetterForProperty {

   
   private String propertyWithoutSetter;

   
   public SourceObjectWithNoSetterForProperty(String propertyWithoutSetter) {
      super();
      this.propertyWithoutSetter = propertyWithoutSetter;
   }


   public String getPropertyWithoutSetter() {
      return propertyWithoutSetter;
   }
   
   
}
