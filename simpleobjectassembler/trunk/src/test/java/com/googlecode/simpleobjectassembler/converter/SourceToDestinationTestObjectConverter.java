package com.googlecode.simpleobjectassembler.converter;

import com.googlecode.simpleobjectassembler.converter.AbstractObjectConverter;

public class SourceToDestinationTestObjectConverter extends AbstractObjectConverter<SourceObject, DestinationObject> {

   public Class<DestinationObject> getDestinationObjectClass() {
      return DestinationObject.class;
   }

   public Class<SourceObject> getSourceObjectClass() {
      return SourceObject.class;
   }

}
