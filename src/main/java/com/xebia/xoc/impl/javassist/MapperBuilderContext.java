package com.xebia.xoc.impl.javassist;

import static com.xebia.xoc.impl.javassist.Utils.classPool;
import static com.xebia.xoc.impl.javassist.Utils.objectType;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.Bytecode;
import javassist.bytecode.Opcode;

public class MapperBuilderContext {
  private static final int LOOP_SIZE_IX = 4;
  private static final int TEMP_RESULT_IX = 3;
  private static final int RESULT_IX = 5;
  public final CtClass sourceClass;
  public final CtClass targetClass;
  public final CtClass mapperClass;
  private final Bytecode bytecode;
  
  public MapperBuilderContext(CtClass sourceClass, CtClass targetClass, CtClass mapperClass, Bytecode bytecode) {
    this.sourceClass = sourceClass;
    this.targetClass = targetClass;
    this.mapperClass = mapperClass;
    this.bytecode = bytecode;
  }

  public void newTargetClass() {
    bytecode.addNew(targetClass);
    bytecode.add(Bytecode.DUP);
  }
  
  public void invokeConstructor(CtClass[] paramTypes) {
    bytecode.addInvokespecial(targetClass, "<init>", CtClass.voidType, paramTypes);
  }
  
  public void loadAndCheckSource() {
    bytecode.addAload(1);
    bytecode.addCheckcast(sourceClass);
  }
  
  public void loadAndCheckReturnType() {
    bytecode.addAload(2);
    bytecode.addCheckcast(targetClass);
  }
  
  public void addReturn() {
    bytecode.addReturn(targetClass);
  }

  public void loadField(String fieldName, String descriptor) {
    bytecode.addAload(0);
    bytecode.addGetfield(mapperClass, fieldName, descriptor);
  }

  public void invokeTransformMethod(String classname, String methodName, CtClass targetType) throws NotFoundException {
    bytecode.addInvokeinterface(classPool.get(classname), methodName, objectType, new CtClass[] { objectType }, 2);
    bytecode.addCheckcast(targetType);
  }

  protected void invokeGetter(CtClass sourceCtClass, CtMethod getterMethod) {
    try {
      bytecode.addInvokevirtual(sourceCtClass, getterMethod.getName(), getterMethod.getReturnType(), getterMethod.getParameterTypes());
    } catch (NotFoundException e) {
      throw new AssertionError(e);
    }
  }
  
  public void invokeSetter(CtMethod setterMethod) {
    try {
      bytecode.addInvokevirtual(targetClass, setterMethod.getName(), CtClass.voidType, setterMethod.getParameterTypes());
    } catch (NotFoundException e) {
      throw new AssertionError(e);
    }
  }

  public void storeTemporaryResult(CtClass type) {
    bytecode.addStore(TEMP_RESULT_IX, type);
  }
  
  public void createResultArray(CtClass componentType) {
    bytecode.addIload(LOOP_SIZE_IX);
    bytecode.addAnewarray(componentType.getName());
  }
  
  public void storeResultValue(CtClass type) {
    bytecode.addStore(RESULT_IX, type);
  }

  public void loadResultValue(CtClass type) {
    bytecode.addLoad(RESULT_IX, type);
  }
  
  public void ensureMaxLocals(int maxLocals) {
    if (bytecode.getMaxLocals() < maxLocals) {
      bytecode.setMaxLocals(maxLocals);
    }
  }

  public void storeLoopSize() {
    bytecode.addAload(TEMP_RESULT_IX);
    bytecode.add(Bytecode.ARRAYLENGTH);
    bytecode.addIstore(LOOP_SIZE_IX);
  }
  
  public class ForLoop {

    private static final int LOOP_IX = 6;

    private final CtClass targetType;
    
    private int gotoOpIndex;

    public ForLoop(CtClass targetType) {
      this.targetType = targetType;
    }

    public void start() {
      bytecode.addIconst(0);
      bytecode.addIstore(LOOP_IX);
      bytecode.addOpcode(Opcode.GOTO);
      gotoOpIndex = bytecode.currentPc();
      bytecode.addIndex(0);
      bytecode.addAload(RESULT_IX);
      bytecode.addIload(LOOP_IX);
    }
    
    public void middle() {
      bytecode.addLoad(TEMP_RESULT_IX, targetType);
      bytecode.addIload(LOOP_IX);
      bytecode.addOpcode(Opcode.AALOAD);
    }
    
    public void end() {
      bytecode.addOpcode(Opcode.AASTORE);
      bytecode.addOpcode(Opcode.IINC);
      bytecode.add(LOOP_IX);
      bytecode.add(1);
      bytecode.write16bit(gotoOpIndex, bytecode.currentPc() - gotoOpIndex + 1);
      bytecode.addIload(LOOP_IX);
      bytecode.addIload(LOOP_SIZE_IX);
      bytecode.addOpcode(Opcode.IF_ICMPLT);
      bytecode.addIndex(gotoOpIndex - bytecode.currentPc() + 3);
    }
    
  }
}
