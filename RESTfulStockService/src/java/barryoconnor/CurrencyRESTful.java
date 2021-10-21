package barryoconnor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.netbeans.xml.schema.currencies.Currencies;
import org.netbeans.xml.schema.currencies.CurrencyType;

/** RESTful api to handle currencies
 *
 * @author Barry O'Connor
 */
@Path("currencypath")
public class CurrencyRESTful {
    private Currencies currencyXML = new Currencies();
    private final String fileCWD = "I:\\NTU\\SCCC\\SCCCProject\\SCCC-Stock-Market-SOA\\RESTfulStockService\\";
    private List<CurrencyType> currencies = null;
    
    private void readCurrencies() throws FileNotFoundException{  
        if (currencies == null || currencies.isEmpty()) { 
            //used to populate the shares List directly as we want to use the version in memory rather than reloading where possible
            currencyXML = new Currencies();
            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(currencyXML.getClass().getPackage().getName());
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                InputStream inputXML = new FileInputStream( fileCWD + "currencies.xml" );
                currencyXML = (Currencies) unmarshaller.unmarshal(inputXML); //NOI18N
            } catch (javax.xml.bind.JAXBException ex) {
                //Handle exception
                java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
            }
            currencies = currencyXML.getCurrency();
        }
    }
    
    
    private Boolean writeCurrencies() throws FileNotFoundException{
        //used to marshall the shares List directly as we want to use the version in memory rather than reloading where possible
        try { 
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(currencyXML.getClass().getPackage().getName());
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            OutputStream outputXML = new FileOutputStream( fileCWD + "currencies.xml" );
            marshaller.marshal( currencyXML, outputXML );
        } catch (javax.xml.bind.JAXBException ex) {
            //Handle exception
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
        }
        
        return true;
    }

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of CurrencyRESTResource
     */
    public CurrencyRESTful() {
    }

    /**
     * Retrieves representation of an instance of com.barryoconnor.CurrencyRESTResource
     * @return an instance of java.lang.String
     */
    @GET
    @Path("/{currency}")
    @Produces(MediaType.TEXT_PLAIN)
    public double getJson(@PathParam("currency") String currency) {
         double rate = -1;

        try { //check if shares List is empty, if not use the version already in memory
                readCurrencies();
            } catch (FileNotFoundException ex) {
                //there was an error, log it
                Logger.getLogger(CurrencyRESTful.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //use a stream and filter for currencies where the symbol matches the parameter value
            //foreach that matches alter currency rate 
            
            CurrencyType correctCurrency = currencies.stream()
                .filter(customer -> currency.equals(customer.getCode()))
                .findAny()
                .orElse(null);
            
            if (correctCurrency != null) {
                rate = correctCurrency.getRate();
            }
            System.out.println(rate);
        return rate;
    }


    /**
     * POST method for updating or creating an instance of CurrencyRESTResource
     * @param content representation for the resource
     */
    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public int postJson(String inputString) {
        JSONObject inputJSONObj = new JSONObject(inputString);
        JSONArray newCurrencies = inputJSONObj.getJSONArray("rates");
        System.out.println(inputString);
        for (int i = 0; i < newCurrencies.length(); i++) {
            String symbol = newCurrencies.getJSONObject(i).getString("symbol");
            Double rate = newCurrencies.getJSONObject(i).getDouble("rate");
            try { //check if shares List is empty, if not use the version already in memory
                readCurrencies();
            } catch (FileNotFoundException ex) {
                //there was an error, log it
                Logger.getLogger(CurrencyRESTful.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //use a stream and filter for currencies where the symbol matches the parameter value
            //foreach that matches alter currency rate        
            currencies.stream().filter(currency -> currency.getCode().equalsIgnoreCase(symbol))
                .forEach(currency -> { currency.setRate(rate); });  
        
        
            //try and write the changes to the XML file
            try { //check if shares List is empty, if not use the version already in memory
                writeCurrencies();
            } catch (FileNotFoundException ex) {
                //there was an error, log it
                Logger.getLogger(CurrencyRESTful.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println(currencies.size());
        return currencies.size();
    }

}
