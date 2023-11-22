package com.galimimus.phishingmonitor.helpers;

import com.galimimus.phishingmonitor.models.Department;
import com.galimimus.phishingmonitor.models.Employee;
import com.galimimus.phishingmonitor.models.User;
import com.mysql.cj.MysqlConnection;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.mysql.jdbc.Driver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class DB {

    private  String db_name = "monitor";
    private  String host = "localhost";
    private  String username = "galimimus";
    private  String password = "pass111";
    private  Connection connection;
    private  MysqlDataSource datasource = new MysqlDataSource();
    public void connect() {
            datasource.setPassword(password);
            datasource.setUser(username);
            datasource.setServerName(host);
            datasource.setDatabaseName(db_name);
            try {
                connection = datasource.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }

    public  User getMe(int user_id) {
        User user;
        try {
            String query = "SELECT name, email, company FROM users WHERE id = " + user_id;
            if(!connection.isClosed()){
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    String name = result.getString(1);
                    String email = result.getString("email");
                    String company = result.getString("company");
                    user = new User(name, email, company);
                    return user;//TODO: переделать возврат по божески
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void close() {
        try {
            connection.close();
        }catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public  void setHost(String host) {
        this.host = host;
    }

    public void setUrl(String db_name) {
        this.db_name = db_name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDb_name() {
        return db_name;
    }

    public String getHost() {
        return host;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<Employee> getEmployees() {
        ArrayList<Employee> employees = new ArrayList<>();
        try {
            String query = "SELECT employees.name, employees.ip, employees.raiting, employees.email, departments.name" +
                    "FROM employees LEFT JOIN departments ON employees.department_id = departments.id";
            if(!connection.isClosed()){
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    Department department = new Department(result.getString(5));
                    Employee employee = new Employee(result.getString(1), result.getString(2), result.getInt(3),
                            result.getString(4), department);
                    employees.add(employee);
                }
                return employees;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return employees;
    }
}
