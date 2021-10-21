/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package barryoconnor;

import javax.swing.JOptionPane;
import org.json.JSONObject;

/** Application for the project - handles swapping frames within the application
 *
 * @author Barry O'Connor
 */
public class StockMarketApplication {
    private final LoginGUI loginScreen;
    private final ClientGUI clientScreen;
    private final RegisterGUI registerScreen;
    
    private StockMarketApplication() {
        //set the parent in each to a reference to this class
        loginScreen = new LoginGUI(this);
        clientScreen = new ClientGUI(this);
        registerScreen = new RegisterGUI(this);
        
        //center the pages
        loginScreen.setLocationRelativeTo(null);
        clientScreen.setLocationRelativeTo(null);
        registerScreen.setLocationRelativeTo(null);
        
        //begin with the login page
        showLogin();       
       
    }
    
    // handles the display of the main application screen
    public void showApplication(JSONObject result){
        
        User tempUser = new User(result);
        WebComms mWC = new WebComms();
        String mString = "";

        
        if(result.getString("kind").equalsIgnoreCase("identitytoolkit#VerifyPasswordResponse")){
            mString = mWC.sendHTTPPOST("http://localhost:8080/RESTfulStockService/webresources/firebasepath/user/get", tempUser.exportToJSON().toString());
        } else if (result.getString("kind").equalsIgnoreCase("identitytoolkit#SignupNewUserResponse")){
            mString = mWC.sendHTTPPOST("http://localhost:8080/RESTfulStockService/webresources/firebasepath/user/add", tempUser.exportToJSON().toString());
        }
        
        JSONObject responseJSONObj = new JSONObject(mString);
        if(responseJSONObj.getBoolean("result")){
            if(result.getString("kind").equalsIgnoreCase("identitytoolkit#VerifyPasswordResponse")){
                tempUser.importFromJSON(responseJSONObj);
            }
            clientScreen.setCurrentUser(tempUser);
            loginScreen.setVisible(false);
            registerScreen.setVisible(false);
            clientScreen.setVisible(true);
        } else {

            String errorMsg = responseJSONObj.getString("message");
            JOptionPane.showMessageDialog(null, errorMsg);

        }

    }
    

    // handles the display of the application login screen
    public void showLogin(){
        registerScreen.setVisible(false);
        clientScreen.setVisible(false);
        loginScreen.setVisible(true);
    }
    
    // handles the display of the application registration screen
    public void showRegistration(){
        loginScreen.setVisible(false);
        clientScreen.setVisible(false);
        registerScreen.setVisible(true);        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        StockMarketApplication SMA = new StockMarketApplication();
    }
    
}
