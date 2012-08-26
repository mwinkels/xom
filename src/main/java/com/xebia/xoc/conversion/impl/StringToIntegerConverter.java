package com.xebia.xoc.conversion.impl;

import com.xebia.xoc.conversion.ConversionException;
import com.xebia.xoc.conversion.Converter;

public class StringToIntegerConverter implements Converter<String, Integer>{

	@Override
	public boolean canConvert(Class<?> sourceClass, Class<?> targetCLass) {
		return sourceClass == String.class && targetCLass == Integer.class;
	}

	@Override
	public Integer convert(String value) throws ConversionException {
		return Integer.parseInt(value);
	}
}
