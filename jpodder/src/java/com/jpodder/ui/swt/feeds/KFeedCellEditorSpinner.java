package com.jpodder.ui.swt.feeds;

import org.eclipse.swt.SWT;
//import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellEditor;

import com.jpodder.data.feeds.IXPersonalFeed;

/**
 * A spinner cell editor.
 */
public class KFeedCellEditorSpinner extends KTableCellEditor {
	private Spinner mSpinner;

	private KeyAdapter keyListener = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			try {
				onKeyPressed(e);
			} catch (Exception ex) {
				// Do nothing
			}
			e.doit = false;
		}
	};

	private TraverseListener travListener = new TraverseListener() {
		public void keyTraversed(TraverseEvent e) {
			onTraverse(e);
		}
	};

	public void open(KTable table, int col, int row, Rectangle rect) {
		super.open(table, col, row, rect);
		Object content = m_Model.getContentAt(m_Col, m_Row);
		if (content instanceof IXPersonalFeed) {
			mSpinner.setSelection(((IXPersonalFeed) content).getMaxDownloads());
		}
	}

	public void close(boolean save) {
		if (save) {
			m_Model.setContentAt(m_Col, m_Row,
					new Integer(mSpinner.getDigits()).toString());
		}
		mSpinner.removeKeyListener(keyListener);
		mSpinner.removeTraverseListener(travListener);
		super.close(save);
		mSpinner = null;
	}

	protected Control createControl() {
		mSpinner = new Spinner(m_Table, SWT.READ_ONLY);
		mSpinner.setIncrement(1);
		mSpinner.setMaximum(20);
		mSpinner.setMinimum(0);
		mSpinner.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
		mSpinner.addKeyListener(keyListener);
		// mSpinner.addTraverseListener(travListener);
		// mSpinner.setCursor(m_ArrowCursor);
		return mSpinner;
	}

	/**
	 * Overwrite the onTraverse method to ignore arrowup and arrowdown events so
	 * that they get interpreted by the editor control.
	 * <p>
	 * Comment that out if you want the up and down keys move the editor.<br>
	 * Hint by David Sciamma.
	 */
	protected void onTraverse(TraverseEvent e) {
		super.onKeyPressed(e);
	}

	public void setBounds(Rectangle rect) {
		super.setBounds(new Rectangle(rect.x, rect.y + 1, rect.width,
				rect.height - 2));
	}

	public void setContent(Object content) {
		if (content instanceof IXPersonalFeed) {
			mSpinner.setSelection(((IXPersonalFeed) content).getMaxDownloads());
		} else if (content instanceof String) {
			mSpinner.setSelection(new Integer((String) content).intValue());
		}
	}
}
