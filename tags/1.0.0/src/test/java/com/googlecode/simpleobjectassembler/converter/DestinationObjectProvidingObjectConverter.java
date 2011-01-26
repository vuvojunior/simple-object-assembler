package com.googlecode.simpleobjectassembler.converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DestinationObjectProvidingObjectConverter extends AbstractObjectConverter<SourceObject, DestinationObject> {

   
   @Override
   public DestinationObject createDestinationObject(SourceObject sourceObject) {
     
      final DestinationObject destinationObject = new DestinationObject();
      final NestedDestinationObject nestedObject = new NestedDestinationObject();
      nestedObject.setString("a");
      nestedObject.setOtherString("b");
      
      final NestedDestinationObject nestedObject2 = new NestedDestinationObject();
      nestedObject2.setString("a");
      nestedObject2.setOtherString("b");
      
      final List<NestedDestinationObject> list = new ArrayList<NestedDestinationObject>();
      list.add(nestedObject);
      list.add(nestedObject2);
      
      destinationObject.setNestedObjectList(list);
      
      final Set<NestedDestinationObject> set = new HashSet<NestedDestinationObject>();
      set.add(nestedObject);
      
      destinationObject.setNestedObjectSet(set);
      
      return destinationObject;
   }
  /* 
   public Class<DestinationObject> getDestinationClass() {
      return DestinationObject.class;
   }

   public Class<SourceObject> getSourceClass() {
      return SourceObject.class;
   }
*/
}
