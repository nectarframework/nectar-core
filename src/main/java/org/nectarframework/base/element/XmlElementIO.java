package org.nectarframework.base.element;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.nectarframework.base.tools.Sanity;
import org.nectarframework.base.tools.StringTools;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlElementIO extends AbstractElementIO {

	/**** Overrides ****/
	@Override
	public Element from(InputStream is) throws IOException {
		return fromInputStream(is);
	}

	@Override
	public Element from(String str) {
		if (!hasXmlHeader(str)) {
			str = xmlHeader() + str;
		}

		ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());

		try {
			return from(bais);
		} catch (IOException e) {
			Sanity.np(e);
			return null;
		}
	}

	
	/**** Utilities ***/

	private boolean hasXmlHeader(String str) {
		// TODO implement me with a nice regex
		return false;
	}

	
	public static String xmlHeader() {
		return "<?xml version=\"1.0\" encoding=\"" + Charset.defaultCharset().toString() + "\"?>\n";
	}

	/**** Implementations ****/

	protected void toWriter(Element elm, Writer writer, boolean header) throws IOException {
		if (header) {
			writer.write(xmlHeader());
		}
		writer.write(("<" + elm.getName()));
		for (String key : elm.getAttributes().keySet()) {
			writer.write((" " + key + "=\"" + StringTools.xmlEncode(elm.getAttributes().get(key)) + "\""));
		}
		if (elm.getChildren().isEmpty()) {
			writer.write("/>");
		} else {
			writer.write(">");
			for (Element child : elm.getChildren()) {
				toWriter(child, writer, false);
			}
			writer.write(("</" + elm.getName() + ">"));
		}
	}

	private Element fromXmlRecurse(org.w3c.dom.Element xe) {
		Element e = new Element(xe.getTagName());

		NamedNodeMap nnm = xe.getAttributes();
		int nnmLen = nnm.getLength();
		for (int t = 0; t < nnmLen; t++) {
			Node n = nnm.item(t);
			if (n.getNodeType() == Node.ATTRIBUTE_NODE) {
				e.add(n.getNodeName(), n.getNodeValue());
			}
		}

		NodeList nl = xe.getChildNodes();
		int nlLen = nl.getLength();
		for (int t = 0; t < nlLen; t++) {
			Node n = nl.item(t);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				e.add(fromXmlRecurse((org.w3c.dom.Element) n));
			}
		}

		return e;
	}

	private Element fromInputStream(InputStream is) throws IOException {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = null;
			documentBuilder = documentBuilderFactory.newDocumentBuilder();

			Document doc = documentBuilder.parse(is);
			org.w3c.dom.Element rootXElm = doc.getDocumentElement();

			Element root = fromXmlRecurse(rootXElm);

			return root;
		} catch (SAXException e) {
			throw new IOException(e);
		} catch (ParserConfigurationException e) {
			throw new IOException(e);
		}
	}

}
