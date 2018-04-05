package ru.ya.rrmstu.core.dao.impls;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ya.rrmstu.core.dao.interfaces.StorageDAO;
import ru.ya.rrmstu.core.database.SQLiteConnection;
import ru.ya.rrmstu.core.exceptions.CurrencyException;
import ru.ya.rrmstu.core.impls.DefaultStorage;
import ru.ya.rrmstu.core.interfaces.Storage;

public class StorageDAOImpl implements StorageDAO {

    private static final String CURRENCY_AMOUNT_TABLE = "currency_amount";
    private static final String STORAGE_TABLE = "storage";

    private List<Storage> storageList = new ArrayList<>();


    @Override
    public List<Storage> getAll() {
        storageList.clear();
        try {


            try (Statement stmt = SQLiteConnection.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("select * from " + STORAGE_TABLE + " order by parent_id");) {

                while (rs.next()) {
                    DefaultStorage storage = new DefaultStorage();
                    storage.setId(rs.getLong("id"));
                    storage.setName(rs.getString("name"));
                    storage.setParentId(rs.getLong("parent_id"));

                    storageList.add(storage);
                }
            }

            // для каждого хранилища загрузить доступны валюты и баланс
            for (Storage storage : storageList) {

                try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("select * from " + CURRENCY_AMOUNT_TABLE + " where storage_id =?");) {

                    stmt.setLong(1, storage.getId());

                    try (ResultSet rs = stmt.executeQuery();) {
                        while (rs.next()) {
                            storage.addCurrency(Currency.getInstance(rs.getString("currency_code")), rs.getBigDecimal("amount"));
                        }
                    }
                }
            }

            return storageList;

        } catch (SQLException e) {
            Logger.getLogger(StorageDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        } catch (CurrencyException e) {
            e.printStackTrace();
        }
        
        return null;
    }


    @Override
    public boolean addCurrency(Storage storage, Currency currency, BigDecimal initAmount) {

        // для автоматического закрытия ресурсов
        try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("insert into " + CURRENCY_AMOUNT_TABLE + "(currency_code, storage_id, amount) values(?,?,?)");) {

            stmt.setString(1, currency.getCurrencyCode());
            stmt.setLong(2, storage.getId());
            stmt.setBigDecimal(3, initAmount);


            if (stmt.executeUpdate() == 1) { // если была добавлена 1 запись
                return true;
            }

        } catch (SQLException e) {
            Logger.getLogger(StorageDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return false;
        // TODO реализовать: вместо true, false - выбрасывать исключение и перехватывать его выше, создать соотв. типы Exception
    }

    @Override
    public boolean deleteCurrency(Storage storage, Currency currency) {

        if (!operationExist(storage, currency)) {// если есть операции, которые привязаны к этому хранилищцу и валюте


            try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("delete from " + CURRENCY_AMOUNT_TABLE + " where storage_id=? and currency_code=?");) {

                stmt.setLong(1, storage.getId());
                stmt.setString(2, currency.getCurrencyCode());


                if (stmt.executeUpdate() == 1) {  // если была обновлена 1 запись
                    return true;
                }

            } catch (SQLException e) {
                Logger.getLogger(StorageDAOImpl.class.getName()).log(Level.SEVERE, null, e);
            }

        }

        return false;
    }

    private boolean operationExist(Storage storage, Currency currency) {
        // TODO реализовать проверку, есть ли операции по этому хранилищу и валюте
        return false;
    }

    @Override
    public boolean updateAmount(Storage storage, Currency currency, BigDecimal amount) {
        try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("update " + CURRENCY_AMOUNT_TABLE + " set amount=? where storage_id=? and currency_code=?");) {

            stmt.setBigDecimal(1, amount);
            stmt.setLong(2, storage.getId());
            stmt.setString(3, currency.getCurrencyCode());

            if (stmt.executeUpdate() == 1) {
                return true;
            }

        } catch (SQLException e) {
            Logger.getLogger(StorageDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return false;
    }

    @Override
    public Storage get(long id) {
        try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("select * from " + STORAGE_TABLE + " where id=?");) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery();) {
                DefaultStorage storage = null;

                if (rs.next()) {
                    storage = new DefaultStorage();
                    storage.setId(rs.getLong("id"));
                    storage.setName(rs.getString("name"));
                    storage.setParentId(rs.getLong("parent_id"));
                }

                return storage;
            }

        } catch (SQLException e) {
            Logger.getLogger(SourceDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return null;
    }


    @Override
    public boolean update(Storage storage) {
        // для упрощения - у хранилища даем изменить только название, изменять parent_id нельзя (для этого можно удалить и заново создать)
        try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("update " + STORAGE_TABLE + " set name=? where id=?");) {

            stmt.setString(1, storage.getName());
            stmt.setLong(2, storage.getId());

            if (stmt.executeUpdate() == 1) {
                return true;
            }

        } catch (SQLException e) {
            Logger.getLogger(StorageDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return false;
    }

    @Override
    public boolean delete(Storage storage) {
        // TODO реализовать - если есть ли операции по данному хранилищу - запрещать удаление
        try (PreparedStatement stmt = SQLiteConnection.getConnection().prepareStatement("delete from " + STORAGE_TABLE + " where id=?");) {

            stmt.setLong(1, storage.getId());

            if (stmt.executeUpdate() == 1) {
                return true;
            }

        } catch (SQLException e) {
            Logger.getLogger(StorageDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return false;
    }


    @Override
    public boolean add(Storage storage) {

        // для хранилища нужно вставлять данные в разные таблицы, поэтому выполняем в одной транзакции
        Connection con = SQLiteConnection.getConnection();

        try {
            con.setAutoCommit(false);// включаем режим ручного подтверждения транзакции (commit)


            // само добавляем само хранилище, т.к. в таблице валют работает foreign key
            try (PreparedStatement stmt = con.prepareStatement("insert into " + STORAGE_TABLE + "(name, parent_id) values(?,?)", Statement.RETURN_GENERATED_KEYS);) {// возвращать id вставленной записи

                stmt.setString(1, storage.getName());

                if (storage.hasParent()) {
                    stmt.setLong(2, storage.getParent().getId());
                } else {
                    stmt.setNull(2, Types.BIGINT);
                }


                if (stmt.executeUpdate() == 1) {// если хранилище добавилось нормально
                    try (ResultSet rs = stmt.getGeneratedKeys()) {// получаем id вставленной записи

                        if (rs.next()) {
                            storage.setId(rs.getLong(1));// не забываем просвоить новый id в объект, иначе дальше не добавятся валюты из-за foreign key
                        }

                        // вставляем все валюты с суммами
                        for (Currency c : storage.getAvailableCurrencies()) {
                            if (!addCurrency(storage, c, storage.getAmount(c))) {// если любой из запросов (добавление валюты) не выполнится - откатываем всю транзакцию
                                con.rollback();
                                return false;
                            }

                        }

                        con.commit();// если все выполнилось без ошибок - подтверждаем транзакцию
                        return true;
                    }

                }

                con.rollback();// если код дошел до этого места - значит что-то пошлое не так, поэтому откатываем транзакцию

            }


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (CurrencyException e) {
            e.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true);// возвращаем настройку обратно
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return false;
    }
}

