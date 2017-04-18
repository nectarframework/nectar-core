package org.nectarframework.base.element;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import com.fasterxml.jackson.databind.ObjectMapper;

// TODO: we shouldn't need databind for this... a simpler JSON parser would probably be faster
public class JsonElementIO extends AbstractElementIO {

	@Override
	public Element from(InputStream is) throws IOException {
		return new ObjectMapper().readValue(is, Element.class);
	}

	@Override
	protected void toWriter(Element elm, Writer writer, boolean header) throws IOException {
		new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(writer, elm);
	}

}
