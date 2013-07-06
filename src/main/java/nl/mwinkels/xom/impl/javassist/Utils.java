package nl.mwinkels.xom.impl.javassist;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class Utils {

    public static final ClassPool classPool = ClassPool.getDefault();

    private static final CtClass getExistingClass(ClassPool classPool, String classname) {
        try {
            return classPool.get(classname);
        } catch (NotFoundException e) {
            throw new AssertionError(e);
        }
    }

    public static final CtClass objectType = getExistingClass(classPool, "java.lang.Object");

    public static final CtClass abstractMapperClass = getExistingClass(classPool, "nl.mwinkels.xom.impl.AbstractClassMapper");

    public static final CtClass converterInterface = getExistingClass(classPool, "nl.mwinkels.xom.conversion.Converter");

    public static final CtClass classMapperInterface = getExistingClass(classPool, "nl.mwinkels.xom.impl.ClassMapper");


}
