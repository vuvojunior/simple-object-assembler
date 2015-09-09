A simple object assembler with the ability to register converters for mapping objects of different types.  Provides basic configuration for simple cases with the option to override and extend behaviour in different ways for more complex mapping scenarios.  Simple Object Assembler has intentionally been designed to be free of xml configuration, opting for a runtime specification of property paths to ignore for a specific invocation and / or conversion logic written in Java.

For simple mapping scenarios, it is extremely easy to setup and use.  At it's simplest, with an instance of the SimpleObjectAssembler, you can convert one object and all it's properties to another type with one line:

`objectAssembler.assemble(sourceObject, Destination.class);`

This will map all matching properties including nested properties and ordered collections such as lists.

If you want to ignore certain fields for conversion you can pass the property paths to ignore at run-time.

`objectAssembler.assemble(sourceObject, Destination.class, "property1", "property2", "property3.nestedProperty");`

Once things get more complicated which they usually do, you can register converters for source and destination type combinations. These converters can define things such as:

  * A custom destination object factory method.
  * A custom conversion method.
  * A list of properties to ignore for all invocations.
  * Custom field mappings (objectA.field1 -> objectB.fieldX)
  * Whether to completely disable automatic field mappings and leave it all up to the custom conversion method.



A basic [Getting Started Guide](http://code.google.com/p/simple-object-assembler/wiki/GettingStartedGuide) is available in the wiki along with some background as to why and some of the success criteria for the first cut.

## Sponsors ##
Thanks to the following companies for donating open source licences to help in the development of Simple Object Assembler.


[![](http://www.jetbrains.com/img/logos/recommend_idea2.gif)](http://www.jetbrains.com/idea/)

[![](http://www.zeroturnaround.com/wp-content/themes/zeroturnaround2.0/gfx/logo.gif)](http://www.zeroturnaround.com/)


## Releases ##

### 1.0.0 (2011-01-26) ###
  * No changes in this release except that the version has been promoted to 1.0.0 and the code is now available from the central maven repo. A minor change to the groupId has been made to satisfy the repo requirements so the group id is now 'com.googlecode.simpleobjectassembler'.

### 0.5.6 (2011-01-25) ###
  * Fixed concurrency issue that can occur if multiple threads attempt to register a generic converter for the same source -> destination combination.

### 0.5.5 (2010-10-21) ###
  * Fixed issue with returning null from converter createDestinationObject method

### 0.5.4 (2010-10-21) ###
  * Resolved concurrency issue with converter registry initialisation. Could occur for apps that get immediately high volume upon startup or where clients invoke multiple concurrent requests.

### 0.5.3 (2010-10-15) ###
  * Modified to not create destination objects if found in cache. Previously destination objects were created regardless and discarded which caused problems if the destination object factory method created relationships with other objects in a hibernate / jpa context

### 0.5.2 ###

  * Not published

### 0.5.1 (2010-09-24) ###
  * Fixed a concurrency issue that occurred when caching initial field mappings for converters. This could potentially happen if on first use of a converter (after app startup) multiple concurrent uses of a converter occurred.

### 0.5.0 (2010-05-27) ###
  * A number of performance fixes, some significant when operating on large object graphs / numbers of properties
  * Added fix for converting between primitives and their autoboxed types
  * Upgraded to require Spring 3.x
  * Changed the way  objects of same type are converted so that they are actually converted not just passed as a reference
  * Now more explicit about the way primitives and their autoboxed equivilents are mapped
  * Dramatically reduced the number of times generic converters are created by registering them on first creation with the converter registry


### 0.4.4 (2009-07-29) ###
  * Fixed a bug that caused lazy hibernate collections to get 'touched' when mapping was set to ignore the property. Whiel this  didn't map the collection it did cause the collection to be loaded unnecessarily.

### 0.4.3 (2009-07-03) ###
  * Fixed a bug where setting `disableAutoMapping()` to true still tries to determine potential mappings for nested objects even though it will never actually map them. This could be problematic if the nested objects could not be mapped automatically and no converter was registered for them.

### 0.4.2 ###
  * Fixed a problem where when attempting to map between different collection types the assembler would not perform the mapping automatically


### 0.4.1 ###
**This is a small api change release that simplifies a couple of commonly used methods. It will require changes to any existing converters. Please see upgrade notes below.**
  * Changed the return type of the `alwaysIgnoreFields()` method on a converter from a `HashSet<String>` to an internally defined `IgnoreSet` which has a string varargs constructor and is chainable. This makes for simpler implementation as you can simply do this... `return new IgnoreSet("field1", "field2", "fieldN")`.
  * Removed the return type on the overridable `convert(...)` method of a converter as it was unnecessary. This method is now void.

Upgrade notes:

  1. Change all converters that implement the `convert(...)` method to return `void` instead of the destination object.
  1. Change all converters that implement the `alwaysIgnoreProperties()` method to return an `IgnoreSet`.

### 0.4.0 ###
  * You no longer need to implement the methods getSourceObjectClass() and getDestinationObjectClass() as these are inferred via generics.
  * You can now set the object assembler to attempt to convert objects automatically if no converter is explicitly registered for the source / destination classes. This is very convenient for basic situations where there are no special rules for the conversion as no converter needs to be written.
  * As an extension to the auto-mapping described above, you can also inject an EntityDao for looking up JPA / Hibernate mapped entities by id when converting to an entity mapped using the javax.persistence.Entity annotation. Basic Hibernate and JPA implementations have been provided but you can plug-in your own as required. While I haven't tested it with anything other than Hibernate, it's highly likely that this will work with any JPA implementation  but you'll need to inject your own. One caveat with this however is that in order to look up the entity using the supplied EntityDao, SimpleObjectAssembler needs to know what field in your dto holds the id of the entity to fetch. To achieve this, annotate your dto with the EntityDto annotation supplying the field to get the entity id from. Eg. @EntityDto(id = "id")
  * Fixed problem where source objects must have a property for every method prefixed with 'get'.  This meant that methods that did things like combine other properties or perform calculations would cause the assembler to fail. These 'computed' getters can now be mapped directly to destination objects.
  * Added default converters for mapping strings and numbers.


### 0.3.1 ###
Skipped straight to 0.3.1 due to a small bug in 0.3.0
  * Fixed a problem where defining exclusions directly in a converter using alwaysIgnoreProperties() for a source > destination field pair that didn't have a converter registered threw an exception on initialization.

### 0.2.0 ###
  * Internal code cleanup and refactoring
  * Using the `*` wildcard in property paths to ignore now also prevents the explicit user-defined `'convert'` method from being called.

### 0.1.0 ###
  * Initial release. See [Getting Started Guide](http://code.google.com/p/simple-object-assembler/wiki/GettingStartedGuide)
