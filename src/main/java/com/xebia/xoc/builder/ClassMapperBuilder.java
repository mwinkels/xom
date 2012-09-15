package com.xebia.xoc.builder;

import com.xebia.xoc.ClassMapper;

public interface ClassMapperBuilder {
  
  <S, T> ClassMapper<S, T> build(Class<S> sourceClass, Class<T> targetClass);
  
}