
package com.betfair.publicapi.types.global.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ViewProfileV2ReqVersionEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ViewProfileV2ReqVersionEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="V1"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ViewProfileV2ReqVersionEnum")
@XmlEnum
public enum ViewProfileV2ReqVersionEnum {

    @XmlEnumValue("V1")
    V_1("V1");
    private final String value;

    ViewProfileV2ReqVersionEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ViewProfileV2ReqVersionEnum fromValue(String v) {
        for (ViewProfileV2ReqVersionEnum c: ViewProfileV2ReqVersionEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
