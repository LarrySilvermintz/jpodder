package com.jpodder.ui.swt.download;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.jpodder.data.download.Download;
import com.jpodder.data.download.DownloadLogic;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.theme.UITheme;
import com.jpodder.util.Util;

import de.kupzog.ktable.KTableModel;
import de.kupzog.ktable.SWTX;
import de.kupzog.ktable.renderers.DefaultCellRenderer;

public class KDownloadTableRenderer extends DefaultCellRenderer {

    protected Display m_Display;

    public KDownloadTableRenderer(int pStyle) {
        super(pStyle);
        m_Display = Display.getDefault();
    }

    public int getOptimalWidth(GC gc, int col, int row, Object content,
            boolean fixed, KTableModel model) {
        return Math.max(gc.stringExtent(content.toString()).x + 8, 120);
    }

    public void drawCell(GC gc, Rectangle rect, int col, int row,
            Object content, boolean focus, boolean fixed, boolean clicked,
            KTableModel model) {
        Color textColor;
        Color backColor;
        Color borderColor;
        if (focus) {
            textColor = UITheme.getInstance().SELECTION_FORGROUND_COLOR;
            backColor = UITheme.getInstance().SELECTION_BACKGROUND_COLOR;
        } else {
            textColor = m_Display.getSystemColor(SWT.COLOR_LIST_FOREGROUND);
            if ((row & 1) == 0) {
                backColor = UITheme.getInstance().TABLE_ODD_BACKGROUND_COLOR;
            } else {
                backColor = (m_Display
                        .getSystemColor(SWT.COLOR_LIST_BACKGROUND));
            }
        }

        borderColor = m_Display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

        // draw the top and bottom horizontal lines in the table.
        gc.setForeground(backColor);
        gc.drawLine(rect.x, rect.y + rect.height, rect.x + rect.width, rect.y
                + rect.height);
        gc.setForeground(borderColor);
        gc.drawLine(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y
                + rect.height);

        if (content instanceof KDownloadTableModel.TableSpaceHolder) {
            gc.setBackground(backColor);
            gc.fillRectangle(rect);
            return;
        }

        if (content instanceof Download) {
            Download lDownload = (Download) content;
            String lDraw = "";
            switch (col) {
                case 0: { // The download index.
                    lDraw = new Integer(row).toString();
                }
                    break;
                case 1: { // The file name.
                    if (lDownload.getEnclosure().getFile() != null) {
                        lDraw = lDownload.getEnclosure().getFile().getName();
                    }else{
                    	lDraw = "Error, File name not set";
                    }
                }
                    break; // The feed title.
                case 2: {
                    lDraw = lDownload.getEnclosure().getFeed().getTitle();
                }
                    break;
                case 3: {
                    lDraw = "TODO"; // this is the proress bar.
                }
                    break;
                case 4: {
                    lDraw = Util.formatSpeed(lDownload.getBytesPerSecond())
                            + " kB/s";
                }
                    break;
                case 5: {
                    lDraw = Util.formatTime(lDownload.getTimeElapsed());
                }
                    break;
                case 6: {
                    String status = KDownloadTableModel.STATUS_NAMES[lDownload
                            .getState()];

                    if (lDownload.getState() == DownloadLogic.RETRYING
                            || lDownload.getState() == DownloadLogic.ERROR
                            || lDownload.getState() == DownloadLogic.RELEASING) {
                        status += lDownload.getMessage();
                    }
                    lDraw = status;
                }
                    break;
                case 7: {
                    lDraw = Util.formatSize(lDownload.getCurrent());
                }
            }
            gc.setForeground(textColor);
            gc.setBackground(backColor);
            gc.fillRectangle(rect);
            SWTX.drawTextImage(gc, lDraw, SWTX.ALIGN_HORIZONTAL_LEFT
                    | SWTX.ALIGN_VERTICAL_TOP, null, SWTX.ALIGN_HORIZONTAL_LEFT
                    | SWTX.ALIGN_VERTICAL_CENTER, rect.x + 3, rect.y,
                    rect.width - 3, rect.height);
        }
    }
}
