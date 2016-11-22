package com.gentics.ferma.orientdb;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.gentics.ferma.annotation.GraphElement;

/**
 * Type cache which also provides resolving methods which cache the result.
 */
public class ElementTypeClassCache {

	private final Map<String, Class> classStringCache = new HashMap<>();
	private String[] basePaths;

	public ElementTypeClassCache(String... packagePaths) {
		this.basePaths = packagePaths;
	}

	public Class forName(final String className) {
		return this.classStringCache.computeIfAbsent(className, (key) -> {
			for (String basePath : basePaths) {
				Set<Class<?>> graphTypeClasses = new Reflections(basePath).getTypesAnnotatedWith(GraphElement.class);
				for (Class<?> clazz : graphTypeClasses) {
					if (clazz.getSimpleName().equals(key)) {
						return clazz;
					}
				}
			}
			return null;
		});
	}
}
