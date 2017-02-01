package eu.fbk.das.process.engine.api.util;

import java.io.File;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UnmarshalHelper {

    private static final Logger logger = LogManager
	    .getLogger(UnmarshalHelper.class);
    private static final String INTERNAL_SEPARATOR = "|";
    static private HashMap<String, Unmarshaller> un_hash = new HashMap<String, Unmarshaller>();
    static private HashMap<String, Object> obj_hash = new HashMap<String, Object>();

    synchronized static public Object doUnmarshal(String ucontext, String jaxb,
	    String xsd, String xml) {
	StringBuilder sb = new StringBuilder();
	sb.append(ucontext);
	sb.append(INTERNAL_SEPARATOR);
	sb.append(jaxb);
	sb.append(INTERNAL_SEPARATOR);
	sb.append(xsd);
	sb.append(INTERNAL_SEPARATOR);
	sb.append(xml);
	String key = sb.toString();
	if (obj_hash.containsKey(key)) {
	    // System.out.println(">>>>> MATCHED!!!!" + key);
	    return obj_hash.get(key);
	}
	// System.out.println(">>>>> UNMATCHED!!!!" + key);
	/*
	 * try { validateXML(xml, xsd); } catch (Exception e) {
	 * e.printStackTrace(); }
	 */

	Unmarshaller un = null;
	if (un_hash.containsKey(jaxb)) {
	    un = un_hash.get(jaxb);
	} else {
	    try {
		JAXBContext context;
		context = JAXBContext.newInstance(jaxb);
		un = context.createUnmarshaller();
	    } catch (JAXBException e) {
		e.printStackTrace();
	    }
	    un_hash.put(jaxb, un);
	}

	Object obj = null;
	try {
	    obj = un.unmarshal(new File(xml));
	} catch (JAXBException e) {
	    logger.error(e.getMessage(), e);
	}

	obj_hash.put(key, obj);
	return obj;
    }

    // static private Document validateXML(String xmlFilename, String
    // xsdFilename)
    // throws Exception {
    //
    // DocumentBuilder db = null;
    // Document d = null;
    // DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    // SchemaFactory sf = SchemaFactory
    // .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    //
    // try {
    // dbf.setSchema(sf.newSchema(new File(xsdFilename)));
    // dbf.setNamespaceAware(true);
    // db = dbf.newDocumentBuilder();
    // db.setErrorHandler(new ErrorHandler() {
    //
    // @Override
    // public void warning(SAXParseException exception)
    // throws SAXException {
    //
    // }
    //
    // @Override
    // public void error(SAXParseException exception)
    // throws SAXException {
    //
    // }
    //
    // @Override
    // public void fatalError(SAXParseException exception)
    // throws SAXException {
    //
    // }
    //
    // });
    // d = db.parse(new File(xmlFilename));
    // } catch (Exception e) {
    // System.out.println(e.getMessage());
    // throw e;
    // }
    // return d;
    // }
}
