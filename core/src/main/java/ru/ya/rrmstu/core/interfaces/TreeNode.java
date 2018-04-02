package ru.ya.rrmstu.core.interfaces;

import java.util.List;

/**
 * Позволяет создать древовидную структуру из любого набора объектов, которые реализуют интерфейс TreeNode
 * Паттерн "Компоновщик" - вольная реализация
 */

public interface TreeNode {

    String getName();

    /**
     * Каждый элемент дерева должен иметь свой уникальный идентификатор
     *
     * @return
     */
    long getId();

    long getParentId();

    /**
     * Добавить один дочерний элемент
     *
     * @param child
     */
    void add(TreeNode child);

    /**
     * Удалить один дочерний элемент
     *
     * @param child
     */
    void remove(TreeNode child);

    /**
     * Дочерних элементов может быть любое количество
     *
     * @return
     */
    List<TreeNode> getChilds();

    /**
     * Получение дочернего элемента по id
     *
     * @param id
     * @return
     */
    TreeNode getChild(long id);

    /**
     * Получение родительского элемента - пригодится в разных ситуациях, например для отчетности по всем узлам дерева
     *
     * @return
     */
    TreeNode getParent();

    /**
     * Установка родительского элемента
     *
     * @param parent
     */
    void setParent(TreeNode parent);

    /**
     * Проверяет, есть ли дочерние элементы
     *
     * @return
     */
    boolean hasChilds();

}
