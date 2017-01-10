package com.jpodder.data.id3;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.jpodder.data.configuration.ConfigurationEvent;
import com.jpodder.data.configuration.IConfigurationListener;
import com.jpodder.data.content.ContentLogic;
import com.jpodder.data.content.ContentVariants;
import com.jpodder.data.content.IContent;
import com.jpodder.data.feeds.IXFile;
import com.jpodder.tasks.AbstractTaskWorker;
import com.jpodder.tasks.TaskLogic;
import com.jpodder.tasks.TaskWorker;
import com.jpodder.util.TokenHandler;

/**
 * An ID3 Logic class. It contains a property listener for tags being added to
 * the generic list. I also contains the rewritting methods to
 * 
 * @see TokenHandler
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 * 
 */
public class ID3Logic implements IConfigurationListener {

    private static Logger sLog = Logger.getLogger(ID3Generic.class.getName());

    private static ID3Logic sSelf;

    public static ID3Logic getInstance() {
        if (sSelf == null) {
            sSelf = new ID3Logic();
        }
        return sSelf;
    }

    public void configurationChanged(ConfigurationEvent event) {
    
    }

    public void ID3Rewrite(Object[] pFileSelection) {

        for (int i = 0; i < pFileSelection.length; i++) {
            IXFile lFile = (IXFile) pFileSelection[i];
            if (lFile.getFile() != null) {
                String type = lFile.getFileType();
                if (type == null && lFile.getFile() != null) {
                    type = ContentLogic.getContentFromFileName(lFile.getFile().getName());
                }
                if (type.equals(IContent.MIME_MPEG)
                        || ContentVariants.isVariant(IContent.MIME_MPEG, type)) {
                    new RewriteTask(lFile, true); // autostarts background task.
                }
            }
        }
    }

    /**
     * Rewrite the ID3 tags
     * 
     * @param pFileWrapper
     * @param pOtherfile
     * @param pTags
     */
    private void rewriteTags(IXFile pFileWrapper, File pOtherfile, List pTags) {
        try {
            TokenHandler lHandler = TokenHandler.getInstance();
            ID3Wrapper lWrapper = lHandler.setIDWrapper(pFileWrapper);
            lHandler.setRSSWrapper(pFileWrapper);
            // Continue in the event listener. We needed to parse this file.
            // first.
            boolean rewrite = false;
            Iterator i = pTags.iterator();
            while (i.hasNext()) {
                ID3TagRewrite lTag = (ID3TagRewrite) i.next();
                String lNewValue = lTag.getValue();
                if (lNewValue.length() > 0) {
                    String lResult = lHandler.replacePlaceHolders(pFileWrapper,
                            lNewValue);
                    // Check the returned value. If a token was provided, it
                    // could be that the token value was empty.
                    if (lResult.length() > 0) {
                        sLog.info("Rewrite id3" + lResult + " to "
                                + pFileWrapper);
                        lWrapper.setContent(lTag.getName(), lResult);
                        rewrite = true;
                    }
                }
            }
            if (pTags.size() > 0 && rewrite) {
                lWrapper.update();
            }
        } catch (Exception e) {
            sLog.info("Tag rewriting failed: " + e);
        }
    }

    /**
     * Rewrite tags from the feed combined with the generic list.
     * 
     * @param pFile
     */
    public void rewriteTags(IXFile pFile) {
        String type = pFile.getFileType();
        // Rewrite the tags for MPEG files only.
        if (type.equals(IContent.MIME_MPEG)
                || ContentVariants.isVariant(IContent.MIME_MPEG, type)) {

            ArrayList<ID3TagRewrite> lTags = new ArrayList<ID3TagRewrite>();
            Iterator i = pFile.getFeed().getTagListIterator();
            while (i.hasNext()) {
                ID3TagRewrite lTag = (ID3TagRewrite) i.next();
                sLog.info("Add tag from per-feed list: " + lTag);
                lTags.add(lTag);
            }
            
            i = ID3Generic.getInstance().getTagListIterator();
            while (i.hasNext()) {
                ID3TagRewrite lTag = (ID3TagRewrite) i.next();
                sLog.info("Checking tag from generic list: " + lTag);
                // Check if the tag already exists and if not then add it
                if (!lTags.contains(lTag)) {
                    sLog.info("tag not found so add it now");
                    lTags.add(lTag);
                }
            }
            
            rewriteTags(pFile, null, lTags);
        } else {
            sLog.info("rewriteTags(), no rewrite on non-MPEG mime type");
        }
    }

    /**
     * Support for id3 is only for MP3 files.
     * 
     * 
     * @param lFile
     * @return
     */
    public boolean supportsID3(IXFile lFile) {
        boolean lEnable = false;
        if (lFile != null && lFile.isLocal()) {
            String type = lFile.getFileType();
            if (type != null) {
                if ((type.equals(IContent.MIME_MPEG) || ContentVariants.isVariant(
                        IContent.MIME_MPEG, type))
                        && lFile.isLocal()) {
                    lEnable = true;
                } else {
                    if (lFile.getFile() != null) {
                        type = ContentLogic.getContentFromFileName(lFile.getFile().getName());
                        if ((type.equals(IContent.MIME_MPEG) || ContentVariants
                                .isVariant(IContent.MIME_MPEG, type))
                                && lFile.isLocal()) {
                            lEnable = true;
                        }
                    }
                }
            }
        }
        return lEnable;
    }
    
    
	/**
	 * A Rewrite task which can execute in the background.
	 * The adapter also allows the task to updtate the progress 
	 * of the task.
	 * @see ITask
	 */
	class RewriteTask extends AbstractTaskWorker {
		
		IXFile mFile;
		
		public RewriteTask(IXFile pFile, boolean pConcurrent) {
			if (pConcurrent) {
				mFile = pFile;
				TaskLogic.getInstance().add(this, null, null);
				start();
			} else {
				rewriteTags(pFile);
			}
		}
		public void finished() {
			super.finished();
			TaskLogic.getInstance().fireTaskCompleted(mFile,
					null);
			TaskLogic.getInstance().remove(this, null, null);
		}
		public Object construct() {
			rewriteTags(mFile);
			return null;
		}
	}
}
