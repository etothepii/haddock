
package com.betfair.publicapi.types.exchange.v5;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for APIResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="APIResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="header" type="{http://www.betfair.com/publicapi/types/exchange/v5/}APIResponseHeader"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "APIResponse", propOrder = {
    "header"
})
@XmlSeeAlso({
    GetMarketResp.class,
    GetMarketInfoResp.class,
    HeartbeatResp.class,
    GetAccountFundsResp.class,
    GetMarketPricesResp.class,
    GetAccountStatementResp.class,
    GetSilksResp.class,
    GetMarketTradedVolumeResp.class,
    UpdateBetsResp.class,
    GetBetMatchesLiteResp.class,
    GetCurrentBetsLiteResp.class,
    GetCurrentBetsResp.class,
    GetBetLiteResp.class,
    GetMUBetsLiteResp.class,
    GetBetResp.class,
    GetCouponResp.class,
    GetBetHistoryResp.class,
    GetInPlayMarketsResp.class,
    GetMarketTradedVolumeCompressedResp.class,
    GetSilksV2Resp.class,
    GetMUBetsResp.class,
    GetPrivateMarketsResp.class,
    GetMarketProfitAndLossResp.class,
    PlaceBetsResp.class,
    GetMarketPricesCompressedResp.class,
    CancelBetsResp.class,
    GetDetailedAvailableMktDepthResp.class,
    GetCompleteMarketPricesCompressedResp.class,
    CancelBetsByMarketResp.class,
    GetAllMarketsResp.class
})
public abstract class APIResponse {

    @XmlElement(required = true, nillable = true)
    protected APIResponseHeader header;

    /**
     * Gets the value of the header property.
     * 
     * @return
     *     possible object is
     *     {@link APIResponseHeader }
     *     
     */
    public APIResponseHeader getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     * 
     * @param value
     *     allowed object is
     *     {@link APIResponseHeader }
     *     
     */
    public void setHeader(APIResponseHeader value) {
        this.header = value;
    }

}
