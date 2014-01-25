
package com.betfair.publicapi.types.global.v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BankAccountDetailsField complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BankAccountDetailsField">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.betfair.com/publicapi/types/global/v3/}AbstractField">
 *       &lt;sequence>
 *         &lt;element name="type" type="{http://www.betfair.com/publicapi/types/global/v3/}BankAccountDetailsFieldEnum"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BankAccountDetailsField", propOrder = {
    "type"
})
public class BankAccountDetailsField
    extends AbstractField
{

    @XmlElement(required = true)
    protected BankAccountDetailsFieldEnum type;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link BankAccountDetailsFieldEnum }
     *     
     */
    public BankAccountDetailsFieldEnum getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link BankAccountDetailsFieldEnum }
     *     
     */
    public void setType(BankAccountDetailsFieldEnum value) {
        this.type = value;
    }

}
