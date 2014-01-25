
package com.betfair.publicapi.types.global.v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ViewProfileV2Req complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ViewProfileV2Req">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.betfair.com/publicapi/types/global/v3/}APIRequest">
 *       &lt;sequence>
 *         &lt;element name="requestVersion" type="{http://www.betfair.com/publicapi/types/global/v3/}ViewProfileV2ReqVersionEnum"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ViewProfileV2Req", propOrder = {
    "requestVersion"
})
public class ViewProfileV2Req
    extends APIRequest
{

    @XmlElement(required = true, nillable = true)
    protected ViewProfileV2ReqVersionEnum requestVersion;

    /**
     * Gets the value of the requestVersion property.
     * 
     * @return
     *     possible object is
     *     {@link ViewProfileV2ReqVersionEnum }
     *     
     */
    public ViewProfileV2ReqVersionEnum getRequestVersion() {
        return requestVersion;
    }

    /**
     * Sets the value of the requestVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link ViewProfileV2ReqVersionEnum }
     *     
     */
    public void setRequestVersion(ViewProfileV2ReqVersionEnum value) {
        this.requestVersion = value;
    }

}
