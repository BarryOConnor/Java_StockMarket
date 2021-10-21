package barryoconnor;

/** helper class to allow for more indepth return values including validity, message and actual result
 *
 * @author Barry O'Connor
 */

public class FirebaseResult {
    private boolean mOperationValid = true;
    private String mResponse = "";
        
    public FirebaseResult(boolean valid, String response) {
        // Class constructor with values. Set the initial values for the class
        mOperationValid = valid; 
        mResponse = response;
    }

    public FirebaseResult() { }

    public boolean getOperationValid() {
        //accessor method for OperationValid
        return mOperationValid;
    }

    public void setOperationValid(boolean value) {
        //mutator method for OperationValid
        mOperationValid = value;
    }

    public String getResponse() {
        //accessor method for OperationValid
        return mResponse;
    }

    public void setResponse(String response) {
        //mutator method for OperationValid
        mResponse = response;
    }

}
