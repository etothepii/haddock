
package com.betfair.publicapi.types.global.v3;

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
 *         &lt;element name="header" type="{http://www.betfair.com/publicapi/types/global/v3/}APIRequestHeader"/>
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
    ViewProfileV2Req.class,
    ModifyProfileReq.class,
    DeletePaymentCardReq.class,
    LogoutReq.class,
    ViewProfileReq.class,
    KeepAliveReq.class,
    AddPaymentCardReq.class,
    SubmitLIMBMessageReq.class,
    DepositFromPaymentCardReq.class,
    GetSubscriptionInfoReq.class,
    WithdrawByBankTransferReq.class,
    WithdrawToPaymentCardReq.class,
    ModifyPasswordReq.class,
    CreateAccountReq.class,
    GetEventsReq.class,
    GetCurrenciesV2Req.class,
    UpdatePaymentCardReq.class,
    GetEventTypesReq.class,
    SetChatNameReq.class,
    ConvertCurrencyReq.class,
    SelfExcludeReq.class,
    ForgotPasswordReq.class,
    RetrieveLIMBMessageReq.class,
    GetCurrenciesReq.class,
    ViewReferAndEarnReq.class,
    TransferFundsReq.class,
    GetPaymentCardReq.class
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
