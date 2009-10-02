package com.googlecode.simpleobjectassembler.converter.mapping;

import java.util.Set;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: robmonie
 * Date: Oct 2, 2009
 * Time: 4:04:19 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class MappingPaths {

   public static final Exclusions exclude(String... ignore) {
      return new Exclusions(ignore);
   }

}
