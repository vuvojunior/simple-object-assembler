package com.googlecode.simpleobjectassembler.converter;

import java.util.List;
import java.util.Set;

import org.springframework.beans.PropertyAccessor;

public interface PropertyMapper {

   public abstract void mapProperties(List<PropertyDescriptorPair> conversionCandidates, Set<String> explicitIgnoreSet,
         PropertyAccessor sourcePropertyAccessor, PropertyAccessor destinationPropertyAccessor,
         ConversionCache conversionCache);

}