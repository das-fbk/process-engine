//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.27 at 04:23:37 PM CET 
//


package eu.fbk.das.process.engine.api.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Father of all activities
 * 
 * <p>Java class for activityType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="activityType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="precondition" type="{http://das.fbk.eu/Annotation}PreconditionType" minOccurs="0"/>
 *         &lt;element name="effect" type="{http://das.fbk.eu/Annotation}EffectType" minOccurs="0"/>
 *         &lt;element name="actionVariable" type="{http://das.fbk.eu/DataType}variableType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "activityType", propOrder = {
    "precondition",
    "effect",
    "actionVariable"
})
@XmlSeeAlso({
    AbstractType.class,
    ConcreteType.class,
    SwitchType.class,
    PickType.class,
    ReceiveType.class,
    InvokeType.class,
    WhileType.class,
    ScopeType.class
})
public abstract class ActivityType {

    protected PreconditionType precondition;
    protected EffectType effect;
    protected List<VariableType> actionVariable;
    @XmlAttribute(name = "name")
    protected String name;

    /**
     * Gets the value of the precondition property.
     * 
     * @return
     *     possible object is
     *     {@link PreconditionType }
     *     
     */
    public PreconditionType getPrecondition() {
        return precondition;
    }

    /**
     * Sets the value of the precondition property.
     * 
     * @param value
     *     allowed object is
     *     {@link PreconditionType }
     *     
     */
    public void setPrecondition(PreconditionType value) {
        this.precondition = value;
    }

    /**
     * Gets the value of the effect property.
     * 
     * @return
     *     possible object is
     *     {@link EffectType }
     *     
     */
    public EffectType getEffect() {
        return effect;
    }

    /**
     * Sets the value of the effect property.
     * 
     * @param value
     *     allowed object is
     *     {@link EffectType }
     *     
     */
    public void setEffect(EffectType value) {
        this.effect = value;
    }

    /**
     * Gets the value of the actionVariable property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the actionVariable property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActionVariable().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VariableType }
     * 
     * 
     */
    public List<VariableType> getActionVariable() {
        if (actionVariable == null) {
            actionVariable = new ArrayList<VariableType>();
        }
        return this.actionVariable;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
