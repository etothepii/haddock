
package com.betfair.publicapi.types.global.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WithdrawByBankTransferModeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WithdrawByBankTransferModeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="VALIDATE"/>
 *     &lt;enumeration value="EXECUTE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WithdrawByBankTransferModeEnum")
@XmlEnum
public enum WithdrawByBankTransferModeEnum {

    VALIDATE,
    EXECUTE;

    public String value() {
        return name();
    }

    public static WithdrawByBankTransferModeEnum fromValue(String v) {
        return valueOf(v);
    }

}
