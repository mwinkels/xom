package nl.mwinkels.xom.impl.javassist;

import java.util.LinkedList;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import nl.mwinkels.xom.conversion.Converter;
import nl.mwinkels.xom.impl.ConverterRegistry;
import org.apache.commons.lang3.StringUtils;

import nl.mwinkels.xom.impl.ClassMapperRegistry;

public class PropertyMapperBuilder extends AbstractElementMapperBuilder {
  
  private final String target;
  
  public PropertyMapperBuilder(ConverterRegistry converterRegistry, ClassMapperRegistry mapperRegistry, String source, String target, Converter<?,?> converter,
      ClassMapperBuilderImpl nestedClassMapperBuilder) {
    super(converterRegistry, mapperRegistry, source, converter, nestedClassMapperBuilder);
    this.target = target;
  }
  
  public void addToBytecode(MapperBuilderContext context) throws CannotCompileException {
    LinkedList<GetterDef> getterChain = findGetterChain(context.sourceClass);
    
    CtMethod setterMethod = findSetterMethod(context.targetClass);
    checkSetterSignature(setterMethod);
    CtClass targetType = getSetterType(setterMethod);
    
    CtClass finalSourceType = getLastGetterType(getterChain, context.sourceClass);
    
    if (finalSourceType.isArray()) {
      
      context.ensureMaxLocals(4);
      prepareInvokeGetter(context);
      invokeGetterChain(context, getterChain);
      context.storeTemporaryResult(finalSourceType);
      if (targetType.isArray()) {
        createNestedMapper(context, getComponentType(finalSourceType), getComponentType(targetType));
        findMapperOrConverterIfRequired(getComponentType(finalSourceType), getComponentType(targetType));
        context.ensureMaxLocals(7);
        context.storeLoopSize();
        context.createResultArray(getComponentType(targetType));
        context.storeResultValue(targetType);
        MapperBuilderContext.ForLoop forLoop = context.new ForLoop(getComponentType(targetType));
        forLoop.start();
        prepareInvokeMapper(context);
        prepareInvokeConverter(context);
        forLoop.middle();
        invokeConverter(context, getComponentType(targetType));
        invokeMapper(context, getComponentType(targetType));
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

  private CtClass getComponentType(CtClass finalSourceType) {
    try {
      return finalSourceType.getComponentType();
    } catch (NotFoundException e) {
      throw new AssertionError(e);
    }
  }

  private CtClass getSetterType(CtMethod setterMethod) {
    try {
      return setterMethod.getParameterTypes()[0];
    } catch (NotFoundException e) {
      throw new AssertionError(e);
    }
  }

  private CtMethod findSetterMethod(CtClass targetCtClass) {
    String setterName = "set" + StringUtils.capitalize(target);
    return findMethod(targetCtClass, setterName);
  }
  
  private void checkSetterSignature(CtMethod setterMethod) {
    try {
      CtClass[] setterParameterTypes = setterMethod.getParameterTypes();
      if (setterParameterTypes.length != 1) {
        throw new RuntimeException("Setter has incorrect number of parameters!");
      }
    } catch (NotFoundException e) {
      throw new AssertionError(e);
    }
  }
  
  @Override
  protected String getName(String suffix) {
    return target + suffix;
  }
  
}
