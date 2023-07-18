package com.oldschoolcoder.intellij.plugin.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EchoStructureViewTreeElement implements StructureViewTreeElement, ItemPresentation {
    final NavigatablePsiElement val;
    final String path;
    public EchoStructureViewTreeElement(NavigatablePsiElement elem, String path) {
        val = elem;
        this.path = path;
    }

    @Override
    public Object getValue() {
        return val;
    }

    @Override
    public @NotNull ItemPresentation getPresentation() {
        return this;
    }

    @Override
    public TreeElement @NotNull [] getChildren() {
        return TreeElement.EMPTY_ARRAY;
    }

    @Override
    public void navigate(boolean requestFocus) {
        val.navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
        return val.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return val.canNavigateToSource();
    }

    @Override
    public @Nullable String getPresentableText() {
        return path;
    }

    @Override
    public @Nullable Icon getIcon(boolean unused) {
        return val.getIcon(0);
    }
}
