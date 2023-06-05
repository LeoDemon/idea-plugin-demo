package tech.demonlee.ideaplugindemo.factory;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.demonlee.ideaplugindemo.config.GlobalConfig;
import tech.demonlee.ideaplugindemo.ui.SettingUI;

import javax.swing.*;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * @author Demon.Lee
 * @date 2023-06-05 14:28
 */
public class SettingFactory implements SearchableConfigurable {

    private final SettingUI settingUI = new SettingUI();

    @Override
    public @NotNull @NonNls String getId() {
        return "read-test-id";
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "ReadSettingConfig";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return settingUI.getComponent();
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        String url = settingUI.getUrlTextField().getText();
        try {
            File file = new File(url);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(0);

            byte[] bytes = new byte[1024 * 1024];
            int readSize = randomAccessFile.read(bytes);

            byte[] copy = new byte[readSize];
            System.arraycopy(bytes, 0, copy, 0, readSize);

            String str = new String(copy, StandardCharsets.UTF_8);

            GlobalConfig.readUI.getTextContent().setText(str);
        } catch (Exception ex) {
            System.out.println("ignore error: " + ex.getMessage());
            throw new ConfigurationException(ex.getMessage(), ex, "Tools - Read Config");
        }
    }
}
