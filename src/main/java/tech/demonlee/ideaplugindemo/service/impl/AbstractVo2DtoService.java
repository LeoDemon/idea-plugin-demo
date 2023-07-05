package tech.demonlee.ideaplugindemo.service.impl;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import tech.demonlee.ideaplugindemo.model.CodeGenerateContext;
import tech.demonlee.ideaplugindemo.model.GetDtoContext;
import tech.demonlee.ideaplugindemo.model.SetDtoContext;
import tech.demonlee.ideaplugindemo.service.CodeGenerate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Demon.Lee
 * @date 2023-07-04 11:50
 */
public abstract class AbstractVo2DtoService implements CodeGenerate {

    protected final String setRegex = "set(\\w+)";
    protected final String getRegex = "get(\\w+)";

    Project project;
    PsiFile psiFile;
    DataContext context;
    CodeGenerateContext generateContext;

    protected AbstractVo2DtoService(Project project, PsiFile psiFile, DataContext context) {
        this.project = project;
        this.psiFile = psiFile;
        this.context = context;
    }

    @Override
    public void doGenerate() {
        this.generateContext = this.getGenerateContext();
        SetDtoContext setDtoContext = this.getSetDtoContext();
        GetDtoContext getDtoContext = this.getGetDtoContextByClipboard();

        System.out.println("------------------>>>>>>>>>>>>>>>>" + setDtoContext);
        System.out.println("------------------<<<<<<<<<<<<<<<<" + getDtoContext);

        this.weavingSetGetCode(setDtoContext, getDtoContext);
    }

    protected CodeGenerateContext getGenerateContext() {
        return new CodeGenerateContext(project, psiFile, context);
    }

    protected abstract SetDtoContext getSetDtoContext();

    // 从剪切板中获取 get 方法集合
    protected abstract GetDtoContext getGetDtoContextByClipboard();

    protected abstract void weavingSetGetCode(SetDtoContext setDtoContext, GetDtoContext getDtoContext);

    protected List<PsiClass> getPsiClassLinkList(PsiClass psiClass) {
        List<PsiClass> psiClassList = new ArrayList<>();
        PsiClass currentClass = psiClass;
        while (null != currentClass && !"Object".equals(currentClass.getName())) {
            psiClassList.add(currentClass);
            currentClass = currentClass.getSuperClass();
        }
        Collections.reverse(psiClassList);
        return psiClassList;
    }

    protected List<String> getMethods(PsiClass psiClass, String regex, String typeStr) {
        PsiMethod[] methods = psiClass.getMethods();
        List<String> methodList = new ArrayList<>();

        // 判断使用了 lombok，需要补全生成 get、set
        if (isUsedLombok(psiClass)) {
            PsiField[] fields = psiClass.getFields();
            for (PsiField psiField : fields) {
                String name = psiField.getName();
                methodList.add(typeStr + name.substring(0, 1).toUpperCase() + name.substring(1));
            }

            for (PsiMethod method : methods) {
                String methodName = method.getName();
                if (Pattern.matches(regex, methodName) && !methodList.contains(methodName)) {
                    methodList.add(methodName);
                }
            }

            return methodList;
        }

        for (PsiMethod method : methods) {
            String methodName = method.getName();
            if (Pattern.matches(regex, methodName)) {
                methodList.add(methodName);
            }
        }

        return methodList;
    }

    private boolean isUsedLombok(PsiClass psiClass) {
        return null != psiClass.getAnnotation("lombok.Data");
    }
}
