
package com.betfair.publicapi.types.global.v3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfBankAccountDetailsField complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfBankAccountDetailsField">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BankAccountDetailsField" type="{http://www.betfair.com/publicapi/types/global/v3/}BankAccountDetailsField" maxOccurs="unbounded" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfBankAccountDetailsField", propOrder = {
    "bankAccountDetailsField"
})
public class ArrayOfBankAccountDetailsField {

    @XmlElement(name = "BankAccountDetailsField", namespace = "http://www.betfair.com/publicapi/types/global/v3/", required = true, nillable = true)
    protected List<BankAccountDetailsField> bankAccountDetailsField;

    /**
     * Gets the value of the bankAccountDetailsField property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bankAccountDetailsField property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBankAccountDetailsField().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BankAccountDetailsField }
     * 
     * 
     */
    public List<BankAccountDetailsField> getBankAccountDetailsField() {
        if (bankAccountDetailsField == null) {
            bankAccountDetailsField = new ArrayList<BankAccountDetailsField>();
        }
        return this.bankAccountDetailsField;
    }

}
