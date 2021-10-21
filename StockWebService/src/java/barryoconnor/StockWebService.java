package barryoconnor;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import docwebservices.CurrencyConversionWSService;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceRef;
import org.json.JSONObject;
import org.netbeans.xml.schema.stocks.ShareType;
import org.netbeans.xml.schema.stocks.StockMarket;

/**
 *
 * @author Barry O'Connor
 */
@WebService(serviceName = "StockWebService")
@Stateless()
public class StockWebService {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/StockWebService/StockWebService.wsdl")
    private StockWebService_Service service_1;

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/CurrencyConvertor/CurrencyConversionWSService.wsdl")
    private CurrencyConversionWSService service;

    private StockMarket sharesXML = new StockMarket();
    private final String fileCWD = "I:\\NTU\\SCCC\\SCCCProject\\SCCC-Stock-Market-SOA\\StockWebService\\";
    private List<ShareType> shares = null;
    
    //create arrays of all the valid search terms
    private static final List validSearchFields = Arrays.asList("companyname","noofshares","symbol","shareprice");
    private static final List validSortList = Arrays.asList("companyname_asc","noofshares_asc","symbol_asc","shareprice_asc","lastupdated_asc","companyname_desc","noofshares_desc","symbol_desc","shareprice_desc","lastupdated_desc");
    private static final List validSearchTypeNumeric = Arrays.asList("=","<",">","<=",">=");
    private static final List validSearchTypeAlpha = Arrays.asList("begins","contains","ends","equals");
    
    
    // comparator class for the XMLGregorianCalendar type so we can sort it
    class DateComparator implements Comparator<ShareType> {
        @Override
        public int compare(ShareType share1, ShareType share2) {
            return share1.getSharePrice().getLastUpdated().compare(share2.getSharePrice().getLastUpdated());
        }
    }
    
    
    private void readStockList() throws FileNotFoundException{  
        if (shares == null || shares.isEmpty()) { 
            //used to populate the shares List directly as we want to use the version in memory rather than reloading where possible
            sharesXML = new StockMarket();
            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(sharesXML.getClass().getPackage().getName());
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                InputStream inputXML = new FileInputStream( fileCWD + "stock.xml" );
                sharesXML = (StockMarket) unmarshaller.unmarshal(inputXML); //NOI18N
            } catch (javax.xml.bind.JAXBException ex) {
                //Handle exception
                java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
            }
            shares = sharesXML.getShare();
        }
    }




    
    private Boolean writeStockList() throws FileNotFoundException{
        //used to marshall the shares List directly as we want to use the version in memory rather than reloading where possible
        try { 
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(sharesXML.getClass().getPackage().getName());
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            OutputStream outputXML = new FileOutputStream( fileCWD + "stock.xml" );
            marshaller.marshal( sharesXML, outputXML );
        } catch (javax.xml.bind.JAXBException ex) {
            //Handle exception
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
        }
        
        return true;
    }
    
    
    
    
    private boolean isInteger(String input) { //Pass in string 
        try { //Try to make the input into an integer 
            Integer.parseInt(input); 
            return true; //Return true if it works 
            } 
        catch(Exception e) {  
            return false; //If it doesn't work return false 
        } 
    } 
    
    private boolean isDouble(String input) { //Pass in string 
        try { //Try to make the input into an integer 
            Double.parseDouble(input); 
            return true; //Return true if it works 
            } 
        catch( Exception e ) {  
            return false; //If it doesn't work return false 
        } 
    } 
    
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }




    
    private OperationResult tradeShares(String symbol, boolean selling, int amount) {
        /* worker function to buy or sell shares. Both operations are so similar, it's inefficient to have both, simply differentiate with a parameter
        *  INPUT
        *  symbol - the stockmarket symbol for the company
        *  selling - boolean for whether selling or not (buying)
        *  amount - amount of shares to buy or sell
        
        *  OUTPUT
        *  an OperationResult with the results of the operation
        */
        
        //create a default instance of the OperationResults class
        OperationResult results = new OperationResult();
        try {
            //check if shares List is empty, if not use the version already in memory
            readStockList();
        } catch (FileNotFoundException ex) {
            //there was an error, log it and return the result
            Logger.getLogger(StockWebService.class.getName()).log(Level.SEVERE, null, ex);
            results.setError("unable to read share list, please try again later");
            return results;
        }   
        
        //use a stream and filter for shares where the symbol matches the parameter value
        //foreach that matches alter theNo of Shares
        
        if(selling){ 
            shares.stream().filter(share -> share.getSymbol().equalsIgnoreCase(symbol))
                .forEach(share -> { share.setNoOfShares(share.getNoOfShares() + amount); });
        } else {
            shares.stream().filter(share -> share.getSymbol().equalsIgnoreCase(symbol))
                .forEach(share -> { share.setNoOfShares(share.getNoOfShares() - amount); });
        }
        
        
        //try and write the changes to the XML file
        try {
            writeStockList();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StockWebService.class.getName()).log(Level.SEVERE, null, ex);
            //set an error on the return value if anything goes wrong
            results.setError("unable to save changes to share list, please try again later");
        } 
        
        //if we get this far, everything was fine so just return the valid results
        results.setValidResults(shares);
        return results;
    }
    
    
    
    
    private OperationResult performSearch(String searchField, String searchType, String searchValue1, String searchValue2) {
        /* Method to search the shares by various fields and values. 
        *
        *  Validation is included as a precaution for future developers using the class. The public methods that access this function
        *  already perform validation checks
        *
        *  INPUT
        *  searchField - the field to search
        *  searchType - the type of search to apply (greater than, less than etc)
        *  searchValue1 - the standard value to search for
        *  searchValue2 - the second value to search for when the search operation is between 2 values
        *
        *  OUTPUT
        *  an OperationResult with the results of the operation
        */  
        
        System.out.println(searchField + " - " + searchType + " - " + searchValue1 + " - " + searchValue2);

        //create a default instance of the OperationResults class
        OperationResult results = new OperationResult();
        try {
            //check if shares List is empty, if not use the version already in memory
            readStockList();
        } catch (FileNotFoundException ex) {
            //there was an error, log it and return the result
            Logger.getLogger(StockWebService.class.getName()).log(Level.SEVERE, null, ex);
            results.setError("unable to read share list, please try again later");
            return results;
        }     
        
        //switch depending on which field was selected for the search
        switch(searchField) {    
            case "companyname":
                switch(searchType) {
                    /*switch depending on the type of search performed.  
                    since we are using a string as the search value, there's really no need for validation
                    as any valid string, even a null will work*/
                    case "equals":
                        results.setValidResults(shares.stream()
                            .filter(share -> share.getCompanyName().equalsIgnoreCase(searchValue1))
                            .collect(Collectors.toList())); 
                        break;
                    case "begins":
                        results.setValidResults(shares.stream()
                            .filter(share -> share.getCompanyName().toUpperCase().startsWith(searchValue1.toUpperCase()))
                            .collect(Collectors.toList())); 
                        break;
                    case "contains":
                        results.setValidResults(shares.stream()
                            .filter(share -> share.getCompanyName().toUpperCase().contains(searchValue1.toUpperCase()))
                            .collect(Collectors.toList()));                        
                        break;
                    case "ends":
                        results.setValidResults(shares.stream()
                            .filter(share -> share.getCompanyName().toUpperCase().endsWith(searchValue1.toUpperCase()))
                            .collect(Collectors.toList())); 
                        break;
                    default:
                        results.setError("search type not supported");
                        break;
                }
                break; 
                
                
                
            case "noofshares": 
                //searchValue must be an int for these searches so make sure it is
                if(!isInteger(searchValue1)){
                    results.setError("Search Value for No of Shares must be an integer");
                    return results;
                } else {
                    int searchVal1 = Integer.parseInt(searchValue1);
                    switch(searchType) {
                        case "=":
                            results.setResultslist(shares.stream()
                                .filter(share -> share.getNoOfShares() == searchVal1)
                                .collect(Collectors.toList()));
                            break; 
                        case ">":
                            results.setResultslist(shares.stream()
                                .filter(share -> share.getNoOfShares() > searchVal1)
                                .collect(Collectors.toList()));
                            break;
                        case "<":
                            results.setResultslist(shares.stream()
                                .filter(share -> share.getNoOfShares() < searchVal1)
                                .collect(Collectors.toList()));                
                            break;
                        case ">=":
                            results.setResultslist(shares.stream()
                                .filter(share -> share.getNoOfShares() >= searchVal1)
                                .collect(Collectors.toList()));
                            break;
                        case "<=":
                            results.setResultslist(shares.stream()
                                .filter(share -> share.getNoOfShares() <= searchVal1)
                                .collect(Collectors.toList()));                
                            break;
                        case "between":
                            if(!isInteger(searchValue2)){
                                results.setError("Search Value for No of Shares must be an integer");
                                return results;
                            } else {
                                int searchVal2 = Integer.parseInt(searchValue2);
                                results.setResultslist(shares.stream()
                                    .filter(share -> share.getNoOfShares() >= searchVal1 && share.getNoOfShares() <= searchVal2)
                                    .collect(Collectors.toList()));
                            }
                            break;
                        default:
                            results.setError("search type not supported");
                            break;
                    }
                }                
                break;          
                
                
            case "symbol":
                String upperSearchValue1 = searchValue1.toUpperCase();
                switch(searchType) {
                    case "equals":
                        results.setValidResults(shares.stream()
                            .filter(share -> share.getSymbol().equalsIgnoreCase(upperSearchValue1))
                            .collect(Collectors.toList())); 
                        break;
                    case "begins":
                        results.setResultslist(shares.stream()
                            .filter(share -> share.getSymbol().toUpperCase().startsWith(upperSearchValue1.toUpperCase()))
                            .collect(Collectors.toList())); 
                        break;
                    case "contains":
                        results.setResultslist(shares.stream()
                            .filter(share -> share.getSymbol().toUpperCase().contains(upperSearchValue1.toUpperCase()))
                            .collect(Collectors.toList())); 
                        break;
                    case "ends":
                        results.setResultslist(shares.stream()
                            .filter(share -> share.getSymbol().toUpperCase().endsWith(upperSearchValue1.toUpperCase()))
                            .collect(Collectors.toList())); 
                        break;
                    default:
                        results.setError("search type not supported");
                        break;
                }
                break; 
                
                
                
            case "shareprice":
                //searchValue must be a double or 2 doubles with a "|" between for these searches so make sure it is
                if(!isDouble(searchValue1)){
                    results.setError("Search Value for Share Price must be a number");
                    return results;
                } else {
                    double searchVal1 = Double.parseDouble(searchValue1);
                    switch(searchType) {
                        case "=":
                            results.setResultslist(shares.stream()
                                .filter(share -> share.getSharePrice().getValue() == searchVal1)
                                .collect(Collectors.toList()));
                            break; 
                        case ">":
                            results.setResultslist(shares.stream()
                                .filter(share -> share.getSharePrice().getValue() > searchVal1)
                                .collect(Collectors.toList()));
                            break;
                        case "<":
                            results.setResultslist(shares.stream()
                                .filter(share -> share.getSharePrice().getValue() < searchVal1)
                                .collect(Collectors.toList()));                
                            break;
                        case ">=":
                            results.setResultslist(shares.stream()
                                .filter(share -> share.getSharePrice().getValue() >= searchVal1)
                                .collect(Collectors.toList()));
                            break;
                        case "<=":
                            results.setResultslist(shares.stream()
                                .filter(share -> share.getSharePrice().getValue() <= searchVal1)
                                .collect(Collectors.toList()));                
                            break;
                        case "between":
                            if(!isDouble(searchValue2)){
                                results.setError("Search Values for Share Price must be an double");
                                return results;
                            } else {
                                double searchVal2 = Double.parseDouble(searchValue2);
                                results.setResultslist(shares.stream()
                                    .filter(share -> share.getSharePrice().getValue() >= searchVal1 && share.getSharePrice().getValue() <= searchVal2)
                                    .collect(Collectors.toList()));
                            }
                            break;
                        default:
                            results.setError("search type not supported");
                            break;
                    }
                }
                
                break;
                
                
            default:
                results.setError("No valid field was submitted");
                break;
        }

        return results;
    }
    
    
    
        
    /**
     * Web service operation
     * @throws java.io.FileNotFoundException
     */
    @WebMethod(operationName = "buy")
    public OperationResult buy(@WebParam(name = "symbol") String symbol, @WebParam(name = "amount") int amount) {
        /* wrapper function to sell shares. 
        *  INPUT
        *  symbol - the stockmarket symbol for the company
        *  amount - amount of shares to buy or sell
        *  
        *  OUTPUT
        *  an OperationResult with the results of the operation
        */  
        
        //return the result of running the worker function with provided parameters
        return tradeShares(symbol, false, amount);
    }
    
    
    
    
    /**
     * Web service operation
     * @throws java.io.FileNotFoundException
     */
    @WebMethod(operationName = "sell")
    public OperationResult sell(@WebParam(name = "symbol") String symbol, @WebParam(name = "amount") int amount) {
        /* wrapper function to sell shares. 
        *  INPUT
        *  symbol - the stockmarket symbol for the company
        *  amount - amount of shares to buy or sell
        *  
        *  OUTPUT
        *  an OperationResult with the results of the operation
        */  
        
        //return the result of running the worker function with provided parameters
        return tradeShares(symbol, true, amount);
    }
    
    

    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "sort")
    public OperationResult sort(@WebParam(name = "sortField") String sortField) {
        /* Method to sort the shares by various fields and directions. 
        *  INPUT
        *  sortField - the field to sort by with "_asc" or "_desc" added to show direction of sort
        *  
        *  OUTPUT
        *  an OperationResult with the results of the operation
        */  
        
        //create a default instance of the OperationResults class
        OperationResult results = new OperationResult();
        
        boolean validSort = validSortList.contains(sortField.toLowerCase());
        
        if(!validSort){ 
            //if the string isn't in one of the provided formats return an error
                results.setError("supplied sort is not supported.");
                return results;
        }
        
        try {
            //check if shares List is empty, if not use the version already in memory
            readStockList();
        } catch (FileNotFoundException ex) {
            //there was an error, log it and return the result
            Logger.getLogger(StockWebService.class.getName()).log(Level.SEVERE, null, ex);
            results.setError("unable to read share list, please try again later");
            return results;
        }        
                
        //switch depending on field and direction of sort
        switch(sortField.toLowerCase()) {
            case "companyname_asc":
                //sort Company Name ascending using comparator
                shares.sort(Comparator.comparing(ShareType::getCompanyName));
                break; 
            case "companyname_desc":
                //sort Company Name descending using comparator using reversed to reverse the sort
                shares.sort(Comparator.comparing(ShareType::getCompanyName).reversed());
                break;
            case "noofshares_asc":
                //sort Number of Shares ascending using comparator
                shares.sort(Comparator.comparing(ShareType::getNoOfShares));
                break;
            case "noofshares_desc":
                //sort Number of Shares descending using comparator using reversed to reverse the sort
                shares.sort(Comparator.comparing(ShareType::getNoOfShares).reversed());
                break;
            case "symbol_asc":
                //sort Symbol ascending using comparator
                shares.sort(Comparator.comparing(ShareType::getSymbol));
                break;
            case "symbol_desc":
                //sort Symbol descending using comparator using reversed to reverse the sort
                shares.sort(Comparator.comparing(ShareType::getSymbol).reversed());
                break;
            case "shareprice_asc":
                //sort Share Price ascending using comparator, since this is a nested object the syntax is different
                shares.sort(Comparator.comparing(ShareType->ShareType.getSharePrice().getValue()));
                break;
            case "shareprice_desc":
                //sort Share Price descending using comparator, since this is a nested object the syntax is different
                shares.sort(Comparator.comparing(ShareType->ShareType.getSharePrice().getValue(),Comparator.reverseOrder()));
                break;
            case "lastupdated_asc":
                //sort LastUpdated ascending using comparator, since this is a nested object the syntax is different
                Collections.sort(shares, new DateComparator());
                break;
            case "lastupdated_desc":
                //sort LastUpdated descending using comparator, since this is a nested object the syntax is different
                Collections.sort(shares, Collections.reverseOrder(new DateComparator()));
                break;  
            default:
            //should have been picked up before the switch but just in case we will add it here
                results.setError("requested sort is not supported.");
                return results;
        }
        
        //apply the sorted list to results instance
        results.setResultslist(shares);
        
        return results;
    }
    
    
    
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "searchBetween")
    public OperationResult searchBetween(@WebParam(name = "searchField") String searchField, @WebParam(name = "startSearchValue") String startSearchValue, @WebParam(name = "endSearchValue") String endSearchValue) {
        /* Method to search the shares by various fields and valuesbetween 2 values.
        *  Overloading would have been a better way to do this but support for this on web services is bad so we have to have 2 functions unfortunately
        *  INPUT
        *  searchField - the field to search
        *  searchType - the type of search to apply (greater than, less than etc)
        *  startSearchValue - the start value to search for
        *  endfirstSearchValue - the start value to search for
        *
        *  OUTPUT
        *  an OperationResult with the results of the operation
        */  
        
        if(searchField.equalsIgnoreCase("noofshares")){
            if(isInteger(startSearchValue) && isInteger(startSearchValue) ){
                return performSearch(searchField, "between", startSearchValue, endSearchValue);
            } else {
                return new OperationResult("Both search values must be integer for this field");
            }            
        } else if (searchField.equalsIgnoreCase("shareprice")){
            if(isDouble(startSearchValue) && isDouble(startSearchValue) ){
                return performSearch(searchField, "between", startSearchValue, endSearchValue);
            } else {
                return new OperationResult("Both search values must be double for this field");
            }
        } else {
            //if the field does not match, return an error
            return new OperationResult("The field submitted was not valid");
        }

    }  
    
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "search")
    public OperationResult search(@WebParam(name = "searchField") String searchField, @WebParam(name = "searchType") String searchType, @WebParam(name = "searchValue") String searchValue) {
        /* Method to validate input before passing to the private performSearch function. 
        *  INPUT
        *  searchField - the field to search
        *  searchType - the type of search to apply (greater than, less than etc)
        *  searchValue - the value to search for
        *
        *  OUTPUT
        *  an OperationResult with the results of the operation
        */  
               
        //create boolean flags for whether the various terms are correct
        boolean validField = validSearchFields.contains(searchField.toLowerCase());
        boolean numericSearch = validSearchTypeNumeric.contains(searchType.toLowerCase());
        boolean alphaSearch = validSearchTypeAlpha.contains(searchType.toLowerCase());
        
        if(validField){
            if(alphaSearch && !numericSearch){
                //alpha search is just a string so no further checks needed, return the results of the search
                return performSearch(searchField, searchType, searchValue, null);
            } else if (!alphaSearch && numericSearch){
                //numeric search so make sure the value is int for NoOfShares and double for SharePrice
                if(( isInteger(searchValue) && searchField.equalsIgnoreCase("noofshares") ) || ( isDouble(searchValue) && searchField.equalsIgnoreCase("shareprice") )){
                    //all good, return the results of the search
                    return performSearch(searchField, searchType, searchValue, null);  
                } else {
                    return new OperationResult("search value is the wrong type");
                }
            } else {
                //if the search type doesn't match, return an error
                return new OperationResult("The requested search type is not supported");
            }            
        } else {
            //if the field does not match, return an error
            return new OperationResult("The field submitted was not valid");
        }
      
    }
    
    /**
     * Web service operation
     * @throws java.io.FileNotFoundException
     */

    private OperationResult updateSharePrice(@WebParam(name = "symbol") String symbol, @WebParam(name = "newValue") double newValue) throws DatatypeConfigurationException {
        
        //return the result of running the worker function with provided parameters
        /* worker function to buy or sell shares. Both operations are so similar, it's inefficient to have both, simply differentiate with a parameter
        *  INPUT
        *  symbol - the stockmarket symbol for the company
        *  selling - boolean for whether selling or not (buying)
        *  amount - amount of shares to buy or sell
        
        *  OUTPUT
        *  an OperationResult with the results of the operation
        */
        
        //create a default instance of the OperationResults class
        OperationResult results = new OperationResult();
        try {
            //check if shares List is empty, if not use the version already in memory
            readStockList();
        } catch (FileNotFoundException ex) {
            //there was an error, log it and return the result
            Logger.getLogger(StockWebService.class.getName()).log(Level.SEVERE, null, ex);
            results.setError("unable to read share list, please try again later");
            return results;
        }   
        
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
        
        //use a stream and filter for shares where the symbol matches the parameter value
        //foreach that matches alter theNo of Shares
        
        shares.stream().filter(share -> share.getSymbol().equalsIgnoreCase(symbol))
            .forEach(share -> { 
                share.getSharePrice().setValue(newValue);
                share.getSharePrice().setLastUpdated(now);
            });

        
        
        //try and write the changes to the XML file
        try {
            writeStockList();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StockWebService.class.getName()).log(Level.SEVERE, null, ex);
            //set an error on the return value if anything goes wrong
            results.setError("unable to save changes to share list, please try again later");
        } 
        
        //if we get this far, everything was fine so just return the valid results
        results.setValidResults(shares);
        return results;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getStockCodes")    
    public String getStockCodes() {
        String codes = "";
        
        try {
            //check if shares List is empty, if not use the version already in memory
            readStockList();
        } catch (FileNotFoundException ex) {
            //there was an error, log it and return the result
            Logger.getLogger(StockWebService.class.getName()).log(Level.SEVERE, null, ex);
        }
          
        // run through currencies and add the formatted string to the list

        for (ShareType share : shares) {
            codes += share.getSymbol() +",";
        }
        codes = codes.substring(0, codes.length() - 1);
        return codes;
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "updateSharePrices")    
    public boolean updateSharePrices() {

        
        HashMap<String, Double> stockData = new HashMap<String, Double>();
        MarketStackAPI msAPI = new MarketStackAPI();

        stockData = msAPI.getLatestStockValues(getStockCodes());

        for(HashMap.Entry<String, Double> entry : stockData.entrySet()) { 
         
            try {
                updateSharePrice(entry.getKey(), entry.getValue());
            } catch (DatatypeConfigurationException ex) {
                Logger.getLogger(StockWebService.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            
        }
          
        return true;
    }
    
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "userLogin")    
    public String userLogin(@WebParam(name = "email") String email,@WebParam(name = "password") String password) {

        
         WebComms mWC = new WebComms();
            JSONObject outputJSONObj = new JSONObject();
            outputJSONObj.put("email",email);
            outputJSONObj.put("password",password);
            
            String mString = mWC.sendHTTPPOST("http://localhost:8080/RESTfulStockService/webresources/firebasepath/user/login", outputJSONObj.toString());
            
  
        return mString;
    }
    
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "userRegister")    
    public String userRegister(@WebParam(name = "email") String email,@WebParam(name = "password") String password) {

        
         WebComms mWC = new WebComms();
            JSONObject outputJSONObj = new JSONObject();
            outputJSONObj.put("email",email);
            outputJSONObj.put("password",password);
            
            String mString = mWC.sendHTTPPOST("http://localhost:8080/RESTfulStockService/webresources/firebasepath/user/register", outputJSONObj.toString());
            
  
        return mString;
    }
    
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "userUpdate")    
    public String userUpdate(@WebParam(name = "currentUser") String currentUser) {

        
         WebComms mWC = new WebComms();

        String mString = mWC.sendHTTPPOST("http://localhost:8080/RESTfulStockService/webresources/firebasepath/user/save", currentUser);
            
  
        return mString;
    }
    
    
   /**
     * Web service operation
     */
    @WebMethod(operationName = "newsUpdate")    
    public String newsUpdate(@WebParam(name = "company") String company) {

        GuardianNewsAPI gNews = new GuardianNewsAPI();

            
            String mString = gNews.getLatestNews(company);
            
  
        return mString;
    } 
    
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "updateCurrenciesValue")    
    public Integer updateCurrenciesValue() throws NoSuchAlgorithmException, KeyManagementException {

        ExchangeRateAPI mERAPI = new ExchangeRateAPI();
       
        String mCurrencyRates = mERAPI.getLatestStockValuesAsJSON("GBP", getCurrencyCodeList());
        WebComms mWC = new WebComms();
        String mString = mWC.sendHTTPPOST("http://localhost:8080/RESTfulStockService/webresources/currencypath/update", mCurrencyRates);
        if(isInteger(mString)){
            return Integer.parseInt(mString);
        } else {
            return -1;
        }
    } 
    
    /**
     * Passthrough to the Currency converter to get the list of currencies
     */
    @WebMethod(operationName = "getCurrencyListFormatted")    
    public java.util.List<java.lang.String> getCurrencyListFormatted() {

        return getCurrencyCodesFormatted();
    } 
    
    /**
     * Passthrough to the Currency converter to get the list of currencies
     */
    @WebMethod(operationName = "getExchangeRate")    
    public double getExchangeRate(@WebParam(name = "currency") String currency) {

        return getConversionRate("GBP", currency);
    } 

    private String getCurrencyCodeList() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        docwebservices.CurrencyConversionWS port = service.getCurrencyConversionWSPort();
        return port.getCurrencyCodeList();
    }

    private java.util.List<java.lang.String> getCurrencyCodesFormatted() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        docwebservices.CurrencyConversionWS port = service.getCurrencyConversionWSPort();
        return port.getCurrencyCodesFormatted();
    }

    private double getConversionRate(java.lang.String arg0, java.lang.String arg1) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        docwebservices.CurrencyConversionWS port = service.getCurrencyConversionWSPort();
        return port.getConversionRate(arg0, arg1);
    }
   
    
}
