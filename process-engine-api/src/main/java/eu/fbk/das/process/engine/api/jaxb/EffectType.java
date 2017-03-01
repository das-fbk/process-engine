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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EffectType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EffectType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="event">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="dpName" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                 &lt;attribute name="eventName" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
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
@XmlType(name = "EffectType", namespace = "http://das.fbk.eu/Annotation", propOrder = {
    "event"
})
public class EffectType {

    protected List<EffectType.Event> event;

    /**
     * Gets the value of the event property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the event property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEvent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EffectType.Event }
     * 
     * 
     */
    public List<EffectType.Event> getEvent() {
        if (event == null) {
            event = new ArrayList<EffectType.Event>();
        }
        return this.event;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="dpName" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *       &lt;attribute name="eventName" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Event {

        @XmlAttribute(name = "dpName")
        @XmlSchemaType(name = "anySimpleType")
        protected String dpName;
        @XmlAttribute(name = "eventName")
        @XmlSchemaType(name = "anySimpleType")
        protected String eventName;

        /**
         * Gets the value of the dpName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDpName() {
            return dpName;
        }

        /**
         * Sets the value of the dpName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDpName(String value) {
            this.dpName = value;
        }

        /**
         * Gets the value of the eventName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEventName() {
            return eventName;
        }

        /**
         * Sets the value of the eventName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEventName(String value) {
            this.eventName = value;
        }

    }

}
