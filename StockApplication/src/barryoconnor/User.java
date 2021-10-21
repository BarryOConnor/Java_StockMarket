/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package barryoconnor;

import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**USER class to hold and access the data from Firebase
 *
 * @author Barry O'Connor
 */
public class User {
    private String mEmail = "";
    private String mUserId = "";
    private String mIdToken = "";
    private String mRefreshToken = "";
    private HashMap<String, Integer> mShares = new HashMap<>();
    
    public User(JSONObject inputJSONObj){
        mEmail = inputJSONObj.getString("email");
        mUserId = inputJSONObj.getString("localId");
        mIdToken = inputJSONObj.getString("idToken");
        mRefreshToken = inputJSONObj.getString("refreshToken");
    };
    
    public User(String email, String userId, String idToken, String refreshToken){
        mEmail = email;
        mUserId = userId;
        mIdToken = idToken;
        mRefreshToken = refreshToken;
    };
    
    
    public String getUserID(){
        return mUserId;
    }
    
    public void setUsername(String userId){
        mUserId = userId; 
    }
    
    public String getEmail(){
        return mEmail;
    }
    
    public void setEmail(String email){
        mEmail = email; 
    }
    
    public String getToken(){
        return mIdToken;
    }
    
    public void setToken(String idToken){
        mIdToken = idToken; 
    }
    
    public String getRefreshToken(){
        return mRefreshToken;
    }
    
    public void setRefreshToken(String refreshToken){
        mRefreshToken = refreshToken; 
    }
    
    public int getNoOfShares(String symbol){
        return mShares.getOrDefault(symbol, 0);
    }
    
    public void setShare(String symbol, int noOfShares){
        mShares.put(symbol, noOfShares);
    }
    
    public void setShares(HashMap<String, Integer> shares){
        mShares.putAll(shares);
    }
    
    public void setTokens(String idToken, String refreshToken){
        mIdToken = refreshToken;
        mRefreshToken = refreshToken; 
    }
    
    public JSONObject exportToJSON(){
        
        JSONArray valuesJSONArray = new JSONArray();

        for(HashMap.Entry<String, Integer> entry : mShares.entrySet()) {        
            JSONObject mapFieldsJSONObj = new JSONObject("{\"mapValue\": { \"fields\": { \"amount\": { \"integerValue\": \"" + entry.getValue() + "\" }, \"symbol\": { \"stringValue\": \"" + entry.getKey() + "\" } } } }");
            valuesJSONArray.put(mapFieldsJSONObj);
        }
        JSONObject arrayValueJSONObj = new JSONObject();
        arrayValueJSONObj.put("values", valuesJSONArray);
        
        JSONObject stocksJSONObj = new JSONObject();
        stocksJSONObj.put("arrayValue", arrayValueJSONObj);
        
        JSONObject fieldsJSONObj = new JSONObject();
        fieldsJSONObj.put("stocks", stocksJSONObj);
        
        JSONObject emailJSONObj = new JSONObject();
        emailJSONObj.put("stringValue", mEmail);
        fieldsJSONObj.put("email", emailJSONObj);

        
        JSONObject outputJSONObj = new JSONObject();
        outputJSONObj.put("fields", fieldsJSONObj);
        outputJSONObj.put("localId", mUserId);
        outputJSONObj.put("idToken", mIdToken);
        outputJSONObj.put("refreshToken", mRefreshToken);
        
        return outputJSONObj;
    }
    
    public void importFromJSON(JSONObject importJSONObj){
        
        
        if( importJSONObj.has("localId") ) { mUserId = importJSONObj.getString("userId"); }
        if( importJSONObj.has("idToken") ) { mIdToken = importJSONObj.getString("idToken"); }
        if( importJSONObj.has("refreshToken") ) { mRefreshToken = importJSONObj.getString("refreshToken"); }
        
        if( importJSONObj.has("fields") ) {
            JSONObject fieldsJSONObj = importJSONObj.getJSONObject("fields");
            
            JSONObject emailJSONObj = fieldsJSONObj.getJSONObject("email");
            mEmail = emailJSONObj.getString("stringValue");
            JSONObject stocksJSONObj = fieldsJSONObj.getJSONObject("stocks");
            JSONObject arrayValueJSONObj = stocksJSONObj.getJSONObject("arrayValue");
            if( arrayValueJSONObj.has("values") ) { 
                JSONArray valuesJSONArray = arrayValueJSONObj.getJSONArray("values");

                for (int i=0; i < valuesJSONArray.length(); i++) {
                    JSONObject currValueJSONObj = valuesJSONArray.getJSONObject(i);
                    JSONObject mapValueJSONObj = currValueJSONObj.getJSONObject("mapValue");
                    JSONObject mapFieldJSONObj = mapValueJSONObj.getJSONObject("fields");
                    JSONObject amountJSONObj = mapFieldJSONObj.getJSONObject("amount");
                    JSONObject symbolJSONObj = mapFieldJSONObj.getJSONObject("symbol");
                    setShare(symbolJSONObj.getString("stringValue"), amountJSONObj.getInt("integerValue"));
                }
            }
        }
    }
    
}
