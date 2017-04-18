package org.nectarframework.base.element;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.nectarframework.base.tools.FileTools;

public class ElementReader {

	enum ExtensionSupport {
		// xml reader
		XML(XmlElementIO.class, new String[] { "xml" }),
		// json reader
		JSON(JsonElementIO.class, new String[] { "json", "jsn" }),
		// yaml reader
		YAML(YamlElementIO.class, new String[] { "yaml", "yml" });

		private Class<? extends ElementIO> implClass;
		private String[] extensions;

		ExtensionSupport(Class<? extends ElementIO> implClass, String... extensions) {
			this.implClass = implClass;
			this.extensions = extensions;
		}

		public static Optional<ExtensionSupport> lookup(String ext) {
			for (ExtensionSupport es : values()) {
				for (String extension : es.extensions) {
					if (extension.compareToIgnoreCase(ext) == 0) {
						return Optional.of(es);
					}
				}
			}
			return Optional.empty();
		}

		public ElementIO newInstance() throws InstantiationException, IllegalAccessException {
			return implClass.newInstance();
		}
	}

	public static Element read(File file) throws InstantiationException, IllegalAccessException, IOException {
		Optional<String> extension = FileTools.getFileExtension(file);
		if (extension.isPresent()) {
			Optional<ExtensionSupport> supportOpt = ExtensionSupport.lookup(extension.get());
			if (supportOpt.isPresent()) {
				return supportOpt.get().newInstance().from(file);
			}
		}
		throw new IllegalArgumentException("file had an unknown / unrecognized extension.");
	}
}
