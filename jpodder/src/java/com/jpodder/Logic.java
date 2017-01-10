package com.jpodder;

import com.jpodder.clock.Clock;
import com.jpodder.data.configuration.Configuration;
import com.jpodder.data.configuration.ConfigurationLogic;
import com.jpodder.data.download.DownloadLogic;
import com.jpodder.data.feeds.XFeedLogic;
import com.jpodder.data.feeds.list.XPersonalFeedList;
import com.jpodder.data.id3.ID3Logic;
import com.jpodder.data.language.LanguageLogic;
import com.jpodder.data.opml.OPMLLogic;
import com.jpodder.data.player.PlayerLogic;
import com.jpodder.net.NetPropertiesHandler;
import com.jpodder.net.NetTask;
import com.jpodder.plugin.PluginLogic;
import com.jpodder.remote.RPCLogic;
import com.jpodder.schedule.SchedulerLogic;
import com.jpodder.tasks.TaskLogic;
import com.jpodder.ui.swt.util.UpgradeHelper;

/**
 * This class initializes the application. The data is self-initializing through
 * their respective data handlers. It has no knowledge of any UI.
 * 
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.1
 */
public class Logic {

	FileHandler lFileHandler = new FileHandler();

	private static Logic sSelf;

	public static Logic getInstance() {
		if (sSelf == null) {
			new Logic(null); // No configuration specified.
		}
		return sSelf;
	}

	public Logic(String pConfigurationDirectory) {

		sSelf = this;

		PluginLogic.getInstance().scanPluginFolder();

		if (!FileHandler.getInstance().initialize(pConfigurationDirectory)) {
			PluginLogic.getInstance().finalize();
			System.exit(0);
		}

		ConfigurationLogic.getInstance().initialize(pConfigurationDirectory);
		// Check for upgrades to be done
		UpgradeHelper.upgrade(ConfigurationLogic.getInstance());

		NetTask.getInstance().addNetActionListener(DownloadLogic.getInstance());

		Clock.getInstance().addActionListener(DownloadLogic.getInstance());
		Clock.getInstance().addActionListener(SchedulerLogic.getInstance());

		ConfigurationLogic.getInstance().addConfigListener(lFileHandler);
		ConfigurationLogic.getInstance().addConfigListener(
				NetPropertiesHandler.getInstance());
		ConfigurationLogic.getInstance().addConfigListener(
				ID3Logic.getInstance());
		ConfigurationLogic.getInstance().addConfigListener(
				PlayerLogic.getInstance());
		ConfigurationLogic.getInstance().addConfigListener(
				OPMLLogic.getInstance());
		ConfigurationLogic.getInstance().addConfigListener(
				SchedulerLogic.getInstance());
		ConfigurationLogic.getInstance().addConfigListener(
				LanguageLogic.getInstance());
		TaskLogic.getInstance().addListener(XFeedLogic.getInstance());

		RPCLogic.getInstance(); // Initialize the RPC Listener configuration.
		Configuration.getInstance(); // Initialize the User configuration.
		XPersonalFeedList.getInstance().getFeedList().addListener(
				DownloadLogic.getInstance());

	}

	public void finalizeLogic() {
		ConfigurationLogic.getInstance().save();
		FileHandler.removeUnusedFeedFiles(XPersonalFeedList.getInstance());
		PluginLogic.getInstance().finalize();
	}

}
