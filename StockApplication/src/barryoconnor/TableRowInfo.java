/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package barryoconnor;

/** Class used to track the currently selected row in the table
 *
 * @author Barry O'Connor
 */
public class TableRowInfo {
    private String mCompanyName = "";
    private String mSymbol = "";
    private int mNoofShares = 0;
    private double mSharePrice = 0.00;
    private String mLastAccessed = "";  
    
    public TableRowInfo(){};
    
    public TableRowInfo(String companyName, String symbol, int noOfShares, double sharePrice, String lastAccessed){
        mCompanyName = companyName;
        mSymbol = symbol;
        mNoofShares = noOfShares;
        mSharePrice = sharePrice;
        mLastAccessed = lastAccessed;
    }
    
    public String getCompanyName(){
        return mCompanyName;
    }
    
    public void setCompanyName(String companyName){
        mCompanyName = companyName;
    }
    
    public String getSymbol(){
        return mSymbol;
    }
    
    public void setSymbol(String symbol){
        mSymbol = symbol;
    } 
    
    public int getNoOfShares(){
        return mNoofShares;
    }
    
    public void setNoOfShares(int noOfShares){
        mNoofShares = noOfShares;
    } 
    
    public double getSharePrice(){
        return mSharePrice;
    }
    
    public void setSharePrice(double sharePrice){
        mSharePrice = sharePrice;
    }
    
    public String getLastAccessed(){
        return mLastAccessed;
    }
    
    public void setLastAccessed(String lastAccessed){
        mLastAccessed = lastAccessed;
    }  
    
    public void setValues(String companyName, String symbol, int noOfShares, double sharePrice, String lastAccessed){
        mCompanyName = companyName;
        mSymbol = symbol;
        mNoofShares = noOfShares;
        mSharePrice = sharePrice;
        mLastAccessed = lastAccessed;
    }
}
