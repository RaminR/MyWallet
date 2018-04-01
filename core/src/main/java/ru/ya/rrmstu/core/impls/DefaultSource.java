package ru.ya.rrmstu.core.impls;

import java.util.List;

import ru.ya.rrmstu.core.abstracts.AbstractTreeNode;
import ru.ya.rrmstu.core.interfaces.Source;
import ru.ya.rrmstu.core.interfaces.TreeNode;
import ru.ya.rrmstu.core.objects.OperationType;

public class DefaultSource extends AbstractTreeNode implements Source {
    private OperationType operationType;

    public DefaultSource() {
    }

    public DefaultSource(String name) {
        super(name);
    }

    public DefaultSource(List<TreeNode> childs) {
        super(childs);
    }

    public DefaultSource(String name, long id) {
        super(name, id);
    }

    public DefaultSource(long id, List<TreeNode> childs, TreeNode parent, String name) {
        super(id, childs, parent, name);
    }

    public DefaultSource(String name, long id, OperationType operationType) {
        super(name, id);
        this.operationType = operationType;
    }


    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    @Override
    public void add(TreeNode child) {

        // TODO применить паттерн
        // для дочернего элемента устанавливаем тип операции родительского элемента
        if (child instanceof DefaultSource) {
            ((DefaultSource) child).setOperationType(operationType);
        }

        super.add(child);
    }
}
