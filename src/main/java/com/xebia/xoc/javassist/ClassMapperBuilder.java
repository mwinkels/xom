package com.xebia.xoc.javassist;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.Bytecode;
import javassist.bytecode.MethodInfo;

import com.xebia.xoc.ClassMapper;

public class ClassMapperBuilder {
  
  private final NameGenerator nameGenerator = new DefaultNameGenerator();
  private final List<PropertyMapperBuilder> properties = new ArrayList<PropertyMapperBuilder>();
  private final List<ConstructorArgumentMapperBuilder> constructorArguments = new ArrayList<ConstructorArgumentMapperBuilder>();
  
  public void addProperty(PropertyMapperBuilder propertyMapperBuilder) {
    properties.add(propertyMapperBuilder);
  }
  
  public void addConstructorArgument(ConstructorArgumentMapperBuilder constructorArgumentMapperBuilder) {
    constructorArguments.add(constructorArgumentMapperBuilder);
  }
  
  public <S, T> ClassMapper<S, T> build(Class<S> sourceClass, Class<T> targetClass) {
    ClassPool classPool = ClassPool.getDefault();
    try {
      String mapperClassName = nameGenerator.mapperClassName(sourceClass, targetClass);
      CtClass mapperCtClass = classPool.makeClass(mapperClassName);
      return build(mapperCtClass, classPool, classPool.get(sourceClass.getName()), classPool.get(targetClass.getName()));
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

  protected <S, T> ClassMapper<S, T> build(CtClass mapperCtClass, ClassPool classPool, CtClass souceCtClass, CtClass targetCtClass) throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException {
    MapperBuilderContext context = addMapMethod(mapperCtClass, classPool, souceCtClass, targetCtClass);
    addFields(context, constructorArguments);
    addFields(context, properties);
    @SuppressWarnings("unchecked")
    Class<ClassMapper<S, T>> mapperClass = mapperCtClass.toClass();
    ClassMapper<S, T> mapperInstance = mapperClass.newInstance();
    setFields(mapperInstance, constructorArguments);
    setFields(mapperInstance, properties);
    return mapperInstance;
  }

  private MapperBuilderContext addMapMethod(CtClass mapperCtClass, ClassPool classPool, CtClass souceCtClass, CtClass targetCtClass)
      throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException {
    CtMethod converterMethod = implementMapMethod(classPool, mapperCtClass);
    MethodInfo methodInfo = converterMethod.getMethodInfo();
    Bytecode bytecode = new Bytecode(methodInfo.getConstPool(), 0, 2 + properties.size());
    MapperBuilderContext context = new MapperBuilderContext(souceCtClass, targetCtClass, mapperCtClass, classPool, bytecode);
    doMap(context);
    bytecode.setMaxStack(calculateStackSize());
    methodInfo.setCodeAttribute(bytecode.toCodeAttribute());
    mapperCtClass.addMethod(converterMethod);
    return context;
  }
  
  private CtMethod implementMapMethod(ClassPool classPool, CtClass mapperCtClass) throws NotFoundException, CannotCompileException {
    CtClass mapperInterface = classPool.get("com.xebia.xoc.ClassMapper");
    mapperCtClass.addInterface(mapperInterface);
    CtMethod mapperInterfaceMethod = mapperInterface.getDeclaredMethod("map");
    CtMethod mapperMethod = new CtMethod(mapperInterfaceMethod, mapperCtClass, null);
    mapperMethod.setModifiers(mapperMethod.getModifiers() & ~Modifier.ABSTRACT);
    return mapperMethod;
  }
  
  private void doMap(MapperBuilderContext context) throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException {
    addCallNew(context.bytecode, context.targetClass);
    CtClass[] paramTypes = handleConstructorArguments(context);
    addInvokeConstructor(context.bytecode, context.targetClass, paramTypes);
    handleProperties(context);
    context.bytecode.addReturn(context.targetClass);
  }
  
  private void addCallNew(Bytecode bytecode, CtClass returnClass) {
    bytecode.addNew(returnClass);
    bytecode.add(Bytecode.DUP);
  }
  
  private CtClass[] handleConstructorArguments(MapperBuilderContext context)
      throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException {
    if (constructorArguments.isEmpty()) {
      return new CtClass[0];
    } else {
      int i = 0;
      CtClass[] paramTypes = findConstructorParameterTypes(context.targetClass);
      for (ConstructorArgumentMapperBuilder constructorArgument : constructorArguments) {
        constructorArgument.addToBytecode(context, paramTypes[i++]);
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
  
  private void handleProperties(MapperBuilderContext context) throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException {
    if (!properties.isEmpty()) {
      context.bytecode.addAstore(2);
      for (PropertyMapperBuilder property : properties) {
        property.addToBytecode(context);
      }
      context.bytecode.addAload(2);
    }
  }
  
  private void addInvokeConstructor(Bytecode bytecode, CtClass returnClass, CtClass[] paramTypes) {
    bytecode.addInvokespecial(returnClass, "<init>", CtClass.voidType, paramTypes);
  }
  
  private void addFields(MapperBuilderContext context, List<? extends AbstractElementMapperBuilder> elements)
      throws CannotCompileException, NotFoundException {
    for (AbstractElementMapperBuilder element : elements) {
      element.addFields(context);
    }
  }
  
  private void setFields(ClassMapper<?, ?> mapperInstance, List<? extends AbstractElementMapperBuilder> elements)
      throws IllegalAccessException {
    for (AbstractElementMapperBuilder element : elements) {
      if (element.hasConverter()) {
        Field field = getField(mapperInstance, element.getConverterFieldName());
        field.set(mapperInstance, element.getConverter());
      }
      if (element.hasMapper()) {
        Field field = getField(mapperInstance, element.getMapperFieldName());
        field.set(mapperInstance, element.getClassMapper());
      }
    }
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

  private int calculateStackSize() {
    return 2 + constructorArguments.size() + countConvertersAndMappers(constructorArguments) + countConvertersAndMappers(properties);
  }
  
  private int countConvertersAndMappers(List<? extends AbstractElementMapperBuilder> elements) {
    int count = 0;
    for (AbstractElementMapperBuilder element : elements) {
      if (element.hasConverter()) {
        count++;
      }
      if (element.hasMapper()) {
        count++;
      }
    }
    return count;
  }
  
}