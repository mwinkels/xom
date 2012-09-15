package com.xebia.xoc.impl;

import com.xebia.xoc.MappingException;

public interface ClassMapper<S, T> {
  
  T map(S source) throws MappingException;
  
  T map(S source, T target) throws MappingException;
}
