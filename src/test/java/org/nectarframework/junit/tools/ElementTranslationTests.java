package org.nectarframework.junit.tools;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.nectarframework.base.element.Element;
import org.nectarframework.base.element.JsonElementIO;
import org.nectarframework.base.element.XmlElementIO;
import org.nectarframework.base.element.YamlElementIO;
import org.nectarframework.base.tools.test.RandomElementGenerator;

public class ElementTranslationTests {

	private RandomElementGenerator randomElementGenerator;
	private XmlElementIO xmlElementIO;
	private JsonElementIO jsonElementIO;
	private YamlElementIO yamlElementIO;

	@Before
	public void setup() {
		randomElementGenerator = new RandomElementGenerator();
		xmlElementIO = new XmlElementIO();
		jsonElementIO = new JsonElementIO();
		yamlElementIO = new YamlElementIO();
	}

	@Test
	public void xmlTest() {
		Element generatedElement = randomElementGenerator.generate();
		String convertedString = xmlElementIO.to(generatedElement);
		Element readElement = xmlElementIO.from(convertedString);

		assertEquals(generatedElement, readElement);
	}

	@Test
	public void jsonTest() {
		Element generatedElement = randomElementGenerator.generate();
		String convertedString = jsonElementIO.to(generatedElement);
		Element readElement = jsonElementIO.from(convertedString);

		assertEquals(generatedElement, readElement);
	}

	@Test
	public void yamlTest() {
		Element generatedElement = randomElementGenerator.generate();
		String convertedString = yamlElementIO.to(generatedElement);
		Element readElement = yamlElementIO.from(convertedString);

		assertEquals(generatedElement, readElement);
	}
	

	@Test
	public void xmlutf8Test() {
		randomElementGenerator.setAllowAllUTF8Chars(true);
		Element generatedElement = randomElementGenerator.generate();
		String convertedString = xmlElementIO.to(generatedElement);
		Element readElement = xmlElementIO.from(convertedString);

		assertEquals(generatedElement, readElement);
	}

	@Test
	public void jsonutf8Test() {
		randomElementGenerator.setAllowAllUTF8Chars(true);
		Element generatedElement = randomElementGenerator.generate();
		String convertedString = jsonElementIO.to(generatedElement);
		Element readElement = jsonElementIO.from(convertedString);
		assertEquals(generatedElement, readElement);
	}

	@Test
	public void yamlutf8Test() {
		randomElementGenerator.setAllowAllUTF8Chars(true);
		Element generatedElement = randomElementGenerator.generate();
		String convertedString = yamlElementIO.to(generatedElement);
		Element readElement = yamlElementIO.from(convertedString);

		assertEquals(generatedElement, readElement);
	}

}
