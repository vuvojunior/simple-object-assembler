package com.googlecode.simpleobjectassembler.beans;

import java.util.Map;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.core.convert.TypeDescriptor;

/**
 * A property accessor that will attempt to use field access for getting
 * property values. If direct field access fails then falls back to getter based
 * access.
 * 
 * @author robmonie
 * 
 */
public class FallbackPropertyAccessor implements PropertyAccessor {


   private PropertyAccessor directFieldAccessor;

   private PropertyAccessor beanWrapper;

   public FallbackPropertyAccessor(Object target) {
      this.directFieldAccessor = new DirectFieldAccessor(target);
      this.beanWrapper = new BeanWrapperImpl(target);
   }

   public Object getPropertyValue(final String propertyName) {
      try {
         return beanWrapper.getPropertyValue(propertyName);
      }
      catch (BeansException e) {
         return directFieldAccessor.getPropertyValue(propertyName);
      }
   }

   public Class getPropertyType(String propertyName) throws BeansException {
      Class type = directFieldAccessor.getPropertyType(propertyName);
      if (type == null) {
         type = beanWrapper.getPropertyType(propertyName);
      }

      return type;
   }

   public TypeDescriptor getPropertyTypeDescriptor(String s) throws BeansException {
      return directFieldAccessor.getPropertyTypeDescriptor(s); 
   }

   public boolean isReadableProperty(String propertyName) {
      return directFieldAccessor.isReadableProperty(propertyName) || beanWrapper.isReadableProperty(propertyName);
   }

   public boolean isWritableProperty(String propertyName) {
      return directFieldAccessor.isWritableProperty(propertyName);
   }

   public void setPropertyValue(PropertyValue pv) throws BeansException {
      directFieldAccessor.setPropertyValue(pv);
   }

   public void setPropertyValue(String propertyName, Object value) throws BeansException {
      directFieldAccessor.setPropertyValue(propertyName, value);
   }

   public void setPropertyValues(Map map) throws BeansException {
      directFieldAccessor.setPropertyValues(map);
   }

   public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid)
         throws BeansException {
      directFieldAccessor.setPropertyValues(pvs, ignoreUnknown, ignoreInvalid);
   }

   public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown) throws BeansException {
      directFieldAccessor.setPropertyValues(pvs, ignoreUnknown);
   }

   public void setPropertyValues(PropertyValues pvs) throws BeansException {
      directFieldAccessor.setPropertyValues(pvs);
   }

}
