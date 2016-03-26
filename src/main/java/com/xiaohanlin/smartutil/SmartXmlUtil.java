package com.xiaohanlin.smartutil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * xml的工具类
 * 
 * @author jiaozi
 *
 */
public class SmartXmlUtil {

	public static Element readxml(String filePath) {
		Document document = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			document = db.parse(filePath);
		} catch (Exception e) {
			throw new RuntimeException("<<SmartXmlUtil>> cannnot parse xml file : " + filePath, e);

		}
		return document.getDocumentElement();
	}

	public static Element readxml(File xmlFile) {
		Document document = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			document = db.parse(xmlFile);
		} catch (Exception e) {
			throw new RuntimeException("<<SmartXmlUtil>> cannnot parse xml file : " + xmlFile.getAbsolutePath(), e);

		}
		return document.getDocumentElement();
	}

	public static String getAttributeAsString(Element element, String attrName) {
		SmartPreconditionUtil.checkArgument(element.hasAttribute(attrName), String.format("There is no attribute(%s) in element(%s)!", attrName, element.getNodeName()));
		return element.getAttribute(attrName);
	}

	public static String getAttributeAsString(Element element, String attrName, String defaultValue) {
		return element.hasAttribute(attrName) ? element.getAttribute(attrName) : defaultValue;
	}

	public static byte getAttributeAsByte(Element element, String attrName) {
		SmartPreconditionUtil.checkArgument(element.hasAttribute(attrName), String.format("There is no attribute(%s) in element(%s)!", attrName, element.getNodeName()));
		return Byte.parseByte(element.getAttribute(attrName));
	}

	public static byte getAttributeAsByte(Element element, String attrName, byte defaultValue) {
		return element.hasAttribute(attrName) ? Byte.parseByte(element.getAttribute(attrName)) : defaultValue;
	}

	public static short getAttributeAsShort(Element element, String attrName) {
		SmartPreconditionUtil.checkArgument(element.hasAttribute(attrName), String.format("There is no attribute(%s) in element(%s)!", attrName, element.getNodeName()));
		return Short.parseShort(element.getAttribute(attrName));
	}

	public static short getAttributeAsShort(Element element, String attrName, short defaultValue) {
		return element.hasAttribute(attrName) ? Short.parseShort(element.getAttribute(attrName)) : defaultValue;
	}

	public static int getAttributeAsInt(Element element, String attrName) {
		SmartPreconditionUtil.checkArgument(element.hasAttribute(attrName), String.format("There is no attribute(%s) in element(%s)!", attrName, element.getNodeName()));
		return Integer.parseInt(element.getAttribute(attrName));
	}

	public static int getAttributeAsInt(Element element, String attrName, int defaultValue) {
		return element.hasAttribute(attrName) ? Integer.parseInt(element.getAttribute(attrName)) : defaultValue;
	}

	public static long getAttributeAsLong(Element element, String attrName) {
		SmartPreconditionUtil.checkArgument(element.hasAttribute(attrName), String.format("There is no attribute(%s) in element(%s)!", attrName, element.getNodeName()));
		return Long.parseLong(element.getAttribute(attrName));
	}

	public static long getAttributeAsLong(Element element, String attrName, long defaultValue) {
		return element.hasAttribute(attrName) ? Long.parseLong(element.getAttribute(attrName)) : defaultValue;
	}

	public static float getAttributeAsFloat(Element element, String attrName) {
		SmartPreconditionUtil.checkArgument(element.hasAttribute(attrName), String.format("There is no attribute(%s) in element(%s)!", attrName, element.getNodeName()));
		return Float.parseFloat(element.getAttribute(attrName));
	}

	public static float getAttributeAsDouble(Element element, String attrName, float defaultValue) {
		return element.hasAttribute(attrName) ? Float.parseFloat(element.getAttribute(attrName)) : defaultValue;
	}

	public static double getAttributeAsDouble(Element element, String attrName) {
		SmartPreconditionUtil.checkArgument(element.hasAttribute(attrName), String.format("There is no attribute(%s) in element(%s)!", attrName, element.getNodeName()));
		return Double.parseDouble(element.getAttribute(attrName));
	}

	public static double getAttributeAsDouble(Element element, String attrName, double defaultValue) {
		return element.hasAttribute(attrName) ? Double.parseDouble(element.getAttribute(attrName)) : defaultValue;
	}

	public static boolean getAttributeAsBoolean(Element element, String attrName) {
		SmartPreconditionUtil.checkArgument(element.hasAttribute(attrName), String.format("There is no attribute(%s) in element(%s)!", attrName, element.getNodeName()));
		return Boolean.parseBoolean(element.getAttribute(attrName));
	}

	public static boolean getAttributeAsBoolean(Element element, String attrName, boolean defaultValue) {
		return element.hasAttribute(attrName) ? Boolean.parseBoolean(element.getAttribute(attrName)) : defaultValue;
	}

	public static HashMap<String, String> getAttributesMap(Element element) {
		NamedNodeMap attributes = element.getAttributes();
		HashMap<String, String> map = new HashMap<String, String>(attributes.getLength());
		for (int i = 0; i < attributes.getLength(); i++) {
			Node item = attributes.item(i);
			map.put(item.getNodeName(), item.getNodeValue());
		}
		return map;
	}

	public static String getCDATA(Element element) {
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
				return node.getNodeValue();
			}
		}
		return null;
	}

	public static List<Element> getElementsByTagName(Element element, String name) {
		List<Element> elementList = new ArrayList<Element>();
		NodeList nodeList = element.getElementsByTagName(name);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elementList.add((Element) node);
			}
		}
		return elementList;
	}

	public static Element getElementByTagName(Element element, String name) {
		NodeList nodeList = element.getElementsByTagName(name);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				return (Element) node;
			}
		}
		return null;
	}

	public static Element getElement(Element element) {
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				return (Element) node;
			}
		}
		return null;
	}

	public static List<Element> getElements(Element element) {
		List<Element> elementList = new ArrayList<Element>();
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				elementList.add((Element) node);
			}
		}
		return elementList;
	}

	public static Element mergeXmlRoot(Element rootSrc, Element rootDst) {
		NodeList nodeList = rootSrc.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			Node newNode = rootDst.getOwnerDocument().importNode(node, true);
			rootDst.appendChild(newNode);
		}
		return rootDst;
	}
}
