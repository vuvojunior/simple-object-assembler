package com.googlecode.simpleobjectassembler.converter.cache;

import com.googlecode.simpleobjectassembler.ObjectAssembler;
import com.googlecode.simpleobjectassembler.converter.AbstractObjectConverter;
import com.googlecode.simpleobjectassembler.converter.ObjectConverter;
import com.googlecode.simpleobjectassembler.converter.mapping.Exclusions;

/**
 * Internal interface to be used when calling code is going to pass a conversion
 * cache into assemble methods. Primarily used by
 * {@link AbstractObjectConverter}
 * 
 * @author robmonie
 * 
 */
public interface CachingObjectAssembler extends ObjectAssembler {

   <T> T assemble(Object sourceObject, ConversionCache conversionCache, Class<T> destinationClass);

   <T> T assemble(Object sourceObject, Class<T> destinationClass, ConversionCache conversionCache,
         Exclusions exclusions);

   <T> T assemble(Object sourceObject, T destinationObject, ConversionCache conversionCache, Exclusions exclusions);

   void registerConverter(ObjectConverter<?, ?> objectConverter);

   boolean converterExists(Class<?> sourceClass, Class<?> destinationClass);

}
