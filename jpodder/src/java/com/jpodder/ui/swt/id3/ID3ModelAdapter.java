package com.jpodder.ui.swt.id3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jpodder.data.id3.ID3Tag;
import com.jpodder.data.id3.ID3TagRewrite;

import de.vdheide.mp3.TagContent;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public class ID3ModelAdapter {

	protected int MIN_ROWS = 10;

	private List mEditedElements = new ArrayList();

	public ID3ModelAdapter() {

	}

	public Object[] getID3Model(Object pObjects) {

		
		if (pObjects == null) {
			throw new IllegalArgumentException();
		}

		if (pObjects instanceof List) {
			List lElements = (List) pObjects;
			// Create a backup of the model to rollback changes.
			for (Iterator it = lElements.iterator(); it.hasNext();) {
				Object lElement = it.next();
				if(lElement instanceof TagContent){
					TagContent lContent = (TagContent)lElement;
					if(lContent.getBinarySubtype() == null){
						mEditedElements.add(lContent);
					}
				}
				
				if(lElement instanceof ID3TagRewrite){
					mEditedElements.add(lElement);
				}
			}
		}
		ArrayList lSpaceHolders = new ArrayList();
		for (int i = 0; i < this.MIN_ROWS; i++) {
			lSpaceHolders.add(new SpaceHolder());
		}
		mEditedElements.addAll(lSpaceHolders);
		
		return mEditedElements.toArray();
	}

	public String getText(Object pObject, int pIndex) {
			if (pObject instanceof ID3TagRewrite) {
			ID3TagRewrite lTag = (ID3TagRewrite) pObject;
			switch (pIndex) {
			case 0:
				return lTag.getName();
			case 1:
				return lTag.getValue();
			case 2:
				return lTag.getDescription();
			}
		}
		if (pObject instanceof TagContent) {
			TagContent lContent = (TagContent) pObject;
			// We need to get a tag object for this.
			ID3Tag lTag = ID3Tag.getTag(lContent.getType());
			if (lTag != null) {
				switch (pIndex) {
				case 0:
					return lTag.getName();
				case 1:
					return lContent.getTextContent();
				case 2:
					return lTag.getDescription();
				}
			}
		}
		return null;
	}

	public void setValue(Object pObject, int pIndex, String pValue) {
		if (pObject instanceof ID3TagRewrite) {
			ID3TagRewrite lTag = (ID3TagRewrite) pObject;
			switch (pIndex) {
			case 1:
				lTag.setValue(pValue);
			}
		}
		if (pObject instanceof TagContent) {
			TagContent lContent = (TagContent) pObject;
			// We need to get a tag object for this.
			ID3Tag lTag = ID3Tag.getTag(lContent.getType());
			if (lTag != null) {
				switch (pIndex) {
				case 1:
					lContent.setContent(pValue);
				}
			}
		}
	}

	public List getEditedElements() {
		// Strip the Spaceholders
		ArrayList lReturnList = new ArrayList();
		Iterator lIter = mEditedElements.iterator();
		while (lIter.hasNext()) {
			Object lObject = lIter.next();
			if (lObject instanceof ID3TagRewrite) {
				lReturnList.add((ID3TagRewrite) lObject);
			}
		}
		return lReturnList;
	}

}
