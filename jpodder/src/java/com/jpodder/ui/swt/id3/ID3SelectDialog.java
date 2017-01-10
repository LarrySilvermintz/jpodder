package com.jpodder.ui.swt.id3;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.jpodder.data.feeds.IXPersonalFeed;
import com.jpodder.data.id3.ID3Generic;
import com.jpodder.data.id3.ID3Tag;
import com.jpodder.data.id3.ID3TagRewrite;
import com.jpodder.ui.swt.comp.CheckTableDialog;
import com.jpodder.ui.swt.comp.ICheckTableItem;
import com.jpodder.util.Messages;

/**
 * Show a dialog which allows the selection of ID tags. The dialog is based on a
 * checked table. The mutations can be saved or the dialog can simply be
 * cancelled.
 * 
 */
public class ID3SelectDialog extends CheckTableDialog {

    private static Logger sLog = Logger.getLogger(ID3SelectDialog.class
            .getName());

    /**
     * Tag selection dialog constructor.
     */
    public ID3SelectDialog() {
        super(Messages.getString("id3tagselectdialog.title"));
    }

    /**
     * Construct a tag selection list.
     * 
     * @param mCurrentFeed
     */
    public static List getID3TagItems() {
        ArrayList<TagCheckTableItem> lCheckedList = new ArrayList<TagCheckTableItem>();
        Iterator i = ID3Tag.getID3TagList().iterator();
        while (i.hasNext()) {
            ID3Tag lTag = (ID3Tag) i.next();
            boolean lIsTagSelected = ID3Generic.getInstance().getId3Tag(
                    lTag.getName()) != null;
            lCheckedList.add(new TagCheckTableItem(lTag,
                    lIsTagSelected));
        }
        return lCheckedList;
    }

    /**
     * Construct a tag selection list, respecting an existing set of selected
     * tags from a <code>Feed</code> object.
     * 
     * @param mCurrentFeed
     */
    public static List getID3TagItems(IXPersonalFeed mCurrentFeed) {

        ArrayList<TagCheckTableItem> lCheckedList = new ArrayList<TagCheckTableItem>();
        Iterator i = ID3Tag.getID3TagList().iterator();
        while (i.hasNext()) {
            ID3Tag lTag = (ID3Tag) i.next();
            boolean lIsTagSelected = ID3Generic.getInstance().getId3Tag(
                    lTag.getName()) != null;
            if (mCurrentFeed != null)
                lIsTagSelected = mCurrentFeed.getId3Tag(lTag.getName()) != null;
            lCheckedList.add(new TagCheckTableItem(lTag,
                    lIsTagSelected));
        }
        return lCheckedList;
    }

    /**
     * Save the checked list to the generic file.
     * 
     * @param mCurrentFeed
     */
    public void saveSelection() {
        Iterator i = get().iterator();
        while (i.hasNext()) {
            ICheckTableItem lCheckedItem = (ICheckTableItem) i.next();
            ID3Tag lTag = (ID3Tag) lCheckedItem.getValue();
            if (lCheckedItem.isChecked()) {
                if (ID3Generic.getInstance().getId3Tag(lTag.getName()) == null) {
                    // Tag does not exist therefore add it
                    // Also add the description.
                    ID3TagRewrite lRewriteTag = new ID3TagRewrite(lTag.getName());
                    lRewriteTag.setDescription(lTag.getDescription());
                    lRewriteTag.setId(lTag.getId());
                    ID3Generic.getInstance().addId3Tag(lRewriteTag);
                }
            } else {
                if (ID3Generic.getInstance().getId3Tag(lTag.getName()) != null) {
                    // Tag does exist so remove it
                    ID3Generic.getInstance().removeId3Tag(
                            new ID3TagRewrite(lTag.getName()));
                }
            }
        }
        
        try {
        	ID3Generic.getInstance().writeResource();
        } catch (Exception e) {
            sLog.error("Error saving the generic file: "
                    + e);
        }
    }

    /**
     * Save the checked list to a feed.
     * 
     * @param mCurrentFeed
     */
    public void saveSelection(IXPersonalFeed mCurrentFeed) {
        Iterator i = get().iterator();
        while (i.hasNext()) {
            ICheckTableItem lCheckedItem = (ICheckTableItem) i.next();
            ID3Tag lTag = (ID3Tag) lCheckedItem.getValue();
            if (lCheckedItem.isChecked()) {
                if (mCurrentFeed.getId3Tag(lTag.getName()) == null) {
                    // Tag does not exist therefore add it
                    ID3TagRewrite lRewriteTag = new ID3TagRewrite(lTag.getName());
                    lRewriteTag.setDescription(lTag.getDescription());                    
                    lRewriteTag.setId(lTag.getId());
                    mCurrentFeed.addId3Tag(lRewriteTag);
                }
            } else {
                if (mCurrentFeed.getId3Tag(lTag.getName()) != null) {
                    // Tag does exist so remove it
                    mCurrentFeed
                            .removeId3Tag(new ID3TagRewrite(lTag.getName()));
                }
            }
        }
    }

    /**
     * Save the selection to a existing list. The list items are of type
     * <code>ID3TagRewrite</code>. Convenience method to merge an existing
     * list with the new selection. De-selected items are removed from the list.
     * Selected items are added to the list.
     * 
     * @param mTagRewriteList
     */
    public int saveSelection(List<ID3TagRewrite> mTagRewriteList) {

        int sizeBefore = mTagRewriteList.size();
        sLog.info("Saving selection before:" + sizeBefore + " elements");
        Iterator i = get().iterator();
        while (i.hasNext()) {
            ICheckTableItem lCheckedItem = (ICheckTableItem) i.next();
            ID3Tag lTag = (ID3Tag) lCheckedItem.getValue();
            ID3TagRewrite lTagRewrite = new ID3TagRewrite(lTag.getName());
            lTagRewrite.setDescription(lTag.getDescription());
            lTagRewrite.setId(lTag.getId());
            // lTagRewrite.setValueChanged(true);
            int index = mTagRewriteList.indexOf(lTagRewrite);
            if (lCheckedItem.isChecked()) {
                if (index < 0) {
                    mTagRewriteList.add(lTagRewrite);
                    sLog.info("Selected tag added " + lTag.getName());
                } else {
                    sLog.info("Selected tag exists " + lTag.getName());
                }
            } else {
                if (index < 0) {
                    sLog.info("De-selected tag does not exist "
                            + lTag.getName());
                } else {
                    mTagRewriteList.remove(lTagRewrite);
                    sLog.info("De-selected tag removed " + lTag.getName());
                }
            }
        }
        sLog.info("after:" + mTagRewriteList.size() + " elements");
        return mTagRewriteList.size() - sizeBefore; // Return the number of
        // tag entries which have been added.
    }

    /**
     * Get the list of checked items.
     * 
     * @return Returns the lCheckedList.
     */
    public List getCheckedList() {
        return get();
    }
}