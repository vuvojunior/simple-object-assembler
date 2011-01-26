package com.googlecode.simpleobjectassembler.converter.dao;

import java.io.Serializable;


public interface EntityDao<T> {
   
   T findById(final Class <T> clazz, final Serializable id);
   
}
