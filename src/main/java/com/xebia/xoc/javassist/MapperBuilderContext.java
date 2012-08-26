package com.xebia.xoc.javassist;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.Bytecode;

public class MapperBuilderContext {
  public final CtClass sourceClass;
  public final CtClass targetClass;
  public final CtClass mapperClass;
  public final ClassPool classPool;
  public final Bytecode bytecode;
  
  public MapperBuilderContext(CtClass sourceClass, CtClass targetClass, CtClass mapperClass, ClassPool classPool, Bytecode bytecode) {
    this.sourceClass = sourceClass;
    this.targetClass = targetClass;
    this.mapperClass = mapperClass;
    this.classPool = classPool;
    this.bytecode = bytecode;
  }
  
}
