package com.jpodder.ui.swt.text;

/**
 * <p>Title: xfile</p>
 * <p>Description: Constructive XML</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Christophe Bouhier
 * @version
 */

/** An insight handler interface.
 * <p>
 * Defines methods for an insight handler.
 * @see wpInsightHandler
 */
public interface InsightHandlerInterface {

  static final short INSIGHT_GRAMMAR = 100;
  static final short INSIGHT_BASIC = 110;

  public void addControl(InsightControl control);

  public void removeControl(InsightControl control);

  public InsightControl getSelectedControl();
}