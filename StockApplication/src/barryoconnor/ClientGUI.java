package barryoconnor;

import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.xml.datatype.XMLGregorianCalendar;
import org.json.JSONObject;
import org.netbeans.xml.schema.stocks.ShareType;


/**
 *
 * @author Barry O'Connor
 */
public class ClientGUI extends javax.swing.JFrame {
    private final StockMarketApplication mParent;
    private LoadingMessage loading = new LoadingMessage();
    //models for the table and various comboboxes
    private DefaultTableModel mStockDataModel;
    private final ComboItem[] mFieldNames = new ComboItem[]{
        new ComboItem("companyname","Company Name"), 
        new ComboItem("symbol","Stock Symbol"),
        new ComboItem("noofshares","Number of Shares"),
        new ComboItem("shareprice", "Share Price")};
    
    private final ComboItem[] mAlphaSearchTypes = new ComboItem[]{
        new ComboItem("contains","Contains"), 
        new ComboItem("begins","Begins With"),
        new ComboItem("ends","Ends With"),
        new ComboItem("equals", "Equals")};
    
    private final ComboItem[] mNumericSearchTypes = new ComboItem[]{
        new ComboItem("=","Equals"), 
        new ComboItem(">","Greater Than"),
        new ComboItem("<","Less Than"),
        new ComboItem(">=","Greater Than or Equal"),
        new ComboItem("<=","Less Than or equal"),
        new ComboItem("between", "Between")};
    
    
    // constants to represent the table column names rather than using int
    private final int COMPANYNAME = 0; 
    private final int SYMBOL = 1; 
    private final int NUMBEROFSHARES = 2; 
    private final int SHAREPRICE = 3; 
    private final int LASTUPDATED = 4;

    // properties needed for the search
    private final String[] mSortFields = new String[] {"companyname", "symbol", "noofshares", "shareprice", "lastupdated"}; //sort field names
    private String mSortOrder = mSortFields[0] + "_asc"; //holds the current sort order
    
    // defaults for converting XMLGregorianCalendar to a usable format
    private final static String TIMESTAMP_PATTERN = "MM/dd/yyyy HH:mm";
    private final static DateFormat TIMESTAMP_FORMATTER = new SimpleDateFormat(TIMESTAMP_PATTERN); 

    // currency properties
    private final List<String> mCurrencies = getCurrencyListFormatted(); //populate currencies from the currency converter
    private String mCurrencySymbols = ""; //a comma separated list of all the available currencies, populated from webservice
    private boolean mCurrencyUpdated = false; //status of the update (has the client updated or not)
    private String mCurrentCurrency = "";  //current currency
    private double mCurrentConversionRate = 1.0; //default conversion rate
    private final String mBaseCurrency = "GBP"; //starting currency code
    
    // user info and current row for the table
    private User mCurrentUser;
    private TableRowInfo mCurrRow = new TableRowInfo();
    
    /**
     * Creates new form MainWindow
     */
    public ClientGUI(StockMarketApplication parent) {
        mParent = parent;
        //initialise the components in the GUI window
        initComponents();
        
        //add the loading overlay
        setGlassPane( loading );
        
        // set the models for comboboxes
        cboFieldName.setModel(new DefaultComboBoxModel(mFieldNames));
        cboSearchType.setModel(new DefaultComboBoxModel(mAlphaSearchTypes));
        
        //set the label and value textbox for the "between search to invisible
        lblSearchAnd.setVisible(false);
        txtSearchValueTwo.setVisible(false);
        
        //populate currency information
        mCurrencies.forEach((currCurrency) -> {
            if(currCurrency.substring(0, 3).equalsIgnoreCase(mBaseCurrency)){
                mCurrentCurrency = currCurrency;
            }
            mCurrencySymbols += currCurrency.substring(0, 3) + ",";
            cboCurrencies.addItem(currCurrency);
        });
        mCurrencySymbols = mCurrencySymbols.substring(0, mCurrencySymbols.length() - 1);
        cboCurrencies.setSelectedItem(mCurrentCurrency);
        
        //enable sorting on table headers
        tblShares.setAutoCreateRowSorter(true);
        
        // set up the news feed panel
        epNews.setContentType("text/html");
        epNews.setEditable(false);
        
        //align the date column to the right instead of the left
        DefaultTableCellRenderer dtcrRight = new DefaultTableCellRenderer();
        dtcrRight.setHorizontalAlignment(SwingConstants.RIGHT);
        tblShares.getColumnModel().getColumn(4).setCellRenderer(dtcrRight);
        
        //populate the table with the default sorted information
        populateStockData(sort(mSortOrder));
        

        // add an event listener to listen for hyperlink clicks by the user
        epNews.addHyperlinkListener((HyperlinkEvent mHLE) -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(mHLE.getEventType())) {
                // open the default browser and show the news article
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(mHLE.getURL().toURI());
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        //onClick adapter for the jTable headers to allow us to track the sort currenctly in use 
        tblShares.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTableHeader header = (JTableHeader)(e.getSource());  
                JTable tableView = header.getTable();  
                TableColumnModel columnModel = tableView.getColumnModel();  
                int viewColumn = columnModel.getColumnIndexAtX(e.getX()); 

                if(mSortOrder.equalsIgnoreCase(mSortFields[viewColumn] + "_asc")){
                    mSortOrder = mSortFields[viewColumn] + "_desc";
                } else {
                    mSortOrder = mSortFields[viewColumn] + "_asc";
                }
            }
        });
        
        
        //onClick adapter for the jTable rows to allow us to track the selected row
        tblShares.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && tblShares.getSelectedRow() != -1){
                    setSelectedInfo();
                }
            }
        });
        
        
        //change listener to allow for the display of messages when the user selects an out of range value        
        spnBuyAmount.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = ((Double)spnBuyAmount.getValue()).intValue();
                int maxValue = mCurrRow.getNoOfShares();
                
                if(value > maxValue){
                    JOptionPane.showMessageDialog(null, "You cannot buy more shares than are available");
                    spnBuyAmount.setValue(((Integer)maxValue).doubleValue());
                } else if(value < 0){
                    JOptionPane.showMessageDialog(null, "You cannot buy a negative number of shares");
                    spnBuyAmount.setValue(0.0);
                }
            }
        });
        
        
        //change listener to allow for the display of messages when the user selects an out of range value
        spnSellAmount.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = ((Double)spnSellAmount.getValue()).intValue();
                int maxValue = mCurrentUser.getNoOfShares(mCurrRow.getSymbol());
                        
                if(value > maxValue){
                    JOptionPane.showMessageDialog(null, "You cannot sell more shares than you own");
                    spnSellAmount.setValue(((Integer)maxValue).doubleValue());
                } else if(value < 0){
                    JOptionPane.showMessageDialog(null, "You cannot sell a negative number of shares");
                    spnSellAmount.setValue(0.0);
                }
            }
        });
        
        //handle logging off if the user closes the form
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                int confirmed = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit the program?", "Exit Program?", JOptionPane.YES_NO_OPTION);
                if (confirmed == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }
    
    
    
    
    /**
     * Method to set the current user
     * @param currentUser the current user
     */
    public void setCurrentUser(User currentUser){
        mCurrentUser = currentUser;
        
        //set the initially selected item if anything exists in the table
        if (tblShares.getRowCount() > 0) {
            tblShares.setRowSelectionInterval(0, 0);
        }
        setSelectedInfo(0);
    }
    
    
    
    
    
    /**
     * Method to populate the "Actions for selected Stock" section so that a user can
     * buy and sell stocks and see how many they own 
     * @param selectedRow - the jTable row selected
     */
    private void updateSelectedinfo(int selectedRow){
        
        //set some const values for the various columns in the jTable can't use an enum
        mCurrRow.setValues(tblShares.getValueAt(selectedRow, COMPANYNAME).toString(),
                tblShares.getValueAt(selectedRow, SYMBOL).toString(),
                Integer.parseInt(tblShares.getValueAt(selectedRow, NUMBEROFSHARES).toString()),
                Double.parseDouble(tblShares.getValueAt(selectedRow, SHAREPRICE).toString()),
                tblShares.getValueAt(selectedRow, LASTUPDATED).toString());

        //set the selected Share values in the relevant labels
        lblSelectedShare.setText(mCurrRow.getCompanyName());
        lblSharesOwned.setText(String.valueOf(mCurrentUser.getNoOfShares(mCurrRow.getSymbol())));
        lblLastUpdatedValue.setText(mCurrRow.getLastAccessed());
        
        /*set the spinner so that the user can select a number between -1 and the number of shares available to buy +1
        * this is one less and one more than we want which allows for a message to be displayed so the user
        * doesn't wonder why they can't change the spinner*/
        SpinnerNumberModel buyableShares = new SpinnerNumberModel(0.0, -1.0, mCurrRow.getNoOfShares() + 1, 1.0);
        
        /*set the spinner so that the user can select a number between -1 and the number of shares they own +1
        * this is one less and one more than we want which allows for a message to be displayed so the user
        * doesn't wonder why they can't change the spinner*/
        SpinnerNumberModel sellableShares = new SpinnerNumberModel(0.0, -1.0, mCurrentUser.getNoOfShares(mCurrRow.getSymbol()) + 1, 1.0);
        
        //apply the updated models to the spinners
        spnBuyAmount.setModel(buyableShares);
        spnSellAmount.setModel(sellableShares);
        
        // handle the news for the currently selected company
        String search = tblShares.getValueAt(selectedRow, COMPANYNAME).toString();
        epNews.setText(newsUpdate(search));
        epNews.setCaretPosition(0);
    }
    
    
    
    
    
    /**
     * wrapper functions to make it easier to set the updated content
     * this one is for manually setting the current row
     * @param selectedRow - the jTable row selected
     */ 
    public void setSelectedInfo(int selectedRow){
        updateSelectedinfo(selectedRow);
    }
    
    
    
    
    /**
     * wrapper functions to make it easier to set the updated content
     * this one is for automatically setting the current row based on the
     * currently selected row
     */ 
    public void setSelectedInfo(){
        updateSelectedinfo(tblShares.getSelectedRow());
    }
    
    
    
    
    
     /**
     * Helper to make handling XMLGregorianCalendar dates easier
     * @param selectedRow - the jTable row selected
     */ 
    public static String formatXMLTimeStamp(XMLGregorianCalendar mCal){
        if (mCal == null){
            return "";
        } else {
              return TIMESTAMP_FORMATTER.format(mCal.toGregorianCalendar().getTime());
        }
    }
    
    
    
     /**
     * Method to populate the jTable with the results from various WebService Operation
     * @param result - OperationResult containing the results of the Webservice Operation
     */ 
    private void populateStockData(OperationResult result){
        
        if (result.isOperationValid()) {
           
            Date date = Calendar.getInstance().getTime();  
            DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy HH:mm");  
            lblLastAccessed.setText("Data retrieved at " + dateFormat.format(date));
            List<ShareType> shares = result.getResultsList();

            mStockDataModel = (DefaultTableModel)tblShares.getModel();
            tblShares.setModel(mStockDataModel);
            mStockDataModel.setRowCount(0);

            shares.forEach((currShare) -> {
                mStockDataModel.addRow( new Object[] { currShare.getCompanyName(), currShare.getSymbol(), currShare.getNoOfShares(), currShare.getSharePrice().getValue() * mCurrentConversionRate, formatXMLTimeStamp(currShare.getSharePrice().getLastUpdated()) });
            });
            
            if(!mCurrRow.getSymbol().equalsIgnoreCase("")){
                for (int i = mStockDataModel.getRowCount() - 1; i >= 0; --i) {
                    if (mStockDataModel.getValueAt(i, SYMBOL).equals(mCurrRow.getSymbol())) {
                            // what if value is not unique?
                            tblShares.setRowSelectionInterval(i, i);
                    }
                }
            }
            
        } else {
            JOptionPane.showMessageDialog(null, result.getErrorInformation());
        }
    }
    
    
     /**
     * Method to get update the currencies which then switches to using the new version
     */ 
    private void updateCurrencies() {                                              
        loading.showLoading();
        // run the code in it's own thread so the GUI doesnt get frozen for seconds
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(updateSharePrices()){
                    if(!mCurrentCurrency.equalsIgnoreCase("")){
                        mCurrentCurrency = (String)cboCurrencies.getSelectedItem();
                        String curr2 = mCurrentCurrency.substring(0,3);
                        if(mCurrencyUpdated){
                            // System.out.println("new");
                            WebComms mWC = new WebComms();
                            String mString = mWC.sendHTTPGet("http://localhost:8080/RESTfulStockService/webresources/currencypath/"+curr2);
                            mCurrentConversionRate =  Double.parseDouble(mString);       
                        } else {
                            // System.out.println("old");
                            mCurrentConversionRate = getExchangeRate(curr2);
                        }
                           System.out.println(curr2 + " - " + mCurrentConversionRate);
                        if(mCurrentConversionRate == -1.0) {
                            JOptionPane.showMessageDialog(null, "there was an error retrieving currency conversion data");
                        } else {
                            populateStockData(sort(mSortOrder));
                        }

                    }
                }
                loading.hideLoading();
            }
        });         
    }     
    
    
    
    
    
    /**
     * Method to get the user info and share ownership information from Firebase
     */ 
    private void updateCurrentUser() {                                              
        loading.showLoading();
        // run the code in it's own thread so the GUI doesnt get frozen for seconds
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String mString = userUpdate(mCurrentUser.exportToJSON().toString());

                JSONObject responseJSONObj = new JSONObject(mString);
                if(!responseJSONObj.getBoolean("result")){

                    String errorMsg = responseJSONObj.getString("message");
                    JOptionPane.showMessageDialog(null, errorMsg);

                } 
                loading.hideLoading();
                }
        });
    }     
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlShares = new javax.swing.JPanel();
        scpnlShares = new javax.swing.JScrollPane();
        tblShares = new javax.swing.JTable();
        cboCurrencies = new javax.swing.JComboBox<>();
        lblCurrencies = new javax.swing.JLabel();
        pnlSearch = new javax.swing.JPanel();
        cboFieldName = new javax.swing.JComboBox<>();
        lblSearchInfo = new javax.swing.JLabel();
        cboSearchType = new javax.swing.JComboBox<>();
        txtSearchValueOne = new javax.swing.JTextField();
        txtSearchValueTwo = new javax.swing.JTextField();
        lblSearchAnd = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        pnlSelectedStock = new javax.swing.JPanel();
        lblSelected = new javax.swing.JLabel();
        lblSelectedShare = new javax.swing.JLabel();
        lblSharesOwned = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btnUpdateSharePrice = new javax.swing.JButton();
        lblLastUpdated = new javax.swing.JLabel();
        lblLastUpdatedValue = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        spnBuyAmount = new javax.swing.JSpinner();
        btnBuyShares = new javax.swing.JButton();
        btnSellShares = new javax.swing.JButton();
        lblBuySell = new javax.swing.JLabel();
        spnSellAmount = new javax.swing.JSpinner();
        jSeparator2 = new javax.swing.JSeparator();
        lblBuySell1 = new javax.swing.JLabel();
        pnlStockData = new javax.swing.JPanel();
        lblLastAccessed = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        btnUpdateCurrencies = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        epNews = new javax.swing.JEditorPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mnuFileExit = new javax.swing.JMenuItem();
        mnuUpdateData = new javax.swing.JMenu();
        mnuUpdateCurrencies = new javax.swing.JMenuItem();
        mnuUpdateRefreshData = new javax.swing.JMenuItem();
        mnuAccount = new javax.swing.JMenu();
        mnuAccountSave = new javax.swing.JMenuItem();
        mnuAccountLogout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Stock Market");
        setMinimumSize(new java.awt.Dimension(1024, 768));

        pnlShares.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Shares"));

        tblShares.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Company Name", "Stock Symbol", "Number of Shares", "Share Price", "Last Updated"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblShares.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblShares.getTableHeader().setReorderingAllowed(false);
        scpnlShares.setViewportView(tblShares);
        if (tblShares.getColumnModel().getColumnCount() > 0) {
            tblShares.getColumnModel().getColumn(4).setResizable(false);
        }

        cboCurrencies.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCurrenciesActionPerformed(evt);
            }
        });

        lblCurrencies.setText("Display Currency using:");

        pnlSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Search"));

        cboFieldName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFieldNameActionPerformed(evt);
            }
        });

        lblSearchInfo.setText("Search for Shares where");

        cboSearchType.setToolTipText("");
        cboSearchType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSearchTypeActionPerformed(evt);
            }
        });

        txtSearchValueOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchValueOneActionPerformed(evt);
            }
        });

        lblSearchAnd.setText("and");

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnReset.setText("Reset Search");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSearchLayout = new javax.swing.GroupLayout(pnlSearch);
        pnlSearch.setLayout(pnlSearchLayout);
        pnlSearchLayout.setHorizontalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSearchInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboSearchType, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSearchValueOne, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSearchAnd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSearchValueTwo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnReset)
                .addGap(18, 18, 18)
                .addComponent(btnSearch)
                .addContainerGap())
        );
        pnlSearchLayout.setVerticalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addGroup(pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearchInfo)
                    .addComponent(cboFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboSearchType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearchValueOne, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearchValueTwo, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSearchAnd)
                    .addComponent(btnSearch)
                    .addComponent(btnReset))
                .addGap(0, 8, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlSharesLayout = new javax.swing.GroupLayout(pnlShares);
        pnlShares.setLayout(pnlSharesLayout);
        pnlSharesLayout.setHorizontalGroup(
            pnlSharesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSharesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSharesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scpnlShares, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSharesLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblCurrencies)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboCurrencies, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlSharesLayout.setVerticalGroup(
            pnlSharesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSharesLayout.createSequentialGroup()
                .addGroup(pnlSharesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboCurrencies, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCurrencies))
                .addGap(10, 10, 10)
                .addComponent(scpnlShares, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlSelectedStock.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Actions for Selected Stock"));

        lblSelected.setText("Company:");

        lblSelectedShare.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        lblSharesOwned.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel1.setText("Shares Owned:");

        btnUpdateSharePrice.setText("Update Share Prices");
        btnUpdateSharePrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateSharePriceActionPerformed(evt);
            }
        });

        lblLastUpdated.setText("Last Updated");

        lblLastUpdatedValue.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        spnBuyAmount.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

        btnBuyShares.setText("Buy");
        btnBuyShares.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuySharesActionPerformed(evt);
            }
        });

        btnSellShares.setText("Sell");
        btnSellShares.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSellSharesActionPerformed(evt);
            }
        });

        lblBuySell.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBuySell.setText("Buy Shares in this Company");

        spnSellAmount.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

        lblBuySell1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBuySell1.setText("Sell Shares in this Company");

        javax.swing.GroupLayout pnlSelectedStockLayout = new javax.swing.GroupLayout(pnlSelectedStock);
        pnlSelectedStock.setLayout(pnlSelectedStockLayout);
        pnlSelectedStockLayout.setHorizontalGroup(
            pnlSelectedStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(pnlSelectedStockLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSelectedStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSelectedStockLayout.createSequentialGroup()
                        .addGroup(pnlSelectedStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(lblSelected, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblLastUpdated))
                        .addGap(18, 18, 18)
                        .addGroup(pnlSelectedStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblLastUpdatedValue, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                            .addComponent(lblSharesOwned, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblSelectedShare, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(btnUpdateSharePrice, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(pnlSelectedStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSelectedStockLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlSelectedStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pnlSelectedStockLayout.createSequentialGroup()
                                .addComponent(spnSellAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnSellShares, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSelectedStockLayout.createSequentialGroup()
                                .addComponent(spnBuyAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnBuyShares, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlSelectedStockLayout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(lblBuySell))
                    .addGroup(pnlSelectedStockLayout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(lblBuySell1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlSelectedStockLayout.setVerticalGroup(
            pnlSelectedStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSelectedStockLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(pnlSelectedStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlSelectedStockLayout.createSequentialGroup()
                        .addComponent(lblBuySell, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlSelectedStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spnBuyAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBuyShares))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(lblBuySell1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlSelectedStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spnSellAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSellShares))
                        .addGap(5, 5, 5))
                    .addComponent(jSeparator1)
                    .addGroup(pnlSelectedStockLayout.createSequentialGroup()
                        .addGroup(pnlSelectedStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblSelectedShare, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblSelected, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlSelectedStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSharesOwned, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlSelectedStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblLastUpdated)
                            .addComponent(lblLastUpdatedValue, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnUpdateSharePrice)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pnlStockData.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Stock Data"));

        btnRefresh.setText("Refresh Share Data");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlStockDataLayout = new javax.swing.GroupLayout(pnlStockData);
        pnlStockData.setLayout(pnlStockDataLayout);
        pnlStockDataLayout.setHorizontalGroup(
            pnlStockDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStockDataLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlStockDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLastAccessed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlStockDataLayout.setVerticalGroup(
            pnlStockDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStockDataLayout.createSequentialGroup()
                .addComponent(lblLastAccessed, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRefresh)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Currency Rates"));

        btnUpdateCurrencies.setText("Update Currency Rates");
        btnUpdateCurrencies.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateCurrenciesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnUpdateCurrencies, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnUpdateCurrencies)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Latest News for this Company")));

        jScrollPane1.setViewportView(epNews);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                .addContainerGap())
        );

        mnuFile.setText("File");

        mnuFileExit.setText("Exit");
        mnuFileExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileExitActionPerformed(evt);
            }
        });
        mnuFile.add(mnuFileExit);

        jMenuBar1.add(mnuFile);

        mnuUpdateData.setText("Update Data");

        mnuUpdateCurrencies.setText("Update Currency Rates");
        mnuUpdateCurrencies.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUpdateCurrenciesActionPerformed(evt);
            }
        });
        mnuUpdateData.add(mnuUpdateCurrencies);

        mnuUpdateRefreshData.setText("Refresh Share Data");
        mnuUpdateRefreshData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUpdateRefreshDataActionPerformed(evt);
            }
        });
        mnuUpdateData.add(mnuUpdateRefreshData);

        jMenuBar1.add(mnuUpdateData);

        mnuAccount.setText("Account");

        mnuAccountSave.setText("Save");
        mnuAccountSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAccountSaveActionPerformed(evt);
            }
        });
        mnuAccount.add(mnuAccountSave);

        mnuAccountLogout.setText("Logout");
        mnuAccountLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAccountLogoutActionPerformed(evt);
            }
        });
        mnuAccount.add(mnuAccountLogout);

        jMenuBar1.add(mnuAccount);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlShares, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlSelectedStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlStockData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlShares, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlStockData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlSelectedStock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
     /**
     * Method to process the search type combo change
     */ 
    private void cboSearchTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSearchTypeActionPerformed
        ComboItem selected = (ComboItem)cboSearchType.getSelectedItem();
        
        if ("between".equalsIgnoreCase(selected.getName())){  
            lblSearchAnd.setVisible(true);
            txtSearchValueTwo.setVisible(true);
        } else {
            lblSearchAnd.setVisible(false);
            txtSearchValueTwo.setVisible(false);
        };
    }//GEN-LAST:event_cboSearchTypeActionPerformed

    
    /**
     * Method to process the field name combo change
     */ 
    private void cboFieldNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboFieldNameActionPerformed
        //Set the search Type combobox to numeric/alphanumeric depending on the field chosen
        ComboItem selected = (ComboItem)cboFieldName.getSelectedItem();
        lblSearchAnd.setVisible(false);
        txtSearchValueTwo.setVisible(false);
        if (mFieldNames[0].getName().equalsIgnoreCase(selected.getName()) || mFieldNames[1].getName().equalsIgnoreCase(selected.getName())){
            cboSearchType.setModel(new DefaultComboBoxModel(mAlphaSearchTypes));
        } else {
            cboSearchType.setModel(new DefaultComboBoxModel(mNumericSearchTypes));
        };
    }//GEN-LAST:event_cboFieldNameActionPerformed

    
    /**
     * Method to process the Search button click
     */ 
    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        loading.showLoading();
        // run the code in it's own thread so the GUI doesnt get frozen for seconds
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
        
                ComboItem selectedField = (ComboItem)cboFieldName.getSelectedItem();
                ComboItem selectedType = (ComboItem)cboSearchType.getSelectedItem();

                if (mNumericSearchTypes[5].getName().equalsIgnoreCase(selectedType.getName())){
                    populateStockData(searchBetween(selectedField.getId(), txtSearchValueOne.getText(), txtSearchValueTwo.getText()));                       
                } else {
                    populateStockData(search(selectedField.getId(), selectedType.getId(), txtSearchValueOne.getText()));
                };
                loading.hideLoading();
            }
        });
    }//GEN-LAST:event_btnSearchActionPerformed

    /**
     * Method to process the Refresh Share Data button click
     */ 
    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        loading.showLoading();
        // run the code in it's own thread so the GUI doesnt get frozen for seconds
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                populateStockData(sort(mSortOrder));
                loading.hideLoading();
            }
        });
    }//GEN-LAST:event_btnRefreshActionPerformed

    
    /**
     * Method to process the change of currencies in the combobox
     */ 
    private void cboCurrenciesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCurrenciesActionPerformed

                updateCurrencies();
     
    }//GEN-LAST:event_cboCurrenciesActionPerformed

    
    /**
     * Method to process the UpdateCurrency Rates Data button click
     */ 
    private void btnUpdateCurrenciesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateCurrenciesActionPerformed
        // TODO add your handling code here:
        loading.showLoading();
        // run the code in it's own thread so the GUI doesnt get frozen for seconds
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int result = updateCurrenciesValue();
                if(result != -1) {
                    mCurrencyUpdated = true;
                }
                loading.hideLoading();
            }
        });
    }//GEN-LAST:event_btnUpdateCurrenciesActionPerformed

    /**
     * Method to process the File->Exit command on the menu
     */ 
    private void mnuFileExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileExitActionPerformed
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_mnuFileExitActionPerformed

    
    /**
     * Method to process the Update Data->Update Currencies command on the menu
     */
    private void mnuUpdateCurrenciesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUpdateCurrenciesActionPerformed
        loading.showLoading();
        // run the code in it's own thread so the GUI doesnt get frozen for seconds
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateCurrencies();
                loading.hideLoading();
            }
        });
    }//GEN-LAST:event_mnuUpdateCurrenciesActionPerformed

    /**
     * Method to process the Update Data->Refresh Share Data command on the menu
     */
    private void mnuUpdateRefreshDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUpdateRefreshDataActionPerformed
        loading.showLoading();
        // run the code in it's own thread so the GUI doesnt get frozen for seconds
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                populateStockData(sort(mSortOrder));
                loading.hideLoading();
            }
        });       
    }//GEN-LAST:event_mnuUpdateRefreshDataActionPerformed

    /**
     * Method to process the Buy button
     */
    private void btnBuySharesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuySharesActionPerformed
        loading.showLoading();
        // run the code in it's own thread so the GUI doesnt get frozen for seconds
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int buyShares = ((Double)spnBuyAmount.getValue()).intValue();
                buy(mCurrRow.getSymbol(),buyShares);

                mStockDataModel.setValueAt(mCurrRow.getNoOfShares() - buyShares, tblShares.getSelectedRow(), NUMBEROFSHARES);
                mCurrRow.setNoOfShares(mCurrRow.getNoOfShares() - buyShares);
                mCurrentUser.setShare(mCurrRow.getSymbol(), mCurrentUser.getNoOfShares(mCurrRow.getSymbol()) + buyShares);
                updateCurrentUser();
                updateSelectedinfo(tblShares.getSelectedRow());
                loading.hideLoading();
            }
        });
    }//GEN-LAST:event_btnBuySharesActionPerformed

    
    /**
     * Method to process the Sell button
     */
    private void btnSellSharesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSellSharesActionPerformed
        loading.showLoading();
        // run the code in it's own thread so the GUI doesnt get frozen for seconds
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int sellShares = ((Double)spnSellAmount.getValue()).intValue();
                sell(mCurrRow.getSymbol(),sellShares); 

                mStockDataModel.setValueAt(mCurrRow.getNoOfShares()+sellShares, tblShares.getSelectedRow(), NUMBEROFSHARES);
                mCurrRow.setNoOfShares(mCurrRow.getNoOfShares() + sellShares);
                mCurrentUser.setShare(mCurrRow.getSymbol(), mCurrentUser.getNoOfShares(mCurrRow.getSymbol()) - sellShares);
                updateCurrentUser();
                updateSelectedinfo(tblShares.getSelectedRow());
                loading.hideLoading();
            }
        });       
    }//GEN-LAST:event_btnSellSharesActionPerformed

    /**
     * Method to process the Logout Menu item
     */
    private void mnuAccountLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAccountLogoutActionPerformed
        loading.showLoading();
        // run the code in it's own thread so the GUI doesnt get frozen for seconds
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateCurrentUser();
                mCurrentUser = null;
                mParent.showLogin();
                loading.hideLoading();
            }
        });
    }//GEN-LAST:event_mnuAccountLogoutActionPerformed

    /**
     * Method to process the Save menu item
     */
    private void mnuAccountSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAccountSaveActionPerformed
        loading.showLoading();
        // run the code in it's own thread so the GUI doesnt get frozen for seconds
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateCurrentUser();
                loading.hideLoading();
            }
        });
    }//GEN-LAST:event_mnuAccountSaveActionPerformed

    /**
     * Method to process the Update Share Prices button
     */
    private void btnUpdateSharePriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateSharePriceActionPerformed
        loading.showLoading();
        // run the code in it's own thread so the GUI doesnt get frozen for seconds
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(updateSharePrices()){
                    populateStockData(sort(mSortOrder));
                }
                loading.hideLoading();
            }
        });   
    }//GEN-LAST:event_btnUpdateSharePriceActionPerformed

    
    /**
     * Method to process the Reset Search Prices button
     */
    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        loading.showLoading();
        // run the code in it's own thread so the GUI doesnt get frozen for seconds
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(updateSharePrices()){
                    populateStockData(sort(mSortOrder));
                }
                loading.hideLoading();
            }
        });       
    }//GEN-LAST:event_btnResetActionPerformed

    private void txtSearchValueOneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchValueOneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchValueOneActionPerformed

    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuyShares;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSellShares;
    private javax.swing.JButton btnUpdateCurrencies;
    private javax.swing.JButton btnUpdateSharePrice;
    private javax.swing.JComboBox<String> cboCurrencies;
    private javax.swing.JComboBox<ComboItem> cboFieldName;
    private javax.swing.JComboBox<ComboItem> cboSearchType;
    private javax.swing.JEditorPane epNews;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblBuySell;
    private javax.swing.JLabel lblBuySell1;
    private javax.swing.JLabel lblCurrencies;
    private javax.swing.JLabel lblLastAccessed;
    private javax.swing.JLabel lblLastUpdated;
    private javax.swing.JLabel lblLastUpdatedValue;
    private javax.swing.JLabel lblSearchAnd;
    private javax.swing.JLabel lblSearchInfo;
    private javax.swing.JLabel lblSelected;
    private javax.swing.JLabel lblSelectedShare;
    private javax.swing.JLabel lblSharesOwned;
    private javax.swing.JMenu mnuAccount;
    private javax.swing.JMenuItem mnuAccountLogout;
    private javax.swing.JMenuItem mnuAccountSave;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenuItem mnuFileExit;
    private javax.swing.JMenuItem mnuUpdateCurrencies;
    private javax.swing.JMenu mnuUpdateData;
    private javax.swing.JMenuItem mnuUpdateRefreshData;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JPanel pnlSelectedStock;
    private javax.swing.JPanel pnlShares;
    private javax.swing.JPanel pnlStockData;
    private javax.swing.JScrollPane scpnlShares;
    private javax.swing.JSpinner spnBuyAmount;
    private javax.swing.JSpinner spnSellAmount;
    private javax.swing.JTable tblShares;
    private javax.swing.JTextField txtSearchValueOne;
    private javax.swing.JTextField txtSearchValueTwo;
    // End of variables declaration//GEN-END:variables

    

    
    /******************************************************************
     * 
     * @param sortField which field and direction to sort by
     * @return an OperationResult object with the sorted list
     */
    private static OperationResult sort(java.lang.String sortField) {
        barryoconnor.StockWebService_Service service = new barryoconnor.StockWebService_Service();
        barryoconnor.StockWebService port = service.getStockWebServicePort();
        return port.sort(sortField);
    }
    
    
    private static OperationResult search(java.lang.String searchField, java.lang.String searchType, java.lang.String searchValue) {
        barryoconnor.StockWebService_Service service = new barryoconnor.StockWebService_Service();
        barryoconnor.StockWebService port = service.getStockWebServicePort();
        return port.search(searchField, searchType, searchValue);
    }

    private static OperationResult searchBetween(java.lang.String searchField, java.lang.String startSearchValue, java.lang.String endSearchValue) {
        barryoconnor.StockWebService_Service service = new barryoconnor.StockWebService_Service();
        barryoconnor.StockWebService port = service.getStockWebServicePort();
        return port.searchBetween(searchField, startSearchValue, endSearchValue);
    }

    private static OperationResult sell(java.lang.String symbol, int amount) {
        barryoconnor.StockWebService_Service service = new barryoconnor.StockWebService_Service();
        barryoconnor.StockWebService port = service.getStockWebServicePort();
        return port.sell(symbol, amount);
    }

    private static OperationResult buy(java.lang.String symbol, int amount) {
        barryoconnor.StockWebService_Service service = new barryoconnor.StockWebService_Service();
        barryoconnor.StockWebService port = service.getStockWebServicePort();
        return port.buy(symbol, amount);
    }

    private static String getStockCodes() {
        barryoconnor.StockWebService_Service service = new barryoconnor.StockWebService_Service();
        barryoconnor.StockWebService port = service.getStockWebServicePort();
        return port.getStockCodes();
    }

    private static boolean updateSharePrices() {
        barryoconnor.StockWebService_Service service = new barryoconnor.StockWebService_Service();
        barryoconnor.StockWebService port = service.getStockWebServicePort();
        return port.updateSharePrices();
    }

    private static String newsUpdate(java.lang.String company) {
        barryoconnor.StockWebService_Service service = new barryoconnor.StockWebService_Service();
        barryoconnor.StockWebService port = service.getStockWebServicePort();
        return port.newsUpdate(company);
    }

    private static String userUpdate(java.lang.String currentUser) {
        barryoconnor.StockWebService_Service service = new barryoconnor.StockWebService_Service();
        barryoconnor.StockWebService port = service.getStockWebServicePort();
        return port.userUpdate(currentUser);
    }

    private static Integer updateCurrenciesValue() {
        barryoconnor.StockWebService_Service service = new barryoconnor.StockWebService_Service();
        barryoconnor.StockWebService port = service.getStockWebServicePort();
        return port.updateCurrenciesValue();
    }

    private static java.util.List<java.lang.String> getCurrencyListFormatted() {
        barryoconnor.StockWebService_Service service = new barryoconnor.StockWebService_Service();
        barryoconnor.StockWebService port = service.getStockWebServicePort();
        return port.getCurrencyListFormatted();
    }

    private static double getExchangeRate(java.lang.String currency) {
        barryoconnor.StockWebService_Service service = new barryoconnor.StockWebService_Service();
        barryoconnor.StockWebService port = service.getStockWebServicePort();
        return port.getExchangeRate(currency);
    }

    
}
