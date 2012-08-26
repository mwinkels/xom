package com.xebia.xoc.javassist;

import java.util.LinkedList;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Bytecode;

import com.xebia.xoc.conversion.Converter;
import com.xebia.xoc.conversion.ConverterRegistry;

public class ConstructorArgumentMapperBuilder extends AbstractElementMapperBuilder {
  
  private final int index;
  
  @SuppressWarnings("rawtypes")
  public ConstructorArgumentMapperBuilder(ClassMapperBuilder classMapperBuilder, ConverterRegistry converterRegistry, String source, int index,
      Converter converter) {
    super(classMapperBuilder, converterRegistry, source, converter);
    this.index = index;
  }
  
  @Override
  public String getConverterFieldName() {
    return "ca" + index + "Converter";
  }
  
  public void addToBytecode(Bytecode bytecode, ClassPool classPool, CtClass targetType, CtClass mapperCtClass) throws NotFoundException {
    CtClass sourceCtClass = sourceCtClass(classPool);
    LinkedList<GetterDef> getterChain = findGetterChain(sourceCtClass);
    
    findConverterIfRequired(getterChain.getLast().ctMethod.getReturnType(), targetType);
    
    prepareInvokeConverter(bytecode, mapperCtClass);
    prepareInvokeGetter(bytecode, sourceCtClass);
    invokeGetterChain(bytecode, getterChain);
    invokeConverter(bytecode, classPool, targetType);
  }
  
}
