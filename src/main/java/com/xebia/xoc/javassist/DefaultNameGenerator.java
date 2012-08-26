package com.xebia.xoc.javassist;

public class DefaultNameGenerator implements NameGenerator {

	@Override
	public String mapperClassName(Class<?> sourceClass, Class<?> targetClass) {
		return "com.xebia.xoc.generated." + toNamePart(sourceClass) + "To" + toNamePart(targetClass) + "Mapper";
	}

	private String toNamePart(Class<?> clz) {
		StringBuilder sb = new StringBuilder();
		if (clz.getDeclaringClass() != null) {
			sb.append(clz.getDeclaringClass().getSimpleName()).append('$'); 
		}
		sb.append(clz.getSimpleName());
		return sb.toString();
	}

}
