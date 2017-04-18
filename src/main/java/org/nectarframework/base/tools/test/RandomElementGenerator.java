package org.nectarframework.base.tools.test;

import java.util.HashMap;
import java.util.Map;

import org.nectarframework.base.element.Element;
import org.nectarframework.base.tools.RandUtils;

public class RandomElementGenerator {

	private int maxDepth = 6;
	private int maxNameLength = 64;
	private int maxChildCount = 6;
	private int maxAttributeNameLength = 64;
	private int maxAttributeCount = 10;
	private int maxValueLength = 256;
	private boolean allowAllUTF8Chars = false;

	private String rootName = "root";

	public Element generate() {
		return generateRecurse(new Element(rootName), 0);
	}
	
	private String getRandString(int min, int max) {
		if (allowAllUTF8Chars) {
			return RandUtils.nextUTF8String(min, max);
		} else {
			return RandUtils.nextPlainStringMixedCase(min, max, 0.2f);
		}
	}

	private Element generateRecurse(Element e, int depth) {
		int randMaxAttributeCount = RandUtils.nextInt(0, maxAttributeCount);

		e.add(generateAttributes(randMaxAttributeCount));

		if (depth < maxDepth) {
			int randChildCount = RandUtils.nextInt(0, maxChildCount);
			for (int t = 0; t < randChildCount; t++) {
				e.add(generateRecurse(new Element(RandUtils.nextPlainStringLowerCase(1, maxNameLength)), depth + 1));
			}
		}
		return e;
	}

	private Map<String, String> generateAttributes(int goalCount) {
		Map<String, String> map = new HashMap<String, String>();
		while (map.size() < goalCount) {
			map.put(RandUtils.nextPlainStringMixedCase(1, maxAttributeNameLength, 0.1f),
					getRandString(1, maxValueLength));
		}
		return map;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public int getMaxNameLength() {
		return maxNameLength;
	}

	public void setMaxNameLength(int maxNameLength) {
		this.maxNameLength = maxNameLength;
	}

	public int getMaxAttributeNameLength() {
		return maxAttributeNameLength;
	}

	public void setMaxAttributeNameLength(int maxAttributeNameLength) {
		this.maxAttributeNameLength = maxAttributeNameLength;
	}

	public int getMaxAttributeCount() {
		return maxAttributeCount;
	}

	public void setMaxAttributeCount(int maxAttributeCount) {
		this.maxAttributeCount = maxAttributeCount;
	}

	public int getMaxValueLength() {
		return maxValueLength;
	}

	public void setMaxValueLength(int maxValueLength) {
		this.maxValueLength = maxValueLength;
	}

	public String getRootName() {
		return rootName;
	}

	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

	public void setAllowAllUTF8Chars(boolean b) {
		this.allowAllUTF8Chars = true;
	}

}
