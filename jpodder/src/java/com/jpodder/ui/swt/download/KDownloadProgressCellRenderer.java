/**
 * This class has been extended to support the style of jPodder. 
 * Don't show horizontal lines. Show the stripped background, show percentage.
 * </p>
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 */
package com.jpodder.ui.swt.download;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.theme.UITheme;

import de.kupzog.ktable.KTableModel;
import de.kupzog.ktable.renderers.DefaultCellRenderer;
import de.kupzog.ktable.renderers.IPercentage;

/**
 * @author Lorenz Maierhofer <lorenz.maierhofer@logicmindguide.com>
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 */
public class KDownloadProgressCellRenderer extends DefaultCellRenderer {
    
    /**
     * @param style The style bits to use.
     * Currently supported are:<br>
     * - INDICATION_FOCUS<br>
     * - INDICATION_FOCUS_ROW<br>
     * - INDICATION_GRADIENT<br>
     * - INDICATION_PERCENTAGE
     */
    public KDownloadProgressCellRenderer(int style) {
        super(style);
    }
    
    /* (non-Javadoc)
     * @see de.kupzog.ktable.KTableCellRenderer#getOptimalWidth(org.eclipse.swt.graphics.GC, int, int, java.lang.Object, boolean)
     */
    public int getOptimalWidth(GC gc, int col, int row, Object content, boolean fixed, KTableModel model) {
        return 20;
    }

    /** 
     * @param content The content is expected to be a Float value between 0 and 1 that represents
     * the fraction of the cell width that should be used for the bar.
     * @see de.kupzog.ktable.KTableCellRenderer#drawCell(GC, Rectangle, int, int, Object, boolean, boolean, boolean, KTableModel)
     */
    public void drawCell(GC gc, Rectangle rect, int col, int row, Object content, 
            boolean focus, boolean fixed, boolean clicked, KTableModel model) {
        
//        Color lBar = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);

        Color lBar = UITheme.getInstance().GENERIC_BACKGROUND_COLOR;
        
        Color borderColor = m_Display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
        Color textColor;
        Color backColor;
        if (focus) {
            textColor = m_Display.getSystemColor(SWT.COLOR_WHITE);
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

        // draw the vertical lines in the table.
        gc.setForeground(backColor);
        gc.drawLine(rect.x, rect.y + rect.height, rect.x + rect.width, rect.y
                + rect.height);
        gc.setForeground(borderColor);
        gc.drawLine(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y
                + rect.height);

        
        if (focus && (m_Style & INDICATION_FOCUS)!=0) {
//            rect = drawDefaultSolidCellLine(gc, rect, COLOR_LINE_LIGHTGRAY, COLOR_LINE_LIGHTGRAY);
            drawBar(gc, rect, content, textColor, backColor, lBar);            
            gc.drawFocus(rect.x, rect.y, rect.width, rect.height);
            
        } else if (focus && (m_Style & INDICATION_FOCUS_ROW)!=0) {
//            rect = drawDefaultSolidCellLine(gc, rect, COLOR_BGROWFOCUS, COLOR_BGROWFOCUS);
//            Color defaultBg = COLOR_BACKGROUND;
            setDefaultBackground(backColor);
            drawBar(gc, rect, content, textColor, backColor, lBar);
//            setDefaultBackground(defaultBg);
            
        } else {
//            rect = drawDefaultSolidCellLine(gc, rect, COLOR_LINE_LIGHTGRAY, COLOR_LINE_LIGHTGRAY);
            drawBar(gc, rect, content, textColor, backColor, lBar);
        }
    }

    protected void drawPercentage(GC gc, Rectangle rect, float m_fraction, Color background, Color foreground){
        String lDraw = new Long( Math.round(m_fraction *100) ).toString() + "%";
        
        gc.setForeground(foreground);
        
        gc.drawString(lDraw, rect.x + rect.width/2, rect.y + 3, true);
//        SWTX.drawTextImage(gc, lDraw, SWTX.ALIGN_HORIZONTAL_CENTER
//                | SWTX.ALIGN_VERTICAL_CENTER, null, SWTX.ALIGN_HORIZONTAL_LEFT
//                | SWTX.ALIGN_VERTICAL_CENTER, rect.x + 3, rect.y,
//                rect.width - 3, rect.height);
    }
    
    /**
     * @param gc
     * @param rect
     * @param m_fraction
     * @param background
     */
    protected void drawGradientBar(GC gc, Rectangle rect, float m_fraction, Color background, Color foreground) {
        int barWidth = Math.round(rect.width*m_fraction);
        gc.setForeground(background);
        gc.setBackground(foreground);
        gc.fillGradientRectangle(rect.x, rect.y, barWidth, rect.height, false);
        gc.setBackground(background);
        gc.fillRectangle(rect.x+barWidth, rect.y, rect.width-barWidth, rect.height);
        
    }

    /**
     * @param gc
     * @param rect
     * @param background
     * @param foreground
     * @param m_fraction
     */
    protected void drawNormalBar(GC gc, Rectangle rect, Color background, Color foreground, float m_fraction) {
        int barWidth = Math.round(rect.width*m_fraction);
        gc.setBackground(foreground);
        gc.fillRectangle(rect.x, rect.y, barWidth, rect.height);
        gc.setBackground(background);
        gc.fillRectangle(rect.x+barWidth, rect.y, rect.width-barWidth, rect.height);
    }
    
    /**
     * @param gc
     * @param rect
     * @param m_fraction
     * @param background
     */
    protected void drawBar(GC gc, Rectangle rect, Object content, Color foregroundColor, Color background, Color foreground) {
        float m_fraction;
        if (content instanceof Float)
            m_fraction = ((Float)content).floatValue();
        else if (content instanceof Double)
            m_fraction = ((Double)content).floatValue();
        else if (content instanceof IPercentage)
            m_fraction = ((IPercentage)content).getPercentage();
        else m_fraction=0;
        
        if (m_fraction>1) m_fraction=1;
        if (m_fraction<0) m_fraction=0;
        
        if ((m_Style & INDICATION_GRADIENT) != 0)
            drawGradientBar(gc, rect, m_fraction, background, foreground);
        else
            drawNormalBar(gc, rect, background, foreground, m_fraction);
        drawPercentage(gc, rect, m_fraction, getBackground(), foregroundColor);
    }
}
