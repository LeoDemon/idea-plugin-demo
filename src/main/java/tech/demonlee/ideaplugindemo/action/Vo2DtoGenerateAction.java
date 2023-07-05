package tech.demonlee.ideaplugindemo.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import tech.demonlee.ideaplugindemo.service.CodeGenerate;
import tech.demonlee.ideaplugindemo.service.impl.Vo2DtoService;

/**
 * @author Demon.Lee
 * @date 2023-07-04 16:44
 */
public class Vo2DtoGenerateAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        CodeGenerate codeGenerate = new Vo2DtoService(e.getProject(), psiFile, e.getDataContext());
        try {
            codeGenerate.doGenerate();
        } catch (Exception ex) {
            ex.printStackTrace();
            Messages.showErrorDialog(e.getProject(),
                    "Notes: 先复制对象后，例如：A a，再光标放到需要织入的对象上，例如：B b！", "错误提示");
        }
    }
}
