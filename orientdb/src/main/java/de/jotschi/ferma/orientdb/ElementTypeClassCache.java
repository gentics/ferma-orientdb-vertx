package de.jotschi.ferma.orientdb;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
				try {
					return Class.forName(basePath + "." + key);
				} catch (final ClassNotFoundException e) {

				}
			}
			throw new IllegalStateException("The class {" + className + "} cannot be found for basePaths {" + Arrays.toString(basePaths) + "}");
		});
	}
}
