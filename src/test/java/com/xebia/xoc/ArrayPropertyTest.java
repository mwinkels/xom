package com.xebia.xoc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;

import org.junit.Test;

import com.xebia.xoc.config.ClassMapperConfig;
import com.xebia.xoc.conversion.ConversionException;
import com.xebia.xoc.conversion.Converter;
import com.xebia.xoc.javassist.AbstractClassMapper;
import com.xebia.xoc.util.BytecodePrinter;

public class ArrayPropertyTest {
  
  public static class Source {
    
    private Integer[] a;
    
    public Integer[] getA() {
      return a;
    }
    
    public void setA(Integer[] a) {
      this.a = a;
    }
  }
  
  public static class Target {
    
    private String[] b;
    
    public String[] getB() {
      return b;
    }
    
    public void setB(String b[]) {
      this.b = b;
    }
    
  }
  
  @Test
  public void shouldCreateMapper() {
    ClassMapperConfig config = new ClassMapperConfig().property("b").from("a").add();
    ClassMapper<Source, Target> mapper = new ConfigurableMapper().withMapper(config, Source.class, Target.class);
    assertThat(mapper, is(notNullValue()));
    Source source = new Source();
    source.setA(new Integer[]{12,14});
    Target target = mapper.map(source);
    assertThat(target, is(notNullValue()));
    assertThat(target.getB(), is(new String[]{"12", "14"}));
  }
  
  public static class MyMapper  {
    
    public Converter<Integer, String> aConverter; 

    protected Target apply(Object source, Object target) throws ConversionException {
      Integer[] as = ((Source) source).getA();
      int size = as.length;
      String[] bs = new String[size];
      int i = 0;
      while (i < size) {
        bs[i] = aConverter.convert(as[i]);
        i++;
      }
      ((Target) target).setB(bs);
      return (Target) target;
    }
    
  }
  
  public static void main(String[] args) throws Throwable {
    ClassPool pool = ClassPool.getDefault();
    CtClass ctClass = pool.get(MyMapper.class.getName());
    System.out.println(new BytecodePrinter(ctClass.getDeclaredMethod("apply").getMethodInfo().getCodeAttribute()).makeString());
  }
  
}