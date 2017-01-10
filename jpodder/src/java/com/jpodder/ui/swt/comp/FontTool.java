package com.jpodder.ui.swt.comp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * @deprecated
 *
 */
public class FontTool {

	public static void setData(FontData[] pData, Integer pStyle, Integer pHeight){
		if(!assertData(pStyle, pHeight)){
			return;
		}
		for (int i = 0; i < pData.length; i++) {
			FontData data = pData[i];
			if(pStyle != null){
				data.setStyle(pStyle.intValue());	
			}
			if(pHeight != null){
				data.setHeight(pHeight.intValue());	
			}
		}
	};
	
	private static boolean assertData(Integer pStyle, Integer pHeight){
		
		// TODO No assertion on Height.
		if(pStyle != null || pHeight != null){
			if((pStyle.intValue() & SWT.NORMAL) == SWT.NORMAL ||  
			   (pStyle.intValue() & SWT.ITALIC) == SWT.ITALIC || 
			   (pStyle.intValue() & SWT.BOLD) == SWT.BOLD ){
				return true;
			}else{
				return false;
			}
			
		}else{
			return false;
		}
	}
}
