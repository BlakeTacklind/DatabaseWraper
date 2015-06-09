package com.dank.exchange.backend;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DatabaseWrapper{
    private static Connection conn;
    private static int userID;
    private static int timeOut = 1000;
    private static int timeOutLong = 5000;
    final private static String url = "jdbc:postgresql://serenity.isozilla.com:5432/"+
            "parcelexchange?sslfactory=org.postgresql.ssl.NonValidatingFactory" +
            "&ssl=true";
    final private static String username = "parcelexchange";
    final private static String password = "Mabc0NDkYRf1yVyIfhRd";

    private static void start() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        conn = DriverManager.getConnection(url, username, password);

        if (conn == null || conn.isClosed()){
            Log.e("Test", "Connection failed!");
        }

    }
    private static void stop(){
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
    private static abstract class SELECT<OutputType> extends AsyncTask<Integer, Integer, OutputType> {
        private String sql;

        public SELECT(String SQLquerry){
            super();
            sql = SQLquerry;
        }

        protected abstract void middle(ResultSet rs) throws SQLException;
        protected abstract OutputType endBackground();
        protected void postRead(){}

        protected OutputType doInBackground(Integer... in){
            ArrayList<User> output = new ArrayList<User>();

            Statement st = null;
            ResultSet rs = null;
            try {
                start();
                st = conn.createStatement();
                rs = st.executeQuery(sql);

                while(rs.next()) {
                    middle(rs);
                }
                postRead();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (rs != null)
                        rs.close();
                    if (st != null)
                        st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                stop();
            }

            return endBackground();
        }
    }
    private static abstract class ALTER<ReturnType> extends AsyncTask<Integer, Integer, ReturnType>{
        private String sql;

        protected ALTER(String SQLstatement){
            sql = SQLstatement;
        }

        protected abstract void setValues(PreparedStatement st) throws SQLException;
        protected abstract ReturnType returnThis();

        protected ReturnType doInBackground(Integer... params) {
            PreparedStatement st = null;
            try {
                start();
                st = conn.prepareStatement(sql);
                setValues(st);
                st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                if (st != null)
                    try {
                        st.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                stop();
            }

            return returnThis();
        }
    }

    /*
    Give your user name
    prints error if failed
    */
    public static boolean logIn(String name) throws TimeoutException {

        logInTask l = new logInTask(name);

        try {
            l.execute().get(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (userID == 0)
            return false;

        return true;
    }
    private static class logInTask extends SELECT<Integer>{

        public logInTask(String name) {
            super("SELECT userid FROM users WHERE username = '" + name + "';");
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            userID = rs.getInt("userid");
        }

        @Override
        protected Integer endBackground() {
            return userID;
        }
    }

    /*
    Add the user by string
    return true if success
    */
    public static boolean addUser(String name) throws TimeoutException {

        addUserTask s = new addUserTask(name);
        try {
            s.execute().get(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        while(userID == 0);

        if (userID == -1)
            return false;

        return true;
    }
    private static class addUserTask extends SELECT<Integer>{

        public addUserTask(String name) {
            super("SELECT \"adduser\" ('" + name +"')");
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            userID = rs.getInt("adduser");
        }

        @Override
        protected Integer endBackground() {
            return userID;
        }
    }

    /*
    Delete user (self)
    returns true if successful
    */
    public static boolean deleteSelf() throws TimeoutException, NotLoggedInException {
        if (userID == 0) throw new NotLoggedInException();

        deleteSelfTask d = new deleteSelfTask();

        try {
            d.execute().get(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(d.returned == 0)
            return false;

        userID = 0;

        return true;
    }
    private static class deleteSelfTask extends SELECT<Integer>{
        private int returned;
        public deleteSelfTask() {
            super("SELECT \"removeuser\" ('" + userID +"')");
            returned = -1;
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            returned = rs.getInt("removeuser");
        }

        @Override
        protected Integer endBackground() {
            return returned;
        }
    }

	/*
	return your list of friends
	may return null if failed
    */
    public static ArrayList<User> getFriends() throws TimeoutException, NotLoggedInException {
        if (userID == 0) throw new NotLoggedInException();

        getFriendsTask f = new getFriendsTask();
        try {
            return f.execute().get(timeOutLong, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }
    private static class getFriendsTask extends SELECT<ArrayList<User>>{
        ArrayList<User> output;

        public getFriendsTask() {
            super("SELECT * FROM users WHERE " +
                    "userid = ANY(SELECT user1 FROM friendship WHERE user2 = "+userID+") OR " +
                    "userid = ANY(SELECT user2 FROM friendship WHERE user1 = "+userID+") " +
                    "ORDER BY username;");
            output = new ArrayList<User>();
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            output.add(new User(rs.getInt("userid"), rs.getString("username")));
        }

        @Override
        protected ArrayList<User> endBackground() {
            return output;
        }
    }

    /*
    returns mutual friends of logged in user and user with id
	may return null if failed
    */
    public static ArrayList<User> getMutualFriends(int id) throws TimeoutException, NotLoggedInException {
        if (userID == 0) throw new NotLoggedInException();

        getMutualTask mf = new getMutualTask(id);

        try {
            return mf.execute().get(timeOutLong, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static class getMutualTask extends SELECT<ArrayList<User>>{
        private ArrayList<User> out;
        public getMutualTask(int id) {
            super("SELECT * FROM users WHERE " +
                    "userid = ANY(SELECT user1 FROM friendship WHERE user2 = "+userID+") OR " +
                    "userid = ANY(SELECT user2 FROM friendship WHERE user1 = "+userID+") " +
                    "INTERSECT " +
                    "SELECT * FROM users WHERE " +
                    "userid = ANY(SELECT user1 FROM friendship WHERE user2 = "+id+") OR " +
                    "userid = ANY(SELECT user2 FROM friendship WHERE user1 = "+id+") " +
                    "ORDER BY username;");
            out = new ArrayList<User>();
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException{
            out.add(new User(rs.getInt("userid"), rs.getString("username")));
        }

        @Override
        protected ArrayList<User> endBackground() {
            return out;
        }
    }

    /*
    Give a user id (or name, not preferable)
    returns contents of knapsack
    */
    public static ArrayList<Item> getKnapsack(int id) throws TimeoutException {
        getKnapsackTask k = new getKnapsackTask(id);

        try {
            return k.execute().get(timeOutLong,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }
    private static class getKnapsackTask extends SELECT<ArrayList<Item>>{
        private ArrayList<Item> output;

        public getKnapsackTask(int id) {
            super("SELECT * FROM items WHERE heldby = "+id+" AND \"inTrade\" = FALSE;");
            output = new ArrayList<Item>();
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            output.add(new Item(rs.getInt("id"), rs.getString("name")));
        }

        @Override
        protected ArrayList<Item> endBackground() {
            return output;
        }
    }

    /*
    add item named string to knapsack
    returns true if posted
    */
    public static boolean addToKnapsack(String itemName) throws TimeoutException, NotLoggedInException {
        if (userID == 0) throw new NotLoggedInException();

        try {
            if (new addToKnapsackTack(itemName).execute().get(timeOutLong,TimeUnit.MILLISECONDS) > 0)
                return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
    private static class addToKnapsackTack extends SELECT<Integer>{
        private int itemNum;

        public addToKnapsackTack(String name) {
            super("SELECT \"additem\" ('" + name + "', " + userID + ");");
            itemNum = -1;
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            rs.getInt("additem");
        }

        @Override
        protected Integer endBackground() {
            return itemNum;
        }
    }

    private static final String requestsQuery = "SELECT requests.id, \"type\", " +
            "\"from\", u1.username AS \"fromName\", \"to\", u2.username AS \"toName\", " +
            "extra1, ARRAY(SELECT items.name FROM items JOIN requests AS r1 " +
            "ON items.id = ANY(r1.extra1) WHERE r1.id = requests.id) AS items1, " +
            "extra2, ARRAY(SELECT items.name FROM items JOIN requests AS r1 " +
            "ON items.id = ANY(r1.extra2) WHERE r1.id = requests.id) AS items2, " +
            "\"extraInt\", u3.username AS \"mmName\", extrastring " +
            "FROM requests " +
            "JOIN users AS u1 ON requests.from = u1.userid " +
            "JOIN users AS u2 ON requests.to = u2.userid " +
            "LEFT JOIN users AS u3 ON requests.\"extraInt\" = u3.userid";

    /*
    remove item with id from knapsack
    returns true if removed successfully
    */
    public static boolean removeFromKnapsack(int itemNumber) throws TimeoutException, NotLoggedInException {
        if (userID == 0) throw new NotLoggedInException();

        try {
            return new removeItemTask(itemNumber).execute().get(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }
    private static class removeItemTask extends SELECT<Boolean>{
        private Boolean ret;
        public removeItemTask(int i) {
            super("SELECT \"removeitem\" ("+i+", "+userID+");");
            ret = false;
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            ret = rs.getBoolean("removeitem");
        }

        @Override
        protected Boolean endBackground() {
            return ret;
        }
    }

    /*
    Get requests TO the logged in user
    Returns the list of requests
     */
    public static ArrayList<Request> getRequestsTo() throws TimeoutException, NotLoggedInException {
        if (userID == 0)throw new NotLoggedInException();

        getRequestToTask rq = new getRequestToTask();

        try {
            return rq.execute().get(timeOutLong, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return new ArrayList<Request>();
    }
    private static class getRequestToTask extends SELECT<ArrayList<Request>>{
        private ArrayList<Request> output;

        public getRequestToTask() {
            super(requestsQuery + " WHERE \"to\" = "+userID+";");
            output = new ArrayList<Request>();
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            output.add(getRequest(rs));
        }

        @Override
        protected ArrayList<Request> endBackground() {
            return output;
        }
    }

    /*
    Get requests FROM the logged in user
    Returns the list of requests
     */
    public static ArrayList<Request> getRequestsFrom() throws TimeoutException, NotLoggedInException{
        if (userID == 0) throw new NotLoggedInException();

        getRequestsFromTask rf = new getRequestsFromTask();

        try {
            return rf.execute().get(timeOutLong,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }
    private static class getRequestsFromTask extends SELECT<ArrayList<Request>>{
        private ArrayList<Request> output;

        public getRequestsFromTask() {
            super(requestsQuery+" WHERE \"from\" = "+userID+";");
            output = new ArrayList<Request>();
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            output.add(getRequest(rs));
        }

        @Override
        protected ArrayList<Request> endBackground() {
            return output;
        }
    }

    /*
    gets a request from current iteration of a result set
    */
    private static Request getRequest (ResultSet rs) throws SQLException {
        int type = rs.getInt("type");
        int id = rs.getInt("id");
        User u1 = new User(rs.getInt("from"), rs.getString("fromName"));
        User u2 = new User(rs.getInt("to"), rs.getString("toName"));

        switch (type){
            case 1:
            case 2:
            case 3:
            case 11:
            case 14:
                return new Request(id, type, u1, u2, null, null, null, null);
            case 10:
                return new Request(id, type, u1, u2, null,
                        ArraysToItems(rs.getArray("extra1"), rs.getArray("items1")),
                        ArraysToItems(rs.getArray("extra2"), rs.getArray("items2")), null);
            case 12:
            case 13:
            case 15:
                return new Request(id, type, u1, u2, null,
                        ArraysToItems(rs.getArray("extra1"), rs.getArray("items1")),
                        ArraysToItems(rs.getArray("extra2"), rs.getArray("items2")),
                        rs.getString("extrastring"));
            case 25:
            default:
                Log.e("getRequest", "Yell at Blake: Request Type not implemented!");
                return new Request(rs.getInt("id"), type, u1, u2, new User(rs.getInt("extraInt"), rs.getString("mmName")), null, null, null);
        }
    }
    private static ArrayList<Item> ArraysToItems (Array ia, Array sa) throws SQLException {
        Integer[] intArr = (Integer[]) ia.getArray();
        String[] strArr = (String[]) sa.getArray();

        if (intArr.length != strArr.length){
            Log.e("ArraysToItems", "Not matching number of id to name for items!");
            return null;
        }

        ArrayList<Item> arr = new ArrayList<Item>(intArr.length);

        for (int i = 0; i < intArr.length; i++){
            arr.add(new Item(intArr[i], strArr[i], true));
        }

        return arr;
    }

    /*
    Pass username to befriend
    returns true if successfully posted
    */
    public static boolean requestFriendship(String name) throws TimeoutException, NotLoggedInException {
        if (userID == 0)throw new NotLoggedInException();

        addFriendTask af = new addFriendTask(name);

        try {
            if (af.execute().get(timeOutLong, TimeUnit.MILLISECONDS) > 0)
                return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }
    private static class addFriendTask extends SELECT<Integer>{
        private int out;

        public addFriendTask(String name) {
            super("SELECT \"addfriend\" ("+userID+", '"+name+"');");
            out = 0;
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            out = rs.getInt("addfriend");
        }

        @Override
        protected Integer endBackground() {
            return out;
        }
    }

    /*
    Pass in yes or no to response, and request number
    returns true if successfully posted response
    */
    public static boolean respondFriendship(int requestID, boolean response) throws TimeoutException, NotLoggedInException {
        if (userID == 0) throw new NotLoggedInException();

        String s;
        if (response) s = "true";
        else s = "false";

        respondFriendshipTask fr = new respondFriendshipTask(requestID, s);

        try {
            if (fr.execute().get(timeOut, TimeUnit.MILLISECONDS) > 0)
                return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }
    private static class respondFriendshipTask extends SELECT<Integer>{
        private int output;

        public respondFriendshipTask(int id, String b) {
            super("SELECT \"respondFriendRequest\" ("+userID+", "+id+", "+b+");");
            output = 0;
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            output = rs.getInt("respondFriendRequest");
        }

        @Override
        protected Integer endBackground() {
            return output;
        }
    }

    /*
    clears a notification.
    Works with friendship Accepted, friendship Rejected, Trade Denied, Middle Man Notification, and Trade Cancelled
    returns true if successful
    */
    public static boolean clearRequest(int requestID) throws TimeoutException, NotLoggedInException {
        if (userID == 0) throw new NotLoggedInException();

        clearRequestTask cr = new clearRequestTask(requestID);

        try {
            if (cr.execute().get(timeOut, TimeUnit.MILLISECONDS) > 0)
                return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }
    private static class clearRequestTask extends SELECT<Integer>{
        private int output;

        public clearRequestTask(int reqID) {
            super("SELECT \"clearNotification\" ("+userID+", "+reqID+");");
            output = 0;
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            output = rs.getInt("clearNotification");
        }

        @Override
        protected Integer endBackground() {
            return output;
        }
    }

    /*
    Removes friendship relation
    (should also remove current requests between users?)
    returns true if successful
    */
    public static boolean removeFriendship(int friendID) throws TimeoutException, NotLoggedInException {
        if (userID == 0) throw new NotLoggedInException();

        removeFriendshipTask rf = new removeFriendshipTask(friendID);

        try {
            if (rf.execute().get(timeOut, TimeUnit.MILLISECONDS) > 0)
                return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }
    private static class removeFriendshipTask extends SELECT<Integer>{
        private int output;

        public removeFriendshipTask(int frID) {
            super("SELECT \"removeFriend\" ("+userID+", "+frID+");");
            output = 0;
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            output = rs.getInt("removeFriend");
        }

        @Override
        protected Integer endBackground() {
            return output;
        }
    }

    /*
    Pass: id, my item(s) to trade, their item(s) to trade
    (temporarily removes items to be traded from senders knapsack?)
    returns true if successfully posted request
    */
    public static boolean requestTrade(int id, ArrayList<Item> myItems, ArrayList<Item> theirItems) throws TimeoutException, NotLoggedInException {
        if (userID == 0) throw new NotLoggedInException();

        requestTradeTask rt = new requestTradeTask(id, myItems, theirItems);

        try {
            if (rt.execute().get(timeOut, TimeUnit.MILLISECONDS) > 0)
                return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }
    private static class requestTradeTask extends SELECT<Integer>{
        private int output;

        public requestTradeTask(int theirID, ArrayList<Item> myI, ArrayList<Item> thI) {
            super("SELECT \"newTradeRequest\" ("+userID+", "+theirID+", "+ArrL2String(myI)+ ", "+ArrL2String(thI)+");");
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            output = rs.getInt("newTradeRequest");
        }

        @Override
        protected Integer endBackground() {
            return output;
        }
    }
    private static String ArrL2String(ArrayList<Item> list){
        if (list == null || list.size() == 0) return "'{}'";

        StringBuilder builder = new StringBuilder("'{"+list.get(0).getID());

        for (int i = 1; i < list.size(); i++) {
            builder.append(", " + list.get(i).getID());
        }

        builder.append("}'");

        return builder.toString();
    }

    /*
    Decline the trade defined by requestID
    returns true if successful
     */
    public static boolean declineTrade(int requestID) throws TimeoutException, NotLoggedInException {
        if (userID == 0) throw new NotLoggedInException();

        declineTradeTask dt = new declineTradeTask(requestID);

        try {
            if(dt.execute().get(timeOut, TimeUnit.MILLISECONDS) > 0)
                return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
    private static class declineTradeTask extends SELECT<Integer>{
        private int output;

        public declineTradeTask(int id) {
            super("SELECT \"declineTrade\" ("+userID+", "+id+");");
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            output = rs.getInt("declineTrade");
        }

        @Override
        protected Integer endBackground() {
            return output;
        }
    }

    /*
    int: request number, string: location name
    returns true if successfully posted response
    */
    public static boolean respondLocation(int requestID, String location) throws TimeoutException, NotLoggedInException {
        if (userID == 0) throw new NotLoggedInException();

        respondLocationTask rl = new respondLocationTask(requestID, location);

        try {
            if(rl.execute().get(timeOut, TimeUnit.MILLISECONDS) > 0)
                return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }
    private static class respondLocationTask extends SELECT<Integer>{
        private int output;

        public respondLocationTask(int id, String loc) {
            super("SELECT \"acceptTrade\" ("+userID+", "+id+", '"+loc+"');");
            output = 0;
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            output = rs.getInt("acceptTrade");
        }

        @Override
        protected Integer endBackground() {
            return output;
        }
    }

    /*
    Accept the location of trade defined by requestID
    returns true if successful
     */
    public static boolean acceptLocation(int requestID) throws TimeoutException, NotLoggedInException{
        if (userID == 0) throw new NotLoggedInException();

        acceptLocationTask al = new acceptLocationTask(requestID);

        try {
            if(al.execute().get(timeOut, TimeUnit.MILLISECONDS) > 0)
                return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
    private static class acceptLocationTask extends SELECT<Integer>{
        private int output;

        public acceptLocationTask(int reqID) {
            super("SELECT \"acceptLocation\" ("+userID+", "+reqID+");");
            output = 0;
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            output = rs.getInt("acceptLocation");
        }

        @Override
        protected Integer endBackground() {
            return output;
        }
    }

    /*
    Completes the trade Items exchange hands
    returns true if successful
     */
    public static boolean completeTrade(int requestID) throws TimeoutException, NotLoggedInException {
        if (userID == 0) throw new NotLoggedInException();

        completeTradeTask ct = new completeTradeTask(requestID);

        try {
            if (ct.execute().get(timeOut, TimeUnit.MILLISECONDS) > 0)
                return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }
    private static class completeTradeTask extends SELECT<Integer>{
        private int output;

        public completeTradeTask(int reqID) {
            super("SELECT \"completeTrade\" ("+userID+", "+reqID+");");
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            output = rs.getInt("completeTrade");
        }

        @Override
        protected Integer endBackground() {
            return output;
        }
    }

    /*
    Canceled a trade that has been accepted by both parties - Can be done by either
    returns true if successful
     */
    public static boolean cancelTrade(int requestID) throws TimeoutException, NotLoggedInException {
        if (userID == 0) throw new NotLoggedInException();

        cancelTradeTask ct = new cancelTradeTask(requestID);

        try {
            if (ct.execute().get(timeOut, TimeUnit.MILLISECONDS) > 0)
                return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } {

        }

        return false;
    }
    private static class cancelTradeTask extends SELECT<Integer>{
        private int output;

        public cancelTradeTask(int reqID) {
            super("SELECT \"cancelTrade\" ("+userID+", "+reqID+");");
        }

        @Override
        protected void middle(ResultSet rs) throws SQLException {
            output = rs.getInt("cancelTrade");
        }

        @Override
        protected Integer endBackground() {
            return output;
        }
    }

    /*
    set middle man status of logged in user as input<br>
    returns true if successful
    */
    public static boolean setMiddleMan(boolean can) throws TimeoutException, NotLoggedInException {
        return false;
    }

    /*
    Get List of middle men mutual to logged in user and friendID<br>
    returns list of mutual middle men
    */
    public static ArrayList<User> getMutualMiddleMen(int friendID) throws TimeoutException, NotLoggedInException{
        return null;
    }

    /*
    Start a trade with a middleman
    returns true if successful
    */
    public static boolean middleManTrade(int theirID, int middleManID, ArrayList<Item> items, boolean toThem) throws TimeoutException, NotLoggedInException{
        return false;
    }
}