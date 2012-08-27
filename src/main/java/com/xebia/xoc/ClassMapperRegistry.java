package com.xebia.xoc;

public interface ClassMapperRegistry {
  
  <S, T> ClassMapper<S, T> findClassMapper(Class<S> sourceClass, Class<T> targetClass);
  
}