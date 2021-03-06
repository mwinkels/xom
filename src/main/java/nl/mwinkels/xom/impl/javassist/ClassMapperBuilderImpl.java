package nl.mwinkels.xom.impl.javassist;

import javassist.*;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.MethodInfo;
import nl.mwinkels.xom.impl.ClassMapper;
import nl.mwinkels.xom.impl.ClassMapperBuilder;

import java.util.ArrayList;
import java.util.List;

import static nl.mwinkels.xom.impl.javassist.Utils.abstractMapperClass;
import static nl.mwinkels.xom.impl.javassist.Utils.classPool;

public class ClassMapperBuilderImpl implements ClassMapperBuilder {

    private final NameGenerator nameGenerator = new DefaultNameGenerator();
    private final List<PropertyMapperBuilder> properties = new ArrayList<PropertyMapperBuilder>();
    private final List<ConstructorArgumentMapperBuilder> constructorArguments = new ArrayList<ConstructorArgumentMapperBuilder>();

    public void addProperty(PropertyMapperBuilder propertyMapperBuilder) {
        properties.add(propertyMapperBuilder);
    }

    public void addConstructorArgument(ConstructorArgumentMapperBuilder constructorArgumentMapperBuilder) {
        constructorArguments.add(constructorArgumentMapperBuilder);
    }

    @Override
    public <S, T> ClassMapper<S, T> build(Class<S> sourceClass, Class<T> targetClass) {
        try {
            String mapperClassName = nameGenerator.mapperClassName(sourceClass, targetClass);
            CtClass mapperCtClass = classPool.makeClass(mapperClassName);
            return build(mapperCtClass, classPool.get(sourceClass.getName()), classPool.get(targetClass.getName()));
        } catch (CannotCompileException e) {
            throw new RuntimeException(e);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected <S, T> ClassMapper<S, T> build(CtClass mapperCtClass, CtClass souceCtClass, CtClass targetCtClass) throws CannotCompileException {
        mapperCtClass.setSuperclass(abstractMapperClass);
        addCreateMethod(mapperCtClass, souceCtClass, targetCtClass);
        addApplyMethod(mapperCtClass, souceCtClass, targetCtClass);
        addFields(mapperCtClass, constructorArguments);
        addFields(mapperCtClass, properties);
        @SuppressWarnings("unchecked")
        Class<ClassMapper<S, T>> mapperClass = mapperCtClass.toClass();
        return createMapper(mapperClass);
    }

    private <T, S> ClassMapper<S, T> createMapper(Class<ClassMapper<S, T>> mapperClass) {
        try {
            ClassMapper<S, T> mapperInstance = mapperClass.newInstance();
            setFields(mapperInstance, constructorArguments);
            setFields(mapperInstance, properties);
            return mapperInstance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void addCreateMethod(CtClass mapperCtClass, CtClass souceCtClass, CtClass targetCtClass) throws CannotCompileException {
        CtMethod createMethod = implementAndGetMethod(mapperCtClass, abstractMapperClass, "create");
        MethodInfo methodInfo = createMethod.getMethodInfo();
        Bytecode bytecode = new Bytecode(methodInfo.getConstPool(), 0, 2);
        MapperBuilderContext context = new MapperBuilderContext(souceCtClass, targetCtClass, mapperCtClass, bytecode);
        doCreate(context);
        doAddMethodBytecode(mapperCtClass, createMethod, methodInfo, bytecode);
    }

    private void addApplyMethod(CtClass mapperCtClass, CtClass souceCtClass, CtClass targetCtClass) throws CannotCompileException {
        CtMethod createMethod = implementAndGetMethod(mapperCtClass, abstractMapperClass, "apply");
        MethodInfo methodInfo = createMethod.getMethodInfo();
        Bytecode bytecode = new Bytecode(methodInfo.getConstPool(), 0, 3);
        MapperBuilderContext context = new MapperBuilderContext(souceCtClass, targetCtClass, mapperCtClass, bytecode);
        doApply(context);
        doAddMethodBytecode(mapperCtClass, createMethod, methodInfo, bytecode);
    }

    private void doAddMethodBytecode(CtClass mapperCtClass, CtMethod createMethod, MethodInfo methodInfo, Bytecode bytecode) throws CannotCompileException {
        CodeAttribute codeAttribute = bytecode.toCodeAttribute();
        try {
            codeAttribute.computeMaxStack();
        } catch (BadBytecode e) {
            throw new AssertionError(e);
        }
        methodInfo.setCodeAttribute(codeAttribute);
        mapperCtClass.addMethod(createMethod);
    }

    private CtMethod implementAndGetMethod(CtClass mapperCtClass, CtClass superClass, String name) throws CannotCompileException {
        try {
            CtMethod abstractMethod = superClass.getDeclaredMethod(name);
            CtMethod mapperMethod = new CtMethod(abstractMethod, mapperCtClass, null);
            mapperMethod.setModifiers(mapperMethod.getModifiers() & ~Modifier.ABSTRACT);
            return mapperMethod;
        } catch (NotFoundException e) {
            throw new AssertionError(e);
        }
    }

    private void doCreate(MapperBuilderContext context) throws CannotCompileException {
        context.newTargetClass();
        CtClass[] paramTypes = handleConstructorArguments(context);
        context.invokeConstructor(paramTypes);
        context.addReturn();
    }

    private CtClass[] handleConstructorArguments(MapperBuilderContext context)
            throws CannotCompileException {
        int i = 0;
        CtClass[] paramTypes = findConstructorParameterTypes(context.targetClass);
        for (ConstructorArgumentMapperBuilder constructorArgument : constructorArguments) {
            constructorArgument.addToBytecode(context, paramTypes[i++]);
        }
        return paramTypes;
    }

    private CtClass[] findConstructorParameterTypes(CtClass returnClass) {
        for (CtConstructor constructor : returnClass.getConstructors()) {
            try {
                CtClass[] parameterTypes = constructor.getParameterTypes();
                if (parameterTypes.length == constructorArguments.size()) {
                    return parameterTypes;
                }
            } catch (NotFoundException e) {
                throw new AssertionError(e);
            }
        }
        throw new RuntimeException("No suitable constructor found.");
    }

    private void doApply(MapperBuilderContext context) throws CannotCompileException {
        handleProperties(context);
        context.loadAndCheckReturnType();
        context.addReturn();
    }

    private void handleProperties(MapperBuilderContext context) throws CannotCompileException {
        for (PropertyMapperBuilder property : properties) {
            property.addToBytecode(context);
        }
    }

    private void addFields(CtClass mapperClass, List<? extends AbstractElementMapperBuilder> elements)
            throws CannotCompileException {
        for (AbstractElementMapperBuilder element : elements) {
            element.addFields(mapperClass);
        }
    }

    private void setFields(ClassMapper<?, ?> mapperInstance, List<? extends AbstractElementMapperBuilder> elements)
            throws IllegalAccessException {
        for (AbstractElementMapperBuilder element : elements) {
            element.setFields(mapperInstance);
        }
    }

}