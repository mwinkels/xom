package com.xebia.xoc.javassist;

import java.util.LinkedList;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtPrimitiveType;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.Bytecode;

import org.apache.commons.lang.StringUtils;

import com.xebia.xoc.ClassMapper;
import com.xebia.xoc.ClassMapperRegistry;
import com.xebia.xoc.conversion.Converter;
import com.xebia.xoc.conversion.ConverterRegistry;

@SuppressWarnings("rawtypes")
abstract class AbstractElementMapperBuilder {
  
  private final ConverterRegistry converterRegistry;
  private final ClassMapperRegistry mapperRegistry;
  private final String source;
  private final ClassMapperBuilder nestedClassMapperBuilder;
  private Converter converter;
  private ClassMapper<?, ?> classMapper;
  
  public AbstractElementMapperBuilder(ConverterRegistry converterRegistry, ClassMapperRegistry mapperRegistry, String source, Converter converter, ClassMapperBuilder nestedClassMapperBuilder) {
    this.converterRegistry = converterRegistry;
    this.mapperRegistry = mapperRegistry;
    this.source = source;
    this.converter = converter;
    this.nestedClassMapperBuilder = nestedClassMapperBuilder;
  }
  
  protected void addFields(CtClass mapperClass, ClassPool classPool) throws CannotCompileException, NotFoundException {
    if (hasConverter()) {
      addField(mapperClass, classPool.get("com.xebia.xoc.conversion.Converter"), getConverterFieldName());
    }
    if (hasMapper()) {
      addField(mapperClass, classPool.get("com.xebia.xoc.ClassMapper"), getMapperFieldName());
    }
  }
  
  private void addField(CtClass mapperClass, CtClass type, String fieldName) throws CannotCompileException {
    CtField field = new CtField(type, fieldName, mapperClass);
    field.setModifiers(Modifier.PUBLIC);
    mapperClass.addField(field);
  }
  
  final static class GetterDef {
    final CtClass ctClass;
    final CtMethod ctMethod;
    
    GetterDef(CtClass ctClass, CtMethod ctMethod) {
      this.ctClass = ctClass;
      this.ctMethod = ctMethod;
    }
  }
  
  protected void prepareInvokeGetter(MapperBuilderContext context) {
    context.loadAndCheckSource();
  }

  protected void invokeGetterChain(MapperBuilderContext context, LinkedList<GetterDef> getterChain) throws NotFoundException {
    for (GetterDef getterDef : getterChain) {
      context.invokeGetter(this, getterDef.ctClass, getterDef.ctMethod);
    }
  }
  
  protected LinkedList<GetterDef> findGetterChain(CtClass currentCtClass) throws NotFoundException {
    LinkedList<GetterDef> getterChain = new LinkedList<GetterDef>();
    if (source != null) {
      String[] parts = source.split("\\.");
      for (String getterName : parts) {
        CtMethod getterMethod = findGetterMethod(currentCtClass, getterName);
        checkGetterSignature(getterMethod);
        getterChain.add(new GetterDef(currentCtClass, getterMethod));
        currentCtClass = getterMethod.getReturnType();
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
  
  protected void invokeConverter(MapperBuilderContext context, CtClass targetType) throws NotFoundException {
    if (hasConverter()) {
      context.invokeTransformMethod("com.xebia.xoc.conversion.Converter", "convert", targetType);
    }
  }

  @SuppressWarnings("unchecked")
  protected void findMapperOrConverterIfRequired(CtClass sourceType, CtClass targetType) {
    boolean typesDiffer = !targetType.equals(sourceType);
    if (typesDiffer && classMapper == null) {
      classMapper = mapperRegistry.findClassMapper(asClass(sourceType), asClass(targetType));
      if (classMapper != null) {
        return;
      }
    }
    if (typesDiffer && converter == null) {
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
  
  protected void prepareInvokeMapper(MapperBuilderContext context) {
    if (hasMapper()) {
      context.loadField(getMapperFieldName(), "Lcom/xebia/xoc/ClassMapper;");
    }
  }

  protected void invokeMapper(MapperBuilderContext context, CtClass targetType) throws NotFoundException {
    if (hasMapper()) {
      context.invokeTransformMethod("com.xebia.xoc.ClassMapper", "map", targetType);
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
  
  protected void createNestedMapper(MapperBuilderContext context, CtClass sourceType, CtClass targetType) throws NotFoundException, CannotCompileException,
  InstantiationException, IllegalAccessException {
    if (hasMapper()) {
      CtClass nestedMapperClass = context.mapperClass.makeNestedClass(StringUtils.capitalize(getName("Mapper")), true);
      classMapper = nestedClassMapperBuilder.build(nestedMapperClass, context.classPool, sourceType, targetType);
    }
  }
  
  public ClassMapper<?, ?> getClassMapper() {
    return classMapper;
  }
  
  protected abstract String getName(String suffix);

  protected CtClass getLastGetterType(LinkedList<GetterDef> getterChain, CtClass sourceType) throws NotFoundException {
    return getterChain.isEmpty() ? sourceType : getterChain.getLast().ctMethod.getReturnType();
  }
  
}