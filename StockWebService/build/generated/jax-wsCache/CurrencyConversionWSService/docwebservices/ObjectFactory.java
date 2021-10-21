
package docwebservices;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the docwebservices package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetConversionRate_QNAME = new QName("http://DOCwebServices/", "GetConversionRate");
    private final static QName _GetConversionRateResponse_QNAME = new QName("http://DOCwebServices/", "GetConversionRateResponse");
    private final static QName _GetCurrencyCodesFormatted_QNAME = new QName("http://DOCwebServices/", "GetCurrencyCodesFormatted");
    private final static QName _GetCurrencyCodesFormattedResponse_QNAME = new QName("http://DOCwebServices/", "GetCurrencyCodesFormattedResponse");
    private final static QName _GetCurrencyCodeList_QNAME = new QName("http://DOCwebServices/", "getCurrencyCodeList");
    private final static QName _GetCurrencyCodeListResponse_QNAME = new QName("http://DOCwebServices/", "getCurrencyCodeListResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: docwebservices
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetConversionRate }
     * 
     */
    public GetConversionRate createGetConversionRate() {
        return new GetConversionRate();
    }

    /**
     * Create an instance of {@link GetConversionRateResponse }
     * 
     */
    public GetConversionRateResponse createGetConversionRateResponse() {
        return new GetConversionRateResponse();
    }

    /**
     * Create an instance of {@link GetCurrencyCodesFormatted }
     * 
     */
    public GetCurrencyCodesFormatted createGetCurrencyCodesFormatted() {
        return new GetCurrencyCodesFormatted();
    }

    /**
     * Create an instance of {@link GetCurrencyCodesFormattedResponse }
     * 
     */
    public GetCurrencyCodesFormattedResponse createGetCurrencyCodesFormattedResponse() {
        return new GetCurrencyCodesFormattedResponse();
    }

    /**
     * Create an instance of {@link GetCurrencyCodeList }
     * 
     */
    public GetCurrencyCodeList createGetCurrencyCodeList() {
        return new GetCurrencyCodeList();
    }

    /**
     * Create an instance of {@link GetCurrencyCodeListResponse }
     * 
     */
    public GetCurrencyCodeListResponse createGetCurrencyCodeListResponse() {
        return new GetCurrencyCodeListResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetConversionRate }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://DOCwebServices/", name = "GetConversionRate")
    public JAXBElement<GetConversionRate> createGetConversionRate(GetConversionRate value) {
        return new JAXBElement<GetConversionRate>(_GetConversionRate_QNAME, GetConversionRate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetConversionRateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://DOCwebServices/", name = "GetConversionRateResponse")
    public JAXBElement<GetConversionRateResponse> createGetConversionRateResponse(GetConversionRateResponse value) {
        return new JAXBElement<GetConversionRateResponse>(_GetConversionRateResponse_QNAME, GetConversionRateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCurrencyCodesFormatted }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://DOCwebServices/", name = "GetCurrencyCodesFormatted")
    public JAXBElement<GetCurrencyCodesFormatted> createGetCurrencyCodesFormatted(GetCurrencyCodesFormatted value) {
        return new JAXBElement<GetCurrencyCodesFormatted>(_GetCurrencyCodesFormatted_QNAME, GetCurrencyCodesFormatted.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCurrencyCodesFormattedResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://DOCwebServices/", name = "GetCurrencyCodesFormattedResponse")
    public JAXBElement<GetCurrencyCodesFormattedResponse> createGetCurrencyCodesFormattedResponse(GetCurrencyCodesFormattedResponse value) {
        return new JAXBElement<GetCurrencyCodesFormattedResponse>(_GetCurrencyCodesFormattedResponse_QNAME, GetCurrencyCodesFormattedResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCurrencyCodeList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://DOCwebServices/", name = "getCurrencyCodeList")
    public JAXBElement<GetCurrencyCodeList> createGetCurrencyCodeList(GetCurrencyCodeList value) {
        return new JAXBElement<GetCurrencyCodeList>(_GetCurrencyCodeList_QNAME, GetCurrencyCodeList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCurrencyCodeListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://DOCwebServices/", name = "getCurrencyCodeListResponse")
    public JAXBElement<GetCurrencyCodeListResponse> createGetCurrencyCodeListResponse(GetCurrencyCodeListResponse value) {
        return new JAXBElement<GetCurrencyCodeListResponse>(_GetCurrencyCodeListResponse_QNAME, GetCurrencyCodeListResponse.class, null, value);
    }

}
