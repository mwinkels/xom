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
  
  protected void addFields(MapperBuilderContext context) throws CannotCompileException, NotFoundException {
    if (hasConverter()) {
      addField(context.mapperClass, context.classPool.get("com.xebia.xoc.conversion.Converter"), getConverterFieldName());
    }
    if (hasMapper()) {
      addField(context.mapperClass, context.classPool.get("com.xebia.xoc.ClassMapper"), getMapperFieldName());
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
  
  protected void prepareInvokeGetter(Bytecode bytecode, CtClass sourceCtClass) {
    bytecode.addAload(1);
    bytecode.addCheckcast(sourceCtClass);
  }
  
  protected void invokeGetterChain(Bytecode bytecode, LinkedList<GetterDef> getterChain) throws NotFoundException {
    for (GetterDef getterDef : getterChain) {
      invokeGetter(bytecode, getterDef.ctClass, getterDef.ctMethod);
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
  
  protected void invokeGetter(Bytecode bytecode, CtClass sourceCtClass, CtMethod getterMethod) throws NotFoundException {
    bytecode.addInvokevirtual(sourceCtClass, getterMethod.getName(), getterMethod.getReturnType(), getterMethod.getParameterTypes());
  }
  
  protected void prepareInvokeConverter(Bytecode bytecode, CtClass mapperClass) {
    if (hasConverter()) {
      bytecode.addAload(0);
      bytecode.addGetfield(mapperClass, getConverterFieldName(), "Lcom/xebia/xoc/conversion/Converter;");
    }
  }
  
  protected void invokeConverter(Bytecode bytecode, ClassPool classPool, CtClass targetTypeType) throws NotFoundException {
    if (hasConverter()) {
      CtClass objectType = classPool.get("java.lang.Object");
      bytecode.addInvokeinterface(classPool.get("com.xebia.xoc.conversion.Converter"), "convert", objectType, new CtClass[] { objectType }, 2);
      bytecode.addCheckcast(targetTypeType);
    }
  }
  
  protected void findConverterIfRequired(CtClass sourceType, CtClass targetType) {
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
  
  protected void prepareInvokeMapper(Bytecode bytecode, CtClass mapperClass) {
    if (hasMapper()) {
      bytecode.addAload(0);
      bytecode.addGetfield(mapperClass, getMapperFieldName(), "Lcom/xebia/xoc/ClassMapper;");
    }
  }
  
  protected void invokeMapper(Bytecode bytecode, ClassPool classPool, CtClass targetTypeType) throws NotFoundException {
    if (hasMapper()) {
      CtClass objectType = classPool.get("java.lang.Object");
      bytecode.addInvokeinterface(classPool.get("com.xebia.xoc.ClassMapper"), "map", objectType, new CtClass[] { objectType }, 2);
      bytecode.addCheckcast(targetTypeType);
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