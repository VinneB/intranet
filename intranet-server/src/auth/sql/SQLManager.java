package auth.sql;

import exceptions.SQLServerConnectionException;

import java.sql.*;
import java.util.Map;

public class SQLManager {
    private SQLServer sqlServer;
    private String serverUser;
    private String serverPassword;

    public SQLManager(SQLServer sqlServer, String serverUser, String serverPassword) {
        this.sqlServer = sqlServer;
        this.serverUser = serverUser;
        this.serverPassword = serverPassword;
    }



    //SERVER ACCESS METHODS
    /**
     * Establishes a connection with the SQL Server.
     * @return returns Connection object
     * @throws SQLException throws this if the server couldn't be accessed
     */
    public Connection establishConnection() throws SQLServerConnectionException {
        if (sqlServer.isConnected()){
            System.out.println("SQL Server is already connected");
            return null;
        }

        try {
            sqlServer.setConnection(DriverManager.getConnection(sqlServer.getServerUrl(), serverUser, serverPassword));
        } catch (SQLException e){
            throw new SQLServerConnectionException("Couldn't establish connection with server", e);
        }

        return sqlServer.getConnection();
    }

    /**
     * General Statement interface. Generates statement and passes the SQL query to the statement. The statement passes the
     * query to the server connection.
     * @throws SQLException
     */
    public void insertData(String[] columns, String[] values, String tableName) throws SQLException, SQLServerConnectionException {
        if (!sqlServer.isConnected()) { //Validates server connection
            throw new SQLServerConnectionException();
        }

        SQLServer.parseBooleansForServer(values); //Converts boolean values into server readable format

        //Build statement
        String statementString = SQLServer.buildInsertStatement(columns, values, tableName);

        try ( //Connection and Statement resources created using configured SQL Driver
              Statement stmt = sqlServer.getConnection().createStatement()
        ) {
            //Execute query
            stmt.execute(statementString);

        } catch (SQLException e) {
            throw new SQLException("Couldn't make query", e);
        }
    }

    public void deleteRow(String tableName, String condition) throws SQLException, SQLServerConnectionException{
        if (!sqlServer.isConnected()){
            throw new SQLServerConnectionException();
        }

        //Build statement
        String statementString = String.format("DELETE FROM %s WHERE %s", tableName, condition);

        try (Statement stmt = sqlServer.getConnection().createStatement()){
            stmt.execute(statementString);
        } catch (SQLException e){
            throw new SQLException("Couldn't execute statement", e);
        }
    }

    /**
     * General Query interface. Generates statement and passes the SQL query to the statement. The statement passes the
     * query to the server connection. Returns two-dimensional map. If you want to select all rows from table, enter condition
     * as 'null'. Queries follow format "SELECT --columns-- FROM --tableName-- WHERE --condition--".
     * @return Map<String, Map<String, Object>> Outer map entries are equivalent to a row. Inner map entries equivalent to column data
     * @throws SQLException
     * @param columns a String array containing the column names of desired columns; use an array with "*" as first element for wildcard;
     *                first element in array is used as key for 'row' data
     * @param tableName name of table to retrieve data from
     * @param condition -- optional -- WHERE condition
     */
    public Map<String, Map<String, Object>> makeQuery(String[] columns, String tableName, String condition)
            throws SQLException, SQLServerConnectionException{
        if (!sqlServer.isConnected()){ //Connection check
            throw new SQLServerConnectionException();
        }

        //Builds query
        String query;
        if (condition != null) {
            query = SQLServer.buildSelectQuery(columns, tableName, condition);
        } else {
            query = SQLServer.buildSelectQuery(columns, tableName, null);
        }

        Map<String, Map<String, Object>> resultMap;
        try ( Statement stmt = sqlServer.getConnection().createStatement() ) {
            //Execute query and return results
            ResultSet results = stmt.executeQuery(query);

            //Interpret results
            if (columns[0].equals("*")){
                resultMap = SQLServer.parseTable(results, null);
            } else {
                resultMap = SQLServer.parseTable(results, columns[0]);
            }

        } catch (SQLException e) {
            throw new SQLException("Couldn't make query", e);
        }

        return resultMap;
    }

}
