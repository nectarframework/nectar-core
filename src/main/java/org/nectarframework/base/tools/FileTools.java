package org.nectarframework.base.tools;

import java.io.File;
import java.util.Optional;

public class FileTools {

	public static Optional<String> getFileExtension(File file) {
		if (file == null) {
			throw new NullPointerException("file argument was null");
		}
		if (!file.isFile()) {
			throw new IllegalArgumentException(
					"getFileExtension(File file)" + " called on File object that wasn't an actual file"
							+ " (perhaps a directory or device?). file had path: " + file.getAbsolutePath());
		}
		String fileName = file.getName();
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			return Optional.of(fileName.substring(i + 1));
		} else {
			return Optional.empty();
		}
	}

}
