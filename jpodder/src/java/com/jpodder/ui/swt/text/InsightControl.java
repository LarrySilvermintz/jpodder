package com.jpodder.ui.swt.text;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;

/**
 * An insight control class.
 * <p>
 * Defines actions & logic for the insight function. The actions are:
 * 
 * <pre>
 *  
 *   
 *      - insight : show  insight.
 *      - escape : hide insight
 *      - enter : insert selection at text position (If any).
 *      - up : move up in the insight list.
 *      - down : move down in the insight list.
 *      - page up : move up one page in insight list.
 *      - page down : move down one page in insight list.
 *      - backspace : remove char, hide insight if start position regained.
 *      - Alphanumerical : lookup &amp; fill text from a tag in the insight list.
 *      
 *     
 *    
 *   
 * </pre>
 * 
 * The insight control class changes the default <code>JTextComponent</code>
 * key behaviour. The original key behaviour is restored when the insight Panel
 * is toggled to "off".
 * <p>
 * When activating the insight function, a panel is shown with a insight list of
 * tags. The upper left corner of the panel is positioned at the caret position.
 * <p>
 * Several convenience methods are implemented for the insightcontrol to perform
 * the following functions:
 * <P>
 * insight invocation ------------------ Insight is invoked either by a
 * keystroke, or automaticly when typing the ' <' character. When invoked
 * automaticly, a timer is actived, if the user types any new key, the invoking
 * is cancelled.
 * 
 * <ul>
 * <li>Get the current offset position.</li>
 * <li>Get text from offset to end of line.</li>
 * <li>Fill text from the offset position.</li>
 * <li>Get the predictive text from the list.</li>
 * </ul>
 * The toggle position is the caret position when the insight control is
 * invoked. (This will be the insertion position). The
 * <code>getToggleToEndText()</code> function gets the text from the toggle
 * offset until the end of the line. The <code>fillToggleWithPrediction()</code>
 * method allows filling of the line, with text from the insight list, starting
 * at the toggle offset. Finally a method to get the predictive text.
 * <p>
 * A typical implementation requires to override the <code>JTextComponent</code>
 * method named <code>replaceSelection()</code>. This allows to capture any
 * pressed key (Except the keys defined here) and invoke the
 * <code>fillToggleWithPrediction()</code> method.
 * <p>
 * 
 * @see InsightInterface
 * @see InsightHandler
 */
public class InsightControl implements InsightInterface {

	private Action insightAction;

	// private Action insightDelayAction;
	//
	// private TextAction escapeAction;
	//
	// private TextAction enterAction;
	//
	// private TextAction upAction;
	//
	// private TextAction downAction;
	//
	// private TextAction backSpaceAction;
	//
	// private TextAction pageUpAction;
	//
	// private TextAction pageDownAction;
	//
	// private Object savedEnterAction;
	//
	// private Object savedEscapeAction;
	//
	// private Object savedUpAction;
	//
	// private Object savedDownAction;
	//
	// private Object savedBackSpaceAction;
	//
	// private Object savedType10Action;
	//
	// private Object savedPageDownAction;
	//
	// private Object savedPageUpAction;
	//
	// private InputMap savedParentInputMap = null;

	private static final int ACTIVATION_DELAY = 300;

	private static final boolean IGNORE_CASE = false;

	final Control target;

	final List list;

	private int togglePosition;

	private boolean autoActivated = false;

	private String predictedText = new String();

	/**
	 * Constructor - define key actions.
	 * <p>
	 * The actions which result in specific behaviour are defined here.
	 * 
	 * @param frame
	 * 
	 * @param tar
	 *            The target textcomponent to which the new keymap is tight.
	 * @param pTitle
	 */
	public InsightControl(Control tar, String pTitle) {
		this.target = tar;
		InsightView mInsightView = new InsightView(pTitle);
		list = mInsightView.getInsightList();

		// Display d;
		// Display.getDefault().addFilter()

		// /////////////////////////////////////////////////////////////////////////
		// INSIGHT KEY
		// /////////////////////////////////////////////////////////////////////////
		target.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				int lCode = arg0.keyCode;
				System.out.println(arg0);
			}

			public void keyReleased(KeyEvent arg0) {
			}
		});
		
		// Migrate below to individual functions.
		
		
		
		// InputMap targetMap = target.getInputMap(JComponent.WHEN_FOCUSED);
		// insightAction = new AbstractAction("InsightAction") {
		// public void actionPerformed(ActionEvent e) {
		// String command = e.getActionCommand();
		// toggleInsight();
		// }
		// };
		// targetMap.put(insightStroke, insightAction);
		//
		// ///////////////////////////////////////////////////////////////////////////
		// // INSIGHT AUTOMATIC KEY
		// ///////////////////////////////////////////////////////////////////////////
		//
		// insightDelayAction = new AbstractAction("InsightAction") {
		// public void actionPerformed(ActionEvent e) {
		// String command = e.getActionCommand();
		// try {
		// target.getDocument().insertString(
		// target.getCaretPosition(),
		// new String(new char[] { CONTROL_CHAR }), null);
		// } catch (BadLocationException ble) {
		// // programatic error
		// }
		// activateInsightTimer();
		// }
		// };
		// Object savedControlAction = targetMap.get(insightStroke_LT);
		// targetMap.put(insightStroke_LT, "none");
		// targetMap.put(insightStroke_LT, insightDelayAction);
		//
		// ///////////////////////////////////////////////////////////////////////////
		// // ESCAPE KEY
		// ///////////////////////////////////////////////////////////////////////////
		//
		// escapeAction = new TextAction("escapeAction") {
		// public void actionPerformed(ActionEvent e) {
		// Object value = list.getSelectedValue();
		// if (value != null && value instanceof String) {
		// Document doc = target.getDocument();
		// Element elem = doc.getDefaultRootElement();
		// int pos = target.getCaretPosition();
		// int line = elem.getElementIndex(pos);
		// int end = elem.getElement(line).getEndOffset();
		// int len = end - pos - 1;
		// try {
		// doc.remove(pos, len);
		// } catch (BadLocationException ble) {
		// ble.getCause().getMessage();
		// // Programmatic Error
		// }
		// }
		// toggleInsight();
		// }
		// };
		//
		// ///////////////////////////////////////////////////////////////////////////
		// // ENTER KEY
		// ///////////////////////////////////////////////////////////////////////////
		//
		// enterAction = new TextAction("enterAction") {
		// public void actionPerformed(ActionEvent event) {
		//
		// JTextComponent target = getTextComponent(event);
		// Document doc = target.getDocument();
		// Element elem = doc.getDefaultRootElement();
		// int line = elem.getElementIndex(togglePosition);
		// Object value = list.getSelectedValue();
		//
		// if (value != null) {
		//
		// if (value instanceof TokenHandler.Token) {
		// value = ((TokenHandler.Token) value).getName();
		// } else {
		// value = value.toString();
		// }
		//
		// // Insert the list Selection if:
		// // 1. No characters have been typed.
		// // 2. The selected list entry, does not start with the typed
		// // chars.
		// // (This is refered to as an override selection).
		// // 3. Complete the prediction, if there is no selection
		// // from the list.
		// String s = (String) value;
		// String p = null;
		// try {
		// // get the length of the inserted text. -1 to ignore the
		// // "\n".
		// int endOffset = elem.getElement(line).getEndOffset();
		// String fullText = elem.getDocument().getText(
		// togglePosition, endOffset - togglePosition);
		// // Get the text from the toggle pos to the end,
		// // this is used to ?
		// int len = endOffset - togglePosition - 1;
		// p = doc.getText(togglePosition, len);
		// if (p.length() > 0 && !s.startsWith(p)) {
		// doc.remove(togglePosition, len);
		// }
		// if (autoActivated) {
		// s = s.substring(1);
		// }
		// doc.insertString(togglePosition, s, null);
		//                        
		// } catch (BadLocationException ble) {
		// ble.getCause().getMessage();
		// // Programmatic Error
		// }
		// // Move the caret to the end of the inserted text and hide this
		// // frame
		// target.setCaretPosition(elem.getElement(line).getEndOffset() - 1);
		// }
		//
		// toggleInsight();
		// }
		// };
		//
		// ///////////////////////////////////////////////////////////////////////////
		// // BACKSPACE KEY
		// ///////////////////////////////////////////////////////////////////////////
		//
		// backSpaceAction = new TextAction("") {
		// public void actionPerformed(ActionEvent event) {
		// Document doc = target.getDocument();
		//
		// // 1. If selection act differently.
		// // 2. Selection:
		// // - remove 1 char.
		// // 3. If invoke position, toggle the mode,
		// // 4. Otherwise repredict the toggle-to-caret text.
		// // (Adapt also the selected item in the Invoke panel model).
		//
		// int caretPos = target.getCaretPosition();
		// Caret car = target.getCaret();
		// int dot = car.getDot();
		// int mark = car.getMark();
		// try {
		// if (dot == mark) { // No selection.
		// // Element elem = doc.getDefaultRootElement();
		// // int line = elem.getElementIndex(caretPos);
		// if (caretPos <= togglePosition) {
		// toggleInsight();
		// } else {
		// doc.remove(caretPos - 1, 1);
		// }
		// } else {
		// String s = target.getSelectedText();
		// doc.remove(dot, mark - dot);
		// }
		// // change selection in model.
		// String text = getToggleToCaretText();
		// predictedText = getPrediction(text);
		//
		// } catch (BadLocationException ble) {
		// // programatic error.
		// }
		// }
		// };
		//
		// ///////////////////////////////////////////////////////////////////////////
		// // UP KEY
		// ///////////////////////////////////////////////////////////////////////////
		//
		// upAction = new TextAction("") {
		// public void actionPerformed(ActionEvent event) {
		// int size = list.getModel().getSize();
		// if (size > 0) {
		// int selectedIndex = list.getSelectedIndex();
		// if (selectedIndex == -1) {
		// selectedIndex = size - 1;
		// } else {
		// selectedIndex = (selectedIndex + size - 1) % size;
		// }
		// list.setSelectedIndex(selectedIndex);
		// list.ensureIndexIsVisible(selectedIndex);
		// }
		// }
		// };
		//
		// ///////////////////////////////////////////////////////////////////////////
		// // DOWN KEY
		// ///////////////////////////////////////////////////////////////////////////
		//
		// downAction = new TextAction("") {
		// public void actionPerformed(ActionEvent event) {
		// int size = list.getModel().getSize();
		// if (size > 0) {
		// int selectedIndex = list.getSelectedIndex();
		// if (selectedIndex == -1) {
		// selectedIndex = 0;
		// } else {
		// selectedIndex = (selectedIndex + 1) % size;
		// }
		// list.setSelectedIndex(selectedIndex);
		// list.ensureIndexIsVisible(selectedIndex);
		// }
		// }
		// };
		//
		// ///////////////////////////////////////////////////////////////////////////
		// // PAGEUP KEY
		// ///////////////////////////////////////////////////////////////////////////
		//
		// pageUpAction = new TextAction("") {
		// public void actionPerformed(ActionEvent event) {
		// if (!list.isSelectionEmpty()) {
		//
		// // 1. condition: Model fits in panel?
		// // - selected = 1st row of visible.
		// // 2. condition: Model don't fit in panel?
		// // 2.a condition: selected is first row of visible?
		// // - selected = (selected - panelsize) mod panelsize.
		// // 2.b condition: selected is not first row of visible?
		// // - selected = 1st row of visible
		// int size = list.getModel().getSize();
		// if (size > 0) {
		// int first = list.getFirstVisibleIndex();
		// int selectedIndex = list.getSelectedIndex();
		// if (selectedIndex != first) {
		// selectedIndex = first;
		// } else {
		// selectedIndex = (selectedIndex + size - list
		// .getVisibleRowCount())
		// % size;
		// }
		//
		// list.setSelectedIndex(selectedIndex);
		// list.ensureIndexIsVisible(selectedIndex);
		// }
		// }
		//
		// }
		// };
		// ///////////////////////////////////////////////////////////////////////////
		// // PAGEDOWN KEY
		// ///////////////////////////////////////////////////////////////////////////
		//
		// pageDownAction = new TextAction("") {
		// public void actionPerformed(ActionEvent event) {
		// if (!list.isSelectionEmpty()) {
		//
		// int size = list.getModel().getSize();
		// if (size > 0) {
		// int last = list.getLastVisibleIndex();
		// int selectedIndex = list.getSelectedIndex();
		// if (selectedIndex != last) {
		// selectedIndex = last;
		// } else {
		// selectedIndex = (selectedIndex + size + list
		// .getVisibleRowCount())
		// % size;
		// }
		//
		// list.setSelectedIndex(selectedIndex);
		// list.ensureIndexIsVisible(selectedIndex);
		// }
		// }
		// }
		// };

	}

	/**
	 * Activate the insight timer.
	 * <p>
	 * A timer is activated, when the timer expires, a check will be done if the
	 * user has typed more keys. This will cause the activation of the insight
	 * panel to be cancelled.
	 */
	public void activateInsightTimer() {
		// CB TODO Migrate.
		// if (!isEnabled()) {
		// final int len = target.getDocument().getLength();
		// Timer activateTimer = new Timer();
		// activateTimer.schedule(new TimerTask() {
		// public void run() {
		// int lenNow = target.getDocument().getLength();
		// if (len == lenNow) {
		// autoActivated = true;
		// toggleInsight();
		// }
		// };
		// }
		//
		// , ACTIVATION_DELAY);
		// } else {
		// toggleInsight();
		// }

	}

	/**
	 * Print the text components keymaps.
	 * <p>
	 * climb the keymap list and print the defined keys to the GUI log window.
	 * <p>
	 * 
	 * @param target
	 *            A text component.
	 */
	public void printKeyMaps(JTextComponent target) {

		// resolve parent keymaps and remove toggle keystroke.
		Keymap resolveMap = target.getKeymap();
		Keymap newMap;
		while (resolveMap != null) {
			KeyStroke[] strokes = resolveMap.getBoundKeyStrokes();
			for (int i = 0; i < strokes.length; i++) {
				System.out.print("stroke code: " + strokes[i].getKeyCode()
						+ "\n");
			}
			newMap = resolveMap.getResolveParent();
			if (newMap == resolveMap) {
				break;
			} else {
				resolveMap = newMap;
			}
		}

	}

	/**
	 * Changes the key behaviour.
	 * <p>
	 * Some default textComponent up and down actions are overriden with custom
	 * key actions. The actions (From DefaultEditorKit) which are overriden are:
	 * <p>
	 * 
	 * <pre>
	 * -Enter - Escape - Up - Down
	 * </pre>
	 * 
	 * <p>
	 * The overriden actions are saved and restored when this layer is hidden.
	 * with the method <code>hide()</code>
	 * <p>
	 * NOTE (i) 29-05-2003, not implemented is the backspace which should handle
	 * remove of characters in the model (Default backspace action from
	 * DefaulEditorKit) but additionally should deselect in the JList. (ii) The
	 * InsightPanel feature is also implemented as an extension in JEditorPanes,
	 * replaceSelection method. This is where lookups are done in the model.
	 * Re-evalution of this implementation choice is needed. - Can override
	 * default Action.
	 * 
	 * <pre>
	 * private void rebind() {
	 * 	InputMap im = getInputMap(); // remove old binding
	 * 	KeyStroke typed010 = KeyStroke.getKeyStroke(&quot;typed \010&quot;);
	 * 	InputMap parent = im;
	 * 	while (parent != null) {
	 * 		parent.remove(typed010);
	 * 		parent = parent.getParent();
	 * 	}
	 * 	// rebind backspace
	 * 	KeyStroke ctrlH = KeyStroke.getKeyStroke(&quot;ctrl H&quot;);
	 * 	im.put(bksp, DefaultEditorKit.deletePrevCharAction);
	 * 	// add new binding
	 * 	KeyStroke bksp = KeyStroke.getKeyStroke(&quot;BACK_SPACE&quot;);
	 * 	im.put(ctrlH, DefaultEditorKit.backwardAction);
	 * }
	 * </pre>
	 * 
	 */
	private void changeKeyBehaviour() {
		// CB TODO This is SWING specific.
		// InputMap targetMap = target.getInputMap(JComponent.WHEN_FOCUSED);
		//
		// savedEscapeAction = targetMap.get(escapeStroke);
		// targetMap.put(escapeStroke, escapeAction);
		//
		// savedEnterAction = targetMap.get(enterStroke);
		// targetMap.put(enterStroke, enterAction);
		//
		// savedUpAction = targetMap.get(upStroke);
		// targetMap.put(upStroke, upAction);
		//
		// savedDownAction = targetMap.get(downStroke);
		// targetMap.put(downStroke, downAction);
		//
		// savedPageUpAction = targetMap.get(pageUpStroke);
		// targetMap.put(pageUpStroke, pageUpAction);
		//
		// savedPageDownAction = targetMap.get(pageDownStroke);
		// targetMap.put(pageDownStroke, pageDownAction);
		//
		// savedType10Action = targetMap.get(typed10);
		// targetMap.put(typed10, "none");
		// targetMap.put(backSpaceStroke, backSpaceAction);

	}

	/**
	 * Restore the orginal keystroke bindings.
	 * <p>
	 * 
	 */
	private void restoreKeyBehaviour() {
		// CB TODO SWING specific.
		// InputMap targetMap = target.getInputMap(JComponent.WHEN_FOCUSED);
		//
		// targetMap.put(enterStroke, savedEnterAction);
		// targetMap.put(escapeStroke, savedEscapeAction);
		// targetMap.put(upStroke, savedUpAction);
		// targetMap.put(downStroke, savedDownAction);
		// targetMap.put(pageUpStroke, savedPageUpAction);
		// targetMap.put(pageDownStroke, savedPageDownAction);
		// targetMap.put(backSpaceStroke, savedBackSpaceAction);
		// targetMap.put(typed10, savedType10Action);

	}

	/**
	 * Print the key bindings for a text component.
	 * <p>
	 * Get all keys (Keystroke) from the Input map and print the associated
	 * object binding.
	 * <p>
	 * Get all keys from the Action map and print the associated object (Action)
	 * binding.
	 * <p>
	 * 
	 * @param target
	 *            The target text component. In this case this should be a
	 *            wpEditor object.
	 * 
	 */
	void printMaps(JTextComponent target) {
		ActionMap aMap = target.getActionMap();
		InputMap iMap = target.getInputMap(JTextComponent.WHEN_FOCUSED);
		KeyStroke[] ikeys = iMap.allKeys();
		for (int j = 0; j < ikeys.length; j++) {
			Object key = iMap.get(ikeys[j]);
			Action action = aMap.get(key);
			System.out.println(j + key.toString() + "| " + ikeys[j].toString()
					+ "| " + (action != null ? action.toString() : "") + "\n");
		}
	}

	/**
	 * Set the model for InsightControl. The model is constructed from the
	 * specified grammar using a schema engine.
	 * <p>
	 * 
	 * @param grammar
	 *            A grammar which will be shown in the InsightPanel.
	 * @see wpGrammar
	 */
	public void setModel(ListModel pList) {

		// if (pList != null)
		// setInsightList(pList);
		// else {
		// setDefaultModel();
		// }
	}

	/**
	 * Toggle showing or hiding the Insight Panel.
	 * <p>
	 * show/hides the panel, while changing or restoring keybindings for insight
	 * logic for predicting text.
	 * <P>
	 * The panel is shown, to the right and under the caret.
	 */
	public void toggleInsight() {

		// // toggle between hide/show
		// if (isEnabled()) {
		// hide();
		// restoreKeyBehaviour();
		// setEnabled(false);
		// autoActivated = false;
		// } else {
		// list.clearSelection();
		// changeKeyBehaviour();
		// setEnabled(true);
		// // Show the Insight Panel at the Caret position
		// // in the editor.
		// togglePosition = target.getCaretPosition();
		// Rectangle caretRectangle = null;
		// try {
		// caretRectangle = target.modelToView(togglePosition);
		// } catch (BadLocationException ble) {
		// // Programatic error.
		// }
		// // Check the JFrame location aswell.
		//
		// Point loc = target.getLocationOnScreen();
		// // Point frameLoc = frame.getLocationOnScreen();
		// Point newLoc = new Point((int) loc.getX()
		// + (int) caretRectangle.getX(), (int) loc.getY()
		// + (int) caretRectangle.getY() + 15);
		// show(newLoc);
		// }
	}

	/**
	 * Fill the matched text from the model in the text document.
	 * <p>
	 * The typed text from the toggle position to the current caret is matched
	 * with the list. The best match is inserted in the text model, from the
	 * toggle offset onwards. The inserted is also selected.
	 * <p>
	 * 
	 * @throws wpException
	 *             Generic exception.
	 */
	public void fillToggleWithPrediction() {

		// Document doc = target.getDocument();
		//
		// if (doc != null) {
		// try {
		// String text = getToggleToCaretText();
		// predictedText = getPrediction(text);
		// if (predictedText != null) {
		// // 1. create insert position 2. insert text 3. move caret to
		// // create a selection.
		// int insertPos = togglePosition + text.length();
		// doc.insertString(insertPos, predictedText, null);
		// target.setCaretPosition(insertPos + predictedText.length());
		// target.moveCaretPosition(insertPos);
		// // @todo: tag completion, with closing bracket or validate
		// // until closing tag.
		// // doc.insertString(caretPos,"/>",null);
		// }
		// } catch (BadLocationException ble) {
		// throw new xException(ble);
		// }
		// }
	}

	/**
	 * Match the provided text with the Insight model.
	 * <p>
	 * The best match is also selected.
	 * <p>
	 * If insight is activated automaticly (When typing a certain key). The
	 * activation key will pre-pended to the lookup text.
	 * <p>
	 * If a match occurs, the already typed characters are trimmed.
	 * 
	 * <p>
	 * 
	 * @param substring
	 *            The typed character from the offset. (When insight was
	 *            invoked).
	 * @return String The predictive text.
	 */
	public String getPrediction(String substring) {

		// CB TODO Need to migrate this.
		// ListModel insightList = (ListModel) getInsightList().getModel();
		// String lookupString = substring;
		// String entry;
		// if (insightList != null) {
		// for (int i = 0; i < insightList.getSize(); i++) {
		//
		// Object obj = insightList.getElementAt(i);
		// if (obj instanceof String) {
		// entry = (String) obj;
		// } else {
		// entry = obj.toString();
		// }
		//
		// // Add the autoactivation character, to the substring text
		// // for
		// // looking a prediction text, only if the entry startswith
		// // the
		// // autoactivation character.
		// int lookOffset = 0;
		// if (autoActivated) {
		// Character c = new Character(CONTROL_CHAR);
		// String ctrlString = c.toString();
		//
		// if (entry.startsWith(ctrlString)) {
		// lookOffset = 1;
		// }
		// }
		// String caseEntry = entry;
		// // if(!IGNORE_CASE){
		// // lookupString = lookupString.toLowerCase();
		// // caseEntry = entry.toLowerCase();
		// // }
		// if (caseEntry.startsWith(lookupString, lookOffset)) {
		// getInsightList().setSelectedIndex(i);
		// getInsightList().ensureIndexIsVisible(i);
		// // trim the already keyed chars.
		// int trim = entry.indexOf(substring) + substring.length();
		// String s = entry.substring(trim);
		// return s;
		// }
		//
		// }
		// // No match, clear the selection.
		// list.clearSelection();
		// }
		return null;
	}

	/**
	 * Get the text from the toggle position to the end of line.
	 * <p>
	 * The toggle position is fixed and set every time the Insight Panel, is
	 * shown.
	 * <p>
	 * 
	 * @return String The text from the caret til the end of the line.
	 * @throws wpException
	 *             Generic exception.
	 */
	// public String getToggleToEndText() throws xException {
	// CB TODO, Rework later.
	// Document doc = target.getDocument();
	// Element elem = doc.getDefaultRootElement();
	// int line = elem.getElementIndex(togglePosition);
	//
	// // substract 1 for stripping the "\n" character.
	// // NOTE 28-05-2003, works for windows but for other platform?
	// int endOffset = elem.getElement(line).getEndOffset() - 1;
	// String s = null;
	// try {
	// s = doc.getText(togglePosition, endOffset - togglePosition);
	// } catch (BadLocationException ble) {
	// throw new xException(ble);
	// }
	// return s;
	// }
	/**
	 * Get the text from toggle position to the current caret offset.
	 * <p>
	 * The toggle position is fixed and set every time the Insight Panel, is
	 * shown.
	 * <p>
	 * 
	 * @return String The text from the caret til the end of the line.
	 */
	// CB TODO Reworklater.
	// public String getToggleToCaretText() {
	//
	// Document doc = target.getDocument();
	// String s = null;
	// try {
	// int pos = target.getCaretPosition();
	// s = doc.getText(togglePosition, pos - togglePosition);
	// } catch (BadLocationException ble) {
	// // programatic error
	// }
	// return s;
	// }
}