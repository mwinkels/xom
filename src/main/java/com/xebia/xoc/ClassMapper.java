package com.xebia.xoc;

public interface ClassMapper<S, T> {
  
  T map(S source);
  
  T map(S source, T target);
}
