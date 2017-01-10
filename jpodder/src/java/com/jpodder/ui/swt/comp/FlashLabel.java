package com.jpodder.ui.swt.comp;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 */
import javax.swing.Timer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Custom JLabel with time based display.
 * <p>
 * Launches a Timer, which cleans the model after preset or specified time in
 * miliseconds
 */
public class FlashLabel extends Label {

    long DELAY = 2000;
    long mDelay;
    Timer timer;
    Color mTextColor;
    
    boolean initialized = false;
   
    /**
     * Constructor with default delay.
     * <p>
     */
    public FlashLabel(Composite pParent) {
        super(pParent, SWT.NONE);
    }

    /**
     * Constructor with specified delay.
     * <p>
     * @param c The color
     * @param mDelay
     *            The delay in miliseconds.
     */
    public FlashLabel(Composite pParent, Color c, long delay) {
        super(pParent, SWT.NONE);
        init(c, delay);
    }
    
    private void init(Color pTextColor, long pDelay){
        mDelay = pDelay;
        this.mTextColor = pTextColor;
        initialized = true;
    }


    /**
     * Override JTextComponent method. Call timer which clears the model after a
     * certain time
     * 
     * @param t
     *            The Text to be displayed.
     */
    public void setText(String t) {
        if (initialized) {
            if (super.getText().length() != 0) {
                cleanText();
            }
            if( timer != null && timer.isRunning()){
                timer.stop();
            }
            super.setText(t);
            timer = new Timer(50,new FadeAction(t, timer));
            timer.setInitialDelay((int) ((mDelay != 0) ? mDelay : DELAY));
            timer.start();
        }
    }

    /**
     * A fading timer. Calls a fading method to make the text disappear.
     */
    public class FadeAction implements ActionListener {
        final String text;
        final Timer timer;
        
        public FadeAction(String text, Timer timer) {
            this.text = text;
            this.timer = timer;
        }

        public void run() {
        }

        public void actionPerformed(ActionEvent e) {
            fadeText(text, timer);            
        }
    }

    public void cleanText() {
        setForeground(mTextColor);
        super.setText("");
    }

    /**
     * Use the Color alpha (Transparency) value to fade the color in certain 
     * steps. We stop the time when the transparency is reaching 0. 
     * (Full trqansparency).
     * 
     * @param text
     * @param t
     */
    public void fadeText(String text, Timer t) {

        Color o = getForeground();
        if (o != null) {
            
            
//            int alpha = o.getAlpha();
//            if (alpha > 10 && alpha <= 255) {
//                alpha -= 15;
//            } else {
//                cleanText();
//                if( t != null){
//                    t.stop();	
//                }
//            }
            
            RGB lRGB = new RGB(o.getRed(), o.getGreen(), o.getBlue());
            Color faded = new Color(Display.getDefault(), lRGB);
            setForeground(faded);
            super.setText(text);
        }
    }
}