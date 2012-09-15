package com.xebia.xoc.impl.beanutils.processors;

import com.xebia.xoc.MappingException;
import com.xebia.xoc.impl.ClassMapper;

public class ClassMapperProcessor implements ValueProcessor {
  
  private final ClassMapper<Object, Object> classMapper;

  public ClassMapperProcessor(ClassMapper<Object, Object> classMapper) {
    this.classMapper = classMapper;
  }

  @Override
  public Object process(Object value) throws MappingException {
    return classMapper.map(value);
  }
  
}
