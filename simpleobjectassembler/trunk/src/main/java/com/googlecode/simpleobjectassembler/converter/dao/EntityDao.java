package com.googlecode.simpleobjectassembler.converter.dao;


public interface EntityDao<T> {
   
   T findById(final Class <T> clazz, final Long id);
   
}
