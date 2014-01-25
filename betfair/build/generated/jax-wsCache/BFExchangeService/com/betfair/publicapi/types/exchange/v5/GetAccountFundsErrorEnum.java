
package com.betfair.publicapi.types.exchange.v5;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetAccountFundsErrorEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="GetAccountFundsErrorEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OK"/>
 *     &lt;enumeration value="EXPOSURE_CALC_IN_PROGRESS"/>
 *     &lt;enumeration value="API_ERROR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "GetAccountFundsErrorEnum")
@XmlEnum
public enum GetAccountFundsErrorEnum {

    OK,
    EXPOSURE_CALC_IN_PROGRESS,
    API_ERROR;

    public String value() {
        return name();
    }

    public static GetAccountFundsErrorEnum fromValue(String v) {
        return valueOf(v);
    }

}
