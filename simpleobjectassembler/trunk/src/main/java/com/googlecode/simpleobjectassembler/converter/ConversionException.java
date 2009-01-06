package com.googlecode.simpleobjectassembler.converter;

public class ConversionException extends RuntimeException {

   private static final long serialVersionUID = -5475721016563077825L;

   public ConversionException(ConverterMappingKey converterMappingKey) {
      super("No converter found for converting: " + converterMappingKey.toString());
   }

   public ConversionException(Class<?> sourceClass, Class<?> destinationClass) {
      super("No converter found for converting: " + sourceClass.getName() + " to " + destinationClass.getName());
   }

   public ConversionException(String message) {
      super(message);
   }
   
   public ConversionException(String message, Throwable cause) {
      super(message, cause);
   }

}
