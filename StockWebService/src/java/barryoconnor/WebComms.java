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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/** Class to handle
 *
 * @author Barry O'Connor
 */
public class WebComms {
        
    public WebComms(){};
    
        public String sendHTTPGet(String url) throws KeyManagementException, NoSuchAlgorithmException {
            String returnval="";
        
            
    /*
     *  fix for
     *    Exception in thread "main" javax.net.ssl.SSLHandshakeException:
     *       sun.security.validator.ValidatorException:
     *           PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
     *               unable to find valid certification path to requested target
     *      
     *  taken from: https://stackoverflow.com/questions/13626965/how-to-ignore-pkix-path-building-failed-sun-security-provider-certpath-suncertp      
     */
    TrustManager[] trustAllCerts = new TrustManager[] {
       new X509TrustManager() {
          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
          }

          public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

          public void checkServerTrusted(X509Certificate[] certs, String authType) {  }

       }
    };

    SSLContext sc;

    sc = SSLContext.getInstance("SSL");
        
    sc.init(null, trustAllCerts, new java.security.SecureRandom());
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

    // Create all-trusting host name verifier
    HostnameVerifier allHostsValid = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
          return true;
        }
    };
    // Install the all-trusting host verifier
    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    /*
     * end of the fix
     */
            
            
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
