package com.xebia.xoc.javassist;

import java.util.LinkedList;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.Bytecode;

import org.apache.commons.lang.StringUtils;

import com.xebia.xoc.conversion.Converter;
import com.xebia.xoc.conversion.ConverterRegistry;

public class PropertyMapperBuilder extends AbstractElementMapperBuilder {
  
  private final String target;
  
  @SuppressWarnings("rawtypes")
  public PropertyMapperBuilder(ClassMapperBuilder classMapperBuilder, ConverterRegistry converterRegistry, String source, String target,
      Converter converter) {
    super(classMapperBuilder, converterRegistry, source, converter);
    this.target = target;
  }
  
  public void addToBytecode(Bytecode bytecode, ClassPool classPool, CtClass mapperCtClass) throws NotFoundException {
    CtClass sourceCtClass = sourceCtClass(classPool);
    LinkedList<GetterDef> getterChain = findGetterChain(sourceCtClass);
    
    CtClass targetCtClass = targetCtClass(classPool);
    CtMethod setterMethod = findSetterMethod(targetCtClass);
    checkSetterSignature(setterMethod);
    CtClass targetType = setterMethod.getParameterTypes()[0];
    
    findConverterIfRequired(getterChain.getLast().ctMethod.getReturnType(), targetType);
    
    bytecode.addAload(2);
    prepareInvokeConverter(bytecode, mapperCtClass);
    prepareInvokeGetter(bytecode, sourceCtClass);
    invokeGetterChain(bytecode, getterChain);
    invokeConverter(bytecode, classPool, targetType);
    invokeSetter(bytecode, targetCtClass, setterMethod);
  }

  private void invokeSetter(Bytecode bytecode, CtClass targetCtClass, CtMethod setterMethod) throws NotFoundException {
    bytecode.addInvokevirtual(targetCtClass, setterMethod.getName(), CtClass.voidType, setterMethod.getParameterTypes());
  }
  
  private CtMethod findSetterMethod(CtClass targetCtClass) {
    String setterName = "set" + StringUtils.capitalize(getTarget());
    return findMethod(targetCtClass, setterName);
  }
  
  private void checkSetterSignature(CtMethod setterMethod) throws NotFoundException {
    CtClass[] setterParameterTypes = setterMethod.getParameterTypes();
    if (setterParameterTypes.length != 1) {
      throw new RuntimeException("Setter has incorrect number of parameters!");
    }
  }

  public String getTarget() {
    return target;
  }
  
  @Override
  public String getConverterFieldName() {
    return getTarget() + "Converter";
  }
  
}
