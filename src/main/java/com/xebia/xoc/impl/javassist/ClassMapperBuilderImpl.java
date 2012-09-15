package com.xebia.xoc.impl.javassist;

import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.MethodInfo;

import com.xebia.xoc.impl.ClassMapper;
import com.xebia.xoc.impl.ClassMapperBuilder;

public class ClassMapperBuilderImpl implements ClassMapperBuilder {
  
  private final NameGenerator nameGenerator = new DefaultNameGenerator();
  private final List<PropertyMapperBuilder> properties = new ArrayList<PropertyMapperBuilder>();
  private final List<ConstructorArgumentMapperBuilder> constructorArguments = new ArrayList<ConstructorArgumentMapperBuilder>();
  
  public void addProperty(PropertyMapperBuilder propertyMapperBuilder) {
    properties.add(propertyMapperBuilder);
  }
  
  public void addConstructorArgument(ConstructorArgumentMapperBuilder constructorArgumentMapperBuilder) {
    constructorArguments.add(constructorArgumentMapperBuilder);
  }
  
  @Override
  public <S, T> ClassMapper<S, T> build(Class<S> sourceClass, Class<T> targetClass) {
    ClassPool classPool = ClassPool.getDefault();
    try {
      String mapperClassName = nameGenerator.mapperClassName(sourceClass, targetClass);
      CtClass mapperCtClass = classPool.makeClass(mapperClassName);
      return build(mapperCtClass, classPool, classPool.get(sourceClass.getName()), classPool.get(targetClass.getName()));
    } catch (CannotCompileException e) {
      throw new RuntimeException(e);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    } catch (BadBytecode e) {
      throw new RuntimeException(e);
    }
  }

  protected <S, T> ClassMapper<S, T> build(CtClass mapperCtClass, ClassPool classPool, CtClass souceCtClass, CtClass targetCtClass) throws NotFoundException, CannotCompileException, BadBytecode {
    CtClass superClass = classPool.get("com.xebia.xoc.impl.AbstractClassMapper");
    mapperCtClass.setSuperclass(superClass);
    addCreateMethod(mapperCtClass, classPool, souceCtClass, targetCtClass, superClass);
    addApplyMethod(mapperCtClass, classPool, souceCtClass, targetCtClass, superClass);
    addFields(mapperCtClass, classPool, constructorArguments);
    addFields(mapperCtClass, classPool, properties);
    @SuppressWarnings("unchecked")
    Class<ClassMapper<S, T>> mapperClass = mapperCtClass.toClass();
    return createMapper(mapperClass);
  }

  private <T, S> ClassMapper<S, T> createMapper(Class<ClassMapper<S, T>> mapperClass) {
    try {
      ClassMapper<S, T> mapperInstance = mapperClass.newInstance();
      setFields(mapperInstance, constructorArguments);
      setFields(mapperInstance, properties);
      return mapperInstance;
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private void addCreateMethod(CtClass mapperCtClass, ClassPool classPool, CtClass souceCtClass, CtClass targetCtClass, CtClass superClass) throws NotFoundException, CannotCompileException, BadBytecode {
    CtMethod createMethod = implementAndGetMethod(mapperCtClass, superClass, "create");
    MethodInfo methodInfo = createMethod.getMethodInfo();
    Bytecode bytecode = new Bytecode(methodInfo.getConstPool(), 0, 2);
    MapperBuilderContext context = new MapperBuilderContext(souceCtClass, targetCtClass, mapperCtClass, classPool, bytecode);
    doCreate(context);
    CodeAttribute codeAttribute = bytecode.toCodeAttribute();
    codeAttribute.computeMaxStack();
    methodInfo.setCodeAttribute(codeAttribute);
    mapperCtClass.addMethod(createMethod);
  }
  
  private void addApplyMethod(CtClass mapperCtClass, ClassPool classPool, CtClass souceCtClass, CtClass targetCtClass, CtClass superClass) throws NotFoundException, CannotCompileException, BadBytecode {
    CtMethod createMethod = implementAndGetMethod(mapperCtClass, superClass, "apply");
    MethodInfo methodInfo = createMethod.getMethodInfo();
    Bytecode bytecode = new Bytecode(methodInfo.getConstPool(), 0, 3);
    MapperBuilderContext context = new MapperBuilderContext(souceCtClass, targetCtClass, mapperCtClass, classPool, bytecode);
    doApply(context);
    CodeAttribute codeAttribute = bytecode.toCodeAttribute();
    codeAttribute.computeMaxStack();
    methodInfo.setCodeAttribute(codeAttribute);
    mapperCtClass.addMethod(createMethod);
  }
  
  private CtMethod implementAndGetMethod(CtClass mapperCtClass, CtClass superClass, String name) throws NotFoundException, CannotCompileException {
    CtMethod abstractMethod = superClass.getDeclaredMethod(name);
    CtMethod mapperMethod = new CtMethod(abstractMethod, mapperCtClass, null);
    mapperMethod.setModifiers(mapperMethod.getModifiers() & ~Modifier.ABSTRACT);
    return mapperMethod;
  }
  
  private void doCreate(MapperBuilderContext context) throws NotFoundException, CannotCompileException, BadBytecode {
    context.newTargetClass();
    CtClass[] paramTypes = handleConstructorArguments(context);
    context.invokeConstructor(paramTypes);
    context.addReturn();
  }

  private CtClass[] handleConstructorArguments(MapperBuilderContext context)
      throws NotFoundException, CannotCompileException, BadBytecode {
    int i = 0;
    CtClass[] paramTypes = findConstructorParameterTypes(context.targetClass);
    for (ConstructorArgumentMapperBuilder constructorArgument : constructorArguments) {
      constructorArgument.addToBytecode(context, paramTypes[i++]);
    }
    return paramTypes;
  }
  
  private CtClass[] findConstructorParameterTypes(CtClass returnClass) throws NotFoundException {
    for (CtConstructor constructor : returnClass.getConstructors()) {
      if (constructor.getParameterTypes().length == constructorArguments.size()) {
        return constructor.getParameterTypes();
      }
    }
    throw new RuntimeException("No suitable constructor found.");
  }
  
  private void doApply(MapperBuilderContext context) throws NotFoundException, CannotCompileException, BadBytecode {
    handleProperties(context);
    context.loadAndCheckReturnType();
    context.addReturn();
  }

  private void handleProperties(MapperBuilderContext context) throws NotFoundException, CannotCompileException, BadBytecode {
    for (PropertyMapperBuilder property : properties) {
      property.addToBytecode(context);
    }
  }
  
  private void addFields(CtClass mapperClass, ClassPool classPool, List<? extends AbstractElementMapperBuilder> elements)
      throws CannotCompileException, NotFoundException {
    for (AbstractElementMapperBuilder element : elements) {
      element.addFields(mapperClass, classPool);
    }
  }
  
  private void setFields(ClassMapper<?, ?> mapperInstance, List<? extends AbstractElementMapperBuilder> elements)
      throws IllegalAccessException {
    for (AbstractElementMapperBuilder element : elements) {
      element.setFields(mapperInstance);
    }
  }

}