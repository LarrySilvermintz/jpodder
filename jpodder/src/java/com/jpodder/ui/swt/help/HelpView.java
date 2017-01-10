package com.jpodder.ui.swt.help;

import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.jpodder.ui.swt.UILauncher;
import com.jpodder.util.Messages;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class HelpView {

    protected MenuItem mHelpMenu;
    protected MenuItem mHelpWebMenu;
    protected MenuItem mHelpAboutMenu;
    
    public HelpView(){
        mHelpMenu = new MenuItem(UILauncher.lWindowUI.lMainMenu, SWT.CASCADE);
        mHelpMenu.setText(Messages.getString("gui.menu.help"));
        mHelpMenu.setAccelerator(SWT.CTRL + 'h');
        mHelpMenu
                .setMenu(new Menu(UILauncher.getInstance().getShell(), SWT.DROP_DOWN));

        mHelpWebMenu = new MenuItem(mHelpMenu.getMenu(), SWT.PUSH);
        mHelpWebMenu.setText("http://www.jpodder.com");
        mHelpWebMenu.addListener(SWT.Selection, new Listener(){
            public void handleEvent(Event arg0) {
            			Program lBrowser = Program.findProgram("htm");
                if(lBrowser == null){
                	lBrowser = Program.findProgram("html");
                }
                if(lBrowser != null){
                	lBrowser.execute("http://www.jpodder.com");
                }
            }
        });
        
        mHelpAboutMenu = new MenuItem(mHelpMenu.getMenu(), SWT.PUSH);
        mHelpAboutMenu.setText(Messages.getString("help.about"));
        mHelpAboutMenu.addListener(SWT.Selection, new Listener(){

            public void handleEvent(Event arg0) {
                new HelpAbout();
            }
        });
    }
}
