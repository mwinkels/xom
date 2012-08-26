package com.xebia.xoc.javassist;

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
  
  void addToBytecode(Bytecode bytecode, ClassPool classPool, CtClass mapperCtClass) throws NotFoundException {
    CtClass sourceCtClass = sourceCtClass(classPool);
    CtMethod getterMethod = findGetterMethod(sourceCtClass);
    
    CtClass targetCtClass = targetCtClass(classPool);
    CtMethod setterMethod = findSetterMethod(targetCtClass);
    
    checkGetterAndSetter(getterMethod, setterMethod);
    
    CtClass targetType = setterMethod.getParameterTypes()[0];
    
    findConverterIfRequired(getterMethod.getReturnType(), targetType);
    
    bytecode.addAload(2);
    prepareConverter(bytecode, mapperCtClass);
    invokeGetter(bytecode, sourceCtClass, getterMethod);
    invokeConverter(bytecode, classPool, targetType);
    bytecode.addInvokevirtual(targetCtClass, setterMethod.getName(), CtClass.voidType, setterMethod.getParameterTypes());
  }
  
  private CtMethod findSetterMethod(CtClass targetCtClass) {
    String setterName = "set" + StringUtils.capitalize(getTarget());
    return findMethod(targetCtClass, setterName);
  }
  
  private void checkGetterAndSetter(CtMethod getterMethod, CtMethod setterMethod) throws NotFoundException {
    if (getterMethod.getParameterTypes().length != 0) {
      throw new RuntimeException("Getter needs parameters!");
    }
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
