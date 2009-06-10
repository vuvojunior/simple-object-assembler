package com.googlecode.simpleobjectassembler.converter.mapping;

import java.util.List;

import org.springframework.beans.PropertyAccessor;

import com.googlecode.simpleobjectassembler.converter.cache.CachingObjectAssembler;
import com.googlecode.simpleobjectassembler.converter.cache.ConversionCache;

public interface PropertyMapper {

   public abstract void mapProperties(List<PropertyDescriptorPair> conversionCandidates, 
         IgnoreSet explicitIgnoreSet,
         PropertyAccessor sourcePropertyAccessor, 
         PropertyAccessor destinationPropertyAccessor,
         ConversionCache conversionCache,
         CachingObjectAssembler objectAssembler);

}