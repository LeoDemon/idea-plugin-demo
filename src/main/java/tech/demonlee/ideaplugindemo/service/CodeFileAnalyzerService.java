package tech.demonlee.ideaplugindemo.service;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import tech.demonlee.ideaplugindemo.model.CodeFileAnalyzerResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Demon.Lee
 * @date 2023-07-03 09:52
 */
public class CodeFileAnalyzerService {

    private static final String overrideAnnotationFQDN = "java.lang.Override";

    private final Project project;

    private final PsiFile psiFile;

    private List<String> privateFields;

    private List<String> publicMethods;

    private List<String> overrideMethods;

    public CodeFileAnalyzerService(Project project, PsiFile psiFile) {
        if (Objects.isNull(project) || Objects.isNull(psiFile)) {
            throw new IllegalArgumentException("invalid project or psiFile");
        }
        this.project = project;
        this.psiFile = psiFile;
    }

    public CodeFileAnalyzerResult analyze() {
        this.privateFields = new ArrayList<>();
        this.publicMethods = new ArrayList<>();
        this.overrideMethods = new ArrayList<>();

        this.psiFile.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitField(PsiField field) {
                if (field.hasModifierProperty(PsiModifier.PRIVATE)) {
                    privateFields.add(field.getName());
                }
            }

            @Override
            public void visitMethod(PsiMethod method) {
                if (method.hasModifierProperty(PsiModifier.PUBLIC)) {
                    publicMethods.add(method.getName());
                }
                PsiAnnotation[] annotations = method.getAnnotations();
                for (PsiAnnotation a : annotations) {
                    PsiJavaCodeReferenceElement e = a.getNameReferenceElement();
                    System.out.println("QualifiedName: " + a.getQualifiedName());
                    if (Objects.nonNull(e)) {
                        System.out.println("QualifiedName2:" + e.getQualifiedName());
                        System.out.println("ReferenceName:" + e.getReferenceName());
                    }
                    System.out.println("----------------------------------------------------------------");
                }
                if (method.hasAnnotation(overrideAnnotationFQDN)) {
                    overrideMethods.add(method.getName());
                }
            }
        });

        return new CodeFileAnalyzerResult(countPrivateFields(), countPublicMethods(), countOverrideMethods());
    }

    private int countPrivateFields() {
        return Optional.ofNullable(this.privateFields).map(List::size).orElse(0);
    }

    private int countPublicMethods() {
        return Optional.ofNullable(this.publicMethods).map(List::size).orElse(0);
    }

    private int countOverrideMethods() {
        return Optional.ofNullable(this.overrideMethods).map(List::size).orElse(0);
    }
}
