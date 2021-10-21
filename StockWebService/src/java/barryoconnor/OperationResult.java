package barryoconnor;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.netbeans.xml.schema.stocks.ShareType;

/** class to act as a return type for the webservice to allow for a valid flag, message and returned list of results
 *
 * @author Barry O'Connor
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationResult {
    private boolean operationValid;
    private String errorInformation;
    private List<ShareType> resultsList;
        
    public OperationResult(boolean valid, String errorString, List<ShareType> values) {
        // Class constructor with values. Set the initial values for the class
        operationValid = valid; 
        errorInformation = errorString;
        resultsList = values;
    }
    public OperationResult(String errorString) {
        // Class constructor with values. Set the initial values for the class
        operationValid = false; 
        errorInformation = errorString;
    }
    public OperationResult() {
        // Defaultlass constructor. Set the default values for the class
        operationValid = true;  
        errorInformation = "";
    }

    public boolean getOperationValid() {
        //accessor method for OperationValid
        return operationValid;
    }

    public void setOperationValid(boolean value) {
        //mutator method for OperationValid
        this.operationValid = value;
    }

    public String getErrorInformation() {
        //accessor method for OperationValid
        return errorInformation;
    }

    public void setErrorInformation(String value) {
        //mutator method for OperationValid
        this.errorInformation = value;
    }

    public List<ShareType> getResultsList() {
        //accessor method for resultsList
        return resultsList;
    }

    public void setResultslist(List<ShareType> values) {
        //mutator method for resultsList
        this.resultsList = values;
    }

    public void setValidResults(List<ShareType> values) {
        //helper method to set a valid set of results
        this.operationValid = true;
        this.errorInformation = "";
        this.resultsList = values;
    }

    public void setError(String value) {
        //helper method to set an error
        this.operationValid = false;
        this.errorInformation = value;
    }

    public void clearError() {
        //helper method to set an error
        this.operationValid = true;
        this.errorInformation = "";
    }
}
