package com.galimimus.phishingmonitor.helpers;

import com.galimimus.phishingmonitor.StartApplication;
import com.galimimus.phishingmonitor.models.Department;
import com.galimimus.phishingmonitor.models.Employee;
import com.galimimus.phishingmonitor.models.Mailing;
import com.galimimus.phishingmonitor.models.User;
import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.Setter;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;


public class DB {

    private final String db_name;
    private final String host;
    private final String username;
    private final String password;

    private  Connection connection;
    private final MysqlDataSource datasource = new MysqlDataSource();
    static final Logger log = Logger.getLogger(StartApplication.class.getName());

    public DB(){
        SettingsSingleton ss = SettingsSingleton.getInstance();
        db_name = ss.getDB_NAME();
        host = ss.getDB_HOST();
        username = ss.getDB_USERNAME();
        password = ss.getDB_PASS();
    }
    public void connect() {
            datasource.setPassword(password);
            datasource.setUser(username);
            datasource.setServerName(host);
            datasource.setDatabaseName(db_name);
            try {
                connection = datasource.getConnection();
                log.logp(Level.INFO, "DB", "connect", "Connection to database established");
            } catch (SQLException e) {
                log.logp(Level.SEVERE, "DB", "connect", e.toString());
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
            }else {
                log.logp(Level.WARNING, "DB", "getMe", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB","getMe", e.toString());
            throw new RuntimeException(e);
        }
        return user;
    }

    public void close() {
        try {
            connection.close();
            log.logp(Level.INFO, "DB","close", "Connection closed");
        }catch(SQLException e) {
            log.logp(Level.SEVERE, "DB","close", e.toString());
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, ArrayList<Employee>> getEmployees() {
        HashMap<String, ArrayList<Employee>> departments = new HashMap<>();
        try {
            String query = "SELECT * FROM departments";
            if(!connection.isClosed()){
                Statement statement = connection.createStatement();
                ResultSet result_dep = statement.executeQuery(query);
                while (result_dep.next()) {
                    query = "SELECT id, name FROM employees WHERE department_id = " + result_dep.getInt(1);
                    statement = connection.createStatement();
                    ResultSet result_emp = statement.executeQuery(query);
                    ArrayList<Employee> employees = new ArrayList<>();
                    while (result_emp.next()) {
                        Employee employee = new Employee(result_emp.getInt(1), result_emp.getString(2));
                        employees.add(employee);
                    }
                    departments.put(result_dep.getString(2), employees);
                }
            }else {
                log.logp(Level.WARNING, "DB", "getEmployees", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB","getEmployee", e.toString());
            throw new RuntimeException(e);
        }
        return departments;
    }

    public Employee getEmployee(String emp_id) {
        Employee emp = null;
        if(Validation.validatePattern(emp_id, Pattern.compile("\\d+"))){
            log.logp(Level.WARNING, "DB", "getEmployee", "Invalid employee id = "+ emp_id);
            return null;
        }
        try {
            String query = "SELECT employees.name, employees.ip, employees.raiting, employees.email, departments.name" +
                    " FROM employees LEFT JOIN departments ON employees.department_id = departments.id WHERE employees.id = " + emp_id;

            if(!connection.isClosed()){
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    Department department = new Department(result.getString(5));
                    emp = new Employee(result.getString(1), result.getString(2), result.getInt(3), result.getString(4), department);
                }
            }else {
                log.logp(Level.WARNING, "DB", "getEmployee", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB", "getEmployee", e.toString());
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
            }else {
                log.logp(Level.WARNING, "DB", "getDepartments", "Connection to database is closed");
            }
        }catch (SQLException e){
            log.logp(Level.SEVERE, "DB", "getDepartments", e.toString());
            throw new RuntimeException(e);
        }
        return deps;
    }

    public ArrayList<Employee> getRecipients(String recipients) {
        ArrayList<Employee> employees = new ArrayList<>();
        String query;
        Department dep;
        if(Validation.isNullOrEmpty(recipients)){
            log.logp(Level.WARNING, "DB", "getRecipients", "Recipients is null or empty");
            return null;
        }
        if(Validation.validateSymbols(recipients)){
            log.logp(Level.WARNING, "DB", "getRecipients", "Recipients contains invalid symbols. resipients = " + recipients);
            return null;
//TODO: При добавлении в бд предусмотреть проверку на валидность имени
        }
        if (Objects.equals(recipients, "все")){
            query = "SELECT ip, email FROM employees";
            dep = new Department(0, "все");
            try {
                if (!connection.isClosed()) {
                    Statement statement = connection.createStatement();
                    ResultSet result = statement.executeQuery(query);
                    while (result.next()) {
                        Employee emp = new Employee(result.getString(1),result.getString(2), dep);
                        employees.add(emp);
                    }
                }else {
                    log.logp(Level.WARNING, "DB", "getRecipients", "Connection to database is closed");
                }
            } catch (SQLException e) {
                log.logp(Level.SEVERE, "DB", "getRecipients", e.toString());
                throw new RuntimeException(e);
            }
        }else {
            query = "SELECT ip, email, department_id FROM employees LEFT JOIN departments ON employees.department_id = departments.id WHERE departments.name = \"" + recipients + "\"";
            try {
                if (!connection.isClosed()) {
                    Statement statement = connection.createStatement();
                    ResultSet result = statement.executeQuery(query);
                    while (result.next()) {
                        dep = new Department(result.getInt(3), recipients);
                        Employee emp = new Employee(result.getString(1),result.getString(2), dep);
                        employees.add(emp);
                    }
                }else {
                    log.logp(Level.WARNING, "DB", "getRecipients", "Connection to database is closed");
                }
            } catch (SQLException e) {
                log.logp(Level.SEVERE, "DB", "getRecipients", e.toString());
                throw new RuntimeException(e);
            }
        }
        return employees;
    }

    public ArrayList<String> getIPs(int dep_id) {
        ArrayList<String> ips = new ArrayList<>();
        String query;
        if (dep_id == 0) {
            query = "SELECT ip FROM employees";
        }else if (dep_id > 0){
            query = "SELECT ip FROM employees WHERE department_id = " + dep_id;
        }
        else {
            log.logp(Level.WARNING, "DB", "getIPs", "Invalid department id = " + dep_id);
            return null;
        }

        try {
            if (!connection.isClosed()) {
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    String ip = result.getString(1);
                    ips.add(ip);
                }
            }else {
                log.logp(Level.WARNING, "DB", "getIPs", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB", "getIPs", e.toString());
            throw new RuntimeException(e);
        }
        return ips;
    }

    public void logLastMailing(String ip, int mailing_id) {
        if(Validation.validateSymbols(ip)){
            log.logp(Level.WARNING, "DB", "logLastMailing", "Invalid ip = "+ ip);
            return;
        }
        Calendar time = Calendar.getInstance();
        Timestamp timestamp = new java.sql.Timestamp(time.getTimeInMillis());
        String query = "INSERT INTO last_mailing (time_of_use, used_ip, mailing_id) VALUES (TIMESTAMP '"+timestamp+"', \""+ip+"\", "+mailing_id+")";

        try {
            if (!connection.isClosed()) {
                Statement statement = connection.createStatement();
                int result = statement.executeUpdate(query);
                log.logp(Level.INFO, "DB", "logLastMailing", "result inserting last mailing = " + result);
            }else {
                log.logp(Level.WARNING, "DB", "logLastMailing", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB", "logLastMailing", e.toString());
            throw new RuntimeException(e);
        }
    }

    public void clearLastMailing() {
        String query = "DELETE FROM last_mailing";
        try {
            if (!connection.isClosed()) {
                Statement statement = connection.createStatement();
                int result = statement.executeUpdate(query);
                log.logp(Level.INFO, "DB", "clearLastMailing", "result clearing last mailing table = " + result);
            }else {
                log.logp(Level.WARNING, "DB", "clearLastMailing", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB", "clearLastMailing", e.toString());
            throw new RuntimeException(e);
        }
    }

    public void logMailing(Mailing mailing) {
        if(mailing == null){
            log.logp(Level.WARNING, "DB", "logMailing", "Mailing object is null");
            return;
        }

        Timestamp timestamp = new Timestamp(mailing.getTime());
        System.out.println(timestamp);
        String query = "INSERT INTO mailings (mailing_time, mailing_dep_id, total_sent, total_used) VALUES (TIMESTAMP '"+timestamp+"', "+
                mailing.getDep_id()+", "+mailing.getTotal_sent()+", 0)";

        try {
            if (!connection.isClosed()) {
                Statement statement = connection.createStatement();
                int result = statement.executeUpdate(query);
                log.logp(Level.INFO, "DB", "logMailing", "result inserting mailing = " + result);
            }else {
                log.logp(Level.WARNING, "DB", "logMailing", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB", "logMailing", e.toString());
            throw new RuntimeException(e);
        }
    }

    public void IncrementTotalUsed(int mailing_id) {
        String query = "UPDATE mailings SET total_used = total_used + 1 WHERE id="+mailing_id;

        try {
            if (!connection.isClosed()) {
                Statement statement = connection.createStatement();
                int result = statement.executeUpdate(query);
                log.logp(Level.WARNING, "DB", "IncrementTotalUsed", "result incrementing total used = " + result);

            }else {
                log.logp(Level.WARNING, "DB", "IncrementTotalUsed", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB", "IncrementTotalUsed", e.toString());
            throw new RuntimeException(e);
        }
    }

    public int getLastMailingId() {
        String query = "SELECT MAX(id) FROM mailings";
        int lastID = 0;
        try {
            if (!connection.isClosed()) {
                Statement statement = connection.createStatement();
                ResultSet res = statement.executeQuery(query);
                res.next();
                lastID = Integer.parseInt(res.getString(1));
            }else {
                log.logp(Level.WARNING, "DB", "getLastMailingId", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB", "getLastMailingId", e.toString());
            throw new RuntimeException(e);
        }
        System.out.println("getLastMailingID ended");
        return lastID;
    }
}
//TODO:
// Настроить ПКМ,
// добавление в бд сотрудников и отделов.
// Вычисление статистики и ее показ в окне.
// Вывод прошедших рассылок в окне.
// Проверить все ли валидируется.


