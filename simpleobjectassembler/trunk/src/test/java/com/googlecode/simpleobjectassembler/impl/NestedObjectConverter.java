package com.googlecode.simpleobjectassembler.impl;

public class NestedObjectConverter extends AbstractObjectConverter<NestedSourceObject, NestedDestinationObject> {

   public Class<NestedDestinationObject> getDestinationObjectClass() {
      return NestedDestinationObject.class;
   }

   public Class<NestedSourceObject> getSourceObjectClass() {
      return NestedSourceObject.class;
   }

}
