package com.jpodder.ui.swt.conf.panel.language;


import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.jpodder.ui.swt.conf.panel.language.TranslationModel.TranslationEntry;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class TranslationLabelProvider extends LabelProvider implements
        ITableLabelProvider, ITableColorProvider {

    TranslationModel mModel;

    public TranslationLabelProvider(TranslationModel pModel) {
        mModel = pModel;
    }

    /**
     */
    public String getColumnText(Object element, int columnIndex) {
        switch (columnIndex) {
        
            case 0: {
                return new Integer(mModel.indexOf(((TranslationEntry) element).getKey())).toString();
            }
            case 1: {
                return ((TranslationEntry) element).getKey();
            }
            case 2: {
            	return ((TranslationEntry) element).getMaster();
            }
            case 3: {
            	return ((TranslationEntry) element).getTranslation();
            }
        }
        return "Doesn't happen";
    }

    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    public Color getForeground(Object element, int columnIndex) {
        return null;
    }

    public Color getBackground(Object element, int columnIndex) {
    	return null;
    }
}