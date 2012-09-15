package com.xebia.xoc.impl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.xebia.xoc.MappingException;
import com.xebia.xoc.config.ClassMapperConfig;
import com.xebia.xoc.conversion.ConversionException;
import com.xebia.xoc.conversion.Converter;
import com.xebia.xoc.impl.ClassMapper;
import com.xebia.xoc.impl.ConfigurableMapper;
import com.xebia.xoc.impl.MapperFactory;

public class PropertySimpleAndCustomConverterTest extends TestBase {
  
  public PropertySimpleAndCustomConverterTest(MapperFactory mapperFactory) {
    super(mapperFactory);
  }

  public class StringToDateConverter implements Converter<String, Date> {
    
    @Override
    public Date convert(String value) throws ConversionException {
      try {
        return new SimpleDateFormat("dd/MM/yyyy").parse(value);
      } catch (ParseException e) {
        throw new ConversionException("Cannot convert date.", e);
      }
    }
    
    @Override
    public boolean canConvert(Class<?> sourceClass, Class<?> targetCLass) {
      return true;
    }
    
  }
  
  public static class Source {
    
    private Integer a;
    private String s;
    
    public String getS() {
      return s;
    }
    
    public void setS(String s) {
      this.s = s;
    }
    
    public Integer getA() {
      return a;
    }
    
    public void setA(Integer a) {
      this.a = a;
    }
  }
  
  public static class Target {
    
    public Date getD() {
      return d;
    }
    
    public void setD(Date d) {
      this.d = d;
    }
    
    private String b;
    private Date d;
    
    public String getB() {
      return b;
    }
    
    public void setB(String b) {
      this.b = b;
    }
    
  }
  
  @Test
  public void shouldCreateMapper() throws MappingException {
    ClassMapperConfig config = new ClassMapperConfig()//
        .property("b").from("a").add()//
        .property("d").from("s").withConverter(new StringToDateConverter()).add();
    ClassMapper<Source, Target> mapper = new ConfigurableMapper(mapperFactory).withMapper(config, Source.class, Target.class);
    assertThat(mapper, is(notNullValue()));
    Source source = new Source();
    source.setA(12);
    source.setS("01/02/2012");
    Target target = mapper.map(source);
    assertThat(target, is(notNullValue()));
    assertThat(target.getB(), is(Integer.toString(source.getA())));
    assertThat(target.getD(), is(new Date(112, Calendar.FEBRUARY, 1)));
  }
  
}