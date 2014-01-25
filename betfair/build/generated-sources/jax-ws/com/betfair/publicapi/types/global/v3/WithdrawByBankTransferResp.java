
package com.betfair.publicapi.types.global.v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WithdrawByBankTransferResp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WithdrawByBankTransferResp">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.betfair.com/publicapi/types/global/v3/}APIResponse">
 *       &lt;sequence>
 *         &lt;element name="errorCode" type="{http://www.betfair.com/publicapi/types/global/v3/}PaymentsErrorEnum"/>
 *         &lt;element name="minorErrorCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="amountWithdrawn" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="minAmount" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="maxAmount" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="amountAvailable" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="transferFee" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="expressTransferFee" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="expressTransferAvailable" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="lastBankAccountDetails" type="{http://www.betfair.com/publicapi/types/global/v3/}BankAccountDetails"/>
 *         &lt;element name="requiredBankAccountDetailsFields" type="{http://www.betfair.com/publicapi/types/global/v3/}ArrayOfBankAccountDetailsField"/>
 *         &lt;element name="transactionId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WithdrawByBankTransferResp", propOrder = {
    "errorCode",
    "minorErrorCode",
    "amountWithdrawn",
    "minAmount",
    "maxAmount",
    "amountAvailable",
    "transferFee",
    "expressTransferFee",
    "expressTransferAvailable",
    "lastBankAccountDetails",
    "requiredBankAccountDetailsFields",
    "transactionId"
})
public class WithdrawByBankTransferResp
    extends APIResponse
{

    @XmlElement(required = true)
    protected PaymentsErrorEnum errorCode;
    @XmlElement(required = true, nillable = true)
    protected String minorErrorCode;
    protected double amountWithdrawn;
    protected double minAmount;
    protected double maxAmount;
    @XmlElement(required = true, type = Double.class, nillable = true)
    protected Double amountAvailable;
    @XmlElement(required = true, type = Double.class, nillable = true)
    protected Double transferFee;
    @XmlElement(required = true, type = Double.class, nillable = true)
    protected Double expressTransferFee;
    @XmlElement(required = true, type = Boolean.class, nillable = true)
    protected Boolean expressTransferAvailable;
    @XmlElement(required = true, nillable = true)
    protected BankAccountDetails lastBankAccountDetails;
    @XmlElement(required = true, nillable = true)
    protected ArrayOfBankAccountDetailsField requiredBankAccountDetailsFields;
    @XmlElement(required = true, nillable = true)
    protected String transactionId;

    /**
     * Gets the value of the errorCode property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentsErrorEnum }
     *     
     */
    public PaymentsErrorEnum getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the value of the errorCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentsErrorEnum }
     *     
     */
    public void setErrorCode(PaymentsErrorEnum value) {
        this.errorCode = value;
    }

    /**
     * Gets the value of the minorErrorCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinorErrorCode() {
        return minorErrorCode;
    }

    /**
     * Sets the value of the minorErrorCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinorErrorCode(String value) {
        this.minorErrorCode = value;
    }

    /**
     * Gets the value of the amountWithdrawn property.
     * 
     */
    public double getAmountWithdrawn() {
        return amountWithdrawn;
    }

    /**
     * Sets the value of the amountWithdrawn property.
     * 
     */
    public void setAmountWithdrawn(double value) {
        this.amountWithdrawn = value;
    }

    /**
     * Gets the value of the minAmount property.
     * 
     */
    public double getMinAmount() {
        return minAmount;
    }

    /**
     * Sets the value of the minAmount property.
     * 
     */
    public void setMinAmount(double value) {
        this.minAmount = value;
    }

    /**
     * Gets the value of the maxAmount property.
     * 
     */
    public double getMaxAmount() {
        return maxAmount;
    }

    /**
     * Sets the value of the maxAmount property.
     * 
     */
    public void setMaxAmount(double value) {
        this.maxAmount = value;
    }

    /**
     * Gets the value of the amountAvailable property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getAmountAvailable() {
        return amountAvailable;
    }

    /**
     * Sets the value of the amountAvailable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setAmountAvailable(Double value) {
        this.amountAvailable = value;
    }

    /**
     * Gets the value of the transferFee property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getTransferFee() {
        return transferFee;
    }

    /**
     * Sets the value of the transferFee property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setTransferFee(Double value) {
        this.transferFee = value;
    }

    /**
     * Gets the value of the expressTransferFee property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getExpressTransferFee() {
        return expressTransferFee;
    }

    /**
     * Sets the value of the expressTransferFee property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setExpressTransferFee(Double value) {
        this.expressTransferFee = value;
    }

    /**
     * Gets the value of the expressTransferAvailable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExpressTransferAvailable() {
        return expressTransferAvailable;
    }

    /**
     * Sets the value of the expressTransferAvailable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExpressTransferAvailable(Boolean value) {
        this.expressTransferAvailable = value;
    }

    /**
     * Gets the value of the lastBankAccountDetails property.
     * 
     * @return
     *     possible object is
     *     {@link BankAccountDetails }
     *     
     */
    public BankAccountDetails getLastBankAccountDetails() {
        return lastBankAccountDetails;
    }

    /**
     * Sets the value of the lastBankAccountDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link BankAccountDetails }
     *     
     */
    public void setLastBankAccountDetails(BankAccountDetails value) {
        this.lastBankAccountDetails = value;
    }

    /**
     * Gets the value of the requiredBankAccountDetailsFields property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfBankAccountDetailsField }
     *     
     */
    public ArrayOfBankAccountDetailsField getRequiredBankAccountDetailsFields() {
        return requiredBankAccountDetailsFields;
    }

    /**
     * Sets the value of the requiredBankAccountDetailsFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfBankAccountDetailsField }
     *     
     */
    public void setRequiredBankAccountDetailsFields(ArrayOfBankAccountDetailsField value) {
        this.requiredBankAccountDetailsFields = value;
    }

    /**
     * Gets the value of the transactionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the value of the transactionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionId(String value) {
        this.transactionId = value;
    }

}
