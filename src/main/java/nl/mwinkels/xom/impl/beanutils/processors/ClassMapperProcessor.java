package nl.mwinkels.xom.impl.beanutils.processors;

import nl.mwinkels.xom.MappingException;
import nl.mwinkels.xom.impl.ClassMapper;

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
