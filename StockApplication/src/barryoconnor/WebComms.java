/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package barryoconnor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/** Class to handle HTTP functionality
 *
 * @author Barry O'Connor
 */
public class WebComms {
        
    public WebComms(){};
    
    /**Handles a HTTP get and returns the result as a string
     *
     * @param url - the url of the resource
     * @return - string result
     */
    public String sendHTTPGet(String url) {
            String returnval="";
        
            try {
                URL mUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
                conn.setRequestMethod( "GET" );
                conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");

                // Check for successful response code or throw error
                if (conn.getResponseCode() != 200) {
                    throw new IOException(conn.getResponseMessage());
                }

                BufferedReader buffRead = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inString;
                StringBuilder sb = new StringBuilder();
                while ((inString = buffRead.readLine()) != null) {
                    sb.append(inString);
                }
                buffRead.close();

                returnval = sb.toString();

            } catch (MalformedURLException me) {
                  System.out.println("MalformedURLException: " + me); 
            } catch (IOException ioe) {
                   System.out.println("IOException: " + ioe); 
            }       
            return returnval;      
        }
    
    
    
    /**Handles a HTTP get and returns the result as a string
    *
    * @param url - the url of the resource
    * @param POSTContent - the JSON content to post
    * @return - string result
    */
    
    public String sendHTTPPOST(String url, String POSTContent) {
        String returnval="";
        
        try {
            URL mUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
            conn.setDoOutput( true );
            conn.setRequestMethod( "POST" );
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8"); 
            DataOutputStream wr = new DataOutputStream( conn.getOutputStream());
            wr.write( POSTContent.getBytes() );
            
            if (conn.getResponseCode() != 200) {
                throw new IOException(conn.getResponseMessage());
            }           
            
            BufferedReader buffRead = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inString;
            StringBuilder sb = new StringBuilder();
            while ((inString = buffRead.readLine()) != null) {
                sb.append(inString);
            }
            buffRead.close();
            
            returnval = sb.toString();
            
        } catch (MalformedURLException me) {
              System.out.println("MalformedURLException: " + me); 
        } catch (IOException ioe) {
               System.out.println("IOException: " + ioe); 
        }       
        return returnval;      
    }
    
}
