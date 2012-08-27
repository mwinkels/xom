package com.xebia.xoc;

import java.util.List;

public interface Mapper extends ClassMapperRegistry {
  
  <S, T> T map(S source, Class<T> target);
  
  <S, T> T map(S source, T target);
  
  <S, T> List<T> map(List<S> source, Class<T> target);
  
}
