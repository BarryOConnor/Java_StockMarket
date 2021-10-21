
package barryoconnor;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.netbeans.xml.schema.stocks.ShareType;


/**
 * <p>Java class for operationResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="operationResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="operationValid" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="errorInformation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="resultsList" type="{http://xml.netbeans.org/schema/Stocks}share_type" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "operationResult", propOrder = {
    "operationValid",
    "errorInformation",
    "resultsList"
})
public class OperationResult {

    protected boolean operationValid;
    protected String errorInformation;
    @XmlElement(nillable = true)
    protected List<ShareType> resultsList;

    /**
     * Gets the value of the operationValid property.
     * 
     */
    public boolean isOperationValid() {
        return operationValid;
    }

    /**
     * Sets the value of the operationValid property.
     * 
     */
    public void setOperationValid(boolean value) {
        this.operationValid = value;
    }

    /**
     * Gets the value of the errorInformation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorInformation() {
        return errorInformation;
    }

    /**
     * Sets the value of the errorInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorInformation(String value) {
        this.errorInformation = value;
    }

    /**
     * Gets the value of the resultsList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resultsList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResultsList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShareType }
     * 
     * 
     */
    public List<ShareType> getResultsList() {
        if (resultsList == null) {
            resultsList = new ArrayList<ShareType>();
        }
        return this.resultsList;
    }

}
