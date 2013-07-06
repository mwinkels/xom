package nl.mwinkels.xom.impl;

import nl.mwinkels.xom.MappingException;

public abstract class AbstractClassMapper<S, T> implements ClassMapper<S, T> {
  
  @Override
  public T map(S source) throws MappingException {
    return apply(source, create(source));
  }
  
  @Override
  public T map(S source, T target) throws MappingException {
    return apply(source, target);
  }
  
  protected abstract T create(S source) throws MappingException;
  
  protected abstract T apply(S source, T target) throws MappingException;
  
}
