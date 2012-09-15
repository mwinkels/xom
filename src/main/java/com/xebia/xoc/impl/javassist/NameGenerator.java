package com.xebia.xoc.impl.javassist;

public interface NameGenerator {
  
  String mapperClassName(Class<?> sourceClass, Class<?> targetClass);
  
}
