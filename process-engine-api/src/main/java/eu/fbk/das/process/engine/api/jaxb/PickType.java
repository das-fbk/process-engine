//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.01.26 at 09:20:05 AM CET 
//

package eu.fbk.das.process.engine.api.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

/**
 * Choose from a list of activities to be performed
 * 
 * 
 * <p>
 * Java class for pickType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="pickType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://das.fbk.eu/Process}activityType">
 *       &lt;sequence>
 *         &lt;element name="onMessage" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;group ref="{http://das.fbk.eu/Process}activity" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="sid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pickType", propOrder = { "onMessage" })
public class PickType extends ActivityType {

    @XmlElement(required = true)
    protected List<PickType.OnMessage> onMessage;

    /**
     * Gets the value of the onMessage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the onMessage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getOnMessage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PickType.OnMessage }
     * 
     * 
     */
    public List<PickType.OnMessage> getOnMessage() {
	if (onMessage == null) {
	    onMessage = new ArrayList<PickType.OnMessage>();
	}
	return this.onMessage;
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
     *         &lt;group ref="{http://das.fbk.eu/Process}activity" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *       &lt;attribute name="sid" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "activity" })
    public static class OnMessage {

	@XmlElements({
		@XmlElement(name = "abstract", type = AbstractType.class),
		@XmlElement(name = "concrete", type = ConcreteType.class),
		@XmlElement(name = "switch", type = SwitchType.class),
		@XmlElement(name = "pick", type = PickType.class),
		@XmlElement(name = "receive", type = ReceiveType.class),
		@XmlElement(name = "invoke", type = InvokeType.class),
		@XmlElement(name = "while", type = WhileType.class),
		@XmlElement(name = "scope", type = ScopeType.class) })
	protected List<ActivityType> activity;
	@XmlAttribute(name = "sid")
	protected String sid;
	@XmlAttribute(name = "name")
	protected String name;

	/**
	 * Gets the value of the activity property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list
	 * will be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the activity property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getActivity().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link AbstractType } {@link ConcreteType } {@link SwitchType }
	 * {@link PickType } {@link ReceiveType } {@link InvokeType }
	 * {@link WhileType } {@link ScopeType }
	 * 
	 * 
	 */
	public List<ActivityType> getActivity() {
	    if (activity == null) {
		activity = new ArrayList<ActivityType>();
	    }
	    return this.activity;
	}

	/**
	 * Gets the value of the sid property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSid() {
	    return sid;
	}

	/**
	 * Sets the value of the sid property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSid(String value) {
	    this.sid = value;
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

    }

}
