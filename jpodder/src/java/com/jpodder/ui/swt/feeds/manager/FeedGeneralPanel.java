package com.jpodder.ui.swt.feeds.manager;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.jpodder.FileHandler;
import com.jpodder.data.feeds.IXPersonalFeed;
import com.jpodder.data.feeds.list.XPersonalFeedList;
import com.jpodder.ui.swt.UILauncher;
import com.jpodder.ui.swt.theme.UITheme;
import com.jpodder.util.Messages;
import com.jpodder.util.TokenHandler;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.
 */
public class FeedGeneralPanel extends Composite {

    Logger mLog = Logger.getLogger(getClass().getName());

    protected Button subscriptionCheckBox;
    protected Spinner maximumSpinner;
    protected Text mUrlText;
    protected Text mRedirectUrlText;
    protected Text mTitleText;
    protected Text mFeedFolderText;
    protected Text mRssFileText;
    protected Label mQualityDescription;
    protected Label mQualityImageLabel;
    protected Button mFeedFolderButton;

    public FeedGeneralPanel(Composite pParent) {
        super(pParent, SWT.NONE);
        // --- Layout is a simple grid.
        GridLayout lLayout = new GridLayout();
        lLayout.numColumns = 3;
        setLayout(lLayout);

        subscriptionCheckBox = new Button(this, SWT.CHECK);
        subscriptionCheckBox.setText(Messages
                .getString("feedInfoview.subscription"));
        GridData lData = new GridData();
        lData.horizontalSpan = 3;
        lData.widthHint = 200;
        subscriptionCheckBox.setLayoutData(lData);

        Label titleLabel = new Label(this, SWT.NONE);
        titleLabel.setText(Messages.getString("feedInfoview.title"));
        
        
        mTitleText = new Text(this, SWT.SINGLE | SWT.BORDER);
//        mTitleText.setEditable(false);
        lData = new GridData(GridData.FILL_HORIZONTAL);
        lData.horizontalSpan = 2;
        lData.grabExcessHorizontalSpace = true;
        lData.widthHint = 300;
        mTitleText.setLayoutData(lData);

        Label urlLabel = new Label(this, SWT.NONE);
        urlLabel.setText(Messages.getString("feedInfoview.url"));

        mUrlText = new Text(this, SWT.SINGLE | SWT.BORDER);
        lData = new GridData(GridData.FILL_HORIZONTAL);
        lData.horizontalSpan = 2;
        lData.grabExcessHorizontalSpace = true;
        mUrlText.setLayoutData(lData);
        // urlText = new LinkField(false);

        Label lRedirectUrlLabel = new Label(this, SWT.NONE);
        lRedirectUrlLabel.setText(Messages
                .getString("feedInfoview.redirecturl"));

        mRedirectUrlText = new Text(this, SWT.SINGLE | SWT.BORDER);
        mRedirectUrlText.setEditable(false);
        lData = new GridData(GridData.FILL_HORIZONTAL);
        lData.horizontalSpan = 2;
        lData.grabExcessHorizontalSpace = true;
        mRedirectUrlText.setLayoutData(lData);

        Label maximumLabel = new Label(this, SWT.NONE);
        maximumLabel.setText(Messages.getString("feedInfoview.maximum"));

        maximumSpinner = new Spinner(this, SWT.NONE);
        // maximumSpinner.setMaximumSize(new Dimension(200, 20));
        lData = new GridData();
        lData.horizontalSpan = 2;
        lData.grabExcessHorizontalSpace = true;
        maximumSpinner.setLayoutData(lData);

        Label feedFolderLabel = new Label(this, SWT.NONE);
        feedFolderLabel.setText(Messages.getString("feedInfoview.feedfolder"));

        mFeedFolderText = new Text(this, SWT.SINGLE | SWT.BORDER);
        lData = new GridData(GridData.FILL_HORIZONTAL);
        lData.grabExcessHorizontalSpace = true;
        lData.widthHint = 300;
        mFeedFolderText.setLayoutData(lData);

        mFeedFolderButton = new Button(this, SWT.PUSH);
        mFeedFolderButton.setText(Messages
                .getString("feedInfoview.folder.browse"));

        Label rssFileLabel = new Label(this, SWT.SINGLE);
        rssFileLabel.setText(Messages.getString("feedInfoview.rssfile"));

        mRssFileText = new Text(this, SWT.SINGLE | SWT.BORDER);
        mRssFileText.setEditable(false);
        lData = new GridData(GridData.FILL_HORIZONTAL);
        lData.horizontalSpan = 2;
        lData.grabExcessHorizontalSpace = true;
        mRssFileText.setLayoutData(lData);

        // rssFileText.setMaximumSize(new Dimension(300, 20));

        Label qualityLabel = new Label(this, SWT.NONE);
        qualityLabel.setText(Messages.getString("feedInfoview.quality"));
        lData = new GridData(GridData.FILL_HORIZONTAL);
        lData.verticalAlignment = SWT.CENTER;
        qualityLabel.setLayoutData(lData);

        mQualityDescription = new Label(this, SWT.NONE);
        lData = new GridData(GridData.FILL_HORIZONTAL);
        lData.verticalAlignment = SWT.CENTER;
        mQualityDescription.setLayoutData(lData);

        mQualityImageLabel = new Label(this, SWT.NONE);
        lData = new GridData();
        lData.heightHint = 25;
        lData.widthHint = 20;
        lData.verticalAlignment = SWT.CENTER;
        mQualityImageLabel.setLayoutData(lData);

        // Messages.getString("feedInfoview.general")
    }

    class FolderSelector implements Listener {

        IXPersonalFeed mFeed;
        Shell mShell;

        public FolderSelector(IXPersonalFeed pFeed, Shell pShell) {
            mFeed = pFeed;
            mShell = pShell;
        }

        public void handleEvent(Event event) {
            DirectoryDialog dialog = new DirectoryDialog(UILauncher.getInstance()
                    .getShell());
            dialog.setFilterPath(mFeed.getFolder()); // Windows specific
            dialog.open();
            String lPath = dialog.getFilterPath();
            String lText = dialog.getText();
            mFeed.setFolder(lPath);
            mFeedFolderText.setText(mFeed.getFolder());
        }

    }

    public void fill(IXPersonalFeed pFeed) {
        // //////////////////////////////////////////////////////
        // GENERAL
        // //////////////////////////////////////////////////////
        if (pFeed.getURL() != null)
            mUrlText.setText(pFeed.getURL().toExternalForm());
        else {
            if (mUrlText.getText().length() == 0) {
                mUrlText.setText(Messages.getString("feeddialog.inserturl"));
                mUrlText.selectAll();
            }
        }
        if (pFeed.getRedirectURL() != null) {
            mRedirectUrlText.setText(pFeed.getRedirectURL().toExternalForm());
        } else {
            mRedirectUrlText.setText(Messages.getString("feed.noredirect"));
        }

        if (pFeed.getTitle() != null && pFeed.getTitle().length() > 0)
            mTitleText.setText(pFeed.getTitle());
        else {
            mTitleText.setText(TokenHandler.RSS_ITEM_TITLE);
        }

        subscriptionCheckBox.setSelection(pFeed.getPoll());
        maximumSpinner.setSelection(new Integer(pFeed.getMaxDownloads())
                .intValue());

        if (pFeed.getFolder() != null && pFeed.getFolder().length() > 0)
            mFeedFolderText.setText(pFeed.getFolder());
        else {
            mFeedFolderText.setText(FileHandler.getPodcastFolder()
                    + File.separator + TokenHandler.RSS_ITEM_TITLE);
        }

        int quality = pFeed.getQuality();
        switch (quality) {
            case XPersonalFeedList.GOOD_QUALITY: {
                mQualityImageLabel.setImage(UITheme.getInstance().getImages().get(
                		UITheme.IMAGE_SUNNY));
            }
                break;
            case XPersonalFeedList.BAD_QUALITY: {
                mQualityImageLabel.setImage(UITheme.getInstance().getImages().get(
                		UITheme.IMAGE_RAINY));
            }
        }
        
        mQualityDescription.setText(pFeed.getQualityDescription());

        if (pFeed.getFile() != null) {
            mRssFileText.setText(pFeed.getFile().getPath());
        } else {
            mRssFileText.setText("");
        }
    }

    /**
     * Fill the dialog for ADD_MODE, with the default values.
     * 
     * @param urlString
     */
    public void fillDefault(IXPersonalFeed pFeed) {
        if (pFeed.getURL() != null)
            mUrlText.setText(pFeed.getURL().toExternalForm());
        else {
            mUrlText.setText(Messages.getString("feeddialog.inserturl"));
            mUrlText.selectAll();
        }
        mTitleText.setText(TokenHandler.RSS_ITEM_TITLE);
        mTitleText.selectAll();
        subscriptionCheckBox.setSelection(true);
        maximumSpinner.setSelection(1);
        mFeedFolderText.setText(FileHandler.getPodcastFolder() + File.separator
                + TokenHandler.RSS_ITEM_TITLE);
    }

    /**
     * Get the current UI values and update the provided feed.
     * 
     * @param pFeed
     */
    public void setFeedValues(IXPersonalFeed pFeed) {
        String urlString = mUrlText.getText();
        URL url = null;
        try {
            url = new URL(urlString);
            pFeed.setURL(url);
        } catch (java.net.MalformedURLException mue) {
            mLog.warn("");
        }
        boolean selected = subscriptionCheckBox.getSelection();
        int max = maximumSpinner.getSelection();
        String title = mTitleText.getText();
        String folder = mFeedFolderText.getText();
        pFeed.setMaxDownloads(max);
        pFeed.setSubscribed(selected);
        pFeed.setFolder(folder);
        pFeed.setPersonalTitle(title);
    }
}