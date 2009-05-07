package com.googlecode.simpleobjectassembler.converter;

import java.util.Set;

import com.googlecode.simpleobjectassembler.converter.AbstractObjectConverter;
import com.googlecode.simpleobjectassembler.converter.ConverterFieldMapping;

public class SourceToDestinationWithDifferentFieldNameObjectConverter extends AbstractObjectConverter<SourceObject, DestinationObject> {

   
   @Override
   public Set<ConverterFieldMapping> customConverterFieldMappings() {
      final Set<ConverterFieldMapping> fieldMappings = super.customConverterFieldMappings();
      fieldMappings.add(new ConverterFieldMapping("differentNameSource", "differentNameDestination"));
      fieldMappings.add(new ConverterFieldMapping("nestedObjectDifferentNameSource", "nestedObjectDifferentNameDestination"));
      fieldMappings.add(new ConverterFieldMapping("nestedObjectListDifferentNameSource", "nestedObjectListDifferentNameDestination"));
      return fieldMappings;
   
   }
   
}
