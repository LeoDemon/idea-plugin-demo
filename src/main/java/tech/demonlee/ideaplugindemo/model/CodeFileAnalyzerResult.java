package tech.demonlee.ideaplugindemo.model;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;

/**
 * @author Demon.Lee
 * @date 2023-07-03 10:26
 */
public class CodeFileAnalyzerResult {

    private int privateFieldCount;
    private int publicMethodCount;
    private int overrideMethodCount;
    private int offset;
    private int lineNum;
    private int startOffset;

    public CodeFileAnalyzerResult(int privateFieldCount, int publicMethodCount, int overrideMethodCount,
                                  DataContext dataContext) {
        this.privateFieldCount = privateFieldCount;
        this.publicMethodCount = publicMethodCount;
        this.overrideMethodCount = overrideMethodCount;
        Editor editor = dataContext.getData(CommonDataKeys.EDITOR);
        assert null != editor;
        Document document = editor.getDocument();
        this.offset = editor.getCaretModel().getOffset();
        this.lineNum = document.getLineNumber(offset);
        this.startOffset = document.getLineStartOffset(lineNum);
    }

    public int getPrivateFieldCount() {
        return privateFieldCount;
    }

    public int getPublicMethodCount() {
        return publicMethodCount;
    }

    public int getOverrideMethodCount() {
        return overrideMethodCount;
    }

    public String display() {
        return "私有属性：" + privateFieldCount +
                ",\n 公有方法：" + publicMethodCount +
                ",\n 重写方法：" + overrideMethodCount +
                ",\n offset：" + offset +
                ",\n lineNum: " + lineNum +
                ",\n startOffset: " + startOffset;
    }
}
