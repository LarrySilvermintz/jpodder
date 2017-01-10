package com.jpodder.ui.swt.theme;

public interface IUIThemeView {
	
	public static final String STYLE_GENERIC = "generic";
	
	public IUIComponentBinder[] getBinders();
	
	public void registerUIThemeBinders();
}