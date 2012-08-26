package com.xebia.xoc.javassist;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;

import com.xebia.xoc.ClassMapper;

public class ClassMapperBuilder<S, T> {
  
  private final Class<S> sourceClass;
  private final Class<T> targetClass;
  
  private final List<PropertyMapperBuilder> properties = new ArrayList<PropertyMapperBuilder>();
  private final List<ConstructorArgumentMapperBuilder> constructorArguments = new ArrayList<ConstructorArgumentMapperBuilder>();
  
  private final NameGenerator nameGenerator = new DefaultNameGenerator();
  
  public ClassMapperBuilder(Class<S> sourceClass, Class<T> targetClass) {
    this.sourceClass = sourceClass;
    this.targetClass = targetClass;
  }
  
  public void addProperty(PropertyMapperBuilder propertyMapperBuilder) {
    properties.add(propertyMapperBuilder);
  }
  
  public void addConstructorArgument(ConstructorArgumentMapperBuilder constructorArgumentMapperBuilder) {
    constructorArguments.add(constructorArgumentMapperBuilder);
  }
  
  public ClassMapper<S, T> build() {
    ClassPool classPool = ClassPool.getDefault();
    try {
      String mapperClassName = nameGenerator.mapperClassName(getSourceClass(), getTargetClass());
      CtClass mapperCtClass = classPool.makeClass(mapperClassName);
      CtMethod converterMethod = implementMapMethod(classPool, mapperCtClass);
      MethodInfo methodInfo = converterMethod.getMethodInfo();
      Bytecode bytecode = doMap(classPool, methodInfo.getConstPool(), mapperCtClass);
      int maxStackSize = 2 + constructorArguments.size();
      maxStackSize += addConverterFields(classPool, mapperCtClass, constructorArguments);
      maxStackSize += addConverterFields(classPool, mapperCtClass, properties);
      bytecode.setMaxStack(maxStackSize);
      methodInfo.setCodeAttribute(bytecode.toCodeAttribute());
      mapperCtClass.addMethod(converterMethod);
      ClassMapper<S, T> mapperInstance = createClass(mapperCtClass).newInstance();
      setConverterFields(mapperInstance, constructorArguments);
      setConverterFields(mapperInstance, properties);
      return mapperInstance;
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (CannotCompileException e) {
      throw new RuntimeException(e);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
  }
  
  private CtMethod implementMapMethod(ClassPool classPool, CtClass mapperCtClass) throws NotFoundException, CannotCompileException {
    CtClass mapperInterface = classPool.get("com.xebia.xoc.ClassMapper");
    mapperCtClass.addInterface(mapperInterface);
    CtMethod mapperInterfaceMethod = mapperInterface.getDeclaredMethod("map");
    CtMethod mapperMethod = new CtMethod(mapperInterfaceMethod, mapperCtClass, null);
    mapperMethod.setModifiers(mapperMethod.getModifiers() & ~Modifier.ABSTRACT);
    return mapperMethod;
  }
  
  private Bytecode doMap(ClassPool classPool, ConstPool constPool, CtClass mapperCtClass) throws NotFoundException {
    Bytecode bytecode = new Bytecode(constPool, 0, 2 + properties.size());
    CtClass returnClass = classPool.get(getTargetClass().getName());
    addCallNew(bytecode, returnClass);
    CtClass[] paramTypes = handleConstructorArguments(classPool, mapperCtClass, bytecode, returnClass);
    addInvokeConstructor(bytecode, returnClass, paramTypes);
    handleProperties(classPool, mapperCtClass, bytecode);
    bytecode.addReturn(returnClass);
    return bytecode;
  }
  
  private void addCallNew(Bytecode bytecode, CtClass returnClass) {
    bytecode.addNew(returnClass);
    bytecode.add(Bytecode.DUP);
  }
  
  private CtClass[] handleConstructorArguments(ClassPool classPool, CtClass mapperCtClass, Bytecode bytecode, CtClass returnClass)
      throws NotFoundException {
    if (constructorArguments.isEmpty()) {
      return new CtClass[0];
    } else {
      int i = 0;
      CtClass[] paramTypes = findConstructorParameterTypes(returnClass);
      for (ConstructorArgumentMapperBuilder constructorArgument : constructorArguments) {
        constructorArgument.addToBytecode(bytecode, classPool, paramTypes[i++], mapperCtClass);
      }
      return paramTypes;
    }
  }
  
  private CtClass[] findConstructorParameterTypes(CtClass returnClass) throws NotFoundException {
    for (CtConstructor constructor : returnClass.getConstructors()) {
      if (constructor.getParameterTypes().length == constructorArguments.size()) {
        return constructor.getParameterTypes();
      }
    }
    throw new RuntimeException("No suitable constructor found.");
  }
  
  private void handleProperties(ClassPool classPool, CtClass mapperCtClass, Bytecode bytecode) throws NotFoundException {
    if (!properties.isEmpty()) {
      bytecode.addAstore(2);
      for (PropertyMapperBuilder property : properties) {
        property.addToBytecode(bytecode, classPool, mapperCtClass);
      }
      bytecode.addAload(2);
    }
  }
  
  private void addInvokeConstructor(Bytecode bytecode, CtClass returnClass, CtClass[] paramTypes) {
    bytecode.addInvokespecial(returnClass, "<init>", CtClass.voidType, paramTypes);
  }
  
  @SuppressWarnings("unchecked")
  private Class<ClassMapper<S, T>> createClass(CtClass mapperCtClass) throws CannotCompileException {
    return (Class<ClassMapper<S, T>>) mapperCtClass.toClass();
  }
  
  private int addConverterFields(ClassPool classPool, CtClass mapperCtClass, List<? extends AbstractElementMapperBuilder> elements)
      throws CannotCompileException, NotFoundException {
    int count = 0;
    for (AbstractElementMapperBuilder element : elements) {
      if (element.hasConverter()) {
        CtField field = new CtField(classPool.get("com.xebia.xoc.conversion.Converter"), element.getConverterFieldName(), mapperCtClass);
        field.setModifiers(Modifier.PUBLIC);
        mapperCtClass.addField(field);
        count++;
      }
    }
    return count;
  }
  
  private void setConverterFields(ClassMapper<S, T> mapperInstance, List<? extends AbstractElementMapperBuilder> elements)
      throws IllegalAccessException {
    for (AbstractElementMapperBuilder element : elements) {
      if (element.hasConverter()) {
        Field field = getConverterField(mapperInstance, element);
        field.set(mapperInstance, element.getConverter());
      }
    }
  }
  
  private Field getConverterField(ClassMapper<S, T> mapperInstance, AbstractElementMapperBuilder element) {
    try {
      return mapperInstance.getClass().getDeclaredField(element.getConverterFieldName());
    } catch (SecurityException e) {
      throw new RuntimeException(e);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }
  
  public Class<S> getSourceClass() {
    return sourceClass;
  }
  
  public Class<T> getTargetClass() {
    return targetClass;
  }
  
}