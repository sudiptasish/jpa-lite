package org.javalabs.jpa.orm.jaxb;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for named-native-queriesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="named-native-queriesType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="named-native-query" type="{http://java.sun.com/xml/ns/persistence/orm}named-native-queryType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "named-native-queriesType", propOrder = {
    "namedNativeQuery"
})
public class NamedNativeQueriesType {

    @XmlElement(name = "named-native-query")
    protected List<NamedNativeQueryType> namedNativeQuery;

    /**
     * Gets the value of the namedNativeQuery property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the namedNativeQuery property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNamedNativeQuery().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NamedNativeQueryType }
     * 
     * 
     */
    public List<NamedNativeQueryType> getNamedNativeQuery() {
        if (namedNativeQuery == null) {
            namedNativeQuery = new ArrayList<NamedNativeQueryType>();
        }
        return this.namedNativeQuery;
    }

}
