package com.jpodder.ui.swt.id3;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class ID3View {
    
    protected Composite mView;
    
    public ID3View(){
    
    }
    private void initialize(Composite pParent){
        mView = new Composite(pParent, SWT.NONE);
        
    }
    
    public void fill(File pFile){
    }
    
}
