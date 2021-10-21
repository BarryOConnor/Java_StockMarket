package barryoconnor;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Helper Class to handle the Guardian News API
 * returns only the useful JSON portion
 * @author Barry O'Connor
 */
import org.json.JSONArray;
import org.json.JSONObject;
public class GuardianNewsAPI {
    private final String access_key = "d1f04d44-d9e4-4d2b-a0fa-d9963177b804";
    private final String guardianURL = "https://content.guardianapis.com/search?section=technology&api-key=" + access_key + "&q=";
    
    public void GuardianNewsAPI(){}
    
    /**
     *
     * @param companyName - name of the company to search for
     * @return JSON array of news items
     */
    public String getLatestNews(String companyName) {  
        String results = "";
              
              
        try {
            if (companyName.contains("(") && companyName.contains(")")) { 
                Pattern regex = Pattern.compile("\\((.*?)\\)");
                Matcher regexMatcher = regex.matcher(companyName);

                while (regexMatcher.find()) {//Finds Matching Pattern in String
                   companyName = regexMatcher.group(1);//Fetching Group from String
                }            
            }

            // populate the Editor Panel with the HTML
            companyName = "\"" + companyName.replace(" ", "%20") + "\"";
            
            URL url = new URL(guardianURL+companyName);
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
            JSONObject response = obj.getJSONObject("response");
            JSONArray dataArray = response.getJSONArray("results");
            for (int i = 0; i < dataArray.length(); i++) {
                results += "<div><h1>" + dataArray.getJSONObject(i).getString("webTitle") + "</h1><a href=\"" + dataArray.getJSONObject(i).getString("webUrl") + "\">" + dataArray.getJSONObject(i).getString("webUrl") + "</a></div><hr />";
            }
        } catch (MalformedURLException me) {
              System.out.println("MalformedURLException: " + me); 
        } catch (IOException ioe) {
               System.out.println("IOException: " + ioe); 
        }       
        return results;      
    }
    
}
