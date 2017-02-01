//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.12.15 at 01:34:21 PM CET 
//

package eu.fbk.das.process.engine.api.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="domainKnowledge">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="internalDomainProperty" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="externalDomainProperty" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="process">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="fragment" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="robustnessAnnotation" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="singleton" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "domainKnowledge", "process", "fragment",
	"robustnessAnnotation" })
@XmlRootElement(name = "domainObject", namespace = "http://das.fbk.eu/DomainObject")
public class DomainObject {

    @XmlElement(namespace = "http://das.fbk.eu/DomainObject", required = true)
    protected DomainObject.DomainKnowledge domainKnowledge;
    @XmlElement(namespace = "http://das.fbk.eu/DomainObject", required = true)
    protected DomainObject.Process process;
    @XmlElement(namespace = "http://das.fbk.eu/DomainObject")
    protected List<DomainObject.Fragment> fragment;
    @XmlElement(namespace = "http://das.fbk.eu/DomainObject")
    protected DomainObject.RobustnessAnnotation robustnessAnnotation;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "singleton")
    protected Boolean singleton;

    /**
     * Gets the value of the domainKnowledge property.
     * 
     * @return possible object is {@link DomainObject.DomainKnowledge }
     * 
     */
    public DomainObject.DomainKnowledge getDomainKnowledge() {
	return domainKnowledge;
    }

    /**
     * Sets the value of the domainKnowledge property.
     * 
     * @param value
     *            allowed object is {@link DomainObject.DomainKnowledge }
     * 
     */
    public void setDomainKnowledge(DomainObject.DomainKnowledge value) {
	this.domainKnowledge = value;
    }

    /**
     * Gets the value of the process property.
     * 
     * @return possible object is {@link DomainObject.Process }
     * 
     */
    public DomainObject.Process getProcess() {
	return process;
    }

    /**
     * Sets the value of the process property.
     * 
     * @param value
     *            allowed object is {@link DomainObject.Process }
     * 
     */
    public void setProcess(DomainObject.Process value) {
	this.process = value;
    }

    /**
     * Gets the value of the fragment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the fragment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getFragment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DomainObject.Fragment }
     * 
     * 
     */
    public List<DomainObject.Fragment> getFragment() {
	if (fragment == null) {
	    fragment = new ArrayList<DomainObject.Fragment>();
	}
	return this.fragment;
    }

    /**
     * Gets the value of the robustnessAnnotation property.
     * 
     * @return possible object is {@link DomainObject.RobustnessAnnotation }
     * 
     */
    public DomainObject.RobustnessAnnotation getRobustnessAnnotation() {
	return robustnessAnnotation;
    }

    /**
     * Sets the value of the robustnessAnnotation property.
     * 
     * @param value
     *            allowed object is {@link DomainObject.RobustnessAnnotation }
     * 
     */
    public void setRobustnessAnnotation(DomainObject.RobustnessAnnotation value) {
	this.robustnessAnnotation = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getName() {
	return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setName(String value) {
	this.name = value;
    }

    /**
     * Gets the value of the singleton property.
     * 
     * @return possible object is {@link Boolean }
     * 
     */
    public boolean isSingleton() {
	if (singleton == null) {
	    return false;
	} else {
	    return singleton;
	}
    }

    /**
     * Sets the value of the singleton property.
     * 
     * @param value
     *            allowed object is {@link Boolean }
     * 
     */
    public void setSingleton(Boolean value) {
	this.singleton = value;
    }

    /**
     * <p>
     * Java class for anonymous complex type.
     * 
     * <p>
     * The following schema fragment specifies the expected content contained
     * within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="internalDomainProperty" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="externalDomainProperty" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "internalDomainProperty",
	    "externalDomainProperty" })
    public static class DomainKnowledge {

	@XmlElement(namespace = "http://das.fbk.eu/DomainObject")
	protected List<DomainObject.DomainKnowledge.InternalDomainProperty> internalDomainProperty;
	@XmlElement(namespace = "http://das.fbk.eu/DomainObject")
	protected List<DomainObject.DomainKnowledge.ExternalDomainProperty> externalDomainProperty;

	/**
	 * Gets the value of the internalDomainProperty property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list
	 * will be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the internalDomainProperty property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getInternalDomainProperty().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link DomainObject.DomainKnowledge.InternalDomainProperty }
	 * 
	 * 
	 */
	public List<DomainObject.DomainKnowledge.InternalDomainProperty> getInternalDomainProperty() {
	    if (internalDomainProperty == null) {
		internalDomainProperty = new ArrayList<DomainObject.DomainKnowledge.InternalDomainProperty>();
	    }
	    return this.internalDomainProperty;
	}

	/**
	 * Gets the value of the externalDomainProperty property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list
	 * will be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the externalDomainProperty property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getExternalDomainProperty().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link DomainObject.DomainKnowledge.ExternalDomainProperty }
	 * 
	 * 
	 */
	public List<DomainObject.DomainKnowledge.ExternalDomainProperty> getExternalDomainProperty() {
	    if (externalDomainProperty == null) {
		externalDomainProperty = new ArrayList<DomainObject.DomainKnowledge.ExternalDomainProperty>();
	    }
	    return this.externalDomainProperty;
	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 * 
	 * <p>
	 * The following schema fragment specifies the expected content
	 * contained within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "")
	public static class ExternalDomainProperty {

	    @XmlAttribute(name = "name", required = true)
	    protected String name;

	    /**
	     * Gets the value of the name property.
	     * 
	     * @return possible object is {@link String }
	     * 
	     */
	    public String getName() {
		return name;
	    }

	    /**
	     * Sets the value of the name property.
	     * 
	     * @param value
	     *            allowed object is {@link String }
	     * 
	     */
	    public void setName(String value) {
		this.name = value;
	    }

	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 * 
	 * <p>
	 * The following schema fragment specifies the expected content
	 * contained within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "")
	public static class InternalDomainProperty {

	    @XmlAttribute(name = "name", required = true)
	    protected String name;

	    /**
	     * Gets the value of the name property.
	     * 
	     * @return possible object is {@link String }
	     * 
	     */
	    public String getName() {
		return name;
	    }

	    /**
	     * Sets the value of the name property.
	     * 
	     * @param value
	     *            allowed object is {@link String }
	     * 
	     */
	    public void setName(String value) {
		this.name = value;
	    }

	}

    }

    /**
     * <p>
     * Java class for anonymous complex type.
     * 
     * <p>
     * The following schema fragment specifies the expected content contained
     * within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Fragment {

	@XmlAttribute(name = "name", required = true)
	protected String name;

	/**
	 * Gets the value of the name property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getName() {
	    return name;
	}

	/**
	 * Sets the value of the name property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setName(String value) {
	    this.name = value;
	}

    }

    /**
     * <p>
     * Java class for anonymous complex type.
     * 
     * <p>
     * The following schema fragment specifies the expected content contained
     * within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Process {

	@XmlAttribute(name = "name", required = true)
	protected String name;

	/**
	 * Gets the value of the name property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getName() {
	    return name;
	}

	/**
	 * Sets the value of the name property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setName(String value) {
	    this.name = value;
	}

    }

    /**
     * <p>
     * Java class for anonymous complex type.
     * 
     * <p>
     * The following schema fragment specifies the expected content contained
     * within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class RobustnessAnnotation {

	@XmlAttribute(name = "name", required = true)
	protected String name;

	/**
	 * Gets the value of the name property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getName() {
	    return name;
	}

	/**
	 * Sets the value of the name property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setName(String value) {
	    this.name = value;
	}

    }

}
