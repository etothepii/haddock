
package com.betfair.publicapi.types.global.v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BankAccountDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BankAccountDetails">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.betfair.com/publicapi/types/global/v3/}BasicBankAccountDetails">
 *       &lt;sequence>
 *         &lt;element name="payee" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="accountType" type="{http://www.betfair.com/publicapi/types/global/v3/}BankAccountTypeEnum"/>
 *         &lt;element name="bankKey" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="routing" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="abiCab" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BankAccountDetails", propOrder = {
    "payee",
    "accountType",
    "bankKey",
    "routing",
    "abiCab"
})
public class BankAccountDetails
    extends BasicBankAccountDetails
{

    @XmlElement(required = true, nillable = true)
    protected String payee;
    @XmlElement(required = true)
    protected BankAccountTypeEnum accountType;
    @XmlElement(required = true, nillable = true)
    protected String bankKey;
    @XmlElement(required = true, nillable = true)
    protected String routing;
    @XmlElement(required = true, nillable = true)
    protected String abiCab;

    /**
     * Gets the value of the payee property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPayee() {
        return payee;
    }

    /**
     * Sets the value of the payee property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPayee(String value) {
        this.payee = value;
    }

    /**
     * Gets the value of the accountType property.
     * 
     * @return
     *     possible object is
     *     {@link BankAccountTypeEnum }
     *     
     */
    public BankAccountTypeEnum getAccountType() {
        return accountType;
    }

    /**
     * Sets the value of the accountType property.
     * 
     * @param value
     *     allowed object is
     *     {@link BankAccountTypeEnum }
     *     
     */
    public void setAccountType(BankAccountTypeEnum value) {
        this.accountType = value;
    }

    /**
     * Gets the value of the bankKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankKey() {
        return bankKey;
    }

    /**
     * Sets the value of the bankKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankKey(String value) {
        this.bankKey = value;
    }

    /**
     * Gets the value of the routing property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRouting() {
        return routing;
    }

    /**
     * Sets the value of the routing property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRouting(String value) {
        this.routing = value;
    }

    /**
     * Gets the value of the abiCab property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAbiCab() {
        return abiCab;
    }

    /**
     * Sets the value of the abiCab property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAbiCab(String value) {
        this.abiCab = value;
    }

}
