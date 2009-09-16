package com.googlecode.simpleobjectassembler;

import com.googlecode.simpleobjectassembler.converter.ObjectConverter;

/**
 * Converter registry
 * 
 * @author robmonie
 * 
 */
public interface ConverterRegistry {

	/**
	 * Get a converter from the registry
	 * 
	 * @param sourceObject
	 * @param destinationClass
	 * @return
	 */
	ObjectConverter<?, ?> getConverter(final Object sourceObject,
			final Class<?> destinationClass);

	/**
	 * Register a converter in the registry. Typically converters are registered
	 * on app startup by calling back on this method post construction.
	 * 
	 * @param converter
	 */
	void registerConverter(ObjectConverter<?, ?> converter);

	/**
	 * Check if a converter exists in the registry
	 * 
	 * @param sourceClass
	 * @param destinationClass
	 * @return
	 */
	boolean converterExists(Class<?> sourceClass, Class<?> destinationClass);

}
