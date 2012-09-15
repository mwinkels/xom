package com.xebia.xoc.beanutils.processors;

import com.xebia.xoc.MappingException;
import com.xebia.xoc.conversion.ConversionException;

public interface ValueProcessor {
 
  Object process(Object value) throws MappingException, ConversionException;
}
