package org.nectarframework.base.element;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.nectarframework.base.tools.Sanity;

public abstract class AbstractElementIO implements ElementIO {

	@Override
	public Element from(File file) throws IOException {
		return from(new FileInputStream(file));
	}

	@Override
	public Element from(StringBuffer strbuff) {
		return from(strbuff.toString());
	}

	@Override
	public void to(Element elm, File file) throws IOException {
		to(elm, new FileOutputStream(file), false);
	}

	@Override
	public void to(Element elm, OutputStream os) throws IOException {
		to(elm, os, false);
	}

	@Override
	public String to(Element elm) {
		return to(elm, false);
	}

	@Override
	public void to(Element elm, StringBuffer strbuff) {
		to(elm, strbuff, false);
	}

	@Override
	public void to(Element elm, File file, boolean header) throws IOException {
		to(elm, new FileOutputStream(file), header);
	}

	@Override
	public Element from(String str) {
		ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
		try {
			return from(bais);
		} catch (IOException e) {
			Sanity.np(e);
			return null;
		}
	}

	@Override
	public void to(Element elm, OutputStream os, boolean header) {
		try {
			toWriter(elm, new OutputStreamWriter(os), header);
		} catch (IOException e) {
			Sanity.np(e);
		}
	}

	@Override
	public String to(Element elm, boolean header) {
		StringBuffer sb = new StringBuffer();
		try {
			toWriter(elm, new StringBufferWriter(sb), header);
		} catch (IOException e) {
			Sanity.np(e);
			return null;
		}
		return sb.toString();
	}

	@Override
	public void to(Element elm, StringBuffer strbuff, boolean header) {
		try {
			toWriter(elm, new StringBufferWriter(strbuff), header);
		} catch (IOException e) {
			Sanity.np(e);
		}
	}

	protected abstract void toWriter(Element elm, Writer writer, boolean header)
			throws IOException;
}
