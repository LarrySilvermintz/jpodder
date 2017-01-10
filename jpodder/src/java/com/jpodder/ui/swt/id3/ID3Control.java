package com.jpodder.ui.swt.id3;

import com.jpodder.data.content.ContentLogic;
import com.jpodder.data.feeds.IXFile;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class ID3Control {

    public void showID3Edit(IXFile file) {
        
    }

    public void showID3View(IXFile pFile) {    	
    	if( ContentLogic.isMP3(pFile.getFile().getAbsolutePath())){
        	ID3FileDialog lDialog = new ID3FileDialog();
        	lDialog.fillDialog(pFile.getFile());
    	}
    }

}
