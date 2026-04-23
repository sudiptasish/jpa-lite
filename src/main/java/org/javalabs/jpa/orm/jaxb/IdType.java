package org.javalabs.jpa.orm.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for idType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="idType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="generated-value" type="{http://java.sun.com/xml/ns/persistence/orm}generated-valueType" minOccurs="0"/&gt;
 *         &lt;element name="column" type="{http://java.sun.com/xml/ns/persistence/orm}columnType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "idType", propOrder = {
    "generatedValue",
    "column"
})
public class IdType {

    @XmlElement(name = "generated-value")
    protected GeneratedValueType generatedValue;
    
    protected ColumnType column;
    
    @XmlAttribute(name = "name")
    protected String name;
    
    @XmlAttribute(name = "type")
    protected String type;

    /**
     * Gets the value of the generatedValue property.
     * 
     * @return
     *     possible object is
     *     {@link GeneratedValueType }
     *     
     */
    public GeneratedValueType getGeneratedValue() {
        return generatedValue;
    }

    /**
     * Sets the value of the generatedValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeneratedValueType }
     *     
     */
    public void setGeneratedValue(GeneratedValueType value) {
        this.generatedValue = value;
    }

    /**
     * Gets the value of the column property.
     * 
     * @return
     *     possible object is
     *     {@link ColumnType }
     *     
     */
    public ColumnType getColumn() {
        return column;
    }

    /**
     * Sets the value of the column property.
     * 
     * @param value
     *     allowed object is
     *     {@link ColumnType }
     *     
     */
    public void setColumn(ColumnType value) {
        this.column = value;
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

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

}
