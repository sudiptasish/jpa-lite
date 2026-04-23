package org.javalabs.jpa.orm.jaxb;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.javalabs.decl.orm package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _EntityMappings_QNAME = new QName("http://java.sun.com/xml/ns/persistence/orm", "entity-mappings");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.javalabs.decl.orm
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EntityMappingsType }
     * 
     */
    public EntityMappingsType createEntityMappingsType() {
        return new EntityMappingsType();
    }

    /**
     * Create an instance of {@link TableType }
     * 
     */
    public TableType createTableType() {
        return new TableType();
    }

    /**
     * Create an instance of {@link NamedNativeQueriesType }
     * 
     */
    public NamedNativeQueriesType createNamedNativeQueriesType() {
        return new NamedNativeQueriesType();
    }

    /**
     * Create an instance of {@link GeneratedValueType }
     * 
     */
    public GeneratedValueType createGeneratedValueType() {
        return new GeneratedValueType();
    }

    /**
     * Create an instance of {@link IdType }
     * 
     */
    public IdType createIdType() {
        return new IdType();
    }

    /**
     * Create an instance of {@link ColumnType }
     * 
     */
    public ColumnType createColumnType() {
        return new ColumnType();
    }

    /**
     * Create an instance of {@link BasicType }
     * 
     */
    public BasicType createBasicType() {
        return new BasicType();
    }

    /**
     * Create an instance of {@link AttributesType }
     * 
     */
    public AttributesType createAttributesType() {
        return new AttributesType();
    }

    /**
     * Create an instance of {@link EntityType }
     * 
     */
    public EntityType createEntityType() {
        return new EntityType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EntityMappingsType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EntityMappingsType }{@code >}
     */
    @XmlElementDecl(namespace = "http://java.sun.com/xml/ns/persistence/orm", name = "entity-mappings")
    public JAXBElement<EntityMappingsType> createEntityMappings(EntityMappingsType value) {
        return new JAXBElement<EntityMappingsType>(_EntityMappings_QNAME, EntityMappingsType.class, null, value);
    }

}
