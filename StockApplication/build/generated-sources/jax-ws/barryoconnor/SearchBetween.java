
package barryoconnor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for searchBetween complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="searchBetween">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="searchField" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="startSearchValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="endSearchValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchBetween", propOrder = {
    "searchField",
    "startSearchValue",
    "endSearchValue"
})
public class SearchBetween {

    protected String searchField;
    protected String startSearchValue;
    protected String endSearchValue;

    /**
     * Gets the value of the searchField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSearchField() {
        return searchField;
    }

    /**
     * Sets the value of the searchField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSearchField(String value) {
        this.searchField = value;
    }

    /**
     * Gets the value of the startSearchValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartSearchValue() {
        return startSearchValue;
    }

    /**
     * Sets the value of the startSearchValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartSearchValue(String value) {
        this.startSearchValue = value;
    }

    /**
     * Gets the value of the endSearchValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndSearchValue() {
        return endSearchValue;
    }

    /**
     * Sets the value of the endSearchValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndSearchValue(String value) {
        this.endSearchValue = value;
    }

}
