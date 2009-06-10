package com.googlecode.simpleobjectassembler.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to tag dtos that are a direct mapping of a persistent entity. 
 * Requires the specification of the property on the dto that holds the 
 * identifier for the entity so it can be used to look up the entity 
 * using Hibernate or JPA.
 *  
 * @author robmonie
 *
 */
@Target( {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EntityDto {

    /**
     * The name of the property that holds the identifier for this class. Use
     * 
     * @return String validatorName
     */
    String id();

    
}
