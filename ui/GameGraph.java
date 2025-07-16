package ui;

import java.util.ArrayList;
import java.util.List;

public class GameGraph<T> {
    public static class Node<T> {
        private final T state;
        private final Node<T> parent;
        private final List<Node<T>> children = new ArrayList<>();
        public Node(T state, Node<T> parent) {
            this.state = state;
            this.parent = parent;
        }

    }

    private Node<T> current;

    public GameGraph(T initialState) {
        current = new Node<>(initialState, null);
    }

    public void addState(T newState) {
        Node<T> child = new Node<>(newState, current);
        current.children.add(child);
        current = child;
    }

    public boolean canUndo() {
        return current != null && current.parent != null;
    }

    public T undo() {
        if (!canUndo()) return null;
        current = current.parent;
        return current.state;
    }

}
