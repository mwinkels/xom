package nl.mwinkels.xom.impl.javassist;

import java.util.LinkedList;

import javassist.CannotCompileException;
import javassist.CtClass;

import nl.mwinkels.xom.conversion.Converter;
import nl.mwinkels.xom.impl.ClassMapperRegistry;
import nl.mwinkels.xom.impl.ConverterRegistry;

public class ConstructorArgumentMapperBuilder extends AbstractElementMapperBuilder {
  
  private final int index;
  
  @SuppressWarnings("rawtypes")
  public ConstructorArgumentMapperBuilder(ConverterRegistry converterRegistry, ClassMapperRegistry mapperRegistry, String source, int index, Converter converter,
      ClassMapperBuilderImpl nestedClassMapperBuilder) {
    super(converterRegistry, mapperRegistry, source, converter, nestedClassMapperBuilder);
    this.index = index;
  }
  
  @Override
  protected String getName(String suffix) {
    return "ca" + index + suffix;
  }
  
  public void addToBytecode(MapperBuilderContext context, CtClass targetType) throws CannotCompileException {
    LinkedList<GetterDef> getterChain = findGetterChain(context.sourceClass);
    
    CtClass finalSourceType = getLastGetterType(getterChain, context.sourceClass);
    createNestedMapper(context, finalSourceType, targetType);
    findMapperOrConverterIfRequired(finalSourceType, targetType);
    
    invokerMapperConverterAndGetters(context, getterChain, targetType);
  }
  
}
