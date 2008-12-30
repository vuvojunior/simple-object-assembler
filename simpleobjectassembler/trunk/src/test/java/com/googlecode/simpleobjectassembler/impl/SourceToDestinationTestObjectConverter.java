package com.googlecode.simpleobjectassembler.impl;

public class SourceToDestinationTestObjectConverter extends AbstractObjectConverter<SourceObject, DestinationObject> {

   public Class<DestinationObject> getDestinationObjectClass() {
      return DestinationObject.class;
   }

   public Class<SourceObject> getSourceObjectClass() {
      return SourceObject.class;
   }

}
