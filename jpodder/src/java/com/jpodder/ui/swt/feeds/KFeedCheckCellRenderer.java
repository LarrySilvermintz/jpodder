package com.jpodder.ui.swt.feeds;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import com.jpodder.ui.swt.theme.UITheme;

import de.kupzog.ktable.KTableModel;
import de.kupzog.ktable.renderers.CheckableCellRenderer;

/**
 * Custom checkable cell renderer which overrides the drawing of the cell by
 * simply drawing lines and background differently.
 */
public class KFeedCheckCellRenderer extends CheckableCellRenderer {
    public KFeedCheckCellRenderer(int style) {
        super(style);
    }

    public void drawCell(GC gc, Rectangle rect, int col, int row,
            Object content, boolean focus, boolean fixed, boolean clicked,
            KTableModel model) {

    	Color backColor;
        if ((row & 1) == 0) {
            backColor = UITheme.getInstance().TABLE_ODD_BACKGROUND_COLOR;
        } else {
            backColor = m_Display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
        }

        Color borderColor = m_Display
                .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

        gc.setForeground(borderColor);
        // Vertical line
        gc.drawLine(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y
                + rect.height);

//    	gc.setBackground(UITheme.getInstance().GENERIC_BACKGROUND_COLOR);
//    	gc.fillRectangle(rect);

        // draw focus sign:
        if (focus && (m_Style & INDICATION_FOCUS) != 0) {
            // rect = drawDefaultSolidCellLine(gc, rect, COLOR_LINE_LIGHTGRAY,
            // COLOR_LINE_LIGHTGRAY);
            drawCheckableImage(gc, rect, content, UITheme.getInstance().SELECTION_BACKGROUND_COLOR, clicked);
//             gc.drawFocus(rect.x, rect.y, rect.width, rect.height);
            backColor = UITheme.getInstance().SELECTION_BACKGROUND_COLOR;

        } else if (focus && (m_Style & INDICATION_FOCUS_ROW) != 0) {
            // rect = drawDefaultSolidCellLine(gc, rect, COLOR_BGROWFOCUS,
            // COLOR_BGROWFOCUS);
            drawCheckableImage(gc, rect, content, COLOR_BGROWFOCUS, clicked);
            backColor = COLOR_BGROWFOCUS;
        } else {
            // rect = drawDefaultSolidCellLine(gc, rect, COLOR_LINE_LIGHTGRAY,
            // COLOR_LINE_LIGHTGRAY);
            drawCheckableImage(gc, rect, content, backColor, clicked);
        }

    	
        // draw the horizontal lines in the table.
        gc.setForeground(backColor);
        gc.drawLine(rect.x, rect.y + rect.height, rect.x + rect.width, rect.y
                + rect.height);
        
//        gc.fillRoundRectangle(rect.x, rect.y, rect.width, rect.height, 20, 20);

    }
}
