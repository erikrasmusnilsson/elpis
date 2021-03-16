package tech.nilsson.utils;

import javafx.scene.Node;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SelectionManager {
    private final List<Node> selectableNodes;
    private int selectedNode;
    private Callback beforeNavigation;
    private Callback afterNavigation;

    public SelectionManager(Node... nodes) {
        selectableNodes = new LinkedList<>();
        selectableNodes.addAll(Arrays.asList(nodes));
        selectedNode = 0;
    }

    public SelectionManager(Callback beforeNavigation, Callback afterNavigation, Node... nodes) {
        this(nodes);
        this.beforeNavigation = beforeNavigation;
        this.afterNavigation = afterNavigation;
    }

    public Node getSelectedNode() {
        return selectableNodes.get(selectedNode);
    }

    public void selectNextNode() {
        beforeNavigation.callback();
        selectedNode = (selectedNode + 1) % selectableNodes.size();
        afterNavigation.callback();
    }

    public void selectPreviousNode() {
        beforeNavigation.callback();
        selectedNode = (selectedNode - 1) < 0 ? selectableNodes.size() - 1 : selectedNode - 1;
        afterNavigation.callback();
    }

    public interface Callback {
        void callback();
    }
}
