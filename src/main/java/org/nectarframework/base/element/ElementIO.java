package org.nectarframework.base.element;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ElementIO {

	public Element from(File file) throws IOException;

	public Element from(InputStream is) throws IOException;

	public Element from(String str);

	public Element from(StringBuffer strbuff);

	public void to(Element elm, File file) throws IOException;
	
	public void to(Element elm, OutputStream os) throws IOException;
	
	public String to(Element elm);
	
	public void to(Element elm, StringBuffer strbuff);

	public void to(Element elm, File file, boolean header) throws IOException;
	
	public void to(Element elm, OutputStream os, boolean header) throws IOException;
	
	public String to(Element elm, boolean header);
	
	public void to(Element elm, StringBuffer strbuff, boolean header);
	
}
