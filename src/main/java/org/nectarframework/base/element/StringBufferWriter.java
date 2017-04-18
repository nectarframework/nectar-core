package org.nectarframework.base.element;

import java.io.IOException;
import java.io.Writer;

public class StringBufferWriter extends Writer {

	private StringBuffer strbuff;

	public StringBufferWriter(StringBuffer strbuff) {
		this.strbuff = strbuff;
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		strbuff.append(cbuf, off, len);
	}

}
