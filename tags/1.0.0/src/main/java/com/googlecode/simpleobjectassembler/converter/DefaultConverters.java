package com.googlecode.simpleobjectassembler.converter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.googlecode.simpleobjectassembler.converter.cache.CachingObjectAssembler;

public class DefaultConverters {

   private Set<ObjectConverter> converters;

   private CachingObjectAssembler objectAssembler;

   public DefaultConverters(CachingObjectAssembler objectAssembler) {

      this.converters = new HashSet<ObjectConverter>();

      // ----- String to number converters

      converters.add(new AbstractObjectConverter<String, Byte>() {

         public Byte createDestinationObject(String string) {
            if (!StringUtils.hasText(string)) {
               return null;
            }
            return Byte.valueOf(string);
         }
      });

      converters.add(new AbstractObjectConverter<String, Short>() {

         public Short createDestinationObject(String string) {
            if (!StringUtils.hasText(string)) {
               return null;
            }
            return Short.valueOf(string);
         }
      });

      converters.add(new AbstractObjectConverter<String, Integer>() {

         public Integer createDestinationObject(String string) {
            if (!StringUtils.hasText(string)) {
               return null;
            }
            return Integer.valueOf(string);
         }
      });

      converters.add(new AbstractObjectConverter<String, Long>() {

         public Long createDestinationObject(String string) {
            if (!StringUtils.hasText(string)) {
               return null;
            }
            return Long.valueOf(string);
         }
      });

      converters.add(new AbstractObjectConverter<String, Float>() {

         public Float createDestinationObject(String string) {
            if (!StringUtils.hasText(string)) {
               return null;
            }
            return Float.valueOf(string);
         }
      });

      converters.add(new AbstractObjectConverter<String, Double>() {

         public Double createDestinationObject(String string) {
            if (!StringUtils.hasText(string)) {
               return null;
            }
            return Double.valueOf(string);
         }
      });

      converters.add(new AbstractObjectConverter<String, BigDecimal>() {

         public BigDecimal createDestinationObject(String string) {
            if (!StringUtils.hasText(string)) {
               return null;
            }
            return BigDecimal.valueOf(Double.valueOf(string));
         }
      });

      // ----- Handle all numbers as Number

      converters.add(new AbstractObjectConverter<Number, String>() {

         public String createDestinationObject(Number number) {
            if (number == null) {
               return null;
            }
            // TODO: //might want to support formatters here at some stage
            return number.toString();
         }
      });
      
      //inject assembler into each converter
      for (ObjectConverter converter : this.converters) {
         converter.setObjectAssembler(objectAssembler);
      }
   }

   public Set<ObjectConverter> getConverters() {
      return converters;
   }

}
