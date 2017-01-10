package com.jpodder.ui.swt.theme;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.RGBColor;

import com.jpodder.FileHandler;
import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.data.configuration.IDataHandler;
import com.jpodder.html.style.Css;
import com.jpodder.html.style.StyleNotFoundException;
import com.jpodder.tasks.ITaskListener;
import com.jpodder.tasks.TaskEvent;
import com.jpodder.tasks.TaskLogic;
import com.steadystate.css.dom.CSSValueImpl;

/**
 * Theme Class. Images are stored in a Map which can be retrieved using
 * getImages(). Should have a more generic + external configuration of the
 * Theme. </br><b>Colors</b>
 * <ul>
 * <li>TABLE_ODD_BACKGROUND_COLOR</li>
 * <li>...</li>
 * </ul>
 * 
 * <b>Images</b>
 * 
 */
public class UITheme implements ITaskListener {

	Logger mLog = Logger.getLogger(getClass().getName());

	private static UITheme sSelf;

	protected ImageRepository mImageRegistry = new ImageRepository();

	public static final int IMAGE_STATUS_BAR = 0;

	public static final int IMAGE_SUNNY = 1;

	public static final int IMAGE_RAINY = 2;

	public static final int IMAGE_JPODDER = 3;

	public static final int IMAGE_UP = 4;

	public static final int IMAGE_DOWN = 5;

	public static final int IMAGE_LEFT = 6;

	public static final int IMAGE_RIGHT = 7;

	public static final int IMAGE_PREVIEW = 8;

	public static final int IMAGE_DIR_BULLIT = 9;

	public static final int IMAGE_DIR_FOLDER_OPEN = 10;

	public static final int IMAGE_DIR_FOLDER_CLOSE = 11;

	public static final int IMAGE_ACTION_BAR = 12;

	public static final int IMAGE_SPLASH = 13;

	public static final int IMAGE_DIR_PODCAST = 14;

	public static final int IMAGE_DIR_PODCAST_SUBSCRIBED = 15;

	public static final int IMAGE_TRAY_IDLE = 16;

	public static final int IMAGE_TRAY_DISCONNECT = 17;

	public static final int IMAGE_TRAY_DOWNLOADING = 18;

	String iconPath = FileHandler.sImageDirectory.getPath() + File.separator;

	String lTheme32 = "themes" + File.separator + "the-error" + File.separator
			+ "32" + File.separator;

	String lTheme16 = "themes" + File.separator + "the-error" + File.separator
			+ "16" + File.separator;

	String lTheme = "themes" + File.separator + "the-error" + File.separator;

	// CB TODO, Use CSS parser to get colors.
	// See resource/config/style.css


	
	// CSS #F5F8F1
	public Color TABLE_ODD_BACKGROUND_COLOR;
	// CSS #0039A3
	public Color FEED_TITLE_COLOR;
	// CSS #dfe3ed
	public Color SELECTION_BACKGROUND_COLOR;

	// CSS
	public Color SELECTION_FORGROUND_COLOR;

	// CSS #F9A004
	public Color GENERIC_BACKGROUND_COLOR;

	public Color INCOMPLETE_DOWNLOAD_COLOR;

	// CSS #f5aef9
	public Color SCHEDULED_COLOR;

	// CSS #FDDA4
	public Color LOCAL_ODD_BACKGROUND_COLOR;

	// CSS
	public Color LOCAL_EVEN_BACKGROUND_COLOR;

	// CSS (Pure Green)
	public Color APP_FONT_COLOR;

	// CSS (White)
	public Color APP_FONT2_COLOR;

	// CSS 
	public Color APP_FONT3_COLOR;

	// CSS (Black)
	public Color APP_BACKGROUND_COLOR;

	// CSS (Light olive)
	public Color APP_BACKGROUND2_COLOR;
	
		
	public Font APP_FONT1 = Display.getDefault().getSystemFont();
	
	
	//	
	// ******************* THEME 2.
	// -----------------------------
	// // CSS (Pure Green)
	// public final Color APP_FONT_COLOR = new Color(Display
	// .getDefault(), 0, 255, 0);
	//
	// // CSS (White)
	// public final Color APP_FONT2_COLOR = new Color(Display
	// .getDefault(), 0, 0, 0);
	//	
	// // CSS (Black)
	// public final Color APP_BACKGROUND_COLOR = new Color(Display
	// .getDefault(), 255, 255, 255);
	//	
	// // CSS (Light olive)
	// public final Color APP_BACKGROUND2_COLOR = new Color(Display
	// .getDefault(), 231, 230, 164);
	//	
	// // CSS #F5F8F1
	// public final Color TABLE_ODD_BACKGROUND_COLOR = new Color(Display
	// .getDefault(), 244, 243, 215);
	//
	// // CSS #0039A3 (Kobalt blue)
	// public final Color FEED_TITLE_COLOR = new Color(Display.getDefault(), 0,
	// 0, 0);
	//
	// // CSS #dfe3ed
	// public final Color SELECTION_BACKGROUND_COLOR = new Color(Display
	// .getDefault(), 255, 255, 168);
	//
	// // CSS #dfe3ed
	// public final Color SELECTION_FORGROUND_COLOR = new Color(Display
	// .getDefault(), 244, 243, 215);
	//	
	// // CSS #F9A004
	// public final Color GENERIC_BACKGROUND_COLOR = new Color(Display
	// .getDefault(), 128, 64, 0);
	//
	// public final Color INCOMPLETE_DOWNLOAD_COLOR = new Color(Display
	// .getDefault(), 251, 251, 192);
	//
	// // CSS #f5aef9
	// public final Color SCHEDULED_COLOR = new Color(Display.getDefault(), 245,
	// 174, 249);
	//
	// // CSS #FDDA4
	// public final Color LOCAL_ODD_BACKGROUND_COLOR = new Color(Display
	// .getDefault(), 207, 231, 231);
	//
	// // CSS
	// public final Color LOCAL_EVEN_BACKGROUND_COLOR = new Color(Display
	// .getDefault(), 231, 230, 228);

	protected ColorRepository mColorRepository;

	protected UIComponentLogic mUIComponentLogic;

	protected List<IUIThemeView> mThemeViews = new ArrayList<IUIThemeView>();

	public static UITheme getInstance() {
		if (sSelf == null) {
			sSelf = new UITheme();
		}
		return sSelf;
	}

	/**
	 * Get the logic which maintains the component bindings. When adding a
	 * styled UIComponent, we also resolve the corresponding resources. If the
	 * requested resources don't exist we create them.
	 * 
	 * @return
	 */
	public UIComponentLogic getComponentLogic() {
		if (mUIComponentLogic == null) {
			mUIComponentLogic = new UIComponentLogic();
		}
		return mUIComponentLogic;
	}

	public UITheme() {

		TaskLogic.getInstance().addListener(this);
		Css lStylesheet = Css.getInstance();
	}
	

	public void initializeLegacy() {
		// Build Image registry.
		mImageRegistry.put(IMAGE_JPODDER, iconPath + lTheme32 + "jpodder.png");
		mImageRegistry.put(IMAGE_SUNNY, iconPath + lTheme16 + "sunny2.png");
		mImageRegistry.put(IMAGE_RAINY, iconPath + lTheme16 + "rainy.png");
		mImageRegistry.put(IMAGE_PREVIEW, iconPath + lTheme16 + "preview.png");
		mImageRegistry.put(IMAGE_UP, iconPath + lTheme16 + "up.png");
		mImageRegistry.put(IMAGE_DOWN, iconPath + lTheme16 + "down.png");
		mImageRegistry.put(IMAGE_RIGHT, iconPath + lTheme16 + "right.png");
		mImageRegistry.put(IMAGE_LEFT, iconPath + lTheme16 + "left.png");
		// imageRegistry.put(IMAGE_PREVIEW, iconPath + lTheme16 +
		// "preview.png");
		mImageRegistry.put(IMAGE_TRAY_IDLE, iconPath + "systray-idle.png");
		// mImageRegistry.put(IMAGE_TRAY_DISCONNECT, iconPath +
		// "systray-disconnect.png");
		mImageRegistry.put(IMAGE_TRAY_DOWNLOADING, iconPath
				+ "systray-download.png");
		mImageRegistry.put(IMAGE_SPLASH, iconPath + "splash.png");

		
		TABLE_ODD_BACKGROUND_COLOR = new Color(Display
				.getDefault(), 250, 250, 237);

		FEED_TITLE_COLOR = new Color(Display.getDefault(), 0,
				57, 163);

		SELECTION_BACKGROUND_COLOR = new Color(Display
				.getDefault(), 223, 227, 237);

		SELECTION_FORGROUND_COLOR = new Color(Display
				.getDefault(), 0, 57, 163);

		GENERIC_BACKGROUND_COLOR = new Color(Display
				.getDefault(), 249, 160, 4);

		INCOMPLETE_DOWNLOAD_COLOR = new Color(Display
				.getDefault(), 251, 251, 192);

		SCHEDULED_COLOR = new Color(Display.getDefault(), 245,
				174, 249);

		LOCAL_ODD_BACKGROUND_COLOR = new Color(Display
				.getDefault(), 253, 221, 164);

		LOCAL_EVEN_BACKGROUND_COLOR = new Color(Display
				.getDefault(), 231, 230, 228);

		APP_FONT_COLOR = new Color(Display.getDefault(), 0, 255,
				0);

		APP_FONT2_COLOR = new Color(Display.getDefault(), 0, 0,
				0);

		APP_FONT3_COLOR = new Color(Display.getDefault(),
				96, 96, 96);

		APP_BACKGROUND_COLOR = new Color(Display.getDefault(),
				255, 255, 255);

		APP_BACKGROUND2_COLOR = new Color(Display.getDefault(),
				250, 250, 237);
		
		
		// TODO, we use the Font registry from SWT to change the style of the 
		// font. 
//		APP_FONT1 = Display.getDefault().getSystemFont();
//		FontTool.setData(APP_FONT1.getFontData(),SWT.ITALIC, null);
		
		
		mColorRepository = new ColorRepository();
	}

	public void addUIThemeView(IUIThemeView pView) {
		if (!mThemeViews.contains(pView)) {
			mThemeViews.add(pView);
			getComponentLogic().addBinders(pView.getBinders());
		}
	}

	public void removeUIThemeView(IUIThemeView pView) {
		if (!mThemeViews.contains(pView)) {
			mThemeViews.add(pView);
		}
	}

	/**
	 * @return
	 */
	public ColorRepository getColors() {
		return mColorRepository;
	}

	public ImageRepository getImages() {
		return mImageRegistry;
	}

	/**
	 * An Image Library.
	 */
	public class ImageRepository {

		HashMap<String, Image> lImages = new HashMap<String, Image>();

		public ImageRepository() {
		}

		public void put(int pIndex, String pImagePath) {
			Image lImage = null;
			try {
				lImage = new Image(Display.getDefault(), pImagePath);
			} catch (SWTException se) {
				// CB TODO, some .png images fail to load (preview & up.png)???
				mLog.warn(se.getMessage() + pImagePath);
			}
			lImages.put(new Integer(pIndex).toString(), lImage);
		}

		public Image get(int pIndex) {
			return (Image) lImages.get(new Integer(pIndex).toString());
		}

		public void dispose() {
			Iterator lIter = lImages.values().iterator();
			while (lIter.hasNext()) {
				Image lImage = (Image) lIter.next();
				if (lImage != null) {
					lImage.dispose();
				}
				lIter.remove();
			}
		}

		public void put(IUIResource pResource) {

		}

		public IUIResource get(String pID) {
			return null;
		}
	}

	public void dispose() {
		getImages().dispose();

		// CB These are static. A bit difficult to handle with UI restarts.
		// TABLE_ODD_BACKGROUND_COLOR.dispose();
		// TITLE_COLOR.dispose();
		// INCOMPLETE_DOWNLOAD_COLOR.dispose();
		// SCHEDULED_COLOR.dispose();
		// LOCAL_ODD_BACKGROUND_COLOR.dispose();
		// LOCAL_EVEN_BACKGROUND_COLOR.dispose();
		APP_FONT_COLOR.dispose();
	}

	class ColorRepository implements IUIResourceRepository {

		HashMap<String, Color> mColorRepository = new HashMap<String, Color>();

		public ColorRepository() {
		}

		public void dispose() {
			Iterator<Color> lIt = mColorRepository.values().iterator();
			while (lIt.hasNext()) {
				lIt.next().dispose();
			}
		}

		public void put(String pID, Color pColor) {
			mColorRepository.put(pID, pColor);
		}

		public Color get(String pID) {
			if (pID != null && mColorRepository.containsKey(pID)) {
				return mColorRepository.get(pID);
			}
			throw new IllegalArgumentException(
					"Non existing resource requested");
		}

		public boolean has(Color pColor) {
			if (mColorRepository.containsValue(pColor)) {
				return true;
			} else {
				return false;
			}
		}

		public String get(Color pColor) {
			Iterator lIt = mColorRepository.entrySet().iterator();
			for (int i = 0; lIt.hasNext(); i++) {
				Entry lEntry = (Entry) lIt.next();
				if (lEntry.getValue().equals(pColor)) {
					return (String) lEntry.getKey();
				}
			}
			return null;
		}
	}

	public class UIComponentLogic {

		List<IUIComponentBinder> mBinderList = new ArrayList<IUIComponentBinder>();

		public void addBinders(IUIComponentBinder[] pBinders) {
			for (int i = 0; i < pBinders.length; i++) {
				if (pBinders[i] != null) {
					addBinder(pBinders[i]);
				}
			}
		}

		public void addBinder(IUIComponentBinder pBinder) {
			mBinderList.add(pBinder);
			// When adding a binder, we try to build resources.
			resolveResources(pBinder);
		}

		public void resolveAllBinders() {
			IUIComponentBinder[] pBinders = new IUIComponentBinder[mBinderList
					.size()];
			mBinderList.toArray(pBinders);
			for (int i = 0; i < pBinders.length; i++) {
				if (pBinders[i] != null) {
					addBinder(pBinders[i]);
				}
			}
		}

		/**
		 * The binder associated style is retrieved and the resources for this
		 * style are created.
		 * 
		 * @param pBinder
		 */
		private void resolveResources(IUIComponentBinder pBinder) {
			try {
				CSSStyleRule lRule = Css.getInstance().getStyleRule(
						pBinder.getID());
				CSSStyleDeclaration lStyle = lRule.getStyle();
				// Get the font for this style.

				for (int p = 0; p < lStyle.getLength(); p++) {
					String lProperty = lStyle.item(p);

					if (lProperty.equals("background")) {
						String lID = newColor(lStyle, lProperty);
						pBinder.setBackgroundColorID(lID);
					}
					if (lProperty.equals("color")) {
						String lID = newColor(lStyle, lProperty);
						pBinder.setForgroundColorID(lID);
					}

					mLog.debug("Property for requested style found: "
							+ lProperty);

				}

			} catch (StyleNotFoundException e) {
				mLog.debug("getBindingResources(), Component style not found"
						+ e.getMessage());
			}
		}

		/**
		 * Return the ID of the color, in the color repository.
		 * 
		 * @param lStyle
		 * @param pProperty
		 * @return
		 */
		private String newColor(CSSStyleDeclaration lStyle, String pProperty) {
			CSSValue lValue = lStyle.getPropertyCSSValue(pProperty);
			int lType = lValue.getCssValueType();
			switch (lType) {
			case 1: // it's a Color
				CSSValueImpl lImpl = (CSSValueImpl) lValue;
				RGBColor lRGB = lImpl.getRGBColorValue();
				String lID;
				Color lColor = newColor(lRGB.getRed(), lRGB.getGreen(), lRGB
						.getBlue());
				if (getColors().has(lColor)) {
					lID = getColors().get(lColor);
				} else {
					lID = getNewID();
					getColors().put(lID, lColor);
				}
				return lID;
			}
			return null; // TODO, throw exception as the type should have
			// been color for the
			// requested property, this is a programmatic flaw.
		}

		private Color newColor(CSSPrimitiveValue r, CSSPrimitiveValue g,
				CSSPrimitiveValue b) {

			Float lRed = new Float(((CSSValueImpl) r).getFloatValue((short) 0));
			Float lGreen = new Float(((CSSValueImpl) g)
					.getFloatValue((short) 0));
			Float lBlue = new Float(((CSSValueImpl) b).getFloatValue((short) 0));
			return new Color(Display.getDefault(), lRed.intValue(), lGreen
					.intValue(), lBlue.intValue());
		};

		public String getNewID() {
			return new Long(System.currentTimeMillis()).toString();
		}
	}

	public void taskCompleted(TaskEvent e) {
		Object lSrc = e.getSource();
		if (lSrc instanceof IDataHandler) {
			if (((IDataHandler) lSrc).getIndex() == ConfigurationLogic.THEME_INDEX) {
				getComponentLogic().resolveAllBinders();
			}
		}
	}

	public void taskAborted(TaskEvent e) {
	}

	public void taskFailed(TaskEvent e) {
	}

}

// CB TODO, MIGRATE THESE, IF WE STILL NEED THEME.

// mArtwork[IMAGE_STATUS_BAR] = new
// ImageIcon(FileHandler.sImageDirectory,
// lTheme32 + "actionbar.png");

// mArtwork[IMAGE_DIR_BULLIT] = new
// ImageIcon(FileHandler.sImageDirectory,
// lTheme16 + "bullit.png");
// mArtwork[IMAGE_DIR_FOLDER_OPEN] = new ImageIcon(
// FileHandler.sImageDirectory, lTheme16 + "folder-open.png");
// mArtwork[IMAGE_DIR_FOLDER_CLOSE] = new ImageIcon(
// FileHandler.sImageDirectory, lTheme16 + "folder-closed.png");
// mArtwork[IMAGE_DIR_PODCAST] = new ImageIcon(
// FileHandler.sImageDirectory, lTheme16 + "podcast.png");
// mArtwork[IMAGE_DIR_PODCAST_SUBSCRIBED] = new ImageIcon(
// FileHandler.sImageDirectory, lTheme16 + "podcast-checked.png");
// mArtwork[IMAGE_ACTION_BAR] = new
// ImageIcon(FileHandler.sImageDirectory,
// lTheme + "action.png");
// mArtwork[IMAGE_SPLASH] = new ImageIcon(FileHandler.sImageDirectory,
// lTheme + "splash.png");

