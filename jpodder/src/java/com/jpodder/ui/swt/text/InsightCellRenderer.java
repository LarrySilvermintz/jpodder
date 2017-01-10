package com.jpodder.ui.swt.text;

/**
 * <p>Title: xfile</p>
 * <p>Description: Constructive XML</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Christophe Bouhier
 * @version
 */

import java.awt.*;
import javax.swing.*;

/** Extends the default list cell renderer.
 * <p>
 * This class overides the <code>getListcellRendererComponent()</code>
 * An appropriate Icon is set in the component (JLabel) and returned.
 * <p>
*  Note: No need to set the text of JLabel?????
 *
 */
public class InsightCellRenderer
    extends DefaultListCellRenderer {
  public InsightCellRenderer() {
  }

  public Component getListCellRendererComponent(JList list,
                                                Object value,
                                                int index,
                                                boolean isSelected,
                                                boolean cellHasFocus
                                                ) {

    Component originalComponent = super.getListCellRendererComponent(list,
                                                value,
                                                index,
                                                isSelected,
                                                cellHasFocus
      );

    return this;
  }
}
