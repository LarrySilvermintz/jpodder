package com.jpodder.html.style;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import com.jpodder.JPodderException;
import com.jpodder.data.configuration.ConfigurationLogic;
import com.steadystate.css.parser.CSSOMParser;

/**
 * CSS Stylesheets parsing logic. Styles can be applied to HTML tags, to create
 * an HTML Document with styles.
 * 
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class Css {

	private static Css sSelf;

	protected CSSStyleSheet mStyleSheet;

	private Logger mLog = Logger.getLogger(getClass().getName());

	public static Css getInstance() {
		if (sSelf == null) {
			sSelf = new Css();
		}
		return sSelf;
	}

	CssDataHandler mHandler = new CssDataHandler(this);

	public Css() {
		try {
			ConfigurationLogic.getInstance().addDataHandler(mHandler);
		} catch (JPodderException e) {
			mLog.warn("Error reading: " + e.getMessage());
		}
	}

	public CSSStyleSheet parse(File pFile) throws JPodderException {
		try {
			Reader r = new FileReader(pFile.getAbsolutePath());
			CSSOMParser parser = new CSSOMParser();
			InputSource lInSource = new InputSource(r);
			CSSStyleSheet styleSheet = parser.parseStyleSheet(lInSource);
			mStyleSheet = styleSheet;

			CSSStyleRule lRule = getStyleRule(0);

			CSSStyleDeclaration lDecl = lRule.getStyle();

			for (int i = 0; i < lDecl.getLength(); i++) {
				String lItem = lDecl.item(i);

				// lDecl.getPropertyCSSValue();
				mLog.info(lItem);
			}
			return styleSheet;
		} catch (IOException e) {
			throw new JPodderException(e.getMessage());
		}
	}

	/**
	 * Get all the rules from a certain type.
	 * 
	 * @param pSheet
	 * @param pType
	 * @return
	 */
	public List getRulesFromType(CSSStyleSheet pSheet, int pType) {
		if (pSheet == null) {
			return null;
		}
		CSSRuleList rules = pSheet.getCssRules();
		ArrayList<CSSRule> lFilteredRules = new ArrayList<CSSRule>();
		for (int i = 0; i < rules.getLength(); i++) {
			CSSRule rule = rules.item(i);

			if (rule.getType() == pType) {
				lFilteredRules.add(rule);
			}
		}
		return lFilteredRules;
	}

	public int getStyleRuleCount() {
		return getRulesFromType(mStyleSheet, CSSRule.STYLE_RULE).size();
	}

	public CSSStyleRule getStyleRule(int pIndex) {
		List lStyleRules = getRulesFromType(mStyleSheet, CSSRule.STYLE_RULE);
		Object[] lRulesArray = lStyleRules.toArray();
		if (pIndex < lRulesArray.length) {
			return (CSSStyleRule) lRulesArray[pIndex];
		} else
			throw new ArrayIndexOutOfBoundsException("Rule not defined");
	}

	public CSSStyleRule getStyleRule(String pID) throws StyleNotFoundException {

		List lStyleRules = getRulesFromType(mStyleSheet, CSSRule.STYLE_RULE);
		if (lStyleRules != null) {
			Object[] lRulesArray = lStyleRules.toArray();
			for (int pIndex = 0; pIndex < lRulesArray.length; pIndex++) {
				CSSStyleRule lRule = (CSSStyleRule) lRulesArray[pIndex];
				String lCssText = cleanRule(lRule.getSelectorText());

				if (lCssText.equals(pID)) {
					String lSelector = lRule.getSelectorText();
					mLog.debug(lRule.getCssText() + ":" + lSelector);
					return lRule;
				}
			}
		}
		throw new StyleNotFoundException();
	}

	public String cleanRule(String pStyle) {
		String lStripped = null;
		boolean goOn = true;
		while (goOn) {
			boolean lStrippedThisRun = false;
			if (lStripped == null) {
				lStripped = pStyle;
			}

			if (lStripped.startsWith("*")) {
				lStripped = pStyle.substring(pStyle.indexOf("*") + 1);
				lStrippedThisRun = true;
			}

			if (lStripped.startsWith("#")) {
				lStripped = pStyle.substring(pStyle.indexOf("#") + 1);
				goOn = false;
				lStrippedThisRun = true;
			}

			if (lStripped.startsWith(".")) {
				lStripped = pStyle.substring(pStyle.indexOf(".") + 1);
				lStrippedThisRun = true;
			}

			if (!lStrippedThisRun) {
				goOn = false;
			}
		}
		return lStripped;
	}

}
