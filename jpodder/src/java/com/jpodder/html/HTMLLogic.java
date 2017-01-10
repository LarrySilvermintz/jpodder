package com.jpodder.html;

import java.io.File;

import org.apache.log4j.Logger;

import com.jpodder.FileHandler;
import com.jpodder.JPodderException;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class HTMLLogic {
    
    protected Logger mLog = Logger.getLogger(getClass().getName());

    private static HTMLLogic sSelf = getInstance();
    public static String STYLE_SHEET;
    
    public static HTMLLogic getInstance(){
        if(sSelf == null){
        	STYLE_SHEET = FileHandler.sLibDirectory.getAbsolutePath() + File.separator + "style.css";            
            sSelf = new HTMLLogic();
        }
        return sSelf;
    }
    
    /**
     * A style from the currently selected stylesheet is applied.
     * @param pTag
     * @return
     */
    public Tag getDivider(){
        Tag lTag = new Tag(Tag.T_DIV);
        return lTag;
    }
}