package org.tree;

public class Main {
    public static void main(String[] args) {
        Tree<Integer> root = new Tree<>(5);
        root.addChild(4);
        var a = new Tree<Integer>(3);
        a.addChild(2);
        var b = a.addChild(1);
        root.addChild(a);
        root.dfs();
        root.remove();
        root.dfs();
    }
}