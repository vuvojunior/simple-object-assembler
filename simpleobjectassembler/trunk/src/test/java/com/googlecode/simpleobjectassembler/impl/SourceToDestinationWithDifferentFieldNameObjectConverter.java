package com.googlecode.simpleobjectassembler.impl;

import java.util.Set;

public class SourceToDestinationWithDifferentFieldNameObjectConverter extends AbstractObjectConverter<SourceObject, DestinationObject> {

   
   @Override
   public Set<ConverterFieldMapping> customConverterFieldMappings() {
      final Set<ConverterFieldMapping> fieldMappings = super.customConverterFieldMappings();
      fieldMappings.add(new ConverterFieldMapping("differentNameSource", "differentNameDestination"));
      fieldMappings.add(new ConverterFieldMapping("nestedObjectDifferentNameSource", "nestedObjectDifferentNameDestination"));
      fieldMappings.add(new ConverterFieldMapping("nestedObjectListDifferentNameSource", "nestedObjectListDifferentNameDestination"));
      return fieldMappings;
   
   }
   
   public Class<DestinationObject> getDestinationObjectClass() {
      return DestinationObject.class;
   }

   public Class<SourceObject> getSourceObjectClass() {
      return SourceObject.class;
   }

}
