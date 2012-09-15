package com.xebia.xoc;

public interface ClassMapper<S, T> {
  
  T map(S source) throws MappingException;
  
  T map(S source, T target) throws MappingException;
}
