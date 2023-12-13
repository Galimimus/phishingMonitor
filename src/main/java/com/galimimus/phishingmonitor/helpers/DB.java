package com.galimimus.phishingmonitor.helpers;

import com.galimimus.phishingmonitor.StartApplication;
import com.galimimus.phishingmonitor.models.*;
import com.mysql.cj.jdbc.MysqlDataSource;

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
                if(user != null){
                    log.logp(Level.INFO, "DB", "getMe", "User exists");
                }else{
                    log.logp(Level.INFO, "DB", "getMe", "User does not exist");
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

    public LinkedHashMap<Department, ArrayList<Employee>> getEmployees() {
        LinkedHashMap<Department, ArrayList<Employee>> departments = new LinkedHashMap<>();
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
                    departments.put(new Department(result_dep.getInt(1),result_dep.getString(2)), employees);
                    log.logp(Level.INFO, "DB", "getEmployees", "getEmployees ended successfully");
                }
            }else {
                log.logp(Level.WARNING, "DB", "getEmployees", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB","getEmployees", e.toString());
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
                log.logp(Level.INFO, "DB", "getEmployees", "getEmployee ended successfully");
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
                log.logp(Level.INFO, "DB", "getDepartments", "getDepartments ended successfully");
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
                    log.logp(Level.INFO, "DB", "getRecipients", "getRecipients all ended successfully");
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
                    log.logp(Level.INFO, "DB", "getRecipients", "getRecipients by department name ended successfully");
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
                log.logp(Level.INFO, "DB", "getIPs", "getIPs ended successfully");
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
        //select 1 from table where id = 1 limit 1
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

/*    public void clearLastMailing() {
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
    }*/

    public void logMailing(Mailing mailing) {
        if(mailing == null){
            log.logp(Level.WARNING, "DB", "logMailing", "Mailing object is null");
            return;
        }

        String query = "INSERT INTO mailings (mailing_time, mailing_dep_id, total_sent, total_used) VALUES (TIMESTAMP '"+mailing.getTime()+"', "+
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

    public void IncrementTotalUsed(int mailing_id, String ip) {
        if(Validation.validateSymbols(ip)){
            log.logp(Level.WARNING, "DB", "IncrementTotalUsed", "Invalid ip = "+ ip);
            return;
        }
        String query = "UPDATE mailings SET total_used = total_used + 1 WHERE id="+mailing_id
                +" AND (SELECT COUNT(*) FROM last_mailing WHERE mailing_id = "+mailing_id
                +" AND used_ip = \""+ip+"\") < 2";

        try {
            if (!connection.isClosed()) {
                Statement statement = connection.createStatement();
                int result = statement.executeUpdate(query);
                log.logp(Level.INFO, "DB", "IncrementTotalUsed", "result incrementing total used = " + result);

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
                lastID = res.getInt(1);
                log.logp(Level.INFO, "DB", "getLastMailingId", "getLastMailingId ended successfully");
            }else {
                log.logp(Level.WARNING, "DB", "getLastMailingId", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB", "getLastMailingId", e.toString());
            throw new RuntimeException(e);
        }
        return lastID;
    }

    public ArrayList<com.galimimus.phishingmonitor.models.Mailing> getMailings() {
        String query = "SELECT * FROM mailings";
        ArrayList<Mailing> mailings = new ArrayList<>();
        try {
            if (!connection.isClosed()) {
                Statement statement = connection.createStatement();
                ResultSet res = statement.executeQuery(query);
                while (res.next()){
                    Mailing mailing = new Mailing(res.getInt(1), res.getTimestamp(2), res.getInt(3), res.getInt(4), res.getInt(5));
                    mailings.add(mailing);
                }
                log.logp(Level.INFO, "DB", "getMailings", "getMailings ended successfully");
            }else {
                log.logp(Level.WARNING, "DB", "getMailings", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB", "getMailings", e.toString());
            throw new RuntimeException(e);
        }
        return mailings;
    }

    public ArrayList<LastMailing> getLastMailing(int id){
        ArrayList<LastMailing> lastMailings = new ArrayList<>();
        String query = "SELECT time_of_use, used_ip FROM last_mailing WHERE mailing_id = " + id;
        try {
            if (!connection.isClosed()) {
                Statement statement = connection.createStatement();
                ResultSet res = statement.executeQuery(query);
                while (res.next()){
                    LastMailing mailing = new LastMailing(res.getTimestamp(1), res.getString(2));
                    lastMailings.add(mailing);
                }
                log.logp(Level.INFO, "DB", "getLastMailing", "getLastMailing ended successfully");
            }else {
                log.logp(Level.WARNING, "DB", "getLastMailing", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB", "getLastMailing", e.toString());
            throw new RuntimeException(e);
        }
        return lastMailings;
    }

    public Department getDepartment(int id) {
        Department dep = null;
        String query = "SELECT name FROM departments WHERE id = "+id;
        try {
            if (!connection.isClosed()) {
                Statement statement = connection.createStatement();
                ResultSet res = statement.executeQuery(query);
                while (res.next()) {
                    dep = new Department(id, res.getString(1));
                }
                log.logp(Level.INFO, "DB", "getDepartment", "getDepartment ended successfully");
            }else {
                log.logp(Level.WARNING, "DB", "getDepartment", "Connection to database is closed");
            }
        }catch (SQLException e){
            log.logp(Level.SEVERE, "DB", "getDepartment", e.toString());
            throw new RuntimeException(e);
        }
        return dep;
    }

    public int getEmpUsedMailings(String ip) {
        Set<LastMailing> mailings = new HashSet<>();
        if(Validation.validateSymbols(ip)){
            log.logp(Level.WARNING, "DB", "getEmpUsedMailings", "Invalid ip = "+ ip);
            return -1;
        }
        String query = "SELECT mailing_id FROM last_mailing WHERE used_ip = \"" + ip+"\"";
        try {
            if (!connection.isClosed()) {
                Statement statement = connection.createStatement();
                ResultSet res = statement.executeQuery(query);
                while (res.next()){
                    LastMailing mailing = new LastMailing(res.getInt(1));
                    mailings.add(mailing);
                }
                log.logp(Level.INFO, "DB", "getEmpUsedMailings", "getEmpUsedMailings ended successfully");
            }else {
                log.logp(Level.WARNING, "DB", "getEmpUsedMailings", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB", "getEmpUsedMailings", e.toString());
            throw new RuntimeException(e);
        }
        return mailings.size();
    }

    public int getEmpTotalMailings(String ip) {
        int totalSent = 0;
        if(Validation.validateSymbols(ip)){
            log.logp(Level.WARNING, "DB", "getEmpTotalMailings", "Invalid ip = "+ ip);
            return -1;
        }

        String query = "SELECT COUNT(*) FROM mailings LEFT JOIN employees ON mailings.mailing_dep_id=employees.department_id WHERE employees.ip = \""+ip+"\"";
        try {

            if(!connection.isClosed()){
                Statement statement = connection.createStatement();
                ResultSet res = statement.executeQuery(query);
                while (res.next()) {
                    totalSent = res.getInt(1);
                }
                log.logp(Level.INFO, "DB", "getEmpTotalMailings", "getEmpTotalMailings ended successfully");
            }else {
                log.logp(Level.WARNING, "DB", "getEmpTotalMailings", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB","getEmpTotalMailings", e.toString());
            throw new RuntimeException(e);
        }
        return totalSent;
    }

    public void updateEmpRaiting(int raiting, String ip){
        if(Validation.validateSymbols(ip)){
            log.logp(Level.WARNING, "DB", "updateEmpRaiting", "Invalid ip = "+ ip);
            return;
        }
        String query = "UPDATE employees SET raiting = "+raiting+ " WHERE ip = \""+ip+"\"";
        try {

            if(!connection.isClosed()){
                Statement statement = connection.createStatement();
                int res = statement.executeUpdate(query);
                log.logp(Level.INFO, "DB", "updateEmpRaiting", "result updating employee raiting = " + res);
            }else {
                log.logp(Level.WARNING, "DB", "updateEmpRaiting", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB","updateEmpRaiting", e.toString());
            throw new RuntimeException(e);
        }
    }

    public int getAverageEmpRaiting() {
        int raiting = 0;

        String query = "SELECT AVG(raiting) FROM employees";
        try {

            if(!connection.isClosed()){
                Statement statement = connection.createStatement();
                ResultSet res = statement.executeQuery(query);
                while (res.next()) {
                    raiting = res.getInt(1);
                }
                log.logp(Level.INFO, "DB", "getAverageEmpRaiting", "getAverageEmpRaiting ended successfully");
            }else {
                log.logp(Level.WARNING, "DB", "getAverageEmpRaiting", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB","getAverageEmpRaiting", e.toString());
            throw new RuntimeException(e);
        }
        return raiting;
    }

    public int getLastMailingRaiting() {
        int lastId = getLastMailingId();
        int raiting = 10;

        String query = "SELECT total_sent, total_used FROM mailings WHERE id = " + lastId;
        try {

            if(!connection.isClosed()){
                Statement statement = connection.createStatement();
                ResultSet res = statement.executeQuery(query);
                while (res.next()) {
                    if(res.getInt(1) !=0) {
                        raiting = (int) (10 - ((double)res.getInt(2) / res.getInt(1)) * 10);
                    }
                }
                log.logp(Level.INFO, "DB", "getLastMailingRaiting", "getLastMailingRaiting ended successfully");
            }else {
                log.logp(Level.WARNING, "DB", "getLastMailingRaiting", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB","getLastMailingRaiting", e.toString());
            throw new RuntimeException(e);
        }
        return raiting;
    }
    public HashMap<String, Integer> getMailingsRaiting() {
        HashMap<String, Integer> raiting = new HashMap<>();

        String query = "SELECT mailing_time, total_sent, total_used FROM mailings";
        try {

            if(!connection.isClosed()){
                Statement statement = connection.createStatement();
                ResultSet res = statement.executeQuery(query);
                while (res.next()) {
                    int tmp = 10;
                    if(res.getInt(2) != 0) {
                        tmp = (int) (10 - ((double)res.getInt(3) / res.getInt(2)) * 10);
                    }
                    raiting.put(res.getString(1), tmp);
                }
                log.logp(Level.INFO, "DB", "getMailingsRaiting", "getMailingsRaiting ended successfully");
            }else {
                log.logp(Level.WARNING, "DB", "getMailingsRaiting", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB","getMailingsRaiting", e.toString());
            throw new RuntimeException(e);
        }
        return raiting;
    }

    public HashMap<String, Integer> getDepsRaiting() {
        HashMap<String, Integer> raitings = new HashMap<>();


        String query = "SELECT d.name AS department_name, AVG(e.raiting) AS average_rating FROM departments d " +
                "JOIN employees e ON d.id = e.department_id GROUP BY d.id, d.name";
        try {

            if(!connection.isClosed()){
                Statement statement = connection.createStatement();
                ResultSet res = statement.executeQuery(query);
                while (res.next()) {
                    raitings.put(res.getString(1), res.getInt(2));
                }
                log.logp(Level.INFO, "DB", "getDepsRaiting", "getDepsRaiting ended successfully");
            }else {
                log.logp(Level.WARNING, "DB", "getDepsRaiting", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB","getDepsRaiting", e.toString());
            throw new RuntimeException(e);
        }

        return raitings;
    }

    public int setDepartment(String name) {
        if(Validation.validateSymbols(name)){
            log.logp(Level.WARNING, "DB", "setDepartment", "Invalid name = "+ name);
            return 0;
        }
        String query = "INSERT INTO departments (name) VALUES (\""+name+"\")";
        try {

            if(!connection.isClosed()){
                Statement statement = connection.createStatement();
                int res = statement.executeUpdate(query);
                log.logp(Level.INFO, "DB", "setDepartment", "result inserting new department = " + res);
            }else {
                log.logp(Level.WARNING, "DB", "setDepartment", "Connection to database is closed");
                return 0;
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB","setDepartment", e.toString());
            throw new RuntimeException(e);
        }
        return 1;
    }

    public int setEmployee(String name, String ip, String email, int depId) {
        if(Validation.validateSymbols(name) || Validation.validateSymbols(ip) ||Validation.validateSymbols(email)){
            log.logp(Level.WARNING, "DB", "setEmployee", "Invalid data: name = "+ name+" ip = "+ip+" email = "+ email);
            return 0;
        }

        //String query = "SELECT id FROM departments WHERE name = \""+depName+"\"";
        String query = "INSERT INTO employees (name, ip, email, department_id) VALUES (\""+name+"\", \""+ip+"\", \""+email+"\", "+depId+")";
        try {

            if(!connection.isClosed()){
/*                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(query);
                result.next();
                if(result.getInt(1) == 0){
                    log.logp(Level.INFO, "DB", "setEmployee", "no department found = " + depName);
                    return 2;
                }*/
                Statement statement = connection.createStatement();
                int res = statement.executeUpdate(query);
                log.logp(Level.INFO, "DB", "setEmployee", "result inserting new employee = " + res);
            }else {
                log.logp(Level.WARNING, "DB", "setEmployee", "Connection to database is closed");
                return 0;
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB","setEmployee", e.toString());
            throw new RuntimeException(e);
        }
        return 1;

    }

    public void DeleteEmployee(int id) {
        String query = "DELETE FROM employees WHERE id = "+id;
        try {
            if (!connection.isClosed()) {
                Statement statement = connection.createStatement();
                int result = statement.executeUpdate(query);
                log.logp(Level.INFO, "DB", "DeleteEmployee", "result deleting employee with id = "+id+" is = " + result);
            }else {
                log.logp(Level.WARNING, "DB", "DeleteEmployee", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB", "DeleteEmployee", e.toString());
            throw new RuntimeException(e);
        }
    }

    public void DeleteDepartment(int id) {
        String query = "DELETE FROM departments WHERE id = "+id;
        try {
            if (!connection.isClosed()) {
                Statement statement = connection.createStatement();
                int result = statement.executeUpdate(query);
                log.logp(Level.INFO, "DB", "DeleteDepartment", "result deleting department with id = "+id+" is = " + result);
            }else {
                log.logp(Level.WARNING, "DB", "DeleteDepartment", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB", "DeleteDepartment", e.toString());
            throw new RuntimeException(e);
        }
    }

    public int updateEmployee(String name, String ip, String email, String depName) {
        if(Validation.validateSymbols(name) || Validation.validateSymbols(ip) ||Validation.validateSymbols(email)||Validation.validateSymbols(depName)){
            log.logp(Level.WARNING, "DB", "updateEmployee", "Invalid data: name = "+ name+" ip = "+ip+" email = "+ email+" depName = "+ depName);
            return 0;
        }

        String query = "SELECT id FROM departments WHERE name = \""+depName+"\"";
        try {

            if(!connection.isClosed()){
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(query);
                result.next();
                if(result.getInt(1) == 0){
                    log.logp(Level.INFO, "DB", "updateEmployee", "no department found = " + depName);
                    return 2;
                }
                query = "UPDATE employees SET name = \""+name+"\", ip = \""+ip+"\", email = \""+email+"\" WHERE department_id = "+result.getInt(1);
                statement = connection.createStatement();
                int res = statement.executeUpdate(query);
                log.logp(Level.INFO, "DB", "updateEmployee", "result updating employee = " + res);
            }else {
                log.logp(Level.WARNING, "DB", "updateEmployee", "Connection to database is closed");
                return 0;
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB","updateEmployee", e.toString());
            throw new RuntimeException(e);
        }
        return 1;
    }

    public int updateDepartment(String name, int id) {
        if(Validation.validateSymbols(name)){
            log.logp(Level.WARNING, "DB", "updateDepartment", "Invalid name = "+ name);
            return 0;
        }
        String query = "UPDATE departments SET name = \""+name+"\" WHERE id = "+id;
        try {

            if(!connection.isClosed()){
                Statement statement = connection.createStatement();
                int res = statement.executeUpdate(query);
                log.logp(Level.INFO, "DB", "updateDepartment", "result updating department = " + res);
            }else {
                log.logp(Level.WARNING, "DB", "updateDepartment", "Connection to database is closed");
                return 0;
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB","updateDepartment", e.toString());
            throw new RuntimeException(e);
        }
        return 1;
    }

    public void DeleteMailing(int id) {
        String query = "DELETE FROM mailings WHERE id = "+id;
        try {
            if (!connection.isClosed()) {
                Statement statement = connection.createStatement();
                int result = statement.executeUpdate(query);
                log.logp(Level.INFO, "DB", "DeleteMailing", "result deleting mailing with id = "+id+" is = " + result);
            }else {
                log.logp(Level.WARNING, "DB", "DeleteMailing", "Connection to database is closed");
            }
        } catch (SQLException e) {
            log.logp(Level.SEVERE, "DB", "DeleteMailing", e.toString());
            throw new RuntimeException(e);
        }
    }
}



