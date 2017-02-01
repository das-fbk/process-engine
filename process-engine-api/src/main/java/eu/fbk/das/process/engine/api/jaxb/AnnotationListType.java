//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.09.01 at 03:11:28 PM CEST 
//


package eu.fbk.das.process.engine.api.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for annotationListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="annotationListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://das.fbk.eu/Annotation}abstractAnnotation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "annotationListType", namespace = "http://das.fbk.eu/Annotation", propOrder = {
    "abstractAnnotation"
})
public class AnnotationListType {

    @XmlElements({
        @XmlElement(name = "goal", type = GoalType.class),
        @XmlElement(name = "precondition", type = PreconditionType.class),
        @XmlElement(name = "effect", type = EffectType.class),
        @XmlElement(name = "compensation", type = CompensationType.class)
    })
    protected List<Object> abstractAnnotation;

    /**
     * Gets the value of the abstractAnnotation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractAnnotation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractAnnotation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GoalType }
     * {@link PreconditionType }
     * {@link EffectType }
     * {@link CompensationType }
     * 
     * 
     */
    public List<Object> getAbstractAnnotation() {
        if (abstractAnnotation == null) {
            abstractAnnotation = new ArrayList<Object>();
        }
        return this.abstractAnnotation;
    }

}
