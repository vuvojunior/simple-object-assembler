package com.googlecode.simpleobjectassembler.converter.mapping;

import java.util.List;
import java.util.Set;

import org.springframework.beans.PropertyAccessor;

import com.googlecode.simpleobjectassembler.converter.cache.CachingObjectAssembler;
import com.googlecode.simpleobjectassembler.converter.cache.ConversionCache;

public interface PropertyMapper {

   public abstract void mapProperties(List<PropertyDescriptorPair> conversionCandidates, 
         Set<String> explicitIgnoreSet,
         PropertyAccessor sourcePropertyAccessor, 
         PropertyAccessor destinationPropertyAccessor,
         ConversionCache conversionCache,
         CachingObjectAssembler objectAssembler);

}