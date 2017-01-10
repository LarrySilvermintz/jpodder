package com.jpodder.ui.swt.feeds;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.jpodder.data.feeds.IXPersonalFeed;
import com.jpodder.data.feeds.list.XPersonalFeedList;
import com.jpodder.ui.swt.theme.UITheme;

import de.kupzog.ktable.KTableModel;
import de.kupzog.ktable.SWTX;
import de.kupzog.ktable.renderers.DefaultCellRenderer;

public class KFeedTableRenderer extends DefaultCellRenderer {

    protected Display m_Display;

    public KFeedTableRenderer(int pStyle) {
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
            textColor = UITheme.getInstance().GENERIC_BACKGROUND_COLOR;
            backColor = UITheme.getInstance().SELECTION_BACKGROUND_COLOR;
        } else {
            textColor = COLOR_TEXT;
            if ((row & 1) == 0) {
                backColor = UITheme.getInstance().TABLE_ODD_BACKGROUND_COLOR;
            } else {
                backColor = (m_Display
                        .getSystemColor(SWT.COLOR_LIST_BACKGROUND));
            }
        }

        borderColor = m_Display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

        // draw the horizontal lines in the table.
        gc.setForeground(backColor);
        gc.drawLine(rect.x, rect.y + rect.height, rect.x + rect.width, rect.y
                + rect.height);
        
        // draw the vertical lines in the table.        
        gc.setForeground(borderColor);
        gc.drawLine(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y
                + rect.height);

        if (content instanceof KFeedTableModel.TableSpaceHolder) {
            gc.setBackground(backColor);
            gc.fillRectangle(rect);
            return;
        }

        if (content instanceof IXPersonalFeed) {
            IXPersonalFeed lFeed = (IXPersonalFeed) content;

            switch (col) {
                case 1: {
                    gc.setBackground(backColor);
                    gc.setForeground(UITheme.getInstance().FEED_TITLE_COLOR);
                    gc.fillRectangle(rect);
                    // applyFont(gc);
                    setStyle(SWT.BOLD);
                    
//                  CB FIXME 	this call leaks! Report to kupzog: applyFont(gc);

                    SWTX.drawTextImage(gc, lFeed.getPersonalTitle(),
                            SWTX.ALIGN_HORIZONTAL_LEFT
                                    | SWTX.ALIGN_VERTICAL_TOP, null,
                            SWTX.ALIGN_HORIZONTAL_LEFT
                                    | SWTX.ALIGN_VERTICAL_CENTER, rect.x + 3,
                            rect.y, rect.width - 3, rect.height);
                    setStyle(0);
                    
//                    CB FIXME 	this call leaks! Report to kupzog: applyFont(gc);
                    String lSize = lFeed.getAccumulatedFolderSize(false);
                    SWTX.drawTextImage(gc, " (" + lSize + ")",
                            SWTX.ALIGN_HORIZONTAL_RIGHT
                                    | SWTX.ALIGN_VERTICAL_TOP, null,
                            SWTX.ALIGN_HORIZONTAL_LEFT
                                    | SWTX.ALIGN_VERTICAL_CENTER, rect.x + 3,
                            rect.y, rect.width - 3, rect.height);

                    gc.setForeground(UITheme.getInstance().GENERIC_BACKGROUND_COLOR);
                    String lUrl;
                    if (lFeed.getURL() != null) {
                        lUrl = lFeed.getURL().toExternalForm();
                    } else {
                        lUrl = "http://";
                    }

                    SWTX.drawTextImage(gc, lUrl, SWTX.ALIGN_HORIZONTAL_LEFT
                            | SWTX.ALIGN_VERTICAL_BOTTOM, null,
                            SWTX.ALIGN_HORIZONTAL_LEFT
                                    | SWTX.ALIGN_VERTICAL_BOTTOM, rect.x + 3,
                            rect.y, rect.width - 3, rect.height);

                }
                    break;
                case 2: {
                    gc.setBackground(backColor);
                    gc.setForeground(textColor);
                    gc.fillRectangle(rect);
                    SWTX.drawTextImage(gc, new Integer(lFeed.getMaxDownloads())
                            .toString(), SWTX.ALIGN_HORIZONTAL_CENTER
                            | SWTX.ALIGN_VERTICAL_CENTER, null,
                            SWTX.ALIGN_HORIZONTAL_LEFT, rect.x + 3, rect.y,
                            rect.width - 3, rect.height);
                    // CB This was part of the old code.
                    // Rectangle save = gc.getClipping();
                    // gc.setClipping(rect);
                    // gc.setClipping(save);
                }
                    break;
                case 3: {
                    
                    gc.setBackground(backColor);
                    gc.fillRectangle(rect);

                    Image lQImage = getQualityImage(lFeed);
                    if (lQImage != null) {
                        // We align the image in the center of the provided
                        // rectangle
                        // bounds
                        Point imageSize = new Point(lQImage.getBounds().width,
                                lQImage.getBounds().height);
                        int yOffset = (rect.height - imageSize.y) / 2;
                        gc.drawImage(lQImage, rect.x + 15, rect.y + yOffset);
                    }                                       
                }
            }
        }

    }

    public Image getQualityImage(IXPersonalFeed pFeed) {
        int lQuality = pFeed.getQuality();
        switch (lQuality) {
            case XPersonalFeedList.GOOD_QUALITY:
                return UITheme.getInstance().getImages().get(UITheme.IMAGE_SUNNY);
            case XPersonalFeedList.BAD_QUALITY:
                return UITheme.getInstance().getImages().get(UITheme.IMAGE_RAINY);
            case XPersonalFeedList.UNKNOW_QUALITY:
            default:
        }
        return null;
    }

    /**
     * Applies the font style of the renderer to the gc that will draw the
     * content.
     * <p>
     * <b>To be called by implementors</b>
     * 
     * @param gc
     *            The gc that will draw the renderers content.
     */
    // protected void applyFont(GC gc) {
    // m_GCfont = gc.getFont();
    // if (m_font == null)
    // m_font = Display.getCurrent().getSystemFont();
    // if ((m_Style & SWT.BOLD) != 0 || (m_Style & SWT.ITALIC)!=0) {
    // FontData[] fd = m_font.getFontData();
    // int style = SWT.NONE;
    // if ((m_Style & SWT.BOLD)!=0)
    // style |= SWT.BOLD;
    // if ((m_Style & SWT.ITALIC)!=0)
    // style |= SWT.ITALIC;
    //            
    // for (int i=0; i<fd.length; i++)
    // fd[i].setStyle(style);
    // m_TMPfont = new Font(Display.getCurrent(), fd);
    // gc.setFont(m_TMPfont);
    // } else
    // gc.setFont(m_font);
    // }
}
