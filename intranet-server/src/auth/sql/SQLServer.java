package auth.sql;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SQLServer {
    //Primarily used for creating serverUrl
    private final String db;
    private final String host;
    private final int port;

    //Server's url
    private final String serverUrl;

    //Server Connection
    private Connection conn;

    public SQLServer(String host, int port, String db){
        this.host = host;
        this.port = port;
        this.db = db;

        this.serverUrl = buildMySqlUrl(host, port, db);
    }

    //GETTERS AND SETTERS
    protected String getServerUrl() {
        return serverUrl;
    }

    protected String getHost() {
        return host;
    }

    protected int getPort() {
        return port;
    }

    protected String getDb(){
        return db;
    }

    protected void setConnection(Connection conn) { this.conn = conn; }

    public Connection getConnection() {
        if (!isConnected()) {
            System.out.println("Server isn't currently connected");
        }
        return conn;
    }

    /**
     * Returns true if SQLServer has established an ongoing connection to designated server
     * @return
     */
    public boolean isConnected(){
        return !(conn == null);
    }

    //STATIC METHODS

    /**
     * Builds MySQL server url following standard MySQL format
     * @param host
     * @param port
     * @param db
     * @return
     */
    protected static String buildMySqlUrl(String host, int port, String db){
        return String.format("jdbc:mysql://%s:%s/%s", host, port, db);
    }

    /**
     * Builds SELECT query string following the format "SELECT --columns-- FROM --tableName WHERE --condition--;". If condition
     * is 'null' then no 'WHERE' condition is added
     * @param columns
     * @param tableName
     * @param condition
     * @return
     */
    protected static String buildSelectQuery(String[] columns, String tableName, String condition){
        StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT ");
        if (columns[0].equals("*")) { //CASE: select *
            query.append("* ");
        } else { //CASE: select column, column, ...
            for (String columnTag : columns) {
                query.append(columnTag + ", ");
            }
            query.delete(query.length() - 2, query.length() - 1);
        }
        query.append("FROM " + tableName + " "); //Specify table name

        if (condition != null){ //Adds conditional clause if condition isn't null
            query.append("WHERE " + condition);
        }

        query.append(";");

        return query.toString();
    }

    protected static String buildInsertStatement(String[] columns, String[] values, String tableName){
        StringBuilder statement = new StringBuilder();
        //INSERT
        statement.append("INSERT INTO " + tableName + " (");
        for (String column : columns){
            statement.append(column + ", ");
        }
        statement.replace(statement.length() - 2, statement.length() - 1, ")");
        //VALUES
        statement.append("VALUES (");
        for (String value : values){
            statement.append(String.format("'%s', ", value));
        }
        statement.replace(statement.length() - 2, statement.length() - 1, ");");

        return statement.toString();
    }

    protected static String[] parseBooleansForServer(String[] valueArray){
        for (int i = 0; i < valueArray.length; i++){
            if (valueArray[i].equals("true")){
                valueArray[i] = "1";
            } else if (valueArray[i].equals("false")){
                valueArray[i] = "0";
            }
        }
        return valueArray;
    }

    /**
     * Parses input 'resultSet' into a two-dimensional map. If 'keyColumn' is provided, then outer-dimensions key is equal
     * to the value stored in 'keyColumn'. Otherwise it's the first column in the ResultSet
     * @param resultSet ResultSet containing data to parse
     * @param keyColumn Column from which to pull 'outer-dimension' keys; input null to use first column in resultSet
     * @return two-dimensional map
     * @throws SQLException thrown if SQLExceptions involving resultSet are raised
     */
    protected static Map<String, Map<String, Object>> parseTable(ResultSet resultSet, String keyColumn) throws SQLException{
        Map<String, Map<String, Object>> resultMap = new HashMap<>();

        ResultSetMetaData metaData = resultSet.getMetaData(); //This contains useful data about current table

        while (resultSet.next()) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++){ //Creates a map of all items in row
                map.put(metaData.getColumnName(i), resultSet.getObject(i));
            }
            //Adds map to resultMap
            if (keyColumn == null) { //CASE: No key column
                resultMap.put(String.valueOf(resultSet.getObject(1)), map);
            } else { //CASE: Given key column
                resultMap.put(String.valueOf(resultSet.getObject(keyColumn)), map);
            }
        }

        return resultMap;
    }
}
