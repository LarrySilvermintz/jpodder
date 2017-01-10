package com.jpodder.ui.swt.comp;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class DisplayTool {
    
    protected static Point p;
    public static Point getCenter(){
        Display lDisplay = Display.getDefault();
        Rectangle lClient = lDisplay.getClientArea();
        p = new Point(lClient.width/2, lClient.height/2);
        return p;
    }
    
    public static Point getCenterPosition(Point pPoint){
        return getCenterPosition(pPoint.x, pPoint.y);
    }
    public static Point getCenterPosition(int pWidth, int pHeight){
        Point p = getCenter();
        
        int x = p.x - pWidth/2 ;
        int y = p.y - pHeight/2;
        return new Point(x,y);
    };
}
