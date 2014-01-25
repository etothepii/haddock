
package com.betfair.publicapi.types.global.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BankAccountDetailsFieldEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BankAccountDetailsFieldEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PAYEE"/>
 *     &lt;enumeration value="BANK_LOCATION_ISO3"/>
 *     &lt;enumeration value="BANK_NAME"/>
 *     &lt;enumeration value="ACCOUNT_HOLDING_BRANCH"/>
 *     &lt;enumeration value="ACCOUNT_NUMBER"/>
 *     &lt;enumeration value="ACCOUNT_TYPE"/>
 *     &lt;enumeration value="BANK_CODE"/>
 *     &lt;enumeration value="SORT_CODE"/>
 *     &lt;enumeration value="BANK_KEY"/>
 *     &lt;enumeration value="BRANCH_CODE"/>
 *     &lt;enumeration value="ROUTING"/>
 *     &lt;enumeration value="BANK_BSB"/>
 *     &lt;enumeration value="BLZ_CODE"/>
 *     &lt;enumeration value="ABI_CAB"/>
 *     &lt;enumeration value="BANK_GIRO_CREDIT_NUMBER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BankAccountDetailsFieldEnum")
@XmlEnum
public enum BankAccountDetailsFieldEnum {

    PAYEE("PAYEE"),
    @XmlEnumValue("BANK_LOCATION_ISO3")
    BANK_LOCATION_ISO_3("BANK_LOCATION_ISO3"),
    BANK_NAME("BANK_NAME"),
    ACCOUNT_HOLDING_BRANCH("ACCOUNT_HOLDING_BRANCH"),
    ACCOUNT_NUMBER("ACCOUNT_NUMBER"),
    ACCOUNT_TYPE("ACCOUNT_TYPE"),
    BANK_CODE("BANK_CODE"),
    SORT_CODE("SORT_CODE"),
    BANK_KEY("BANK_KEY"),
    BRANCH_CODE("BRANCH_CODE"),
    ROUTING("ROUTING"),
    BANK_BSB("BANK_BSB"),
    BLZ_CODE("BLZ_CODE"),
    ABI_CAB("ABI_CAB"),
    BANK_GIRO_CREDIT_NUMBER("BANK_GIRO_CREDIT_NUMBER");
    private final String value;

    BankAccountDetailsFieldEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BankAccountDetailsFieldEnum fromValue(String v) {
        for (BankAccountDetailsFieldEnum c: BankAccountDetailsFieldEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
