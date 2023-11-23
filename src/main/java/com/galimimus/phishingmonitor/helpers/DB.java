package com.galimimus.phishingmonitor.helpers;

import com.galimimus.phishingmonitor.models.Department;
import com.galimimus.phishingmonitor.models.Employee;
import com.galimimus.phishingmonitor.models.User;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
        User user = null;
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
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
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

    public HashMap<String, ArrayList<Employee>> getEmployees() {
        HashMap<String, ArrayList<Employee>> departments = new HashMap<>();
        try {
            //String query = "SELECT employees.name, employees.ip, employees.raiting, employees.email, departments.name" +
            //        "FROM employees LEFT JOIN departments ON employees.department_id = departments.id ORDER BY departments.name ASC";
            String query = "SELECT * FROM departments";
            if(!connection.isClosed()){
                Statement statement = connection.createStatement();
                ResultSet result_dep = statement.executeQuery(query);
                while (result_dep.next()) {
                    //Department department = new Department(result.getString(5));
                    query = "SELECT id, name FROM employees WHERE department_id = " + result_dep.getInt(1);
                    statement = connection.createStatement();
                    ResultSet result_emp = statement.executeQuery(query);
                    ArrayList<Employee> employees = new ArrayList<>();
                    while (result_emp.next()) {
                        Employee employee = new Employee(result_emp.getInt(1), result_emp.getString(2));
                        employees.add(employee);
                    }
                    //System.out.println("\n"+result_dep.getString(1) + result_dep.getString(2)+"\n");
                    departments.put(result_dep.getString(2), employees);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return departments;
    }

    public Employee getEmployee(String emp_id) {
        Employee emp = null;
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(emp_id);
        String id = "";
        while (matcher.find()) {
            id = matcher.group();
        }
        try {
            String query = "SELECT employees.name, employees.ip, employees.raiting, employees.email, departments.name" +
                    " FROM employees LEFT JOIN departments ON employees.department_id = departments.id WHERE employees.id = " + id;

            if(!connection.isClosed()){
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    Department department = new Department(result.getString(5));
                    emp = new Employee(result.getString(1), result.getString(2), result.getInt(3), result.getString(4), department);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return emp;
    }

    public ArrayList<Department> getDepartments() {
        ArrayList<Department> deps = new ArrayList<>();
        String query = "SELECT * FROM departments";
        try {
            if (!connection.isClosed()) {
                Statement statement = connection.createStatement();
                ResultSet result_dep = statement.executeQuery(query);
                while (result_dep.next()) {
                    Department department = new Department(result_dep.getString(2));
                    deps.add(department);
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return deps;
    }

    public ArrayList<Employee> getRecipients(String recipients) {
        ArrayList<Employee> employees = new ArrayList<>();
        String query = "";
        if(Validation.isNullOrEmpty(recipients)){
            return null;// TODO: лог и обработка
        }
        if (Objects.equals(recipients, "все")){
            query = "SELECT ip, email FROM employees";
        }else {
            query = "SELECT ip, email FROM employees LEFT JOIN departments ON employees.department_id = departments.id WHERE departments.name = " + recipients;
        }
        try {
            if (!connection.isClosed()) {
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    Employee emp = new Employee(result.getString(1),result.getString(2));
                    employees.add(emp);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return employees;
    }
}
