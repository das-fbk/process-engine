package eu.fbk.das.process.engine.impl.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import eu.fbk.das.process.engine.api.jaxb.VariableType;

public class VariableUtils {

	public static Document newEmptyDocument() {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document doc;

		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		doc = builder.newDocument();
		return doc;
	}

	public static Element newElement(String elementName, String elementValue) {
		Document doc = newEmptyDocument();
		Element el = doc.createElement(elementName);
		Node node = doc.createTextNode(elementValue);
		el.appendChild(node);
		return el;
	}

	public static List<VariableType> cloneList(List<VariableType> variable) {
		List<VariableType> clone = new ArrayList<VariableType>();
		// VariableType var = new VariableType();
		for (int i = 0; i < variable.size(); i++) {
			VariableType var = new VariableType();
			var.setName(variable.get(i).getName());

			Element mElement = (Element) variable.get(i).getContent();
			Element mElementClone = VariableUtils.newElement(
					mElement.getNodeName(), mElement.getTextContent());

			var.setContent(mElementClone);

			// var.setContent(variable.get(i).getContent());
			clone.add(var);
		}
		return clone;
	}
}
