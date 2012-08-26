package com.xebia.xoc.javassist;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;
import javassist.bytecode.Bytecode;

import org.apache.commons.lang.StringUtils;

import com.xebia.xoc.conversion.Converter;
import com.xebia.xoc.conversion.ConverterRegistry;

@SuppressWarnings("rawtypes")
abstract class AbstractElementMapperBuilder {
  
  protected final ClassMapperBuilder classMapperBuilder;
  protected final ConverterRegistry converterRegistry;
  protected Converter converter;
  private final String source;
  
  public AbstractElementMapperBuilder(ClassMapperBuilder classMapperBuilder, ConverterRegistry converterRegistry, String source, Converter converter) {
    this.classMapperBuilder = classMapperBuilder;
    this.converterRegistry = converterRegistry;
    this.source = source;
    this.converter = converter;
  }
  
  protected void invokeGetter(Bytecode bytecode, CtClass sourceCtClass, CtMethod getterMethod) throws NotFoundException {
    bytecode.addAload(1);
    bytecode.addCheckcast(sourceCtClass);
    bytecode.addInvokevirtual(sourceCtClass, getterMethod.getName(), getterMethod.getReturnType(), getterMethod.getParameterTypes());
  }
  
  protected void invokeConverter(Bytecode bytecode, ClassPool classPool, CtClass targetTypeType) throws NotFoundException {
    if (hasConverter()) {
      CtClass objectType = classPool.get("java.lang.Object");
      bytecode.addInvokeinterface(classPool.get("com.xebia.xoc.conversion.Converter"), "convert", objectType, new CtClass[] { objectType }, 2);
      bytecode.addCheckcast(targetTypeType);
    }
  }
  
  protected void prepareConverter(Bytecode bytecode, CtClass mapperClass) {
    if (hasConverter()) {
      bytecode.addAload(0);
      bytecode.addGetfield(mapperClass, getConverterFieldName(), "Lcom/xebia/xoc/conversion/Converter;");
    }
  }
  
  protected void findConverterIfRequired(CtClass sourceType, CtClass targetType) {
    if (!targetType.equals(sourceType) && converter == null) {
      converter = converterRegistry.findConverter(asClass(sourceType), asClass(targetType));
    }
  }
  
  private Class asClass(CtClass ctClass) {
    try {
      if (ctClass.isPrimitive()) {
        return Class.forName(((CtPrimitiveType) ctClass).getWrapperName());
      }
      return Class.forName(ctClass.getName());
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
  
  protected CtMethod findGetterMethod(CtClass sourceCtClass) {
    String getterName1 = "get" + StringUtils.capitalize(getSource());
    String getterName2 = "is" + StringUtils.capitalize(getSource());
    return findMethod(sourceCtClass, getterName1, getterName2);
  }
  
  protected CtMethod findMethod(CtClass ctClass, String... names) {
    for (String name : names) {
      for (CtMethod method : ctClass.getMethods()) {
        if (method.getName().equals(name)) {
          return method;
        }
      }
    }
    throw new RuntimeException("method not found");
  }
  
  protected CtClass sourceCtClass(ClassPool classPool) throws NotFoundException {
    return classPool.get(classMapperBuilder.getSourceClass().getName());
  }
  
  protected CtClass targetCtClass(ClassPool classPool) throws NotFoundException {
    return classPool.get(classMapperBuilder.getTargetClass().getName());
  }
  
  public boolean hasConverter() {
    return converter != null;
  }
  
  public Converter getConverter() {
    return converter;
  }
  
  public abstract String getConverterFieldName();
  
  public String getSource() {
    return source;
  }
  
}