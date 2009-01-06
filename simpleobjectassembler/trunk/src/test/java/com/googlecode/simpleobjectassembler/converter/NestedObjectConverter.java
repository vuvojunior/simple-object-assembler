package com.googlecode.simpleobjectassembler.converter;

import com.googlecode.simpleobjectassembler.converter.AbstractObjectConverter;

public class NestedObjectConverter extends AbstractObjectConverter<NestedSourceObject, NestedDestinationObject> {

   public Class<NestedDestinationObject> getDestinationObjectClass() {
      return NestedDestinationObject.class;
   }

   public Class<NestedSourceObject> getSourceObjectClass() {
      return NestedSourceObject.class;
   }

}
