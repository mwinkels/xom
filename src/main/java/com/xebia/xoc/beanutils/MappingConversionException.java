package com.xebia.xoc.beanutils;

import java.util.List;

import com.xebia.xoc.MappingException;
import com.xebia.xoc.conversion.ConversionException;

public class MappingConversionException extends MappingException {

  private List<ConversionException> conversionExceptions;

  public MappingConversionException() {
    super("ConversionErrors");
  }

  public void add(ConversionException e) {
    conversionExceptions.add(e);
  }
}
