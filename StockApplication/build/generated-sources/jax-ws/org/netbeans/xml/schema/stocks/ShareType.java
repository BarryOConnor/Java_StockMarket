
package org.netbeans.xml.schema.stocks;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for share_type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="share_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="company_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="symbol" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="no_of_shares" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="share_price">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="currency" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *                   &lt;element name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "share_type", propOrder = {
    "companyName",
    "symbol",
    "noOfShares",
    "sharePrice"
})
public class ShareType {

    @XmlElement(name = "company_name", required = true)
    protected String companyName;
    @XmlElement(required = true)
    protected String symbol;
    @XmlElement(name = "no_of_shares")
    protected int noOfShares;
    @XmlElement(name = "share_price", required = true)
    protected ShareType.SharePrice sharePrice;

    /**
     * Gets the value of the companyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Sets the value of the companyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyName(String value) {
        this.companyName = value;
    }

    /**
     * Gets the value of the symbol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Sets the value of the symbol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSymbol(String value) {
        this.symbol = value;
    }

    /**
     * Gets the value of the noOfShares property.
     * 
     */
    public int getNoOfShares() {
        return noOfShares;
    }

    /**
     * Sets the value of the noOfShares property.
     * 
     */
    public void setNoOfShares(int value) {
        this.noOfShares = value;
    }

    /**
     * Gets the value of the sharePrice property.
     * 
     * @return
     *     possible object is
     *     {@link ShareType.SharePrice }
     *     
     */
    public ShareType.SharePrice getSharePrice() {
        return sharePrice;
    }

    /**
     * Sets the value of the sharePrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShareType.SharePrice }
     *     
     */
    public void setSharePrice(ShareType.SharePrice value) {
        this.sharePrice = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="currency" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}double"/>
     *         &lt;element name="last_updated" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "currency",
        "value",
        "lastUpdated"
    })
    public static class SharePrice {

        @XmlElement(required = true)
        protected String currency;
        protected double value;
        @XmlElement(name = "last_updated", required = true)
        @XmlSchemaType(name = "dateTime")
        protected XMLGregorianCalendar lastUpdated;

        /**
         * Gets the value of the currency property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCurrency() {
            return currency;
        }

        /**
         * Sets the value of the currency property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCurrency(String value) {
            this.currency = value;
        }

        /**
         * Gets the value of the value property.
         * 
         */
        public double getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         */
        public void setValue(double value) {
            this.value = value;
        }

        /**
         * Gets the value of the lastUpdated property.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getLastUpdated() {
            return lastUpdated;
        }

        /**
         * Sets the value of the lastUpdated property.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setLastUpdated(XMLGregorianCalendar value) {
            this.lastUpdated = value;
        }

    }

}
