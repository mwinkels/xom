package nl.mwinkels.xom.impl;

import java.util.ArrayList;
import java.util.List;

import nl.mwinkels.xom.Mapper;
import nl.mwinkels.xom.MappingException;


@SuppressWarnings("unchecked")
public abstract class AbstractMapper implements Mapper, ClassMapperRegistry {
  
  @Override
  public <S, T> T map(S source, Class<T> target) throws MappingException {
    return findClassMapper((Class<S>)source.getClass(), target).map(source);
  }

  @Override
  public <S, T> T map(S source, T target) throws MappingException {
    return findClassMapper((Class<S>)source.getClass(), (Class<T>)target.getClass()).map(source, target);
  }
  
  @Override
  public <S, T> List<T> map(List<S> source, Class<T> target) throws MappingException {
    ArrayList<T> result = new ArrayList<T>(source.size());
    for (S s : source) {
      result.add(map(s, target));
    }
    return result;
  }
  
}