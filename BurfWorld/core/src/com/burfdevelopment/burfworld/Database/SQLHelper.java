//package com.burfdevelopment.burfworld;
//
///**
// * Created by burfies1 on 28/07/15.
// */
//import SQLite.JDBCDriver;
//
//import java.io.File;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//public class SQLHelper {
//
//    private String DATABASE_NAME;
//    private JDBCDriver driv;
//    private Connection connection;
//
//    final String DB_PATH = new File(System.getenv("HOME"), "Library/")
//            .getAbsolutePath();
//
//    public SQLHelper(String DATABASE_NAME) {
//        this.DATABASE_NAME = DATABASE_NAME;
//        driv = new JDBCDriver();
//        try {
//            connection = driv.connect("sqlite:/" + DB_PATH + this.DATABASE_NAME, null);
//        } catch (SQLException e) {
//        }
//    }
//
//    public SQLHelper() {
//    }
//
//    public void execUpdate(String sql) {
////        Util.log("Running SQL " + sql);
//        try {
//            Statement statement = connection.createStatement();
//            statement.executeUpdate(sql);
//            //connection.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public ResultSet execQuery(String query) {
////        Util.log("Running SQL query " + query);
//        try {
//            Statement statement = connection.createStatement();
//            ResultSet rs = statement.executeQuery(query);
//            return rs;
//        } catch (SQLException e) {
//            return null;
//        }
//    }
//
//}
