package com.xebia.xoc.impl.javassist;

import static com.xebia.xoc.impl.javassist.Utils.classMapperInterface;
import static com.xebia.xoc.impl.javassist.Utils.converterInterface;

import java.lang.reflect.Field;
import java.util.LinkedList;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtPrimitiveType;
import javassist.Modifier;
import javassist.NotFoundException;

import org.apache.commons.lang.StringUtils;

import com.xebia.xoc.conversion.Converter;
import com.xebia.xoc.impl.ClassMapper;
import com.xebia.xoc.impl.ClassMapperRegistry;
import com.xebia.xoc.impl.ConverterRegistry;

@SuppressWarnings("rawtypes")
abstract class AbstractElementMapperBuilder {
  
  private final ConverterRegistry converterRegistry;
  private final ClassMapperRegistry mapperRegistry;
  private final String source;
  private final ClassMapperBuilderImpl nestedClassMapperBuilder;
  private Converter converter;
  private ClassMapper<?, ?> classMapper;
  
  public AbstractElementMapperBuilder(ConverterRegistry converterRegistry, ClassMapperRegistry mapperRegistry, String source, Converter converter, ClassMapperBuilderImpl nestedClassMapperBuilder) {
    this.converterRegistry = converterRegistry;
    this.mapperRegistry = mapperRegistry;
    this.source = source;
    this.converter = converter;
    this.nestedClassMapperBuilder = nestedClassMapperBuilder;
  }
  
  protected void addFields(CtClass mapperClass) throws CannotCompileException {
    if (hasConverter()) {
      addField(mapperClass, converterInterface, getConverterFieldName());
    }
    if (hasMapper()) {
      addField(mapperClass, classMapperInterface, getMapperFieldName());
    }
  }

  private void addField(CtClass mapperClass, CtClass type, String fieldName) throws CannotCompileException {
    CtField field = new CtField(type, fieldName, mapperClass);
    field.setModifiers(Modifier.PUBLIC);
    mapperClass.addField(field);
  }
  
  public void setFields(ClassMapper<?, ?> mapperInstance) throws IllegalAccessException {
    if (hasConverter()) {
      setField(mapperInstance, getConverterFieldName(), getConverter());
    }
    if (hasMapper()) {
      setField(mapperInstance, getMapperFieldName(), getClassMapper());
    }
  }

  private void setField(ClassMapper<?, ?> mapperInstance, String fieldName, Object value) throws IllegalAccessException {
    Field field = getField(mapperInstance, fieldName);
    field.set(mapperInstance, value);
  }
  
  private Field getField(ClassMapper<?, ?> mapperInstance, String fieldName) {
    try {
      return mapperInstance.getClass().getDeclaredField(fieldName);
    } catch (SecurityException e) {
      throw new RuntimeException(e);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }
  
  protected void invokerMapperConverterAndGetters(MapperBuilderContext context, LinkedList<GetterDef> getterChain, CtClass targetType) {
    prepareInvokeMapper(context);
    prepareInvokeConverter(context);
    prepareInvokeGetter(context);
    invokeGetterChain(context, getterChain);
    invokeConverter(context, targetType);
    invokeMapper(context, targetType);
  }
  
  protected void prepareInvokeGetter(MapperBuilderContext context) {
    context.loadAndCheckSource();
  }
  
  final static class GetterDef {
    final CtClass ctClass;
    final CtMethod ctMethod;
    
    GetterDef(CtClass ctClass, CtMethod ctMethod) {
      this.ctClass = ctClass;
      this.ctMethod = ctMethod;
    }
  }
  
  protected void invokeGetterChain(MapperBuilderContext context, LinkedList<GetterDef> getterChain) {
    for (GetterDef getterDef : getterChain) {
      context.invokeGetter(getterDef.ctClass, getterDef.ctMethod);
    }
  }
  
  protected LinkedList<GetterDef> findGetterChain(CtClass currentCtClass) {
    LinkedList<GetterDef> getterChain = new LinkedList<GetterDef>();
    if (source != null) {
      String[] parts = source.split("\\.");
      for (String getterName : parts) {
        try {
          CtMethod getterMethod = findGetterMethod(currentCtClass, getterName);
          checkGetterSignature(getterMethod);
          getterChain.add(new GetterDef(currentCtClass, getterMethod));
          currentCtClass = getterMethod.getReturnType();
        } catch (NotFoundException e) {
          throw new AssertionError(e);
        }
      }
    }
    return getterChain;
  }
  
  private void checkGetterSignature(CtMethod getterMethod) throws NotFoundException {
    if (getterMethod.getParameterTypes().length != 0) {
      throw new RuntimeException("Getter needs parameters!");
    }
  }
  
  protected void prepareInvokeConverter(MapperBuilderContext context) {
    if (hasConverter()) {
      context.loadField(getConverterFieldName(), "Lcom/xebia/xoc/conversion/Converter;");
    }
  }
  
  protected void invokeConverter(MapperBuilderContext context, CtClass targetType) {
    if (hasConverter()) {
      try {
        context.invokeTransformMethod("com.xebia.xoc.conversion.Converter", "convert", targetType);
      } catch (NotFoundException e) {
        throw new AssertionError(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  protected void findMapperOrConverterIfRequired(CtClass sourceType, CtClass targetType) {
    boolean typesDiffer = typesDiffer(sourceType, targetType);
    if (typesDiffer && classMapper == null) {
      classMapper = mapperRegistry.findClassMapper(asClass(sourceType), asClass(targetType));
    }
    if (classMapper != null) {
      return;
    }
    if (typesDiffer && converter == null) {
      converter = converterRegistry.findConverter(asClass(sourceType), asClass(targetType));
    }
  }

  private boolean typesDiffer(CtClass sourceType, CtClass targetType) {
    try {
      return !sourceType.subtypeOf(targetType);
    } catch (NotFoundException e) {
      throw new AssertionError(e);
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
  
  protected void prepareInvokeMapper(MapperBuilderContext context) {
    if (hasMapper()) {
      context.loadField(getMapperFieldName(), "Lcom/xebia/xoc/impl/ClassMapper;");
    }
  }

  protected void invokeMapper(MapperBuilderContext context, CtClass targetType) {
    if (hasMapper()) {
      try {
        context.invokeTransformMethod("com.xebia.xoc.impl.ClassMapper", "map", targetType);
      } catch (NotFoundException e) {
        throw new AssertionError(e);
      }
    }
  }
  
  protected CtMethod findGetterMethod(CtClass sourceCtClass, String property) {
    String getterName1 = "get" + StringUtils.capitalize(property);
    String getterName2 = "is" + StringUtils.capitalize(property);
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
    throw new RuntimeException(String.format("method [%s] not found.", names));
  }
  
  public boolean hasConverter() {
    return converter != null;
  }
  
  public Converter getConverter() {
    return converter;
  }
  
  public String getConverterFieldName() {
    return getName("Converter");
  }
  
  public boolean hasMapper() {
    return classMapper != null || nestedClassMapperBuilder != null;
  }
  
  public String getMapperFieldName() {
    return getName("Mapper");
  }
  
  protected void createNestedMapper(MapperBuilderContext context, CtClass sourceType, CtClass targetType) throws CannotCompileException {
    if (hasMapper()) {
      CtClass nestedMapperClass = context.mapperClass.makeNestedClass(StringUtils.capitalize(getName("Mapper")), true);
      classMapper = nestedClassMapperBuilder.build(nestedMapperClass, sourceType, targetType);
    }
  }
  
  public ClassMapper<?, ?> getClassMapper() {
    return classMapper;
  }
  
  protected abstract String getName(String suffix);

  protected CtClass getLastGetterType(LinkedList<GetterDef> getterChain, CtClass sourceType) {
    try {
      return getterChain.isEmpty() ? sourceType : getterChain.getLast().ctMethod.getReturnType();
    } catch (NotFoundException e) {
      throw new AssertionError(e);
    }
  }

}