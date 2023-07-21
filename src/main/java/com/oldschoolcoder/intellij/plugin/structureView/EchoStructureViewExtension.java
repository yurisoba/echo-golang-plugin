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
import java.util.HashMap;

public class EchoStructureViewExtension implements StructureViewExtension {

    @Override
    public Class<? extends PsiElement> getType() {
        return GoFile.class;
    }

    @Override
    public StructureViewTreeElement[] getChildren(PsiElement parent) {
        var elems = new HashMap<String, EchoStructureViewTreeElement>();
        var roots = new ArrayList<String>();
        parent.accept(new GoRecursiveVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                switch (element.getText()) {
                    case "New" -> {
                        try {
                            // TODO: convert to getPrevSiblingOfType() for more stable parsing
                            var oneUp = element.getPrevSibling().getPrevSibling().getFirstChild();
                            if (!oneUp.getText().equals("echo"))
                                break;
                            var varName = PsiTreeUtil.getPrevSiblingOfType(
                                    PsiTreeUtil.getParentOfType(oneUp, GoCallExpr.class),
                                    GoVarDefinition.class
                            ).getFirstChild().getText();
                            // TODO: do we have to handle multiple echo instance ..?
                            elems.put(varName, new EchoStructureViewTreeElement(
                                    (NavigatablePsiElement) (PsiTreeUtil.getParentOfType(element, GoExpression.class)),
                                    varName
                            ));
                            roots.add(varName);
                        } catch (NullPointerException | IndexOutOfBoundsException e) {
                            break;
                        }
                    }
                    case "GET", "POST", "PUT", "DELETE", "PATCH" -> {
                        /*
                        It should be safe to assume that `Group` or `echo` is defined before usage.
                        However, for future-sake, this assumption should be noted explicitly.
                         */
                        try {
                            var varName = element.getPrevSibling().getPrevSibling().getFirstChild().getText();
                            var expr = ((GoArgumentList) element.getParent().getNextSibling()).getExpressionList();
                            elems.get(varName).routes.add(
                                    new EchoStructureViewTreeElement(
                                            (NavigatablePsiElement) (PsiTreeUtil.getParentOfType(element, GoExpression.class)),
                                            element.getText() + " " + expr.get(0).getText()
                                    ));
                        } catch (NullPointerException | IndexOutOfBoundsException e) {
                            break;
                        }
                    }
                    case "Group" -> {
                        try {
                            var parentElem = element.getPrevSibling().getPrevSibling().getFirstChild();
                            var expr = ((GoArgumentList) element.getParent().getNextSibling()).getExpressionList();
                            var varName = PsiTreeUtil.getPrevSiblingOfType(
                                    PsiTreeUtil.getParentOfType(parentElem, GoCallExpr.class),
                                    GoVarDefinition.class
                            ).getFirstChild().getText();
                            var elem = new EchoStructureViewTreeElement(
                                    (NavigatablePsiElement) (PsiTreeUtil.getParentOfType(element, GoExpression.class)),
                                    expr.get(0).getText()
                            );
                            elems.get(parentElem.getText()).groups.add(elem);
                            elems.put(varName, elem);
                        } catch (NullPointerException | IndexOutOfBoundsException e) {
                            break;
                        }
                    }
                }
                super.visitElement(element);
            }
        });
        if (roots.size() > 0)
            return new EchoStructureViewTreeElement[]{elems.get(roots.get(0))};
        else
            return EchoStructureViewTreeElement.EMPTY_ARRAY;
    }

    @Override
    public @Nullable Object getCurrentEditorElement(Editor editor, PsiElement parent) {
        return null;
    }
}
