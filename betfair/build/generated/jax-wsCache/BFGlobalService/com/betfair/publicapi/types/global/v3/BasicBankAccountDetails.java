
package com.betfair.publicapi.types.global.v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BasicBankAccountDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BasicBankAccountDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bankName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="accountHoldingBranch" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bankGiroCreditNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="accountNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sortCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bankCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="blzCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bankBsb" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="branchCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bankLocationIso3" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BasicBankAccountDetails", propOrder = {
    "bankName",
    "accountHoldingBranch",
    "bankGiroCreditNumber",
    "accountNumber",
    "sortCode",
    "bankCode",
    "blzCode",
    "bankBsb",
    "branchCode",
    "bankLocationIso3"
})
@XmlSeeAlso({
    BankAccountDetails.class
})
public class BasicBankAccountDetails {

    @XmlElement(required = true, nillable = true)
    protected String bankName;
    @XmlElement(required = true, nillable = true)
    protected String accountHoldingBranch;
    @XmlElement(required = true, nillable = true)
    protected String bankGiroCreditNumber;
    @XmlElement(required = true, nillable = true)
    protected String accountNumber;
    @XmlElement(required = true, nillable = true)
    protected String sortCode;
    @XmlElement(required = true, nillable = true)
    protected String bankCode;
    @XmlElement(required = true, nillable = true)
    protected String blzCode;
    @XmlElement(required = true, nillable = true)
    protected String bankBsb;
    @XmlElement(required = true, nillable = true)
    protected String branchCode;
    @XmlElement(required = true, nillable = true)
    protected String bankLocationIso3;

    /**
     * Gets the value of the bankName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * Sets the value of the bankName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankName(String value) {
        this.bankName = value;
    }

    /**
     * Gets the value of the accountHoldingBranch property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountHoldingBranch() {
        return accountHoldingBranch;
    }

    /**
     * Sets the value of the accountHoldingBranch property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountHoldingBranch(String value) {
        this.accountHoldingBranch = value;
    }

    /**
     * Gets the value of the bankGiroCreditNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankGiroCreditNumber() {
        return bankGiroCreditNumber;
    }

    /**
     * Sets the value of the bankGiroCreditNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankGiroCreditNumber(String value) {
        this.bankGiroCreditNumber = value;
    }

    /**
     * Gets the value of the accountNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the value of the accountNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountNumber(String value) {
        this.accountNumber = value;
    }

    /**
     * Gets the value of the sortCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSortCode() {
        return sortCode;
    }

    /**
     * Sets the value of the sortCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSortCode(String value) {
        this.sortCode = value;
    }

    /**
     * Gets the value of the bankCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * Sets the value of the bankCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankCode(String value) {
        this.bankCode = value;
    }

    /**
     * Gets the value of the blzCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBlzCode() {
        return blzCode;
    }

    /**
     * Sets the value of the blzCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBlzCode(String value) {
        this.blzCode = value;
    }

    /**
     * Gets the value of the bankBsb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankBsb() {
        return bankBsb;
    }

    /**
     * Sets the value of the bankBsb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankBsb(String value) {
        this.bankBsb = value;
    }

    /**
     * Gets the value of the branchCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBranchCode() {
        return branchCode;
    }

    /**
     * Sets the value of the branchCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBranchCode(String value) {
        this.branchCode = value;
    }

    /**
     * Gets the value of the bankLocationIso3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankLocationIso3() {
        return bankLocationIso3;
    }

    /**
     * Sets the value of the bankLocationIso3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankLocationIso3(String value) {
        this.bankLocationIso3 = value;
    }

}
