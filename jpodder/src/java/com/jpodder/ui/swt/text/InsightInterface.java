package com.jpodder.ui.swt.text;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

/** An insight interface.
 * <p>
 * Defines constants for the insight function.
 *
 */
public interface InsightInterface {

  static final String ESCAPE_KEY = "escapeInsight";
  static final String ENTER_KEY = "enterInsight";
  static final String UP_KEY = "upInsight";
  static final String DOWN_KEY = "downInsight";
  static final char CONTROL_CHAR = '$';

  KeyStroke insightStroke = KeyStroke.getKeyStroke(KeyEvent.VK_T,
      InputEvent.CTRL_MASK);
  KeyStroke insightStroke_LT = KeyStroke.getKeyStroke("typed $");
  KeyStroke insightGrammarStroke = KeyStroke.getKeyStroke(KeyEvent.VK_I,
      InputEvent.CTRL_MASK);

  KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
  KeyStroke enterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
  KeyStroke upStroke = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
  KeyStroke downStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
  KeyStroke backSpaceStroke = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0);
  KeyStroke deleteStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
  KeyStroke pageUpStroke = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0);
  KeyStroke pageDownStroke = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0);
  KeyStroke typed10 = KeyStroke.getKeyStroke("typed \010");

  public String defaultList[] = {
      "<?xml version \"1.0\" encoding \"utf-8\" ?>",
      "</>",
      "<!-- -->",
      "targetnamespace"
  };

}
