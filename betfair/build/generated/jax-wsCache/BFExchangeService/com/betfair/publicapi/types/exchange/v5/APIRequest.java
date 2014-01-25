
package com.betfair.publicapi.types.exchange.v5;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for APIRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="APIRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="header" type="{http://www.betfair.com/publicapi/types/exchange/v5/}APIRequestHeader"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "APIRequest", propOrder = {
    "header"
})
@XmlSeeAlso({
    GetBetLiteReq.class,
    GetMarketTradedVolumeReq.class,
    GetMarketInfoReq.class,
    GetMarketTradedVolumeCompressedReq.class,
    GetCurrentBetsLiteReq.class,
    GetAllMarketsReq.class,
    GetInPlayMarketsReq.class,
    GetBetMatchesLiteReq.class,
    GetMarketProfitAndLossReq.class,
    CancelBetsReq.class,
    GetMarketPricesCompressedReq.class,
    GetAccountFundsReq.class,
    GetMUBetsLiteReq.class,
    PlaceBetsReq.class,
    GetCurrentBetsReq.class,
    GetAccountStatementReq.class,
    GetMarketReq.class,
    UpdateBetsReq.class,
    GetMUBetsReq.class,
    CancelBetsByMarketReq.class,
    GetDetailedAvailableMktDepthReq.class,
    GetCouponReq.class,
    GetBetHistoryReq.class,
    GetCompleteMarketPricesCompressedReq.class,
    GetMarketPricesReq.class,
    GetPrivateMarketsReq.class,
    HeartbeatReq.class,
    GetBetReq.class,
    GetSilksReq.class
})
public abstract class APIRequest {

    @XmlElement(required = true, nillable = true)
    protected APIRequestHeader header;

    /**
     * Gets the value of the header property.
     * 
     * @return
     *     possible object is
     *     {@link APIRequestHeader }
     *     
     */
    public APIRequestHeader getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     * 
     * @param value
     *     allowed object is
     *     {@link APIRequestHeader }
     *     
     */
    public void setHeader(APIRequestHeader value) {
        this.header = value;
    }

}
