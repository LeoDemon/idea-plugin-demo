package tech.demonlee.ideaplugindemo.service.impl;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import tech.demonlee.ideaplugindemo.model.GetDtoContext;
import tech.demonlee.ideaplugindemo.model.SetDtoContext;
import tech.demonlee.ideaplugindemo.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Demon.Lee
 * @date 2023-07-04 11:51
 */
public class Vo2DtoService extends AbstractVo2DtoService {

    private int repair = 0;

    public Vo2DtoService(Project project, PsiFile psiFile, DataContext context) {
        super(project, psiFile, context);
    }

    @Override
    protected SetDtoContext getSetDtoContext() {
        repair = 0;
        PsiClass psiClass = null;
        String clazzParamName = null;

        PsiElement psiElement = generateContext.getPsiElement();

        // 鼠标定位到类
        if (psiElement instanceof PsiClass) {
            psiClass = (PsiClass) psiElement;
            System.out.println("location psiClass: " + psiClass.getName());

            // 通过光标步长递进找到属性名称
            PsiFile psiFile = generateContext.getPsiFile();
            Editor editor = generateContext.getEditor();
            int offsetStep = generateContext.getOffset() + 1;

            PsiElement elementAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
            while (null == elementAt || elementAt instanceof PsiWhiteSpace ||
                    elementAt.getText().equals(psiClass.getName())) {
                elementAt = psiFile.findElementAt(++offsetStep);
            }

            // 最终拿到属性名称
            clazzParamName = elementAt.getText();
            System.out.println("location psiClass --> clazzParamName: " + clazzParamName);
        }

        // 鼠标定位到属性
        if (psiElement instanceof PsiLocalVariable) {
            PsiLocalVariable psiLocalVariable = (PsiLocalVariable) psiElement;
            clazzParamName = psiLocalVariable.getName();
            System.out.println("location psiLocalVariable: " + clazzParamName);

            // 通过光标步长递进找到属性名称
            PsiFile psiFile = generateContext.getPsiFile();
            Editor editor = generateContext.getEditor();
            int offsetStep = generateContext.getOffset() - 1;

            PsiElement elementAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
            while (null == elementAt || elementAt.getText().equals(clazzParamName) ||
                    elementAt instanceof PsiWhiteSpace) {
                elementAt = psiFile.findElementAt(--offsetStep);
                if (elementAt instanceof PsiWhiteSpace) {
                    ++repair;
                }
            }

            String clazzName = elementAt.getText();
            System.out.println("location psiLocalVariable --> clazzName: " + clazzName);

            Project project = generateContext.getProject();
            GlobalSearchScope searchScope = GlobalSearchScope.projectScope(project);
            PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project).getClassesByName(clazzName, searchScope);
            psiClass = psiClasses[0];
            System.out.println("location psiLocalVariable search psiClass: " + psiClass.getName());

            repair += psiClass.getName().length();
        }

        Pattern setMtd = Pattern.compile(setRegex);
        // 获取类的set方法并存放起来
        List<String> paramList = new ArrayList<>();
        Map<String, String> paramMtdMap = new HashMap<>();

        List<PsiClass> psiClassLinkList = getPsiClassLinkList(psiClass);
        for (PsiClass psi : psiClassLinkList) {
            List<String> methodsList = getMethods(psi, setRegex, "set");
            for (String methodName : methodsList) {
                // 替换属性
                String param = setMtd.matcher(methodName).replaceAll("$1").toLowerCase();
                // 保存获取的属性信息
                paramMtdMap.put(param, methodName);
                paramList.add(param);
            }
        }

        return new SetDtoContext(clazzParamName, paramList, paramMtdMap);
    }

    @Override
    protected GetDtoContext getGetDtoContextByClipboard() {
        // 获取剪切板信息 【实际使用可补充一些必要的参数判断】
        String systemClipboardText = Utils.getSystemClipboardText().trim();

        System.out.println("get systemClipboardText: " + systemClipboardText);

        // 按照默认规则提取信息，例如：UserDto userDto
        String[] split = systemClipboardText.split("\\s");

        if (split.length < 2) {
            return new GetDtoContext(null, null, new HashMap<>());
        }

        String clazzName = split[0].trim();
        String clazzParam = split[1].trim();

        // 获取类
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(generateContext.getProject())
                .getClassesByName(clazzName, GlobalSearchScope.projectScope(generateContext.getProject()));
        PsiClass psiClass = psiClasses[0];
        List<PsiClass> psiClassLinkList = getPsiClassLinkList(psiClass);

        Map<String, String> paramMtdMap = new HashMap<>();
        Pattern getM = Pattern.compile(getRegex);

        for (PsiClass psi : psiClassLinkList) {
            List<String> methodsList = getMethods(psi, getRegex, "get");
            for (String methodName : methodsList) {
                String param = getM.matcher(methodName).replaceAll("$1").toLowerCase();
                paramMtdMap.put(param, methodName);
            }
        }

        return new GetDtoContext(clazzName, clazzParam, paramMtdMap);
    }

    @Override
    protected void weavingSetGetCode(SetDtoContext setDtoContext, GetDtoContext getDtoContext) {
        Application application = ApplicationManager.getApplication();

        // 获取空格位置长度
        int distance = Utils.getWordStartOffset(generateContext.getEditorText(), generateContext.getOffset()) -
                generateContext.getStartOffset() - repair;

        application.runWriteAction(() -> {
            Document document = generateContext.getDocument();
            Editor editor = generateContext.getEditor();
            StringBuilder blankSpace = new StringBuilder();
            blankSpace.append(" ".repeat(Math.max(0, distance)));

            int lineNumberCurrent = document.getLineNumber(generateContext.getOffset()) + 1;

            List<String> setMtdList = setDtoContext.getParamList();
            for (String param : setMtdList) {
                int lineStartOffset = document.getLineStartOffset(lineNumberCurrent++);

                // 插入自动生成的代码
                WriteCommandAction.runWriteCommandAction(generateContext.getProject(), () -> {
                    String setMethodStr = blankSpace +
                            setDtoContext.generateSetMethodStr(param) + "(" +
                            getDtoContext.generateGetMethodCode(param) + ");\n";
                    // 插入一行内容
                    document.insertString(lineStartOffset, setMethodStr);

                    // 移动光标
                    editor.getCaretModel().moveToOffset(lineStartOffset + 2);
                    // 将编辑器滚动到当前光标的位置：ScrollType.MAKE_VISIBLE 表示滚动后要可见
                    editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                });
            }
        });
    }
}
