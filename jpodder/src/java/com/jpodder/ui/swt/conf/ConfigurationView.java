package com.jpodder.ui.swt.conf;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;

import com.jpodder.ui.swt.IView;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.conf.panel.ConnectionPanel;
import com.jpodder.ui.swt.conf.panel.DownloadPanel;
import com.jpodder.ui.swt.conf.panel.IConfigurationPanel;
import com.jpodder.ui.swt.conf.panel.ID3Panel;
import com.jpodder.ui.swt.conf.panel.LanguagePanel;
import com.jpodder.ui.swt.conf.panel.MediaPanel;
import com.jpodder.ui.swt.conf.panel.MimePanel;
import com.jpodder.ui.swt.conf.panel.OPMLSyncPanel;
import com.jpodder.ui.swt.conf.panel.SchedulerPanel;
import com.jpodder.util.Logger;
import com.jpodder.util.Messages;

/**
 * A JPanel class which holds all the UI definitions for the application
 * propeterties.
 */
public class ConfigurationView implements IView {

	private Logger mLog = Logger.getLogger(getClass().getName());

	public static final Hashtable propertyComponents = new Hashtable();

	public static final int DEFAULT_PANEL_WIDTH = 650;

	protected Button propsSaveButton;

	protected ConfigurationTree mConfTree;

	protected Composite mView;

	protected Composite mPanels;

	protected static ConfigurationNode mRootNode = null;

	protected ConfigurationNode mPluginNode;

	public String PROPS_CAT_SETTINGS;
	public String PROPS_CAT_DOWNLOAD;
	public String PROPS_CAT_CONNECTION;
	public String PROPS_CAT_SCHEDULING;
	public String PROPS_CAT_MEDIA;
	public String PROPS_CAT_ID3;
	public String PROPS_CAT_MIME;
	public String PROPS_CAT_PRODUCTION;
	public String PROPS_CAT_LANGUAGE;
	public String PROPS_CAT_LOG;
	public String PROPS_CAT_ONECLICK;
	public String PROPS_CAT_SYNC;
	public String PROPS_CAT_PLUGIN;
	
	/**
	 * Constructor.
	 */
	public ConfigurationView() {

		PROPS_CAT_SETTINGS = Messages.getString("propertiescontrol.settings");
		PROPS_CAT_DOWNLOAD = Messages.getString("propertiescontrol.download");
		PROPS_CAT_CONNECTION = Messages
				.getString("propertiescontrol.connection");
		PROPS_CAT_SCHEDULING = Messages
				.getString("propertiescontrol.scheduling");
		PROPS_CAT_MEDIA = Messages.getString("propertiescontrol.media");
		PROPS_CAT_ID3 = Messages.getString("propertiescontrol.id3");
		PROPS_CAT_MIME = Messages.getString("propertiescontrol.mime");
		// public static final String PROPS_CAT_IMPORT = Messages
		// .getString("propertiescontrol.import");
		PROPS_CAT_PRODUCTION = Messages
				.getString("propertiescontrol.production");
		PROPS_CAT_LANGUAGE = Messages.getString("propertiescontrol.language");
		PROPS_CAT_LOG = Messages.getString("propertiescontrol.log");
		// public static final String PROPS_CAT_ADVANCED = Messages
		// .getString("propertiescontrol.advanced");
		String PROPS_CAT_ONECLICK = Messages
				.getString("propertiescontrol.oneclick");
		String PROPS_CAT_SYNC = Messages.getString("propertiescontrol.sync");

		String PROPS_CAT_PLUGIN = Messages
				.getString("propertiescontrol.plugin");

		mView = new Composite(UILauncher.getInstance().getShell(), SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		mView.setLayout(gridLayout);

		// --------- Button Group

		Group lButtonGroup = new Group(mView, SWT.SHADOW_IN);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		lButtonGroup.setLayoutData(gridData);
		RowLayout lButtonRowLayout = new RowLayout(SWT.HORIZONTAL);
		lButtonRowLayout.wrap = false;
		lButtonRowLayout.pack = false;
		lButtonGroup.setLayout(lButtonRowLayout);

		propsSaveButton = new Button(lButtonGroup, SWT.PUSH);

		// --------- Tree

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_FILL);
		gridData.horizontalSpan = 1;
		gridData.widthHint = 200;
		gridData.grabExcessVerticalSpace = true;

		mConfTree = new ConfigurationTree(mView);
		mConfTree.getTree().setLayoutData(gridData);

		mPanels = new Composite(mView, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_FILL);
		gridData.horizontalSpan = 1;
		// gridData.widthHint = 200;
		mPanels.setLayoutData(gridData);
		mPanels.setLayout(new FillLayout());

		mRootNode = new ConfigurationNode(PROPS_CAT_SETTINGS, null);

		addChildNode(mRootNode, new ConfigurationNode(PROPS_CAT_DOWNLOAD,
				new DownloadPanel()));
		addChildNode(mRootNode, new ConfigurationNode(PROPS_CAT_CONNECTION,
				new ConnectionPanel()));
		addChildNode(mRootNode, new ConfigurationNode(PROPS_CAT_SCHEDULING,
				new SchedulerPanel()));
		ConfigurationNode mediaNode = new ConfigurationNode(PROPS_CAT_MEDIA,
				new MediaPanel());
		addChildNode(mRootNode, mediaNode);
		addChildNode(mediaNode, new ConfigurationNode(PROPS_CAT_MIME,
				new MimePanel()));
		addChildNode(mediaNode, new ConfigurationNode(PROPS_CAT_ID3,
				new ID3Panel()));
		addChildNode(mRootNode, new ConfigurationNode(PROPS_CAT_LANGUAGE,
				new LanguagePanel()));
		addChildNode(mRootNode, new ConfigurationNode(PROPS_CAT_SYNC,
				new OPMLSyncPanel()));
		mPluginNode = new ConfigurationNode(PROPS_CAT_PLUGIN, null);
		addChildNode(mRootNode, mPluginNode);

		mConfTree.getViewer().setInput(getRootNode());
	}

	public Composite getView() {
		return mView;
	}

	public void addPluginPanel(String pKey, IConfigurationPanel pPanel) {
		if (com.jpodder.util.Debug.WITH_DEV_DEBUG) {
			mLog.devDebug("addPanel(), key: " + pKey + ", panel: " + pPanel);
		}
		ConfigurationNode lChild = new ConfigurationNode(pKey, pPanel);
		addChildNode(mPluginNode, lChild);

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				mConfTree.mViewer.setInput(getRootNode());
			}
		});
	}

	/**
	 * Recursive function, parsing through the tree and returning the panel
	 * assiociated with a key.
	 * 
	 * @param node
	 * @param key
	 *            The key for which we should get the property node.
	 * @return Object A propety panel.
	 */
	public Object getPanel(ConfigurationNode node, final String key) {
		if (node.mNodeName.equals(key)) {
			if (node.panel != null) {
				return node.panel;
			} else {
				return null;
			}
		} else {
			if (node.mChildren != null) {
				Iterator it = node.mChildren.keySet().iterator();
				while (it.hasNext()) {
					String childKey = (String) it.next();
					ConfigurationNode childNode = (ConfigurationNode) node.mChildren
							.get(childKey);
					Object panel = getPanel(childNode, key);
					if (panel != null)
						return panel;
				}
			}
		}
		return null;
	}

	/**
	 * Add a child to a node.
	 * 
	 * @param parent
	 * 
	 * @param key
	 * @param panel
	 */
	private void addChildNode(ConfigurationNode parent, ConfigurationNode pChild) {
		if (parent != null) {
			if (parent.mChildren == null) {
				parent.mChildren = new TreeMap();
			}
			parent.mChildren.put(pChild.mNodeName, pChild);
		}
	}

	/**
	 * @return Returns the rootNode.
	 */
	public static ConfigurationNode getRootNode() {
		return mRootNode;
	}

	public boolean isStatic() {
		return true;
	}

	public void setStatic(boolean pStatic) {
		// This view is always re-generated..
	}
}