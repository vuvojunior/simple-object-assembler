package com.googlecode.simpleobjectassembler.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * Basic map cache of objects converted during the current invocation of
 * {@link SimpleObjectAssembler}.
 * 
 * @author robmonie
 * 
 */
public class ConversionCache {

   private Map<SourceObjectAndDestinationTypeKey, Object> conversionCacheMap = new HashMap<SourceObjectAndDestinationTypeKey, Object>();

   public <T> T getConvertedObjectBySourceObjectAndDestinationType(Object source, Class<T> destinationType) {
      if (source == null || destinationType == null) {
         return null;
      }
      return (T) conversionCacheMap.get(new SourceObjectAndDestinationTypeKey(source, destinationType));
   }

   public void cacheConvertedObjectBySourceObject(Object source, Object destination, Class<?> destinationType) {
      if (source == null || destination == null) {
         return;
      }
      conversionCacheMap.put(new SourceObjectAndDestinationTypeKey(source, destinationType), destination);
   }

}
