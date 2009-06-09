package com.googlecode.simpleobjectassembler.converter.dao;

import java.io.Serializable;

import org.springframework.orm.jpa.support.JpaDaoSupport;

public class JpaEntityDao<T> extends JpaDaoSupport implements EntityDao<T> {

   public T findById(final Class<T> clazz, final Serializable id) {

      return getJpaTemplate().find(clazz, id);

   }

}
