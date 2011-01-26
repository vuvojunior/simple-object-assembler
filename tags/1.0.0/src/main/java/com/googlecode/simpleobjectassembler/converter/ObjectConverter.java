package com.googlecode.simpleobjectassembler.converter;

import com.googlecode.simpleobjectassembler.ObjectAssembler;
import com.googlecode.simpleobjectassembler.converter.cache.CachingObjectAssembler;
import com.googlecode.simpleobjectassembler.converter.cache.ConversionCache;
import com.googlecode.simpleobjectassembler.converter.mapping.Exclusions;

/**
 * A converter for converting a source object to a destination type. Should be
 * used in combination with a single instance of an {@link ObjectAssembler} that
 * holds a registry of converters. Developers generally wouldn't reference a
 * converter directly and should opt for using the assembler which will find the
 * appropriate converter for the source object, destination class combination.
 *
 * @author robmonie
 *
 * @param <Source>
 * @param <Destination>
 */
public interface ObjectConverter<Source, Destination> {

   /**
    * Convert the source to an instance of the destination object type,
    * ignoring specific properties if supplied. A wildcard string "*" can be
    * specified for the ignoreProperties argument if no properties are to be
    * mapped under a given path. This is useful in situations such as when the
    * converter is intended to simply populate / create a destination object by
    * looking it up from the database but not incur the overhead of populating
    * any of the fields as they are known not to change within the context.
    *
    * @param source
    * @param conversionCache
    * @param exclusions
    *           properties to ignore if autoMapIdenticalFields() returns true
    * @return
    */
   Destination convert(Source source,
                                  ConversionCache conversionCache,
                                  Exclusions exclusions);

   /**
    * Map the source object to the destination ignoring specific
    * properties if supplied. A wildcard string "*" can be specified for the
    * ignoreProperties argument if no properties are to be mapped under a given
    * path. This is useful in situations such as when the converter is intended
    * to simply populate / create a destination object by looking it up from the
    * database but not incur the overhead of populating any of the fields as
    * they are known not to change within the context.
    *
    * @param source
    * @param destination
    * @param conversionCache
    * @param exclusions
    * @return
    */
   Destination convert(Source source,
                                  Destination destination,
                                  ConversionCache conversionCache,
                                  Exclusions exclusions);

   /**
    * Returns the type of the source object that this converter acts upon.
    * Required for run time evaluation of the generic type.
    *
    * @return
    */
   Class<Source> getSourceClass();

   /**
    * Returns the type of the destinationObject returned by this converter.
    * Required for run time evaluation of the generic type.
    *
    * @return
    */
   Class<Destination> getDestinationClass();


   void setObjectAssembler(CachingObjectAssembler objectAssembler);


}
