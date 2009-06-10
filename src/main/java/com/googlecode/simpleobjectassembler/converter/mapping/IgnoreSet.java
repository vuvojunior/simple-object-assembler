package com.googlecode.simpleobjectassembler.converter.mapping;

import java.util.HashSet;
import java.util.Set;


public class IgnoreSet {

   private final Set<String> properties = new HashSet<String>();

   public IgnoreSet() {
     
   }
   
   public IgnoreSet(String... properties) {
      for(String property : properties) {
         this.properties.add(property);
      }
   }
   
   public IgnoreSet add(String property) {
      this.properties.add(property);
      return this;
   }
   
   public IgnoreSet remove(String property) {
      this.properties.remove(property);
      return this;
   }
   
   public IgnoreSet empty() {
      this.properties.clear();
      return this;
   } 
   
   public Set<String> getSet() {
      return this.properties;
   }

   public boolean contains(String value) {
      return this.properties.contains(value);
   }
   
}
