package ru.ya.rrmstu.core.dao.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ya.rrmstu.core.abstracts.AbstractOperation;
import ru.ya.rrmstu.core.dao.interfaces.OperationDAO;
import ru.ya.rrmstu.core.database.SQLiteConnection;
import ru.ya.rrmstu.core.enums.OperationType;
import ru.ya.rrmstu.core.impls.operations.ConvertOperation;
import ru.ya.rrmstu.core.impls.operations.IncomeOperation;
import ru.ya.rrmstu.core.impls.operations.OutcomeOperation;
import ru.ya.rrmstu.core.impls.operations.TransferOperation;
import ru.ya.rrmstu.core.interfaces.Operation;
import ru.ya.rrmstu.core.interfaces.Source;
import ru.ya.rrmstu.core.interfaces.Storage;

public class OperationDAOImpl implements OperationDAO {

    private static final String OPERATION_TABLE = "operation";
    private List<Operation> operationList = new ArrayList<>();

    // чтобы эти объекты не получать еще раз из БД - передаем сюда
    private Map<Long, Source> sourceIdentityMap;// чтобы получить по id нужный Source
    private Map<Long, Storage> storageIdentityMap; // чтобы получить по id нужный Storage

    public OperationDAOImpl(Map<Long, Source> sourceIdentityMap, Map<Long, Storage> storageIdentityMap) {// IdentityMap - распространенное понятие, это коллекция, где вместо ключа id, а значение - это сам объект
        this.sourceIdentityMap = sourceIdentityMap;
        this.storageIdentityMap = storageIdentityMap;
    }

    @Override
    public List<Operation> getAll() {
        operationList.clear();

        try (Statement stmt = SQLiteConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("select * from " + OPERATION_TABLE);) {

            while (rs.next()) {

                operationList.add(fillOperation(rs));

            }

            return operationList;// должен содержать только корневые элементы

        } catch (SQLException e) {
            Logger.getLogger(OperationDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return null;
    }


    @Override
    public Operation get(long id) {
        try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("select * from " + OPERATION_TABLE + " where id=?");) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery();) {

                if (rs.next()) {
                    AbstractOperation operation = fillOperation(rs);
                    return operation;
                }

            }

        } catch (SQLException e) {
            Logger.getLogger(SourceDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return null;
    }

    // заполняет объект операции (общие + специфичные поля)
    private AbstractOperation fillOperation(ResultSet rs) throws SQLException {

        OperationType operationType = OperationType.getType(rs.getInt("type_id"));

        // заполняем специфичные поля
        AbstractOperation operation = createOperation(operationType, rs);

        // заполняем общие поля для всех типов операций
        operation.setId(rs.getLong("id"));
        operation.setOperationType(operationType);
        Calendar datetime = Calendar.getInstance();
        datetime.setTimeInMillis(rs.getLong("datetime"));
        operation.setDateTime(datetime);
        operation.setDescription(rs.getString("description"));

        return operation;
    }

    // создаем нужный экземпляр и заполняем его специфичные поля
    private AbstractOperation createOperation(OperationType operationType, ResultSet rs) throws SQLException {


        // список операций должен загружаться при запуске программы только один - тогда можно использовать лямбда выражения для поиска объектов по id
        // для всех новых операций - поиск по id проводиться не будет
        switch (operationType) {
            case INCOME: { // доход
                IncomeOperation operation = new IncomeOperation();

                operation.setFromSource(sourceIdentityMap.get(rs.getLong("from_source_id"))); // откуда поступили деньги
                operation.setFromCurrency(Currency.getInstance(rs.getString("from_currency_code"))); // в какой валюте поступили
                operation.setFromAmount(rs.getBigDecimal("from_amount")); // сумма поступления
                operation.setToStorage(storageIdentityMap.get(rs.getLong("to_storage_id")));// куда положим эти деньги


                return operation;
            }
            case OUTCOME: { // расход
                OutcomeOperation operation = new OutcomeOperation();

                operation.setFromStorage(storageIdentityMap.get(rs.getLong("from_storage_id"))); // откуда взяли деньги
                operation.setFromCurrency(Currency.getInstance(rs.getString("from_currency_code")));// в какой валюте расход
                operation.setFromAmount(rs.getBigDecimal("from_amount")); // сумма расхода
                operation.setToSource(sourceIdentityMap.get(rs.getLong("to_source_id")));// на что потратили


                return operation;
            }

            case TRANSFER: { // перевод в одной валюте между хранилищами
                TransferOperation operation = new TransferOperation();

                operation.setFromStorage(storageIdentityMap.get(rs.getLong("from_storage_id")));// откуда переводим
                operation.setFromCurrency(Currency.getInstance(rs.getString("from_currency_code"))); // в какой валюте переводим
                operation.setFromAmount(rs.getBigDecimal("from_amount")); // какую сумму переводим
                operation.setToStorage(storageIdentityMap.get(rs.getLong("to_storage_id"))); // не создаем новый объект, используем ранее созданный объект источника

                return operation;
            }

            case CONVERT: { // конвертация из любой валюты в любую между хранилищами
                ConvertOperation operation = new ConvertOperation();

                operation.setFromStorage(storageIdentityMap.get(rs.getLong("from_storage_id"))); // откуда конвертируем
                operation.setFromCurrency(Currency.getInstance(rs.getString("from_currency_code"))); // в каой валюте
                operation.setFromAmount(rs.getBigDecimal("from_amount")); // какая сумма в исходной валюте


                operation.setToStorage(storageIdentityMap.get(rs.getLong("to_storage_id"))); // куда конвертируем
                operation.setToCurrency((Currency.getInstance(rs.getString("to_currency_code")))); // валюта поступления
                operation.setToAmount(rs.getBigDecimal("to_amount")); // какая итоговая сумма поступила в этой валюте


                return operation;
            }


        }

        return null;// если ни один из типов операция не подошел, можно также выбрасывать исключение

    }


    @Override
    // при обновлении не даем менять тип операции - только данные самой операции (дата, суммы, источники, хранилища, описание)
    public boolean update(Operation operation) {
        return (delete(operation) && add(operation));// при обновлении - удаляем старую операцию, добавляем новую, т.к. могут поменяться хранилища, источники
    }

    @Override
    public boolean delete(Operation operation) {
        // TODO реализовать - если есть ли операции по данному хранилищу - запрещать удаление
        try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("delete from " + OPERATION_TABLE + " where id=?");) {

            stmt.setLong(1, operation.getId());

            if (stmt.executeUpdate() == 1) {
                return true;
            }

        } catch (SQLException e) {
            Logger.getLogger(SourceDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return false;
    }

    @Override
    public boolean add(Operation operation) {


        String sql = createInsertSql(operation); // подготовить sql с нужными параметрами, в зависимости от типа операции


        try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {

            // общие поля для всех типов операций
            stmt.setLong(1, operation.getDateTime().getTimeInMillis());
            stmt.setLong(2, operation.getOperationType().getId());
            stmt.setString(3, operation.getDescription());


            // выбираем специфичные поля
            switch (operation.getOperationType()) {
                case INCOME:
                    IncomeOperation incomeOperation = (IncomeOperation)operation;

                    stmt.setLong(4, incomeOperation.getFromSource().getId());
                    stmt.setString(5, incomeOperation.getFromCurrency().getCurrencyCode());
                    stmt.setBigDecimal(6, incomeOperation.getFromAmount());
                    stmt.setLong(7, incomeOperation.getToStorage().getId());
                    break;

                case OUTCOME:
                    OutcomeOperation outcomeOperation = (OutcomeOperation)operation;

                    stmt.setLong(4, outcomeOperation.getFromStorage().getId());
                    stmt.setString(5, outcomeOperation.getFromCurrency().getCurrencyCode());
                    stmt.setBigDecimal(6, outcomeOperation.getFromAmount());
                    stmt.setLong(7, outcomeOperation.getToSource().getId());
                    break;

                case TRANSFER:
                    TransferOperation transferOperation = (TransferOperation)operation;

                    stmt.setLong(4, transferOperation.getFromStorage().getId());
                    stmt.setString(5, transferOperation.getFromCurrency().getCurrencyCode());
                    stmt.setBigDecimal(6, transferOperation.getFromAmount());
                    stmt.setLong(7, transferOperation.getToStorage().getId());
                    break;

                case CONVERT:
                    ConvertOperation convertOperation = (ConvertOperation)operation;

                    stmt.setLong(4, convertOperation.getFromStorage().getId());
                    stmt.setString(5, convertOperation.getFromCurrency().getCurrencyCode());
                    stmt.setBigDecimal(6, convertOperation.getFromAmount());
                    stmt.setLong(7, convertOperation.getToStorage().getId());
                    stmt.setString(8, convertOperation.getToCurrency().getCurrencyCode());
                    stmt.setBigDecimal(9, convertOperation.getToAmount());
                    break;
            }



            if (stmt.executeUpdate() == 1) {// если объект добавился нормально
                try (ResultSet rs = stmt.getGeneratedKeys()) {// получаем id вставленной записи

                    if (rs.next()) {
                        operation.setId(rs.getLong(1));// не забываем просвоить новый id в объект
                    }

                    return true;
                }

            }

        } catch (SQLException e) {
            Logger.getLogger(SourceDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return false;
    }



    private String createInsertSql(Operation operation) {

        StringBuilder sb = new StringBuilder("insert into " + OPERATION_TABLE + " (datetime, type_id, description, ");// sql собирается частями, в зависимости от типа операции

        switch (operation.getOperationType()) {
            case INCOME:
                return sb.append("from_source_id, from_currency_code, from_amount, to_storage_id) values(?,?,?,?,?,?,?)").toString();
            case OUTCOME:
                return sb.append("from_storage_id, from_currency_code, from_amount, to_source_id) values(?,?,?,?,?,?,?)").toString();
            case TRANSFER:
                return sb.append("from_storage_id, from_currency_code, from_amount, to_storage_id) values(?,?,?,?,?,?,?)").toString();
            case CONVERT:
                return sb.append("from_storage_id, from_currency_code, from_amount, to_storage_id, to_currency_code, to_amount) values(?,?,?,?,?,?,?,?,?)").toString();
        }

        return null;

    }


    @Override
    public List<Operation> getList(OperationType operationType) {
        operationList.clear();

        try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("select * from " + OPERATION_TABLE + " where type_id=?")) {

            stmt.setLong(1, operationType.getId());

            try (ResultSet rs = stmt.executeQuery();) {

                while (rs.next()) {

                    operationList.add(fillOperation(rs));

                }

                return operationList;// должен содержать только корневые элементы

            } catch (SQLException e) {
                Logger.getLogger(OperationDAOImpl.class.getName()).log(Level.SEVERE, null, e);
            }


        } catch (SQLException e) {
            Logger.getLogger(OperationDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return null;
    }
}
