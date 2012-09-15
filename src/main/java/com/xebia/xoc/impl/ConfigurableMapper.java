package com.xebia.xoc.impl;

import java.util.ArrayList;
import java.util.List;

import com.xebia.xoc.config.ClassMapperConfig;

public class ConfigurableMapper extends AbstractMapper {
  
  public class ClassMapperEntry<S, T> {
    
    private final ClassMapper<S, T> classMapper;
    private final Class<S> sourceClass;
    private final Class<T> targetClass;
    
    public ClassMapperEntry(ClassMapper<S, T> classMapper, Class<S> sourceClass, Class<T> targetClass) {
      this.classMapper = classMapper;
      this.sourceClass = sourceClass;
      this.targetClass = targetClass;
    }
    
    public boolean canMap(Class<?> sourceClass, Class<?> targetClass) {
      return this.sourceClass.isAssignableFrom(sourceClass) && this.targetClass.isAssignableFrom(targetClass);
    }
    
  }
  
  private final List<ClassMapperEntry<?, ?>> classMappers = new ArrayList<ClassMapperEntry<?, ?>>();
  private final MapperFactory mapperFactory;
  private final ConverterRegistry converterRegistry = new ConverterRegistry();
  
  public ConfigurableMapper(MapperFactory mapperFactory) {
    this.mapperFactory = mapperFactory;
    converterRegistry.registerDefaultConverters();
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <S, T> ClassMapper<S, T> findClassMapper(Class<S> sourceClass, Class<T> targetClass) {
    for (ClassMapperEntry<?, ?> entry : classMappers) {
      if (entry.canMap(sourceClass, targetClass)) {
        return (ClassMapper<S, T>) entry.classMapper;
      }
    }
    return null;
  }
  
  public <S, T> ClassMapper<S, T> withMapper(ClassMapperConfig classMapperConfig, Class<S> sourceClass, Class<T> targetClass) {
    ClassMapper<S, T> classMapper = mapperFactory.fromConfig(classMapperConfig, converterRegistry, this).build(sourceClass, targetClass);
    classMappers.add(new ClassMapperEntry<S, T>(classMapper, sourceClass, targetClass));
    return classMapper;
  }
  
}
