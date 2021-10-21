/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package barryoconnor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*
 *  Simple implementation of a Glass Pane that will capture and ignore all
 *  events as well paint the glass pane to give the frame a "disabled" look.
 *
 *  Adapted from a tutorial by Rob Camick at: https://tips4java.wordpress.com/2008/11/07/disabled-glass-pane/
 */
public class LoadingMessage extends JComponent implements KeyListener {
	
        private JLabel lblLoading = new JLabel("Loading...",JLabel.CENTER);
        private Color background = new Color(254, 254, 254, 125);        

	public LoadingMessage()
	{
                //set the background colour and laod the animated gif Label into the panel
		
		this.setLayout( new GridBagLayout() );
		this.add(lblLoading, new GridBagConstraints());
                lblLoading.setBackground(Color.GRAY);
                lblLoading.setForeground(Color.WHITE);
                lblLoading.setFont(new Font("", Font.PLAIN, 30));
                

		//  Disable Mouse, Key and Focus events so that the user cannot click or interact with the form underneath
		this.addMouseListener( new MouseAdapter() {} );
		this.addMouseMotionListener( new MouseMotionAdapter() {} );
		this.addKeyListener( this );
		this.setFocusTraversalKeysEnabled(false);
	}

	/*
	 *  By Default a GlassPane is transparent - we need to repaint to make it colored
	 */
	@Override
	protected void paintComponent(Graphics graphics)
	{
    
                int x = getWidth() / 4;
                int y = getHeight() / 4;
                int w = getWidth() / 2;
                int h = getHeight()/2;
                int arc = 20;

                Graphics2D graphics2D = (Graphics2D) graphics.create();
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // draw a rounded rectangle to set as the background for the label
                graphics2D.setColor(new Color(0, 0, 0, 220));
                graphics2D.fillRoundRect(x, y, w, h, arc, arc);

                graphics2D.setStroke(new BasicStroke(1f));
                graphics2D.setColor(Color.WHITE);
                graphics2D.drawRoundRect(x, y, w, h, arc, arc);

                graphics2D.dispose();

	}


        //intercept and ignore any key events so that the user can't type
	public void keyPressed(KeyEvent e)
	{
		e.consume();
	}

	public void keyTyped(KeyEvent e) {}

	public void keyReleased(KeyEvent e)
	{
		e.consume();
	}

	/*
	 *  Show the loading message and set the cursor to the wait cursor
	 */
	public void showLoading()
	{
		this.setVisible( true );
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		this.requestFocusInWindow();
	}

	/*
	 *  Hide the loading message and set the cursor back to normal
	 */
	public void hideLoading()
	{
		this.setCursor(null);
		this.setVisible( false );
	}
}
