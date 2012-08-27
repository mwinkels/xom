package com.xebia.xoc.javassist;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.Bytecode;

public class MapperBuilderContext {
  public final CtClass sourceClass;
  public final CtClass targetClass;
  public final CtClass mapperClass;
  public final ClassPool classPool;
  private final Bytecode bytecode;
  private final CtClass objectType;
  
  public MapperBuilderContext(CtClass sourceClass, CtClass targetClass, CtClass mapperClass, ClassPool classPool, Bytecode bytecode) throws NotFoundException {
    this.sourceClass = sourceClass;
    this.targetClass = targetClass;
    this.mapperClass = mapperClass;
    this.classPool = classPool;
    this.bytecode = bytecode;
    this.objectType = classPool.get("java.lang.Object");
  }

  public void newTargetClass() {
    bytecode.addNew(targetClass);
    bytecode.add(Bytecode.DUP);
  }
  
  public void invokeConstructor(CtClass[] paramTypes) {
    bytecode.addInvokespecial(targetClass, "<init>", CtClass.voidType, paramTypes);
  }
  
  public void loadAndCheckSource() {
    bytecode.addAload(1);
    bytecode.addCheckcast(sourceClass);
  }
  
  public void loadAndCheckReturnType() {
    bytecode.addAload(2);
    bytecode.addCheckcast(targetClass);
  }
  
  public void addReturn() {
    bytecode.addReturn(targetClass);
  }

  public void loadField(String fieldName, String descriptor) {
    bytecode.addAload(0);
    bytecode.addGetfield(mapperClass, fieldName, descriptor);
  }

  public void invokeTransformMethod(String classname, String methodName, CtClass targetType) throws NotFoundException {
    bytecode.addInvokeinterface(classPool.get(classname), methodName, objectType, new CtClass[] { objectType }, 2);
    bytecode.addCheckcast(targetType);
  }

  protected void invokeGetter(AbstractElementMapperBuilder abstractElementMapperBuilder, CtClass sourceCtClass, CtMethod getterMethod) throws NotFoundException {
    bytecode.addInvokevirtual(sourceCtClass, getterMethod.getName(), getterMethod.getReturnType(), getterMethod.getParameterTypes());
  }
  
  public void invokeSetter(CtMethod setterMethod) throws NotFoundException {
    bytecode.addInvokevirtual(targetClass, setterMethod.getName(), CtClass.voidType, setterMethod.getParameterTypes());
  }

}
