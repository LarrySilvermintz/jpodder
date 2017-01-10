package com.jpodder.ui.swt.feeds.manager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer </a>
 * @version 1.0
 */

import com.jpodder.data.feeds.stats.XFeedEventHistory;
import com.jpodder.util.Messages;

/**
 * A history panel UI which presents feed event history. 
 * 
 */
public class FeedHistoryPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1547975873271998L;
	protected JComboBox mHistoryCombo;
    protected JTextField mEventText;
    protected JTextField mDateText = new JTextField();
    private static final int PANEL_HEIGHT = 35;
    private static final int PANEL_WIDTH = 500;
    
    public JTextField getEventField(){ return mEventText;}
    public JTextField getDateField(){ return mDateText;}
    public JComboBox getHistoryCombo(){ return mHistoryCombo;}
    
    public FeedHistoryPanel(){
        
        JLabel historyLabel = new JLabel(Messages
                .getString("feedInfoview.events"));
        historyLabel.setPreferredSize(new Dimension(150, 20));

        mHistoryCombo = new JComboBox();
        mHistoryCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                Object item = e.getItem();
                if (item instanceof XFeedEventHistory.XFeedHistoryEvent) {
                    XFeedEventHistory.XFeedHistoryEvent event = (XFeedEventHistory.XFeedHistoryEvent) item;
                    mEventText.setText(event.getFormatedFlags());
                    mDateText.setText(event.getDate());
                }
            }
        });
        JPanel historySelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        historySelectPanel.setMaximumSize(new Dimension(PANEL_WIDTH,
                PANEL_HEIGHT));
        historySelectPanel.setAlignmentX(new Float(0.0).floatValue());
        historySelectPanel.add(historyLabel);
        historySelectPanel.add(mHistoryCombo);

        JLabel eventLabel = new JLabel(Messages.getString("feedInfoview.type"));
        eventLabel.setPreferredSize(new Dimension(150, 20));
        mEventText = new JTextField();
        mEventText.setColumns(30);
        mEventText.setEditable(false);
        JPanel historyEventPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        historyEventPanel.setAlignmentX(new Float(0.0).floatValue());
        historyEventPanel.setMaximumSize(new Dimension(PANEL_WIDTH,
                PANEL_HEIGHT));
        historyEventPanel.add(eventLabel);
        historyEventPanel.add(mEventText);

        JLabel dateLabel = new JLabel(Messages.getString("feedInfoview.date"));
        dateLabel.setPreferredSize(new Dimension(150, 20));
        mDateText.setColumns(30);
        mDateText.setEditable(false);
        JPanel historyDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        historyDatePanel.setAlignmentX(new Float(0.0).floatValue());
        historyDatePanel
                .setMaximumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        historyDatePanel.add(dateLabel);
        historyDatePanel.add(mDateText);

        setAlignmentX(new Float(0.0).floatValue());
        setLayout(new BoxLayout(this,
                BoxLayout.PAGE_AXIS));
        setBorder(new TitledBorder(BorderFactory
                .createEtchedBorder(Color.white, new Color(148, 145, 140)),
                Messages.getString("feedInfoview.history")));
        add(historySelectPanel);
        add(historyDatePanel);
        add(historyEventPanel);    
    }
    
}