package ru.ya.rrmstu.core.interfaces;

import java.util.List;

// позволяет создать древовидную структуру из любого набора объектов, которые реализуют интерфейс TreeNode
// паттерн "Компоновщик" - вольная реализация
public interface TreeNode {

    String getName();

    long getId(); // каждый элемент дерева должен иметь свой уникальный идентификатор

    void setId(long id); // установить id

    long getParentId();

    void add(TreeNode child); // добавить один дочерний элемент

    void remove(TreeNode child); // удалить один дочерний элемент

    List<TreeNode> getChilds(); // дочерних элементов может быть любое количество

    TreeNode getChild(long id); // получение дочернего элемента по id

    TreeNode getParent(); // получение родительского элемента - пригодится в разных ситуациях, например для отчетности по всем узлам дерева

    void setParent(TreeNode parent);	// установка родительского элемента

    boolean hasChilds(); // проверяет, есть ли дочерние элементы

    boolean hasParent(); // проверяет, есть ли родитель

}
