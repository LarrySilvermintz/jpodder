package com.jpodder.ui.swt.text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * An insight panel class. Shows a label and a list inside a borderless shell.
 * 
 * <p>
 * 
 */
public class InsightView {

	private static final short PANEL_WIDTH = 200;

	private static final short TITLE_HEIGHT = 20;

	private static final short PANEL_HEIGHT = 200;

	private List mInsightList;

	private Label mTitleLabel;

	private Shell mInsightShell;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param frame
	 * @param title
	 */
	public InsightView(String title) {
		init(title);
		fitSizeToModel();
	}

	/**
	 * Initialize the visual components.
	 * <p>
	 * An InsightPanel is build up of 2 panels. The first panel is the a Label,
	 * the second is a scroll list.
	 * <p>
	 * 
	 * @param title
	 */
	public void init(String title) {

		Display lDisplay = Display.getDefault();
		mInsightShell = new Shell(lDisplay, SWT.APPLICATION_MODAL);
		mTitleLabel = new Label(mInsightShell, SWT.NONE);
		mTitleLabel.setText(title);
		mInsightList = new List(mInsightShell, SWT.NONE);

		// list.setCellRenderer(new InsightCellRenderer());
		// setDefaultModel();
		// // list.setBackground(panelCol);
		// view = new JScrollPane(list);
		// view
		// .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//
	}

	public void setInsightList(Object[] lData) {
		// CB TODO Implement, Conver to String[] or use a customer list
		// renderer.
	}

	/**
	 * @param lData
	 */
	public void setInsightList(String[] lData) {
		if (lData != null && (lData.length > 0)) {
			mInsightList.setItems(lData);
			fitSizeToModel();
		}
	}

	/**
	 * Fit the size of the panel with the model size.
	 * <p>
	 */
	public void fitSizeToModel() {
		int size;
		int cellHeight = 1;

		// if (list.getCellBounds(0, 0) == null) {
		// size = 0;
		// } else {
		// cellHeight = (int) list.getCellBounds(0, 0).getHeight();
		// size = list.getModel().getSize();
		// }
		// int rowCount = list.getVisibleRowCount();
		// rowCount = Math.min(size, rowCount);
		// list.setVisibleRowCount(rowCount);
		// int height = (rowCount * (cellHeight + 4));
		// height = ( height <= PANEL_HEIGHT ) ? height : PANEL_HEIGHT;
		// setBounds(0, 0, PANEL_WIDTH, height + TITLE_HEIGHT);
		// // setPreferredSize(new Dimension(PANEL_WIDTH, height +
		// TITLE_HEIGHT));
		// view.setPreferredSize(new Dimension(PANEL_WIDTH, height));
	}

	/**
	 * Get the list object.
	 * <p>
	 * 
	 * @return List The insight panel list.
	 * @see List
	 */
	// CB TODO Why dfo we need this?
	public List getInsightList() {
		return mInsightList;
	}

	/**
	 * Show this Class in the Layered (POPUP) pane.
	 * <p>
	 * Add a layer to the LayeredPane of the JFrame. Additionally change arrow
	 * key behaviour.
	 * 
	 * @param loc
	 *            The location where to show the pane.
	 */

	public void show(Point loc) {
		mInsightList.setLocation(loc);
		mInsightShell.setVisible(true);
	}

	/**
	 * Hide(Remove) this Class from the Layered Pane.
	 * <p>
	 * Remove a layer from the LayerdPane.
	 */
	public void hide() {
		mInsightShell.setVisible(false);
	}

	/**
	 * @return
	 */
	public Shell getView() {
		return mInsightShell;
	}

}