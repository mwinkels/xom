package com.xebia.xoc.javassist;

import java.util.LinkedList;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;

import org.apache.commons.lang.StringUtils;

import com.xebia.xoc.ClassMapperRegistry;
import com.xebia.xoc.conversion.Converter;
import com.xebia.xoc.conversion.ConverterRegistry;
import com.xebia.xoc.javassist.MapperBuilderContext.ForLoop;
import com.xebia.xoc.util.BytecodePrinter;

public class PropertyMapperBuilder extends AbstractElementMapperBuilder {
  
  private final String target;
  
  public PropertyMapperBuilder(ConverterRegistry converterRegistry, ClassMapperRegistry mapperRegistry, String source, String target, Converter<?,?> converter,
      ClassMapperBuilder nestedClassMapperBuilder) {
    super(converterRegistry, mapperRegistry, source, converter, nestedClassMapperBuilder);
    this.target = target;
  }
  
  public void addToBytecode(MapperBuilderContext context) throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException, BadBytecode {
    LinkedList<GetterDef> getterChain = findGetterChain(context.sourceClass);
    
    CtMethod setterMethod = findSetterMethod(context.targetClass);
    checkSetterSignature(setterMethod);
    CtClass targetType = setterMethod.getParameterTypes()[0];
    
    CtClass finalSourceType = getLastGetterType(getterChain, context.sourceClass);
    
    if (finalSourceType.isArray()) {
      
      context.ensureMaxLocals(4);
      prepareInvokeGetter(context);
      invokeGetterChain(context, getterChain);
      context.storeTemporaryResult(finalSourceType);
      if (targetType.isArray()) {
        createNestedMapper(context, finalSourceType.getComponentType(), targetType.getComponentType());
        findMapperOrConverterIfRequired(finalSourceType.getComponentType(), targetType.getComponentType());
        context.ensureMaxLocals(7);
        context.storeLoopSize();
        context.createResultArray(targetType.getComponentType());
        context.storeResultValue(targetType);
        ForLoop forLoop = context.new ForLoop(targetType.getComponentType());
        forLoop.start();
        prepareInvokeMapper(context);
        prepareInvokeConverter(context);
        forLoop.middle();
        invokeConverter(context, targetType.getComponentType());
        invokeMapper(context, targetType.getComponentType());
        forLoop.end();
        context.loadAndCheckReturnType();
        context.loadResultValue(targetType);
        context.invokeSetter(setterMethod);
      }
      // TODO collection
      // TODO scalar
    } else {
      createNestedMapper(context, finalSourceType, targetType);
      findMapperOrConverterIfRequired(finalSourceType, targetType);
      
      context.loadAndCheckReturnType();
      invokerMapperConverterAndGetters(context, getterChain, targetType);
      context.invokeSetter(setterMethod);
    }
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
