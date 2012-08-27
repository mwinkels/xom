package com.xebia.xoc.javassist;

import java.util.LinkedList;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.Bytecode;

import org.apache.commons.lang.StringUtils;

import com.xebia.xoc.ClassMapperRegistry;
import com.xebia.xoc.conversion.Converter;
import com.xebia.xoc.conversion.ConverterRegistry;

public class PropertyMapperBuilder extends AbstractElementMapperBuilder {
  
  private final String target;
  
  public PropertyMapperBuilder(ConverterRegistry converterRegistry, ClassMapperRegistry mapperRegistry, String source, String target, Converter<?,?> converter,
      ClassMapperBuilder nestedClassMapperBuilder) {
    super(converterRegistry, mapperRegistry, source, converter, nestedClassMapperBuilder);
    this.target = target;
  }
  
  public void addToBytecode(MapperBuilderContext context) throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException {
    LinkedList<GetterDef> getterChain = findGetterChain(context.sourceClass);
    
    CtMethod setterMethod = findSetterMethod(context.targetClass);
    checkSetterSignature(setterMethod);
    CtClass targetType = setterMethod.getParameterTypes()[0];
    
    CtClass finalSourceType = getLastGetterType(getterChain, context.sourceClass);
    createNestedMapper(context, finalSourceType, targetType);
    findConverterIfRequired(finalSourceType, targetType);
    
    context.bytecode.addAload(2);
    prepareInvokeMapper(context.bytecode, context.mapperClass);
    prepareInvokeConverter(context.bytecode, context.mapperClass);
    prepareInvokeGetter(context.bytecode, context.sourceClass);
    invokeGetterChain(context.bytecode, getterChain);
    invokeConverter(context.bytecode, context.classPool, targetType);
    invokeMapper(context.bytecode, context.classPool, targetType);
    invokeSetter(context.bytecode, context.targetClass, setterMethod);
  }

  private void invokeSetter(Bytecode bytecode, CtClass targetCtClass, CtMethod setterMethod) throws NotFoundException {
    bytecode.addInvokevirtual(targetCtClass, setterMethod.getName(), CtClass.voidType, setterMethod.getParameterTypes());
  }
  
  private CtMethod findSetterMethod(CtClass targetCtClass) {
    String setterName = "set" + StringUtils.capitalize(target);
    return findMethod(targetCtClass, setterName);
  }
  
  private void checkSetterSignature(CtMethod setterMethod) throws NotFoundException {
    CtClass[] setterParameterTypes = setterMethod.getParameterTypes();
    if (setterParameterTypes.length != 1) {
      throw new RuntimeException("Setter has incorrect number of parameters!");
    }
  }
  
  @Override
  protected String getName(String suffix) {
    return target + suffix;
  }
  
}
