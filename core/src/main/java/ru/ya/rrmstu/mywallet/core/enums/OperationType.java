package ru.ya.rrmstu.mywallet.core.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum OperationType {

    INCOME(1), OUTCOME(2), TRANSFER(3), CONVERT(4); // нумерация id - как в таблице

    private static Map<Integer, OperationType> map = new HashMap<>();
    private static List<OperationType> list = new ArrayList<>();

    static {
        for (OperationType oper : OperationType.values()) {
            map.put(oper.getId(), oper);
            list.add(oper);
        }
    }

    private Integer id;

    private OperationType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static OperationType getType(int id) {
        return map.get(id);
    }

    public static List<OperationType> getList(){
        return list;
    }


}
