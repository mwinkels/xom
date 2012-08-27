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
    findMapperOrConverterIfRequired(finalSourceType, targetType);
    
    context.loadAndCheckReturnType();
    prepareInvokeMapper(context);
    prepareInvokeConverter(context);
    prepareInvokeGetter(context);
    invokeGetterChain(context, getterChain);
    invokeConverter(context, targetType);
    invokeMapper(context, targetType);
    context.invokeSetter(setterMethod);
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
