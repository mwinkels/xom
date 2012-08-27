package com.xebia.xoc;

import java.util.ArrayList;
import java.util.List;

import com.xebia.xoc.config.ClassMapperConfig;
import com.xebia.xoc.conversion.ConverterRegistry;
import com.xebia.xoc.javassist.MapperFactory;

public class ConfigurableMapper implements Mapper {
  
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
  private final MapperFactory mapperFactory = new MapperFactory();
  private final ConverterRegistry converterRegistry = new ConverterRegistry();
  
  public ConfigurableMapper() {
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

  @Override
  public <S, T> T map(S source, Class<T> target) {
    return findClassMapper((Class<S>)source.getClass(), target).map(source);
  }

  @Override
  public <S, T> T map(S source, T target) {
    return findClassMapper((Class<S>)source.getClass(), (Class<T>)target.getClass()).map(source, target);
  }
  
}
