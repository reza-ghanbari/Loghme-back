package dataAccess.Mappers;

import dataAccess.ConnectionPool;
import dataAccess.DAO.RestaurantDAO;
import business.Domain.Location;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class RestaurantMapper extends Mapper<RestaurantDAO, String> {

    private final String tableName = "Restaurants";

    public RestaurantMapper() throws SQLException{
        Connection connection = ConnectionPool.getConnection();

        String createTable = "create table if not exists " + tableName + " (\n" +
                "   id varchar(100) primary key,\n" +
                "   name varchar(100) not null,\n" +
                "   logo varchar(255) not null,\n" +
                "   locx int not null,\n" +
                "   locy int not null\n" +
                ");";
        Statement stmt = connection.createStatement();
        stmt.execute(createTable);
        if(stmt!=null && !stmt.isClosed()){
            stmt.close();
        }
        if(connection!=null && !connection.isClosed()){
            connection.close();
        }
    }

    @Override
    protected String getFindStatement(String id) {
        return "select * from " + tableName + " where id = \"" + id + "\";";
    }

    @Override
    protected String getInsertStatement(RestaurantDAO obj) {
        return "insert into " + tableName + "(id, name, logo, locx, locy) " +
                "values (\"" + obj.getId() + "\", \"" + obj.getName() + "\", \"" + obj.getLogoAddress() +
                 "\", " + obj.getLocation().getX() + ", " + obj.getLocation().getY() + ");";
    }

    @Override
    protected String getDeleteStatement(String id) {
        return "delete from " + tableName + " where id = \"" + id + "\";";
    }

    @Override
    protected RestaurantDAO getObject(ResultSet rs) throws SQLException {
        return new RestaurantDAO(rs.getString("name"), rs.getString("logo")
                , new Location(rs.getInt("locx"), rs.getInt("locy")), rs.getString("id"));
    }

    private String concatValues(ArrayList<RestaurantDAO> restaurants) {
        StringBuilder result = new StringBuilder();
        for (RestaurantDAO restaurant : restaurants)
            result.append(",(\"" + restaurant.getId() + "\",\"" + restaurant.getName() + "\",\"" + restaurant.getLogoAddress()
                    + "\"," + restaurant.getLocation().getX() + "," + restaurant.getLocation().getY() + ")");
        return result.toString().substring(1);
    }

    public void insertAllRestaurants(ArrayList<RestaurantDAO> restaurants) throws SQLException {
        if (restaurants.size() < 1)
            return;
        String content = this.concatValues(restaurants);
        String query = "insert into " + tableName + "(id, name, logo, locx, locy) values " + content + ";";
        Connection connection = ConnectionPool.getConnection();
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
        if(statement!=null && !statement.isClosed()){
            statement.close();
        }
        if(connection!=null && !connection.isClosed()){
            connection.close();
        }
    }

    public ArrayList<RestaurantDAO> getAllRestaurantsInfo() throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        String query = "select id, name, logo, locx, locy from " + tableName + ";";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        ArrayList<RestaurantDAO> restaurants = new ArrayList<>();
        while (rs.next())
            restaurants.add(getObject(rs));
        if(rs!=null && !rs.isClosed()){
            rs.close();
        }
        if(statement!=null && !statement.isClosed()){
            statement.close();
        }
        if(connection!=null && !connection.isClosed()){
            connection.close();
        }
        return restaurants;
    }

    public ArrayList<RestaurantDAO> getAllRestaurantsInRangePageByPage(int pageSize,int pageNumber,double range,Location location) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        String whereClause = " POWER(locx - "+location.getX()+",2)+POWER(locy - "+location.getY()+",2) <= " + range*range;
        String query = "select id, name, logo, locx, locy from " + tableName + " where "+whereClause+" ORDER by id ASC LIMIT "+pageNumber*pageSize+","+pageSize+";";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        ArrayList<RestaurantDAO> restaurants = new ArrayList<>();
        while (rs.next())
            restaurants.add(getObject(rs));
        if(rs!=null && !rs.isClosed()){
            rs.close();
        }
        if(statement!=null && !statement.isClosed()){
            statement.close();
        }
        if(connection!=null && !connection.isClosed()){
            connection.close();
        }
        return restaurants;
    }

    public int getAllRestaurantsInRangeCount(double range,Location location) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        String whereClause = " POWER(locx - "+location.getX()+",2)+POWER(locy - "+location.getY()+",2) <= " + range*range;
        String query = "select count(*) as count from " + tableName + " where "+whereClause+";";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        int count = 0;
        while (rs.next())
            count = rs.getInt("count");
        if(rs!=null && !rs.isClosed()){
            rs.close();
        }
        if(statement!=null && !statement.isClosed()){
            statement.close();
        }
        if(connection!=null && !connection.isClosed()){
            connection.close();
        }
        return count;
    }

    public ArrayList<RestaurantDAO> getAllRestaurantsMatchNameAndInRange(String name,double range,Location location,int pageNumber,int pageSize) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        String whereClause = "name LIKE \"%"+name+"%\"and POWER(locx-"+location.getX()+",2)+POWER(locy-"+location.getY()+",2) <= " + range*range;
        String query = "select id, name, logo, locx, locy from " + tableName + " where "+whereClause+
                " order by id limit "+(pageNumber*pageSize)+","+(pageSize)+";";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        ArrayList<RestaurantDAO> restaurants = new ArrayList<>();
        while (rs.next())
            restaurants.add(getObject(rs));
        if(rs!=null && !rs.isClosed()){
            rs.close();
        }
        if(statement!=null && !statement.isClosed()){
            statement.close();
        }
        if(connection!=null && !connection.isClosed()){
            connection.close();
        }
        return restaurants;
    }

    public int getAllRestaurantsMatchNameAndInRangeCount(String name,double range,Location location) throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        String whereClause = "name LIKE \"%"+name+"%\"and POWER(locx-"+location.getX()+",2)+POWER(locy-"+location.getY()+",2) <= " + range*range;
        String query = "select count(*) as count from " + tableName + " where "+whereClause+";";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        int count = 0;
        while (rs.next())
            count = rs.getInt("count");
        if(rs!=null && !rs.isClosed()){
            rs.close();
        }
        if(statement!=null && !statement.isClosed()){
            statement.close();
        }
        if(connection!=null && !connection.isClosed()){
            connection.close();
        }
        return count;
    }

    public HashMap<String, Boolean> getRestaurantsId() throws SQLException {
        Connection connection = ConnectionPool.getConnection();
        String query = "select id from " + tableName + ";";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        HashMap<String, Boolean> ids = new HashMap<>();
        while (rs.next())
            ids.put(rs.getString("id"), true);
        if(rs!=null && !rs.isClosed()){
            rs.close();
        }
        if(stmt!=null && !stmt.isClosed()){
            stmt.close();
        }
        if(connection!=null && !connection.isClosed()){
            connection.close();
        }
        return ids;
    }

}