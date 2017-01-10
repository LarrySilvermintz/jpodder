package com.jpodder.ui.swt.media;

import java.io.File;
import java.text.ParseException;
import java.util.Date;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.jpodder.data.feeds.IXFile;
import com.jpodder.data.feeds.IXPersonalEnclosure;
import com.jpodder.data.feeds.XEnclosureException;
import com.jpodder.data.feeds.XItemException;
import com.jpodder.ui.swt.theme.UITheme;
import com.jpodder.util.Util;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class MediaTableProvider extends LabelProvider implements
		ITableLabelProvider, ITableColorProvider, ITableFontProvider {

	MediaView.ModelPreparator mPreparator;

	FontRegistry registry = new FontRegistry();

	public MediaTableProvider(MediaView.ModelPreparator pPreparator) {
		mPreparator = pPreparator;
	}

	/**
	 */
	public String getColumnText(Object element, int columnIndex) {

		Object lResult = getValueAt((IXFile) element, columnIndex);

		if (lResult instanceof Integer) {
			String lFormatted = Util.formatSize(lResult);
			return ((Number) lResult).intValue() == 0 ? "" : lFormatted;
		}
		if (lResult instanceof Boolean) {
			if (((Boolean) lResult).booleanValue()) {
				return "Yes";
			} else {
				return "No";
			}
		}
		if (lResult instanceof String) {
			return (String) lResult;
		}
		return lResult == null ? "?????" : lResult.toString();
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
	 *      int)
	 */
	public Image getColumnImage(Object pElement, int pColumnIndex) {
		if (pElement instanceof IXFile) {
			IXFile lEnclosure = (IXFile) pElement;
			switch (pColumnIndex) {
			case 0: {
				return null; // Bug in Windows?
			}
			case 1: {
				if (mPreparator != null) {
					return mPreparator.getImage(lEnclosure);
				}
			}
			}
		}
		return null;
	}

	public Color getForeground(Object pElement, int pColumnIndex) {
		Color lColor = null;

		if (!(pElement instanceof IXFile)) {
			return null;
		}

		int lRowIndex = mPreparator.indexOf((IXFile) pElement);

		if (pColumnIndex == 1) {

			int lStatus = mPreparator.getFeed().getEnclosureStatus(lRowIndex);
			if (lStatus >= 0) {
				// Check the enclosure status if a new pRow is detected.
				if ((lStatus & 8) != 8 || (lStatus & 4) == 4) {
					lColor = UITheme.getInstance().APP_FONT3_COLOR;
					return lColor;
				}

			} else { // These are the local files.

			}
		}
		return lColor;

	}

	public Color getBackground(Object pElement, int pColumnIndex) {

		Color lColor = null;
		int lRowIndex;
		if (!(pElement instanceof IXFile)) {
			return null;
		}

		lRowIndex = mPreparator.indexOf((IXFile) pElement);

		if (lRowIndex >= 0) {

			int lStatus = mPreparator.getFeed().getEnclosureStatus(lRowIndex);
			if (lStatus >= 0) {
				// Check the enclosure status if a new pRow is detected.
				if ((lStatus & 2) == 2) {
					lColor = UITheme.getInstance().SCHEDULED_COLOR;
					return lColor;
				}
				if ((lStatus & 4) == 4) {
					lColor = UITheme.getInstance().INCOMPLETE_DOWNLOAD_COLOR;
					return lColor;
				}

			} else { // These are the local files.
				if ((lRowIndex & 1) == 0) {
					lColor = UITheme.getInstance().LOCAL_ODD_BACKGROUND_COLOR;
				}
			}
		}
		return lColor;

	}

	public Font getFont(Object pElement, int pColumnIndex) {
		Font lFont = null;

		if (!(pElement instanceof IXFile)) {
			return null;
		}

		int lRowIndex = mPreparator.indexOf((IXFile) pElement);

		if (pColumnIndex == 1) {

			int lStatus = mPreparator.getFeed().getEnclosureStatus(lRowIndex);
			if (lStatus >= 0) {
				// Check the enclosure status if a new pRow is detected.
				if ((lStatus & 8) != 8 || (lStatus & 4) == 4) {
					// CB TODO A bit of hack.
					return registry.getItalic(Display.getCurrent()
							.getSystemFont().getFontData()[0].getName());
				}

			} else { // These are the local files.

			}
		}
		return lFont;
	}

	/**
	 * Returns the image with the given key, or <code>null</code> if not
	 * found.
	 */
	public Object getValueAt(IXFile pFile, int column) {
		Object lReturn = "";

 		switch (column) {
		case 0: {
			return new Integer(mPreparator.indexOf(pFile)).toString();
		}
		case 1: {
			if (pFile instanceof IXPersonalEnclosure) {
				IXPersonalEnclosure lEncl = (IXPersonalEnclosure) pFile;
				try {
					return lEncl.getItem().getTitle();
				} catch (XEnclosureException e) {
					return lEncl.getPersonalURL().toExternalForm();
				} catch (XItemException e) {
					return lEncl.getPersonalURL().toExternalForm();
				}
			}
			return pFile.getName();
		}
		case 2: {// Size web
			if (pFile instanceof IXPersonalEnclosure) {
				lReturn = new Integer(pFile.getFileLength());
			} else {
				lReturn = new Integer(0);
			}
			break;
		}
		case 3: {// Size disk
			File localFile = pFile.getFile();
			if (localFile != null && !localFile.getPath().equals("null")) {
				lReturn = new Integer((int) localFile.length());
			} else {
				lReturn = "";
			}
			break;
		}
		case 4: { // If in player.
			lReturn = new Boolean(pFile.getInPlayer());
			break;
		}
		case 5: { // Last modified
			if (pFile instanceof IXPersonalEnclosure) {
				IXPersonalEnclosure lEnclosure = (IXPersonalEnclosure) pFile;
				try {
					String lDateString = lEnclosure.getItem().getPubDate();
					Date lDate = Util.resolvedDateRFC822(lDateString);
					lReturn = Util.formatDate(lDate, "EEEE, MMM d hh:mm a");
				} catch (ParseException e) {
				} catch (IllegalArgumentException iae) {
				} catch (XEnclosureException e) {
				} catch (XItemException e) {
				}
			} else {
				Date lDate = pFile.getDate();
				lReturn = Util.formatDate(lDate, "EEEE, MMM d hh:mm a");
			}
			break;
		}
		case 6: { // marked
			lReturn = new Boolean(pFile.isMarked());
			break;
		}
		default:
			lReturn = "";
		}
		return lReturn;
	}

}