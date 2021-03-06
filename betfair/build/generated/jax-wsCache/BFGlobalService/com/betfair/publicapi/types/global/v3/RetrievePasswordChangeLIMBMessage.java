
package com.betfair.publicapi.types.global.v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for RetrievePasswordChangeLIMBMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RetrievePasswordChangeLIMBMessage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="messageId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="enforceDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="indicator" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RetrievePasswordChangeLIMBMessage", propOrder = {
    "messageId",
    "enforceDate",
    "indicator"
})
public class RetrievePasswordChangeLIMBMessage {

    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer messageId;
    @XmlElement(required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar enforceDate;
    protected boolean indicator;

    /**
     * Gets the value of the messageId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMessageId() {
        return messageId;
    }

    /**
     * Sets the value of the messageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMessageId(Integer value) {
        this.messageId = value;
    }

    /**
     * Gets the value of the enforceDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEnforceDate() {
        return enforceDate;
    }

    /**
     * Sets the value of the enforceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEnforceDate(XMLGregorianCalendar value) {
        this.enforceDate = value;
    }

    /**
     * Gets the value of the indicator property.
     * 
     */
    public boolean isIndicator() {
        return indicator;
    }

    /**
     * Sets the value of the indicator property.
     * 
     */
    public void setIndicator(boolean value) {
        this.indicator = value;
    }

}
