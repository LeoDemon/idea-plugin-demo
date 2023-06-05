package tech.demonlee.ideaplugindemo.ui;

import javax.swing.*;

/**
 * @author Demon.Lee
 * @date 2023-06-05 13:57
 */
public class SettingUI {
    private JPanel mainPanel;
    private JPanel settingPanel;
    private JLabel urlLabel;
    private JTextField urlTextField;
    private JButton urlBtn;

    public SettingUI() {
    }

    public JComponent getComponent() {
        return mainPanel;
    }

    public JTextField getUrlTextField() {
        return urlTextField;
    }
}
