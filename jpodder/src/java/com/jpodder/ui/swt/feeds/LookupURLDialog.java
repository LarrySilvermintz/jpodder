package com.jpodder.ui.swt.feeds;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.jpodder.util.Messages;

/**
 * A Dialog used to edit feeds. Shows a dialog, which allows the user to add
 * a feed.
 */
public class LookupURLDialog extends JDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8684900801646702856L;

	JButton okButton = new JButton(Messages.getString("general.ok"));

    JButton cancelButton = new JButton(Messages.getString("general.cancel"));

    short option = JOptionPane.CANCEL_OPTION;

    public Object selectedItem;

    /**
     * Constructor.
     * 
     * @param feeds
     */
    public LookupURLDialog(Vector feeds) {

        setTitle(Messages.getString("feedcontrol.lookupURL"));
        setResizable(false);

        setSize(new Dimension(350, 170));
        this.setModal(true);

        JPanel selectPanel = new JPanel();
        selectPanel.setLayout(new BoxLayout(selectPanel,
                BoxLayout.PAGE_AXIS));
        selectPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // SpringLayout sl = new SpringLayout();
        // selectPanel.setLayout(sl);
        JTextArea lookupExplain = new JTextArea();
        lookupExplain.setBackground(UIManager.getColor("menu"));
        lookupExplain.setFont(new java.awt.Font("Microsoft Sans Serif", 0,
                12));
        lookupExplain.setForeground(SystemColor.desktop);
        // lookupExplain.setMaximumSize(new Dimension(612, 50));
        // lookupExplain.setMinimumSize(new Dimension(612, 50));
        // lookupExplain.setPreferredSize(new Dimension(500, 50));
        lookupExplain.setEditable(false);
        lookupExplain.setMargin(new Insets(0, 0, 0, 0));
        lookupExplain.setText(Messages
                .getString("feedcontrol.lookupURLHelp"));
        lookupExplain.setLineWrap(true);
        lookupExplain.setWrapStyleWord(true);

        JComboBox combo = new JComboBox(feeds);
        combo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                selectedItem = e.getItem();
            }
        });
        selectedItem = combo.getSelectedItem();
        combo.setSize(250, 100);

        selectPanel.add(lookupExplain);
        selectPanel.add(combo);
        selectPanel.add(Box.createRigidArea(new Dimension(350, 40)));

        // SpringUtilities.makeCompactGrid(selectPanel, 2, 1, 6, 6, 5, 5);
        JPanel buttonPanel = new JPanel();

        okButton.addActionListener(new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = -8316347506993777560L;

			public void actionPerformed(ActionEvent e) {
                option = JOptionPane.OK_OPTION;
                hide();
            }
        });

        cancelButton.addActionListener(new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 6185207289645031882L;

			public void actionPerformed(ActionEvent e) {
                option = JOptionPane.CANCEL_OPTION;
                hide();
            }
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(selectPanel, BorderLayout.CENTER);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }
}