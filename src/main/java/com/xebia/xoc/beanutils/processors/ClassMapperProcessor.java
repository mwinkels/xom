package com.xebia.xoc.beanutils.processors;

import com.xebia.xoc.ClassMapper;
import com.xebia.xoc.MappingException;

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
