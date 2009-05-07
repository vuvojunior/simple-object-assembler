package com.googlecode.simpleobjectassembler.converter.persistence;


public interface EntityDao<T> {
   
   T findById(final Class <T> clazz, final Long id);
   
}
