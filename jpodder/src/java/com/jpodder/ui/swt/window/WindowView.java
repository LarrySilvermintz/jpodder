package com.jpodder.ui.swt.window;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.jpodder.Main;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.theme.UITheme;
import com.jpodder.util.Messages;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class WindowView {

	Logger mLog = Logger.getLogger(getClass().getName());

	public static final int DEFAULT_APP_HEIGHT = 600;

	public static final int DEFAULT_APP_WIDTH = 800;

	private WindowController mController;


	public Menu lMainMenu;

	MenuItem lFileSystemMenu;

	MenuItem lFileSystemExit;

	MenuItem lFileSystemNew;
	
		
	public WindowView() {
		mLog.info("<init>");
		
		Shell mShell = UILauncher.getInstance().getShell();
		mShell.setImage(UITheme.getInstance().getImages().get(
				UITheme.IMAGE_JPODDER));
//		mShell.setSize(100, 100);
		mShell.setText(Main.APP_TITLE);
		mShell.setLayout(new GridLayout());

		lMainMenu = new Menu(mShell, SWT.BAR);
		lFileSystemMenu = new MenuItem(lMainMenu, SWT.CASCADE, 0);
		lFileSystemMenu.setText(Messages.getString("gui.menu.file"));
		lFileSystemMenu.setAccelerator(SWT.CTRL + 'f');
		lFileSystemMenu.setMenu(new Menu(mShell, SWT.DROP_DOWN));
		lFileSystemExit = new MenuItem(lFileSystemMenu.getMenu(), SWT.PUSH);
		lFileSystemExit.setText(Messages.getString("main.exit"));

		mShell.setMenuBar(lMainMenu);
	}
	

	public void setController(WindowController pController) {
		mController = pController;
	}


}
