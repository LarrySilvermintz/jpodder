package com.jpodder.ui.swt;

import org.apache.log4j.Logger;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;

import com.jpodder.ui.swt.bind.AbstractBinder;

/**
 * Holds drag and drop sources
 */
public class UIDnD {

	static Logger sLog = Logger.getLogger(UIDnD.class.getName());
	
	public class DnDBinder extends AbstractBinder{
		public DnDBinder(Object pComponent) {
			super(pComponent);
		}
	}
	
	public UIDnD(){
		
	}
	
	public static void resolveControl(){
	}
	
	public static void addSource(Control pControl, int pOperations) {

			
		final DnDBinder lBinder = new UIDnD().new DnDBinder(pControl);
		DragSource source = new DragSource(pControl, pOperations);
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };

		source.setTransfer(types);

		source.addDragListener(new DragSourceListener() {

			public void dragStart(DragSourceEvent event) {
				sLog.info("drag initiated");

				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					 event.data = lBinder.save();
				}
			}

			/**
			 * event.data has to be set.
			 */
			public void dragSetData(DragSourceEvent event) {
				sLog.info("drag set data");
				// Provide the data of the requested type.
				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					event.data = lBinder.save().toString();
				}
			}

			public void dragFinished(DragSourceEvent event) {
				sLog.info("drag finished");
			}
		});
	}

	public static void addTarget(Control pControl, int pOperations) {
		
		DropTarget target = new DropTarget(pControl, pOperations);

		// Receive data in Text or File format
		final TextTransfer textTransfer = TextTransfer.getInstance();
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		Transfer[] types = new Transfer[] { fileTransfer, textTransfer };
		target.setTransfer(types);

		target.addDropListener(new DropTargetListener() {
			public void dragEnter(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail = DND.DROP_COPY;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
				// will accept text but prefer to have files dropped
				for (int i = 0; i < event.dataTypes.length; i++) {
					if (fileTransfer.isSupportedType(event.dataTypes[i])) {
						event.currentDataType = event.dataTypes[i];
						// files should only be copied
						if (event.detail != DND.DROP_COPY) {
							event.detail = DND.DROP_NONE;
						}
						break;
					}
				}
			}

			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
				if (textTransfer.isSupportedType(event.currentDataType)) {
					// NOTE: on unsupported platforms this will return null
					Object o = textTransfer.nativeToJava(event.currentDataType);
					String t = (String) o;
				}
			}

			public void dragOperationChanged(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail = DND.DROP_COPY;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
				// allow text to be moved but files should only be copied
				if (fileTransfer.isSupportedType(event.currentDataType)) {
					if (event.detail != DND.DROP_COPY) {
						event.detail = DND.DROP_NONE;
					}
				}
			}

			public void dragLeave(DropTargetEvent event) {
			
			}

			public void dropAccept(DropTargetEvent event) {
			}

			public void drop(DropTargetEvent event) {
				if (textTransfer.isSupportedType(event.currentDataType)) {
					String text = (String) event.data;
					sLog.info("Dropped: data:" + text);
				}
				if (fileTransfer.isSupportedType(event.currentDataType)) {
					String[] files = (String[]) event.data;
				}
			}
		});

	}
}
