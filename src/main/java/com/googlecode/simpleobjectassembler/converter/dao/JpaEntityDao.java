package com.googlecode.simpleobjectassembler.converter.dao;

import javax.persistence.EntityManagerFactory;
import java.io.Serializable;

public class JpaEntityDao<T> implements EntityDao<T> {

   EntityManagerFactory entityManagerFactory;

   public T findById(final Class<T> clazz, final Serializable id) {
      return entityManagerFactory.createEntityManager().find(clazz, id);
   }

}
