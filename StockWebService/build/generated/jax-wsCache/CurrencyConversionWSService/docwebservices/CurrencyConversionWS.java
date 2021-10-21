
package docwebservices;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.11-b150120.1832
 * Generated source version: 2.2
 * 
 */
@WebService(name = "CurrencyConversionWS", targetNamespace = "http://DOCwebServices/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface CurrencyConversionWS {


    /**
     * 
     * @return
     *     returns java.util.List<java.lang.String>
     */
    @WebMethod(operationName = "GetCurrencyCodesFormatted")
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "GetCurrencyCodesFormatted", targetNamespace = "http://DOCwebServices/", className = "docwebservices.GetCurrencyCodesFormatted")
    @ResponseWrapper(localName = "GetCurrencyCodesFormattedResponse", targetNamespace = "http://DOCwebServices/", className = "docwebservices.GetCurrencyCodesFormattedResponse")
    @Action(input = "http://DOCwebServices/CurrencyConversionWS/GetCurrencyCodesFormattedRequest", output = "http://DOCwebServices/CurrencyConversionWS/GetCurrencyCodesFormattedResponse")
    public List<String> getCurrencyCodesFormatted();

    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns double
     */
    @WebMethod(operationName = "GetConversionRate")
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "GetConversionRate", targetNamespace = "http://DOCwebServices/", className = "docwebservices.GetConversionRate")
    @ResponseWrapper(localName = "GetConversionRateResponse", targetNamespace = "http://DOCwebServices/", className = "docwebservices.GetConversionRateResponse")
    @Action(input = "http://DOCwebServices/CurrencyConversionWS/GetConversionRateRequest", output = "http://DOCwebServices/CurrencyConversionWS/GetConversionRateResponse")
    public double getConversionRate(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1);

    /**
     * 
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getCurrencyCodeList", targetNamespace = "http://DOCwebServices/", className = "docwebservices.GetCurrencyCodeList")
    @ResponseWrapper(localName = "getCurrencyCodeListResponse", targetNamespace = "http://DOCwebServices/", className = "docwebservices.GetCurrencyCodeListResponse")
    @Action(input = "http://DOCwebServices/CurrencyConversionWS/getCurrencyCodeListRequest", output = "http://DOCwebServices/CurrencyConversionWS/getCurrencyCodeListResponse")
    public String getCurrencyCodeList();

}