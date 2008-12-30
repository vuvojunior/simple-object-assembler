package com.googlecode.simpleobjectassembler;

import com.googlecode.simpleobjectassembler.impl.ConversionCache;

/**
 * A converter for converting a source object to a destination type. Should be used in combination with a single instance of an
 * {@link ObjectAssembler} that holds a registry of converters. Developers generally wouldn't reference a converter directly and
 * should opt for using the assembler which will find the appropriate converter for the source object, destination class
 * combination.
 * 
 * @author robmonie
 * 
 * @param <SourceObjectClass>
 * @param <DestinationObjectClass>
 */
public interface ObjectConverter <SourceObjectClass, DestinationObjectClass> {

   /**
    * Convert the sourceObject to an instance of the destination object type, ignoring specific properties if supplied. A
    * wildcard string "*" can be specified for the ignoreProperties argument if no properties are to be mapped under a given
    * path. This is useful in situations such as when the converter is intended to simply populate / create a destination object
    * by looking it up from the database but not incur the overhead of populating any of the fields as they are known not to
    * change within the context.
    * 
    * @param sourceObject
    * @param ignoreProperties
    *           properties to ignore if autoMapIdenticalFields() returns true
    * @return
    */
   DestinationObjectClass convert(SourceObjectClass sourceObject, ConversionCache conversionCache, String[] ignoreProperties);

   /**
    * Convert the sourceObject to the destinationObject type
    * 
    * @param sourceObject
    * @return destinationObject
    */
   //DestinationObjectClass convert(SourceObjectClass sourceObject);

   /**
    * Map the source object to the destinationObject ignoring specific properties if supplied. A wildcard string "*" can be
    * specified for the ignoreProperties argument if no properties are to be mapped under a given path. This is useful in situations such as when
    * the converter is intended to simply populate / create a destination object by looking it up from the database but not
    * incur the overhead of populating any of the fields as they are known not to change within the context.
    * 
    * @param sourceObject
    * @param destinationObject
    * @param ignoreProperties
    * @return
    */
   DestinationObjectClass convert(SourceObjectClass sourceObject, DestinationObjectClass destinationObject, ConversionCache conversionCache,
         String[] ignoreProperties);

   /**
    * Returns the type of the source object that this converter acts upon. Required for run time evaluation of the generic type.
    * 
    * @return
    */
   Class<SourceObjectClass> getSourceObjectClass();

   /**
    * Returns the type of the destinationObject returned by this converter. Required for run time evaluation of the generic
    * type.
    * 
    * @return
    */
   Class<DestinationObjectClass> getDestinationObjectClass();

}
