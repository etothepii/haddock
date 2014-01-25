
package com.betfair.publicapi.types.global.v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WithdrawByBankTransferReq complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WithdrawByBankTransferReq">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.betfair.com/publicapi/types/global/v3/}APIRequest">
 *       &lt;sequence>
 *         &lt;element name="mode" type="{http://www.betfair.com/publicapi/types/global/v3/}WithdrawByBankTransferModeEnum"/>
 *         &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="bankAccountDetails" type="{http://www.betfair.com/publicapi/types/global/v3/}BankAccountDetails"/>
 *         &lt;element name="expressTransfer" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WithdrawByBankTransferReq", propOrder = {
    "mode",
    "amount",
    "bankAccountDetails",
    "expressTransfer",
    "password"
})
public class WithdrawByBankTransferReq
    extends APIRequest
{

    @XmlElement(required = true)
    protected WithdrawByBankTransferModeEnum mode;
    protected double amount;
    @XmlElement(required = true)
    protected BankAccountDetails bankAccountDetails;
    protected boolean expressTransfer;
    @XmlElement(required = true, nillable = true)
    protected String password;

    /**
     * Gets the value of the mode property.
     * 
     * @return
     *     possible object is
     *     {@link WithdrawByBankTransferModeEnum }
     *     
     */
    public WithdrawByBankTransferModeEnum getMode() {
        return mode;
    }

    /**
     * Sets the value of the mode property.
     * 
     * @param value
     *     allowed object is
     *     {@link WithdrawByBankTransferModeEnum }
     *     
     */
    public void setMode(WithdrawByBankTransferModeEnum value) {
        this.mode = value;
    }

    /**
     * Gets the value of the amount property.
     * 
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     */
    public void setAmount(double value) {
        this.amount = value;
    }

    /**
     * Gets the value of the bankAccountDetails property.
     * 
     * @return
     *     possible object is
     *     {@link BankAccountDetails }
     *     
     */
    public BankAccountDetails getBankAccountDetails() {
        return bankAccountDetails;
    }

    /**
     * Sets the value of the bankAccountDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link BankAccountDetails }
     *     
     */
    public void setBankAccountDetails(BankAccountDetails value) {
        this.bankAccountDetails = value;
    }

    /**
     * Gets the value of the expressTransfer property.
     * 
     */
    public boolean isExpressTransfer() {
        return expressTransfer;
    }

    /**
     * Sets the value of the expressTransfer property.
     * 
     */
    public void setExpressTransfer(boolean value) {
        this.expressTransfer = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

}
