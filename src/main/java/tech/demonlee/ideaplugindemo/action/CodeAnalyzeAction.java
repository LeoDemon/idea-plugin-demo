package tech.demonlee.ideaplugindemo.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;
import tech.demonlee.ideaplugindemo.model.CodeFileAnalyzerResult;
import tech.demonlee.ideaplugindemo.service.CodeFileAnalyzerService;

/**
 * @author Demon.Lee
 * @date 2023-07-03 10:43
 */
public class CodeAnalyzeAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (project == null || psiFile == null) {
            Messages.showErrorDialog("No project or file selected", "Code Analyze Demo");
            return;
        }
        System.out.println("psiFile:" + psiFile.getClass().getName());
        PsiJavaFile javaFile = null;
        if (psiFile instanceof PsiJavaFile) {
            javaFile = (PsiJavaFile) psiFile;
        }

        if (javaFile == null) {
            Messages.showErrorDialog("Not java file", "Code Analyze Demo");
            return;
        }

        CodeFileAnalyzerService analyzerService = new CodeFileAnalyzerService(project, javaFile);
        CodeFileAnalyzerResult result = analyzerService.analyze();
        Messages.showMessageDialog("Code Analyze Result, " + result.display(),
                "Code Analyze Demo", Messages.getInformationIcon());
    }
}
