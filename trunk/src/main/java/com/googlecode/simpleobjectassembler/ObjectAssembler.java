package com.googlecode.simpleobjectassembler;

import com.googlecode.simpleobjectassembler.converter.dao.EntityDao;
import com.googlecode.simpleobjectassembler.converter.mapping.Exclusions;

/**
 * <p>
 * An assembler with a registry of converters that are registered against source
 * and destination types. By Default, all properties with the same name and type
 * will be mapped from the source object to the destination object. Where they
 * have the same name but a different type, the assembler will look up it's
 * {@link ConverterRegistry} for a converter for that combination of source and
 * destination types. When searching the registry for converters for a given
 * source and destination type, if no converter is found, the registry will
 * continue searching against all superclasses of the source object until one is
 * found or the searches reaches {@link Object}.
 * </p>
 * <p>
 * In order to prevent an object graph from being mapped in it's entirety, the
 * assembler can be passed an array of fields to ignore. These fields can be
 * nested in which case they are defined using dot "." notation.
 * </p>
 * <p>
 * For example, on a source object with a collection of children, you could
 * ignore mapping of all child "lastName" properties while still mapping other
 * child fields with an ignore parameter of:
 * </p>
 * <code>new String[]{"children.lastName"}</code>
 *
 * @author robmonie
 */
public interface ObjectAssembler extends ConverterRegistry {

   /**
    * Assemble the source object to the destination class by looking up the
    * assembler registry for a converter of this type combination.
    *
    * @param <T>
    * @param sourceObject
    * @param destinationClass
    * @return
    */
   <T> T assemble(Object sourceObject, Class<T> destinationClass);

   /**
    * <p>
    * Assemble the source object to the destination class by looking up the
    * assembler registry for a converter of this type combination. has the
    * option of passing an array of field paths to ignore. Supports nested paths
    * including collections. When a field path refers to a collection, it
    * applies to all objects within the collection. It is not possible to target
    * certain collection entries by index.
    * </p>
    * <p>
    * A wildcard string "*" can be specified for the ignoreProperties argument
    * if no properties are to be mapped under a given path. This is useful in
    * situations such as when the converter is intended to simply populate /
    * create a destination object by looking it up from the database but not
    * incur the overhead of populating any of the fields as they are known not
    * to change within the context.
    * </p>
    *
    * @param <T>
    * @param sourceObject
    * @param destinationClass
    * @param ignoreProperties
    * @return
    */
   <T> T assemble(Object sourceObject, Class<T> destinationClass, String... ignoreProperties);

   /**
    * <p>
    * Assemble the source object to the destination class by looking up the
    * assembler registry for a converter of this type combination. has the
    * option of passing an array of field paths to ignore. Supports nested paths
    * including collections. When a field path refers to a collection, it
    * applies to all objects within the collection. It is not possible to target
    * certain collection entries by index.
    * </p>
    * <p>
    * A wildcard string "*" can be specified for the ignoreProperties argument
    * if no properties are to be mapped under a given path. This is useful in
    * situations such as when the converter is intended to simply populate /
    * create a destination object by looking it up from the database but not
    * incur the overhead of populating any of the fields as they are known not
    * to change within the context.
    * </p>
    *
    * @param <T>
    * @param sourceObject
    * @param destinationClass
    * @param exclusions
    * @return
    */
   <T> T assemble(Object sourceObject, Class<T> destinationClass, Exclusions exclusions);

/**
    * <p>
    * Map the fields from the first object to the second object.
    * </p>
    *
    * @param sourceObject
    * @param destinationObject
    */
   <T> T assemble(Object sourceObject, T destinationObject);


   /**
    * <p>
    * Map the fields from the first object to the second object, ignoring any
    * properties specified
    * </p>
    * <p>
    * A wildcard string "*" can be specified for the ignoreProperties argument
    * if no properties are to be mapped under a given path. This is useful in
    * situations such as when the converter is intended to simply populate /
    * create a destination object but not incur the overhead of populating
    * any of the fields as they are known not to change within the context.
    * </p>
    * <p>
    * If mapping directly from collection to collection, the destination collection
    * object needs to be able to determine the collection type at run time. Due to
    * the nature of type erasure in generics, this is only possible if the generic type
    * information is specified at the class level. In practice this means that the following
    * will not work:
    * </p>
    * <p>
    * objectAssmbler.assemble(sourceList, new ArrayList<Destination>());
    * </p>
    * <p>
    *  ... but if you use an anonymous class that the defines the generic type information in the class
    * definiation it will work. This is very minor syntax difference:
    * </p>
    * <p>
    * objectAssmbler.assemble(sourceList, new ArrayList<Destination>(){});
    * </p>
    * <p>
    * While this isn't entirely intuitive, it is concise and it seems better than the alternatives at this stage.
    * </p>
    *
    * @param sourceObject
    * @param destinationObject
    * @param ignoreProperties
    */
   <T> T assemble(Object sourceObject, T destinationObject, String... ignoreProperties);

   /**
    * <p>
    * Map the fields from the first object to the second object, ignoring any
    * properties specified
    * </p>
    * <p>
    * A wildcard string "*" can be specified for the ignoreProperties argument
    * if no properties are to be mapped under a given path. This is useful in
    * situations such as when the converter is intended to simply populate /
    * create a destination object but not incur the overhead of populating
    * any of the fields as they are known not to change within the context.
    * </p>
    * <p>
    * If mapping directly from collection to collection, the destination collection
    * object needs to be able to determine the collection type at run time. Due to
    * the nature of type erasure in generics, this is only possible if the generic type
    * information is specified at the class level. In practice this means that the following
    * will not work:
    * </p>
    * <p>
    * objectAssmbler.assemble(sourceList, new ArrayList<Destination>());
    * </p>
    * <p>
    *  ... but if you use an anonymous class that the defines the generic type information in the class
    * definiation it will work. This is very minor syntax difference:
    * </p>
    * <p>
    * objectAssmbler.assemble(sourceList, new ArrayList<Destination>(){});
    * </p>
    * <p>
    * While this isn't entirely intuitive, it is concise and it seems better than the alternatives at this stage.
    * </p>
    *
    * @param sourceObject
    * @param destinationObject
    * @param exclusions
    */
   <T> T assemble(Object sourceObject, T destinationObject, Exclusions exclusions);

   /**
    * Should the assembler attempt to automatically convert objects based purely
    * on reflection if no specific converter is found.
    *
    * @return
    */
   boolean isAutomapWhenNoConverterFound();

   /**
    * Get the EntityDao, if one exists from the assembler.
    *
    * @return
    */
   EntityDao<? extends Object> getEntityDao();

}
