package com.xebia.xoc;

public interface Mapper extends ClassMapperRegistry {
  
  <S, T> T map(S source, Class<T> target);
  
  <S, T> T map(S source, T target);
  
}
