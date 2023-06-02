package pl.gawryszewski.edp_projekt.model;

import pl.gawryszewski.edp_projekt.application.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHandler {
    private static final String TABLE_NAME = "items";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_ITEM_NAME = "ITEM_NAME";
    private static final String COLUMN_PRICE = "PRICE";
    private static final String COLUMN_ITEM_DESC = "ITEM_DESCRIPTION";
    private static final String COLUMN_IMAGE = "IMAGE";
    private final String url;
    private final String user;
    private final String password;
    private Connection connection;
    private static DataBaseHandler instance;
    private DataBaseHandler(){
        Config properties = Config.getInstance();
        url = properties.getDbUrl();
        user = properties.getDbUserName();
        password = properties.getDbPassword();
        connect();
    }
    public static DataBaseHandler getInstance(){
        if(instance == null){
            synchronized (DataBaseHandler.class){
                if (instance == null){
                    instance = new DataBaseHandler();
                }
            }
        }
        return instance;
    }
    public void connect() {
        if(connection == null){
            try {
                connection = DriverManager.getConnection(url, user, password);
            } catch (SQLException e){
                e.printStackTrace();
                System.out.println("Could not connect to database");
            }
        }
    }
    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
            System.out.println("Could not close database connection");
        }
    }
    public int addOne(Item item){
        String sql = "INSERT INTO "+TABLE_NAME+" ("+COLUMN_ITEM_NAME+", "+COLUMN_PRICE+", "+COLUMN_ITEM_DESC+", "
                +COLUMN_IMAGE+") VALUES (?, ?, ?, ?)";
        int id = -1;
        try {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, item.getName());
            statement.setFloat(2, Float.parseFloat(item.getPrice()));
            statement.setString(3, item.getDescription());
            statement.setBytes(4, item.getImageData());
            int affectedRows = statement.executeUpdate();
            if(affectedRows>0){
                ResultSet ids = statement.getGeneratedKeys();
                if(ids.next()){
                    id = ids.getInt(1);
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("Could not add to database");
        }
        return id;
    }
    public List<Item> getAll() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM "+TABLE_NAME;
        try{
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet results = statement.executeQuery();
            while(results.next()){
                items.add(new Item(results.getInt(COLUMN_ID), results.getString(COLUMN_ITEM_NAME), String.valueOf(results.getFloat(COLUMN_PRICE)),
                        results.getString(COLUMN_ITEM_DESC), results.getBytes(COLUMN_IMAGE)));
            }
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("Could not load from database");
        }
        return items;
    }
    public List<Item> getByFilter(String filter){
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_ITEM_NAME+" LIKE ?";
        int i = 0;
        try{
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, filter+"%");
            ResultSet results = statement.executeQuery();
            while(results.next()){
                items.add(new Item(results.getInt(COLUMN_ID), results.getString(COLUMN_ITEM_NAME), String.valueOf(results.getFloat(COLUMN_PRICE)),
                        results.getString(COLUMN_ITEM_DESC), results.getBytes(COLUMN_IMAGE)));
                i++;
            }
            System.out.println("loaded "+i);
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("Could not load from database");
        }
        return items;
    }
    public void updateItem(Item item){
        String sql = "UPDATE "+ TABLE_NAME+" SET "+COLUMN_ITEM_NAME+" = ?, "+COLUMN_PRICE+" = ?, "+
                COLUMN_ITEM_DESC+" = ?, "+COLUMN_IMAGE+" = ? WHERE " +COLUMN_ID+" = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, item.getName());
            statement.setFloat(2, Float.parseFloat(item.getPrice()));
            statement.setString(3, item.getDescription());
            statement.setBytes(4, item.getImageData());
            statement.setInt(5, item.getId());
            statement.execute();
        } catch(SQLException e){
            e.printStackTrace();
            System.out.println("Could not update item");
        }
    }
    public void deleteItem(Item item){
        String sql = "DELETE FROM "+TABLE_NAME+" WHERE "+COLUMN_ID+" = "+item.getId();
        try{
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.execute();
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("Could not delete item");
        }
    }
}
