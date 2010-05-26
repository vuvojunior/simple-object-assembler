package com.googlecode.simpleobjectassembler.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class PrimitiveTypeUtils {

   /*
    * Maps primitive types to their autoboxed object type
    */
   private static Map<Class, Class> primitiveToObjectTypeMap = new HashMap<Class, Class>();

   /*
    * contains all object types for primitives
    */
   private static Set<Class> autoboxedPrimitiveTypes = new HashSet<Class>();

   static {
      primitiveToObjectTypeMap.put(byte.class, Byte.class);
      primitiveToObjectTypeMap.put(short.class, Short.class);
      primitiveToObjectTypeMap.put(int.class, Integer.class);
      primitiveToObjectTypeMap.put(long.class, Long.class);
      primitiveToObjectTypeMap.put(double.class, Double.class);
      primitiveToObjectTypeMap.put(float.class, Float.class);
      primitiveToObjectTypeMap.put(boolean.class, Boolean.class);
      primitiveToObjectTypeMap.put(char.class, Character.class);

      autoboxedPrimitiveTypes.add(Byte.class);
      autoboxedPrimitiveTypes.add(Short.class);
      autoboxedPrimitiveTypes.add(Integer.class);
      autoboxedPrimitiveTypes.add(Long.class);
      autoboxedPrimitiveTypes.add(Float.class);
      autoboxedPrimitiveTypes.add(Double.class);
      autoboxedPrimitiveTypes.add(Boolean.class);
      autoboxedPrimitiveTypes.add(Character.class);
      autoboxedPrimitiveTypes.add(String.class);
   }


   /*
    * Get the autoboxed class type for a primitive
    */
   public static final Class getAutoboxedTypeForPrimitive(Class primitiveType) {
      return primitiveToObjectTypeMap.get(primitiveType);
   }

   /*
    * Test whether the class is that of an autoboxed primitive
    */
   public static boolean isPrimitiveEquivilent(Class clazz) {
      return autoboxedPrimitiveTypes.contains(clazz);
   }


}
