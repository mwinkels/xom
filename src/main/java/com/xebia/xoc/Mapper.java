package com.xebia.xoc;

public interface Mapper {

	<S, T> ClassMapper<S, T> getClassMapper(Class<S> sourceClass, Class<T> targetClass);
}
