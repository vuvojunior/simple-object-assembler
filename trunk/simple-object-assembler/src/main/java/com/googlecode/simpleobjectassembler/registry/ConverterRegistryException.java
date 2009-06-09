package com.googlecode.simpleobjectassembler.registry;

import com.googlecode.simpleobjectassembler.converter.mapping.ConverterMappingKey;

public class ConverterRegistryException extends RuntimeException {

   private static final long serialVersionUID = 1L;

   public ConverterRegistryException(ConverterMappingKey converterMappingKey) {
      super("Attempting to register a duplicate converter for " + converterMappingKey.toString()
            + ". Please ensure that there is only a single converter registered per source / destination type.");
   }

   public ConverterRegistryException(String message) {
      super(message);
   }

   public ConverterRegistryException(String message, Throwable cause) {
      super(message, cause);
   }

}
