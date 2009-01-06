package com.googlecode.simpleobjectassembler.converter;

import com.googlecode.simpleobjectassembler.ObjectAssembler;

/**
 * Internal interface to be used when calling code is going to pass a conversion cache into assemble methods. Primarily used by
 * {@link AbstractObjectConverter}
 * 
 * @author robmonie
 * 
 */
public interface CachingObjectAssembler extends ObjectAssembler {

   <T> T assemble(Object sourceObject, ConversionCache conversionCache, Class<T> destinationClass);

   <T> T assemble(Object sourceObject, Class<T> destinationClass, ConversionCache conversionCache, String... ignoreProperties);

   <T> T assemble(Object sourceObject, T destinationObject, ConversionCache conversionCache, String... ignoreProperties);

   void registerConverter(ObjectConverter<?, ?> objectConverter);

   boolean converterExists(Class<?> sourceClass, Class<?> destinationClass);

}
