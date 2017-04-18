package org.nectarframework.base.element;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class YamlElementIO extends AbstractElementIO {

	@Override
	public Element from(InputStream is) throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		return mapper.readValue(is, Element.class);
	}

	@Override
	protected void toWriter(Element elm, Writer writer, boolean header) throws IOException {
		new ObjectMapper(new YAMLFactory()).writerWithDefaultPrettyPrinter().writeValue(writer, elm);

	}
	

}
