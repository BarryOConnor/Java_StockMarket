/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package barryoconnor;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONArray;


/**
 *
 * @author Barry O'Connor
 */
public class FirebaseWebComms {
    private final String APIKEY = "AIzaSyA8VRiNYSC0VEDyNe_egsgAKt16ftktteE";
        
    public FirebaseWebComms(){};
    
    private FirebaseResult sendHTTPPOST(String url, String POSTContent,String contentType, String token) {
        try {
            URL mUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
            conn.setDoOutput( true );
            conn.setRequestMethod( "POST" );
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
            
            if(!contentType.isEmpty()){
                conn.setRequestProperty("Content-Type", contentType);
            } else {
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            }
            
            if(!token.isEmpty()){
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }
            
            DataOutputStream wr = new DataOutputStream( conn.getOutputStream());
            wr.write( POSTContent.getBytes() );
            
            if (conn.getResponseCode() != 200 && conn.getResponseCode() != 400) {
                return new FirebaseResult(false, conn.getResponseMessage());
            } else if (conn.getResponseCode() == 400){
                BufferedReader buffRead = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String inString;
                StringBuilder sb = new StringBuilder();
                while ((inString = buffRead.readLine()) != null) {
                    sb.append(inString);
                }
                buffRead.close();

                JSONObject inputJSONObj = new JSONObject(sb.toString());
                JSONObject errorObj = inputJSONObj.getJSONObject("error");
                return new FirebaseResult(false, errorObj.getString("message"));
            }           
            
            BufferedReader buffRead = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inString;
            StringBuilder sb = new StringBuilder();
            while ((inString = buffRead.readLine()) != null) {
                sb.append(inString);
            }
            buffRead.close();
            
            return new FirebaseResult(true, sb.toString());
            
        } catch (MalformedURLException me) {
                return new FirebaseResult(false, "MalformedURLException: " + me);
        } catch (IOException ioe) {
                return new FirebaseResult(false, "IOException: " + ioe); 
        }        
      
    }
    
    
    private FirebaseResult sendHTTPPATCH(String url, String POSTContent,String contentType, String token) {
        try {
            URL mUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
            conn.setDoOutput( true );
            //conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            conn.setRequestMethod( "POST" );
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
            
            if(!contentType.isEmpty()){
                conn.setRequestProperty("Content-Type", contentType);
            } else {
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            }
            
            if(!token.isEmpty()){
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }
            
            DataOutputStream wr = new DataOutputStream( conn.getOutputStream());
            wr.write( POSTContent.getBytes() );
            
            if (conn.getResponseCode() != 200 && conn.getResponseCode() != 400) {
                return new FirebaseResult(false, conn.getResponseMessage());
            } else if (conn.getResponseCode() == 400){
                BufferedReader buffRead = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String inString;
                StringBuilder sb = new StringBuilder();
                while ((inString = buffRead.readLine()) != null) {
                    sb.append(inString);
                }
                buffRead.close();

                JSONObject inputJSONObj = new JSONObject(sb.toString());
                JSONObject errorObj = inputJSONObj.getJSONObject("error");
                return new FirebaseResult(false, errorObj.getString("message"));
            }           
            
            BufferedReader buffRead = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inString;
            StringBuilder sb = new StringBuilder();
            while ((inString = buffRead.readLine()) != null) {
                sb.append(inString);
            }
            buffRead.close();
            
            return new FirebaseResult(true, sb.toString());
            
        } catch (MalformedURLException me) {
                return new FirebaseResult(false, "MalformedURLException: " + me);
        } catch (IOException ioe) {
                return new FirebaseResult(false, "IOException: " + ioe); 
        }        
      
    }
    
    
    private FirebaseResult sendHTTPGET(String url, String token) {
        try {
            URL mUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
            conn.setRequestMethod( "GET" );
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
                       
            if(!token.isEmpty()){
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }
                       
            if (conn.getResponseCode() != 200 && conn.getResponseCode() != 400) {
                return new FirebaseResult(false, conn.getResponseMessage());
            } else if (conn.getResponseCode() == 400){
                BufferedReader buffRead = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String inString;
                StringBuilder sb = new StringBuilder();
                while ((inString = buffRead.readLine()) != null) {
                    sb.append(inString);
                }
                buffRead.close();

                JSONObject inputJSONObj = new JSONObject(sb.toString());
                JSONObject errorObj = inputJSONObj.getJSONObject("error");
                return new FirebaseResult(false, errorObj.getString("message"));
            }           
            
            BufferedReader buffRead = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inString;
            StringBuilder sb = new StringBuilder();
            while ((inString = buffRead.readLine()) != null) {
                sb.append(inString);
            }
            buffRead.close();
            
            return new FirebaseResult(true, sb.toString());
            
        } catch (MalformedURLException me) {
                return new FirebaseResult(false, "MalformedURLException: " + me);
        } catch (IOException ioe) {
                return new FirebaseResult(false, "IOException: " + ioe); 
        }        
      
    }
    
    
    public FirebaseResult login(String email, String password){
        JSONObject outputJSONObj = new JSONObject();
        outputJSONObj.put("email", email);
        outputJSONObj.put("password", password);
        outputJSONObj.put("returnSecureToken", true);
        return sendHTTPPOST("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="+APIKEY, outputJSONObj.toString(),"","");
        
    }
    
    public FirebaseResult register(String email, String password){
        JSONObject outputJSONObj = new JSONObject();
        outputJSONObj.put("email", email);
        outputJSONObj.put("password", password);
        outputJSONObj.put("returnSecureToken", true);
        return sendHTTPPOST("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key="+APIKEY, outputJSONObj.toString(),"","");
    }
    
    public FirebaseResult addUser(JSONObject fields, String documentID, String token){
        return sendHTTPPOST("https://firestore.googleapis.com/v1/projects/sccc-79ede/databases/(default)/documents/users?key=" + APIKEY + "&documentId=" + documentID, fields.toString(),"", token);    
    }
    
    public FirebaseResult getUser(String documentID, String token){
        return sendHTTPGET("https://firestore.googleapis.com/v1/projects/sccc-79ede/databases/(default)/documents/users/" + documentID + "?key=" + APIKEY, token);    
    }
    
    public FirebaseResult saveUser(JSONObject fields, String documentID, String token){
        return sendHTTPPATCH("https://firestore.googleapis.com/v1/projects/sccc-79ede/databases/(default)/documents/users/" + documentID + "?key=" + APIKEY, fields.toString(),"", token);    
    }
}
