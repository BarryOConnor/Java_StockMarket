package barryoconnor;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


/**
 * Helper Class to handle the MarketStack (stock price information) API
 * returns only the useful JSON portion
 * @author Barry O'Connor
 */
import org.json.JSONArray;
import org.json.JSONObject;
public class MarketStackAPI {
    private final String access_key = "3b1dcea9f670ce939a1d73278632ff26";
    private final String eodStockURL = "http://api.marketstack.com/v1/eod/latest?access_key=" + access_key + "&symbols=";
    
    public void MarketStackAPI(){}
    
    /**
     *
     * @param symbols - comma separated list of Stock Symbols to query
     * @return JSON array of Symbols and corresponding closing prices
     */
    public HashMap<String, Double> getLatestStockValues(String symbols) {  
        HashMap<String, Double> stockData = new HashMap<String, Double>();        
        try {
            URL url = new URL(eodStockURL+symbols);
            System.out.println(url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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

            JSONObject obj = new JSONObject(sb.toString());
            JSONArray dataArray = obj.getJSONArray("data");
            for (int i = 0; i < dataArray.length(); i++) {
                stockData.put(dataArray.getJSONObject(i).getString("symbol"), dataArray.getJSONObject(i).getDouble("close"));
            }
        } catch (MalformedURLException me) {
              System.out.println("MalformedURLException: " + me); 
        } catch (IOException ioe) {
               System.out.println("IOException: " + ioe); 
        }       
        return stockData;      
    }
    
}
