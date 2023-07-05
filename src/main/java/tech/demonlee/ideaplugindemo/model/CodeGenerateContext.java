package tech.demonlee.ideaplugindemo.model;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import lombok.Getter;

/**
 * @author Demon.Lee
 * @date 2023-07-04 12:04
 */
@Getter
public class CodeGenerateContext {

    // 数据上下文
    private DataContext dataContext;
    private Project project;
    private PsiFile psiFile;
    private PsiElement psiElement;
    // 文档：文本文件内容加载到内存中的表示
    private Document document;
    // 文本编辑器
    private Editor editor;
    private CharSequence editorText;
    // 当前位置（光标）距离 (0,0) 的偏移量
    private Integer offset;
    // 起始位置：当前行（光标所在行）所在的起始位置
    private Integer startOffset;
    // 行号：光标所在行
    private Integer lineNum;

    public CodeGenerateContext(Project project, PsiFile psiFile, DataContext dataContext) {
        Editor editor = dataContext.getData(CommonDataKeys.EDITOR);
        assert null != editor;
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        Document document = editor.getDocument();
        int offset = editor.getCaretModel().getOffset();
        int lineNum = document.getLineNumber(offset);
        int startOffset = document.getLineStartOffset(lineNum);

        this.dataContext = dataContext;
        this.project = project;
        this.psiFile = psiFile;
        this.psiElement = psiElement;
        this.document = document;
        this.editor = editor;
        this.editorText = document.getCharsSequence();
        this.offset = offset;
        this.startOffset = startOffset;
        this.lineNum = lineNum;
    }
}
