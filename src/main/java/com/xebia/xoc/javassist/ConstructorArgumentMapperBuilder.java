package com.xebia.xoc.javassist;

import java.util.LinkedList;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

import com.xebia.xoc.ClassMapperRegistry;
import com.xebia.xoc.conversion.Converter;
import com.xebia.xoc.conversion.ConverterRegistry;

public class ConstructorArgumentMapperBuilder extends AbstractElementMapperBuilder {
  
  private final int index;
  
  @SuppressWarnings("rawtypes")
  public ConstructorArgumentMapperBuilder(ConverterRegistry converterRegistry, ClassMapperRegistry mapperRegistry, String source, int index, Converter converter,
      ClassMapperBuilder nestedClassMapperBuilder) {
    super(converterRegistry, mapperRegistry, source, converter, nestedClassMapperBuilder);
    this.index = index;
  }
  
  @Override
  protected String getName(String suffix) {
    return "ca" + index + suffix;
  }
  
  public void addToBytecode(MapperBuilderContext context, CtClass targetType) throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException {
    LinkedList<GetterDef> getterChain = findGetterChain(context.sourceClass);
    
    CtClass finalSourceType = getLastGetterType(getterChain, context.sourceClass);
    createNestedMapper(context, finalSourceType, targetType);
    findMapperOrConverterIfRequired(finalSourceType, targetType);
    
    prepareInvokeMapper(context);
    prepareInvokeConverter(context);
    prepareInvokeGetter(context);
    invokeGetterChain(context, getterChain);
    invokeConverter(context, targetType);
    invokeMapper(context, targetType);
  }
  
}
