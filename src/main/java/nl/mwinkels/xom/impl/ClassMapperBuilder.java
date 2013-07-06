package nl.mwinkels.xom.impl;


public interface ClassMapperBuilder {
  
  <S, T> ClassMapper<S, T> build(Class<S> sourceClass, Class<T> targetClass);
  
}