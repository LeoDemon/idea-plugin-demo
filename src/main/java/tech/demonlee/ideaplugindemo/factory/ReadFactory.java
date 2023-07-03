package tech.demonlee.ideaplugindemo.factory;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import tech.demonlee.ideaplugindemo.config.GlobalConfig;
import tech.demonlee.ideaplugindemo.ui.ReadUI;

/**
 * @author Demon.Lee
 * @date 2023-06-05 14:17
 */
public class ReadFactory implements ToolWindowFactory {

    private final ReadUI readUI = new ReadUI();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(readUI.getComponent(), "", false);
        toolWindow.getContentManager().addContent(content);
        GlobalConfig.readUI = readUI;
    }
}
