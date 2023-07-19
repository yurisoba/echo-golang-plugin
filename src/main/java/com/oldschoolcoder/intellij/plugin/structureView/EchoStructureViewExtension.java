package com.oldschoolcoder.intellij.plugin.structureView;

import com.goide.psi.*;
import com.intellij.ide.structureView.StructureViewExtension;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class EchoStructureViewExtension implements StructureViewExtension {

    @Override
    public Class<? extends PsiElement> getType() {
        return GoFile.class;
    }

    @Override
    public StructureViewTreeElement[] getChildren(PsiElement parent) {
        var arr = new ArrayList<EchoStructureViewTreeElement>();
        parent.accept(new GoRecursiveVisitor(){
            @Override
            public void visitElement(@NotNull PsiElement element) {
                switch (element.getText()) {
                    case "GET", "POST" -> {
                        try {
                            var expr = ((GoArgumentList) element.getParent().getNextSibling()).getExpressionList();
                            arr.add(new EchoStructureViewTreeElement(
                                    (NavigatablePsiElement) (PsiTreeUtil.getParentOfType(element, GoExpression.class)),
                                    element.getText() + " " + expr.get(0).getText()
                            ));
                        } catch (NullPointerException | IndexOutOfBoundsException e) {
                            break;
                        }
                    }
                }
                super.visitElement(element);
            }
        });
        return arr.toArray(StructureViewTreeElement[]::new);
    }

    @Override
    public @Nullable Object getCurrentEditorElement(Editor editor, PsiElement parent) {
        return null;
    }
}
