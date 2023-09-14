package org.tree;

import javax.lang.model.type.NullType;
import java.util.ArrayList;
import java.util.List;

public class Tree<T> {
    private final T node;
    private Tree<T> parent = null;
    private ArrayList<Tree<T>> children = new ArrayList<>();
    private int color = 0;

    public Tree(T node) {
        this.node = node;
    }

    public Tree<T> addChild(Tree<T> child) {
        child.parent = this;
        this.children.add(child);
        return child;
    }

    public Tree<T> addChild(T node) {
        Tree<T> child = new Tree<>(node);
        return this.addChild(child);
    }

    public void remove() {
        this.children.clear();
        if (this.parent != null) {
            this.parent.children.remove(this);
        }
    }

    public void dfs() {
        System.out.println(this.node);
        this.color = 1;
        for (var child : this.children) {
            child.dfs();
        }
        this.color = 2;
    }
}
