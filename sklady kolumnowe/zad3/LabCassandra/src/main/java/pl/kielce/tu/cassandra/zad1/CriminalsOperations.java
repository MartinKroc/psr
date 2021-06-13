package pl.kielce.tu.cassandra.zad1;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

public class CriminalsOperations extends SManager {
    public CriminalsOperations(CqlSession session) {
        super(session);
    }

    public void createTable() {
        executeSimpleStatement(
                "CREATE TABLE criminal (\n" +
                        "    id int PRIMARY KEY,\n" +
                        "    name text,\n" +
                        "    crime text,\n" +
                        "    penalty int\n" +
                        ");");
    }

    public void insertIntoTable(int nextId, String name, String crime, int penalty) {
        String statement = "INSERT INTO criminal (id, name, crime, penalty) " + " VALUES (" + nextId + ",'" + name + "','" + crime + "'," + penalty + ");";
        System.out.println(statement);
        executeSimpleStatement(statement);
    }

    public void updateIntoTable(int penalty, int id) {
        String statement = "UPDATE criminal SET penalty = " + penalty + " WHERE id = " + id + ";";
        executeSimpleStatement(statement);
    }

    public void deleteFromTable(int id) {
        String statement = "DELETE FROM criminal WHERE id = " + id + ";";
        executeSimpleStatement(statement);
    }

    public void selectFromTable() {
        String statement = "SELECT * FROM criminal;";
        ResultSet resultSet = session.execute(statement);
        for (Row row : resultSet) {
            System.out.print("\nprzestepca: ");
            System.out.print(row.getInt("id") + ", ");
            System.out.print(row.getString("name") + ", ");
            System.out.print(row.getString("crime") + ", ");
            System.out.print(row.getInt("penalty") + ", ");
        }
        System.out.println();
        System.out.println("Statement \"" + statement + "\" executed successfully");
    }

    public void dropTable() {
        executeSimpleStatement("DROP TABLE criminal;");
    }

    public void selectFromTableById(int id) {
        String statement = "SELECT * FROM criminal WHERE id=" + id + ";";
        ResultSet resultSet = session.execute(statement);
        for (Row row : resultSet) {
            System.out.print("przestepca: ");
            System.out.print(row.getInt("id") + ", ");
            System.out.print(row.getString("name") + ", ");
            System.out.print(row.getString("crime") + ", ");
            System.out.print(row.getInt("penalty") + ", ");
        }
        System.out.println();
        System.out.println("Statement \"" + statement + "\" executed successfully");
    }

    public void selectFromTableByQuery(String crime) {
        String statement = "SELECT * FROM criminal WHERE crime='" + crime + "' ALLOW FILTERING;";
        ResultSet resultSet = session.execute(statement);
        for (Row row : resultSet) {
            System.out.print("przestepca: ");
            System.out.print(row.getInt("id") + ", ");
            System.out.print(row.getString("name") + ", ");
            System.out.print(row.getString("crime") + ", ");
            System.out.print(row.getInt("penalty") + ", ");
        }
        System.out.println();
        System.out.println("Statement \"" + statement + "\" executed successfully");
    }

    public void processCrimes() {
        String temp = "";
        String statement = "SELECT * FROM criminal;";
        ResultSet resultSet = session.execute(statement);
        for (Row row : resultSet) {
            temp = row.getString("crime");
            if(temp.equals("kradziez")) {
                updateIntoTable(5, row.getInt("id"));
            }
        }
        System.out.println();
    }
}
