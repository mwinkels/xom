package com.xebia.xoc;

public abstract class AbstractMapper implements Mapper {
  
  @SuppressWarnings("unchecked")
  @Override
  public <S, T> T map(S source, Class<T> target) {
    return findClassMapper((Class<S>)source.getClass(), target).map(source);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <S, T> T map(S source, T target) {
    return findClassMapper((Class<S>)source.getClass(), (Class<T>)target.getClass()).map(source, target);
  }
  
}