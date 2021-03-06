
package com.betfair.publicapi.types.exchange.v5;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetCompleteMarketPricesCompressedResp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetCompleteMarketPricesCompressedResp">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.betfair.com/publicapi/types/exchange/v5/}APIResponse">
 *       &lt;sequence>
 *         &lt;element name="errorCode" type="{http://www.betfair.com/publicapi/types/exchange/v5/}GetCompleteMarketPricesErrorEnum"/>
 *         &lt;element name="completeMarketPrices" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="currencyCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="minorErrorCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCompleteMarketPricesCompressedResp", propOrder = {
    "errorCode",
    "completeMarketPrices",
    "currencyCode",
    "minorErrorCode"
})
public class GetCompleteMarketPricesCompressedResp
    extends APIResponse
{

    @XmlElement(required = true)
    protected GetCompleteMarketPricesErrorEnum errorCode;
    @XmlElement(required = true, nillable = true)
    protected String completeMarketPrices;
    @XmlElement(required = true, nillable = true)
    protected String currencyCode;
    @XmlElement(required = true, nillable = true)
    protected String minorErrorCode;

    /**
     * Gets the value of the errorCode property.
     * 
     * @return
     *     possible object is
     *     {@link GetCompleteMarketPricesErrorEnum }
     *     
     */
    public GetCompleteMarketPricesErrorEnum getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the value of the errorCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetCompleteMarketPricesErrorEnum }
     *     
     */
    public void setErrorCode(GetCompleteMarketPricesErrorEnum value) {
        this.errorCode = value;
    }

    /**
     * Gets the value of the completeMarketPrices property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompleteMarketPrices() {
        return completeMarketPrices;
    }

    /**
     * Sets the value of the completeMarketPrices property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompleteMarketPrices(String value) {
        this.completeMarketPrices = value;
    }

    /**
     * Gets the value of the currencyCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Sets the value of the currencyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrencyCode(String value) {
        this.currencyCode = value;
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

}
