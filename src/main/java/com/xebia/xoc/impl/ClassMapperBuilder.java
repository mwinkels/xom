package com.xebia.xoc.impl;


public interface ClassMapperBuilder {
  
  <S, T> ClassMapper<S, T> build(Class<S> sourceClass, Class<T> targetClass);
  
}