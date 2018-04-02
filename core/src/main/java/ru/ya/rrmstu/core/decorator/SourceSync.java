package ru.ya.rrmstu.core.decorator;


import java.util.List;

import ru.ya.rrmstu.core.dao.interfaces.SourceDAO;
import ru.ya.rrmstu.core.enums.OperationType;
import ru.ya.rrmstu.core.interfaces.Source;

public class SourceSync implements SourceDAO {

    private SourceDAO sourceDAO;
    private List<Source> sourceList;

    public SourceSync(SourceDAO sourceDAO) {
        this.sourceDAO = sourceDAO;
    }

    @Override
    public List<Source> getAll() {
        if (sourceList == null) {
            sourceList = sourceDAO.getAll();
        }
        return sourceList;
    }

    @Override
    public Source get(long id) {
        return null;
    }

    @Override
    public boolean update(Source source) {
        if (sourceDAO.update(source)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Source source) {
        // TODO добавить нужные Exceptions
        if (sourceDAO.delete(source)) {
            sourceList.remove(source);
            sourceDAO.getList(source.getOperationType()).remove(source);
            return true;
        }
        return false;
    }

    @Override
    public List<Source> getList(OperationType operationType) {
        return sourceDAO.getList(operationType);
    }
}
