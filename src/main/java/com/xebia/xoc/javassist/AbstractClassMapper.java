package com.xebia.xoc.javassist;

import com.xebia.xoc.ClassMapper;

public abstract class AbstractClassMapper<S, T> implements ClassMapper<S, T> {
  
  @Override
  public T map(S source) {
    return apply(source, create(source));
  }
  
  @Override
  public T map(S source, T target) {
    return apply(source, target);
  }
  
  protected abstract T create(S source);
  
  protected abstract T apply(S source, T target);
  
}
