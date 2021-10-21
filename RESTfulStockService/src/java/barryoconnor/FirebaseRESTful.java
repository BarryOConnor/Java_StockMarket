/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package barryoconnor;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * REST Web Service which acts as a helper class for the Firebase API
 *
 * @author Barry O'Connor
 */
@Path("firebasepath")
public class FirebaseRESTful {
    private final String APIKEY = "AIzaSyA8VRiNYSC0VEDyNe_egsgAKt16ftktteE";

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of FirebaseResource
     */
    public FirebaseRESTful() {
    }

    /** handles user login
     *
     * @param inputString JSON with email and password
     * @return firebase result as JSON
     */
    @POST
    @Path("user/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String postLoginJson(String inputString) {
        JSONObject inputJSONObj = new JSONObject(inputString);
        String email = inputJSONObj.getString("email");
        String password = inputJSONObj.getString("password");
        
        FirebaseWebComms fb = new FirebaseWebComms();
        FirebaseResult fbResult = fb.login(email, password);
        
        JSONObject outputJSONObj = new JSONObject();
        
        if(fbResult.getOperationValid()){
            outputJSONObj = new JSONObject(fbResult.getResponse());
            outputJSONObj.put("result", fbResult.getOperationValid());
        } else {   
            outputJSONObj.put("result", fbResult.getOperationValid());
            outputJSONObj.put("message", fbResult.getResponse());
        }

        return outputJSONObj.toString();
    }
    
    /** handles user registration
     *
     * @param inputString JSON with email and password
     * @return firebase result as JSON
     */
    @POST
    @Path("user/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String postRegisterJson(String inputString) {
        JSONObject inputJSONObj = new JSONObject(inputString);
        String email = inputJSONObj.getString("email");
        String password = inputJSONObj.getString("password");
        
        FirebaseWebComms fb = new FirebaseWebComms();
        FirebaseResult fbResult = fb.register(email, password);
        
        JSONObject outputJSONObj = new JSONObject();
        
        if(fbResult.getOperationValid()){
            outputJSONObj = new JSONObject(fbResult.getResponse());
            outputJSONObj.put("result", fbResult.getOperationValid());
        } else {   
            outputJSONObj.put("result", fbResult.getOperationValid());
            outputJSONObj.put("message", fbResult.getResponse());
        }

        return outputJSONObj.toString();
    }
    
    
    /** handles user add
     *
     * @param inputString JSON with localID and idToken to create a new database document for the user
     * @return firebase result as JSON
     */
    @POST
    @Path("user/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addUser(String inputString) {
        JSONObject inputJSONObj = new JSONObject(inputString);
        JSONObject fieldsJSONObj = new JSONObject();
        fieldsJSONObj.put("fields", inputJSONObj.getJSONObject("fields"));
        
        FirebaseWebComms fb = new FirebaseWebComms();
        FirebaseResult fbResult = fb.addUser(fieldsJSONObj, inputJSONObj.getString("localId"), inputJSONObj.getString("idToken"));
        
        JSONObject outputJSONObj = new JSONObject();
        
        if(fbResult.getOperationValid()){
            outputJSONObj = new JSONObject(fbResult.getResponse());
            outputJSONObj.put("result", fbResult.getOperationValid());
        } else {   
            outputJSONObj.put("result", fbResult.getOperationValid());
            outputJSONObj.put("message", fbResult.getResponse());
        }

        return outputJSONObj.toString();
    }
    
    /** handles user get
     *
     * @param inputString JSON with localID and idToken to return the corresponding user document from Firebase
     * @return firebase result as JSON
     */
    @POST
    @Path("user/get")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getUser(String inputString) {
        JSONObject inputJSONObj = new JSONObject(inputString);
        
        FirebaseWebComms fb = new FirebaseWebComms();
        FirebaseResult fbResult = fb.getUser(inputJSONObj.getString("localId"), inputJSONObj.getString("idToken"));
        
        JSONObject outputJSONObj = new JSONObject();
        
        if(fbResult.getOperationValid()){
            outputJSONObj = new JSONObject(fbResult.getResponse());
            outputJSONObj.put("result", fbResult.getOperationValid());
        } else {   
            outputJSONObj.put("result", fbResult.getOperationValid());
            outputJSONObj.put("message", fbResult.getResponse());
        }

        return outputJSONObj.toString();
    }
    
    
    /** handles user save
     *
     * @param inputString JSON with localID and idToken to save the information of the user
     * @return firebase result as JSON
     */
    @POST
    @Path("user/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String saveUser(String inputString) {
        JSONObject inputJSONObj = new JSONObject(inputString);
        JSONObject fieldsJSONObj = new JSONObject();
        fieldsJSONObj.put("fields", inputJSONObj.getJSONObject("fields"));
        
        FirebaseWebComms fb = new FirebaseWebComms();
        FirebaseResult fbResult = fb.saveUser(fieldsJSONObj, inputJSONObj.getString("localId"), inputJSONObj.getString("idToken"));
        
        JSONObject outputJSONObj = new JSONObject();
        
        if(fbResult.getOperationValid()){
            outputJSONObj = new JSONObject(fbResult.getResponse());
            outputJSONObj.put("result", fbResult.getOperationValid());
        } else {   
            outputJSONObj.put("result", fbResult.getOperationValid());
            outputJSONObj.put("message", fbResult.getResponse());
        }

        return outputJSONObj.toString();
    }
}
