package tech.demonlee.ideaplugindemo.action;

import com.intellij.diff.*;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.merge.MergeRequest;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author Demon.Lee
 * @date 2023-08-14 11:00
 */
public class CodeMergeAction extends AnAction {

    private static final Logger log = Logger.getInstance(CodeMergeAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        if (project == null || editor == null) {
            log.warn("project or editor is null...");
            return;
        }

        // 获取当前打开的文件内容
        Document document = editor.getDocument();
        String currentContent = document.getText();

        // 选择另一个文件进行对比
        VirtualFile selectedFile =
                FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileDescriptor(), project, null);
        if (Objects.isNull(selectedFile)) {
            log.warn("no file is selected...");
            return;
        }

        // 读取选中文件的内容
        String selectedContent;
        try {
            selectedContent = new String(selectedFile.contentsToByteArray());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        log.warn("ok, compare now...");
        // 创建 DiffContentFactory 实例
        DiffContentFactory contentFactory = DiffContentFactory.getInstance();

        // 使用 DiffContentFactory 创建两个 DiffContent 对象
        DiffContent content1 = contentFactory.create(currentContent);
        DiffContent content2 = contentFactory.create(selectedContent);

        // 构建 DiffRequest 对象，并设置需要进行对比的内容和其他选项
        DiffRequest diffRequest = new SimpleDiffRequest("My Diff Demo", content1, content2, "Curr1", "New2");

        // show diff
        DiffManager diffManager = DiffManager.getInstance();
        diffManager.showDiff(project, diffRequest);

        var titles = List.of("Left", "Base", "Right");
        var contents = List.of(currentContent, currentContent, selectedContent);
        DiffRequestFactory factory = DiffRequestFactoryImpl.getInstance();
        MergeRequest mergeRequest = null;
        try {
            mergeRequest = factory.createMergeRequest(project, JavaFileType.INSTANCE, document,
                    contents, null, titles, mergeResult -> {
                        log.warn("mergeResult-1: " + mergeResult.name());
                        log.warn("mergeResult-2: " + mergeResult);
                    });
        } catch (InvalidDiffRequestException ex) {
            log.warn("create merge request failed: " + ex);
            return;
        }

        // show manual merge window
        diffManager.showMerge(project, mergeRequest);
    }
}
