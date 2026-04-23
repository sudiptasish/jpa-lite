package org.javalabs.jpa.orm.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for entityType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="entityType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="table" type="{http://java.sun.com/xml/ns/persistence/orm}tableType"/&gt;
 *         &lt;element name="named-native-queries" type="{http://java.sun.com/xml/ns/persistence/orm}named-native-queriesType" minOccurs="0"/&gt;
 *         &lt;element name="attributes" type="{http://java.sun.com/xml/ns/persistence/orm}attributesType"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="class" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "entityType", propOrder = {
    "table",
    "namedNativeQueries",
    "attributes"
})
public class EntityType {

    @XmlElement(required = true)
    protected TableType table;
    
    @XmlElement(name = "named-native-queries")
    protected NamedNativeQueriesType namedNativeQueries;
    
    @XmlElement(required = true)
    protected AttributesType attributes;
    
    @XmlAttribute(name = "class")
    protected String clazz;
    
    @XmlAttribute(name = "name")
    protected String name;

    /**
     * Gets the value of the table property.
     * 
     * @return
     *     possible object is
     *     {@link TableType }
     *     
     */
    public TableType getTable() {
        return table;
    }

    /**
     * Sets the value of the table property.
     * 
     * @param value
     *     allowed object is
     *     {@link TableType }
     *     
     */
    public void setTable(TableType value) {
        this.table = value;
    }

    /**
     * Gets the value of the namedNativeQueries property.
     * 
     * @return
     *     possible object is
     *     {@link NamedNativeQueriesType }
     *     
     */
    public NamedNativeQueriesType getNamedNativeQueries() {
        return namedNativeQueries;
    }

    /**
     * Sets the value of the namedNativeQueries property.
     * 
     * @param value
     *     allowed object is
     *     {@link NamedNativeQueriesType }
     *     
     */
    public void setNamedNativeQueries(NamedNativeQueriesType value) {
        this.namedNativeQueries = value;
    }

    /**
     * Gets the value of the attributes property.
     * 
     * @return
     *     possible object is
     *     {@link AttributesType }
     *     
     */
    public AttributesType getAttributes() {
        return attributes;
    }

    /**
     * Sets the value of the attributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributesType }
     *     
     */
    public void setAttributes(AttributesType value) {
        this.attributes = value;
    }

    /**
     * Gets the value of the clazz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * Sets the value of the clazz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClazz(String value) {
        this.clazz = value;
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
