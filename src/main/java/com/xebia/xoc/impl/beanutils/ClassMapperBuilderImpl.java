package com.xebia.xoc.impl.beanutils;

import java.util.ArrayList;
import java.util.List;

import com.xebia.xoc.impl.ClassMapper;
import com.xebia.xoc.impl.ClassMapperBuilder;
import com.xebia.xoc.impl.beanutils.processors.ValueProcessorFactory;

public class ClassMapperBuilderImpl implements ClassMapperBuilder {
  
  private final ValueProcessorFactory valueProcessorFactory;
  
  protected final List<ConstructorArgumentExtractor> constructorArguments = new ArrayList<ConstructorArgumentExtractor>();

  protected final List<PropertyExtractor> properties = new ArrayList<PropertyExtractor>();

  public ClassMapperBuilderImpl(ValueProcessorFactory valueProcessorFactory) {
    this.valueProcessorFactory = valueProcessorFactory;
  }

  @Override
  public <S, T> ClassMapper<S, T> build(Class<S> sourceClass, Class<T> targetClass) {
    return new ClassMapperImpl<S, T>(valueProcessorFactory, constructorArguments, properties, sourceClass, targetClass);
  }

  public void addConstructorArgumentExtractor(ConstructorArgumentExtractor constructorArgumentMapperBuilder) {
    constructorArguments.add(constructorArgumentMapperBuilder);
  }

  public void addPropertyExtactor(PropertyExtractor propertyExtractor) {
    properties.add(propertyExtractor);
  }
  
}
