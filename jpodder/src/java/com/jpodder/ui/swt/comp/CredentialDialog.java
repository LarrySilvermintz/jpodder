package com.jpodder.ui.swt.comp;

/**
 * CB TODO, Migrate to SWT.
 */
public class CredentialDialog {

//    static int result = -1;
//
//    private static JTextField nameField;
//
//    private static JPasswordField passField;
//
//    public static JDialog dialog = new JDialog();

    public static int showDialog() {
        return -1;
        
//        dialog.setModal(true);
//        dialog.setTitle("Login");
//        Dimension size = new Dimension(300, 125);
//        dialog.setSize(size);
//        dialog.setLocation(Util.centerLocations(size));
//        dialog.setResizable(false);
//        JLabel nameLabel = new JLabel("User name:");
//        nameField = new JTextField();
//
//        JLabel passLabel = new JLabel("Password:");
//        passField = new JPasswordField();
//
//        JPanel loginPanel = new JPanel();
//        SpringLayout layout = new SpringLayout();
//        loginPanel.setLayout(layout);
//        loginPanel.add(nameLabel);
//        loginPanel.add(nameField);
//        loginPanel.add(passLabel);
//        loginPanel.add(passField);
//        SpringUtilities.makeCompactGrid(loginPanel, 2, 2, 6, 6, 6, 6);
//
//        JButton OKButton = new JButton("Ok");
//        JButton CancelButton = new JButton("Cancel");
//
//        OKButton.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                result = JOptionPane.OK_OPTION;
//                dialog.setVisible(false);
//            }
//        });
//
//        CancelButton.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                result = JOptionPane.CANCEL_OPTION;
//                dialog.hide();
//            }
//        });
//
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        buttonPanel.add(OKButton);
//        buttonPanel.add(CancelButton);
//
//        JPanel mainPanel = new JPanel();
//        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
//        mainPanel.add(loginPanel);
//        mainPanel.add(buttonPanel);
//        
//        dialog.getContentPane().removeAll();
//        dialog.getContentPane().add(mainPanel);
//        dialog.getRootPane().setDefaultButton(OKButton);
//        dialog.show();
//        return result;
    }
}