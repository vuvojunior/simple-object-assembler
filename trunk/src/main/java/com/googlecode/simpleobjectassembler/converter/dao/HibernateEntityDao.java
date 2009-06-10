package com.googlecode.simpleobjectassembler.converter.dao;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HibernateEntityDao<T> implements EntityDao<T> {

   private SessionFactory sessionFactory;
   
   public HibernateEntityDao(SessionFactory sessionFactory) {
      this.sessionFactory = sessionFactory;
   }

   public T findById(final Class<T> clazz, final Serializable id) {

      final Session session = sessionFactory.getCurrentSession();
      return (T) session.get(clazz, id);

   }

}
