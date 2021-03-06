/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package barryoconnor;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;
import org.json.JSONObject;


/** Registration form for the application
 *
 * @author Barry O'Connor
 */
public class RegisterGUI extends javax.swing.JFrame {
    private StockMarketApplication mParent;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!]).{8,}$";
    private LoadingMessage loading = new LoadingMessage();
    

    /**
     * Creates new form LoginGUI
     * @param parent
     */
    public RegisterGUI(StockMarketApplication parent) {
        mParent = parent;
        initComponents();

        //add the loading overlay
        setGlassPane( loading );
    }
    
    
    public final static boolean isValidPassword(final String input) {
        /*
        function to validate a password against the password policy, which requires one upper, one lower, one numeric and one of "@#$%^&+=!".
        This is done by using a regular expression and matching against this pattern.
        - input : input takes the password in string form
        - returns: whether the string matches the pattern
        */

        Pattern pwPattern;
        Matcher patternMatcher;
        pwPattern = Pattern.compile(PASSWORD_PATTERN);
        patternMatcher = pwPattern.matcher(input);
        return patternMatcher.matches();
    }
    
    public final static boolean isValidEmail(final String input) {
        /*
        function to validate a password against the password policy, which requires one upper, one lower, one numeric and one of "@#$%^&+=!".
        This is done by using a regular expression and matching against this pattern.
        - input : input takes the password in string form
        - returns: whether the string matches the pattern
        */

        Pattern pwPattern;
        Matcher patternMatcher;
        pwPattern = Pattern.compile(EMAIL_PATTERN);
        patternMatcher = pwPattern.matcher(input);
        return patternMatcher.matches();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblError = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        lblPassword = new javax.swing.JLabel();
        btnRegister = new javax.swing.JButton();
        btnLogin = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(400, 300));

        lblError.setForeground(new java.awt.Color(255, 51, 51));
        lblError.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblError.setFocusable(false);
        lblError.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Register");

        lblEmail.setLabelFor(txtEmail);
        lblEmail.setText("E-mail");

        txtEmail.setToolTipText("Enter your Email");

        txtPassword.setToolTipText("Enter your Password");

        lblPassword.setLabelFor(txtPassword);
        lblPassword.setText("password");

        btnRegister.setText("Register");
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });

        btnLogin.setText("Back to Login");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                                .addComponent(lblPassword)
                                .addGap(18, 18, 18)
                                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                                .addComponent(lblEmail)
                                .addGap(36, 36, 36)
                                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(66, 66, 66))
                    .addComponent(lblError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEmail)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLogin)
                    .addComponent(btnRegister))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblError, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        mParent.showLogin();        
    }//GEN-LAST:event_btnLoginActionPerformed

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegisterActionPerformed
        loading.showLoading();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String email = txtEmail.getText();
                char[] pass = txtPassword.getPassword();
                String password = new String(pass);


                if(email.isEmpty() || password.isEmpty()){
                    lblError.setText("Email and Password are required!");
                } else if(!isValidEmail(email)){
                    lblError.setText("Email is not valid");
                } else if (!isValidPassword(password)){
                    lblError.setText("<html>Password must contain uppercase, lowercase, numbers and one of @#$%^&+=!</html>");
                } else {
                    loading.showLoading();

                    String response = userRegister(email,password);
                    JSONObject responseJSONObj = new JSONObject(response);

                    loading.hideLoading();
                    if(responseJSONObj.getBoolean("result")){
                        mParent.showApplication(responseJSONObj);
                    } else {

                        String errorMsg = responseJSONObj.getString("message");
                        if(errorMsg.equalsIgnoreCase("EMAIL_EXISTS")){
                            lblError.setText("<html>An account with that email already exists</html>");
                        }
                        lblError.setText("<html>" + errorMsg + "</html>");
                    }

                }
            loading.hideLoading();
            }
        });
    }//GEN-LAST:event_btnRegisterActionPerformed

   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnRegister;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration//GEN-END:variables

    private static String userRegister(java.lang.String email, java.lang.String password) {
        barryoconnor.StockWebService_Service service = new barryoconnor.StockWebService_Service();
        barryoconnor.StockWebService port = service.getStockWebServicePort();
        return port.userRegister(email, password);
    }
}
