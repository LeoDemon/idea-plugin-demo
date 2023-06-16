package tech.demonlee.ideaplugindemo;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * @author Demon.Lee
 * @date 2023-06-16 13:51
 */
public class MyProjectManagerListener implements ProjectManagerListener {

    @Override
    public void projectClosed(@NotNull Project project) {
        Messages.showInfoMessage("Project 关闭了......", "Project Closed");
    }

    @Override
    public void projectOpened(@NotNull Project project) {
        Messages.showInfoMessage("Project 打开了......", "Project Opened");
    }
}
