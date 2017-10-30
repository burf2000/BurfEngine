//package com.burfdevelopment.burfworld.Database;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
///**
// * Created by burfies1 on 28/07/15.
// */
//public class SQLMessage extends SQLHelper {
//
//    private String message;
//    private String sender;
//    private int status;
//    private Long updateTime;
//    private int id;
//    private String from;
//
//    public String getFrom() {
//        return from;
//    }
//
//    public void setFrom(String from) {
//        this.from = from;
//    }
//
//    public String getSender() {
//        return sender;
//    }
//
//    public void setSender(String sender) {
//        this.sender = sender;
//    }
//
//    public int getStatus() {
//        return status;
//    }
//
//    public void setStatus(int status) {
//        this.status = status;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public Long getUpdateTime() {
//        return updateTime;
//    }
//
//    public void setUpdateTime(Long updateTime) {
//        this.updateTime = updateTime;
//    }
//
//
//    public static final String KEY_MESSAGE = "message";
//    public static final String KEY_ID = "_id";
//    public static final String KEY_DATE = "update_date";
//    public static final String KEY_FROM = "from";
//    public static final String KEY_TO = "to";
//    public static final String KEY_STATUS = "status";
//
//    private final String tableName = "Messages";
//
//    public SQLMessage(String DATABASE_NAME) {
//        super(DATABASE_NAME);
//
//        final String CREATE_MESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS "
//                + tableName + "(" + KEY_ID
//                + " INTEGER PRIMARY KEY NOT NULL," + KEY_MESSAGE + " TEXT,"
//                + KEY_FROM + " TEXT," + KEY_TO + " TEXT," + KEY_DATE + " TEXT,"
//                + KEY_STATUS + " INTEGER)";
//
//        execUpdate(CREATE_MESSAGES_TABLE);
//    }
//
//    public SQLMessage(int id, String message, String from, String to, long date, int status) {
//
//        super();
//
//        this.id = id;
//        this.sender = to;
//        this.from = from;
//        this.updateTime = date;
//        this.message = message;
//        this.status = status;
//
//    }
//
//    public void addMessage(SQLMessage m) {
//        final String ADD_MESSAGES = "INSERT into " + tableName + " VALUES('"
//                + m.getId() + "','" + m.getMessage() + "', '" + m.getSender()
//                + m.getUpdateTime() + "', '" + m.getStatus()
//                + "')";
//        execUpdate(ADD_MESSAGES);
//    }
//
//    public SQLMessage getMessage(int id) {
//        ResultSet rs = execQuery("SELECT * FROM " + tableName + " WHERE "
//                + KEY_ID + " = " + String.valueOf(id));
//        try {
//            if (rs.next()) {
//                SQLMessage m = messageFromResult(rs);
//                return m;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public SQLMessage messageFromResult(ResultSet rs) {
//        try {
//            return new SQLMessage(rs.getInt(KEY_ID),
//                    rs.getString(KEY_MESSAGE),
//                    rs.getString(KEY_FROM),
//                    rs.getString(KEY_TO),
//                    rs.getLong(KEY_DATE),
//                    rs.getInt(KEY_STATUS));
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//
//}
