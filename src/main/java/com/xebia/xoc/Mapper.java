package com.xebia.xoc;

import java.util.List;

public interface Mapper {
  
  <S, T> T map(S source, Class<T> target) throws MappingException;
  
  <S, T> T map(S source, T target) throws MappingException;
  
  <S, T> List<T> map(List<S> source, Class<T> target) throws MappingException;
  
}
