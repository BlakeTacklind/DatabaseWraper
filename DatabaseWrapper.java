
    public static class DatabaseWrapper{
        private static Connection conn;
        private static int userID;
        private static int timeOut = 1000;
        private static int timeOutLong = 5000;
        final private static String url = "jdbc:postgresql://serenity.isozilla.com:5432/"+
                "parcelexchange?sslfactory=org.postgresql.ssl.NonValidatingFactory" +
                "&ssl=true";
        final private static String username = "parcelexchange";
        final private static String password = "Mabc0NDkYRf1yVyIfhRd";

        private static void start() throws SQLException{
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            //Properties props = new Properties();
            //props.setProperty("user", username);
            //props.setProperty("password", password);
            //props.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
            //props.setProperty("ssl", "true");
            //props.setProperty("loginTimeout", "5");
            //props.setProperty("socketTimeout", "15");

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

        /*
        Give your user name
        prints error if failed
        */
        public static Boolean logIn(String name) throws TimeoutException {

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
            protected void postRead() {

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
            protected void postRead() {

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
            if (userID == 0){
                throw new NotLoggedInException();
            }

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
            protected void postRead() {

            }

            @Override
            protected Integer endBackground() {
                return returned;
            }
        }

    	/*
	    return your list of friends
        */
        public static ArrayList<User> getFriends() throws TimeoutException, NotLoggedInException {
            if (userID == 0)
                throw new NotLoggedInException();

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
            protected void postRead() {

            }

            @Override
            protected ArrayList<User> endBackground() {
                return output;
            }
        }

        private static abstract class SELECT<OutputType> extends AsyncTask<Integer, Integer, OutputType>{
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

        /*

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
            protected void postRead() {

            }

            @Override
            protected ArrayList<User> endBackground() {
                return out;
            }
        }

        /*
        Give a user id (or name, not preferable)
        returns connents of knapsack
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
                super("SELECT * FROM items WHERE heldby = "+id+";");
                output = new ArrayList<Item>();
            }

            @Override
            protected void middle(ResultSet rs) throws SQLException {
                output.add(new Item(rs.getInt("id"), rs.getString("name")));
            }

            @Override
            protected void postRead() {

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
            protected void postRead() {

            }

            @Override
            protected Integer endBackground() {
                return itemNum;
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
            protected void postRead() {

            }

            @Override
            protected Boolean endBackground() {
                return ret;
            }
        }

        public static ArrayList<Request> getRequestsTo() throws TimeoutException, NotLoggedInException {
            if (userID == 0)throw new NotLoggedInException();

            getRequestToTask rq = new getRequestToTask();

            try {
                return rq.execute().get(timeOut, TimeUnit.MILLISECONDS);
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
                super("SELECT * FROM requests WHERE \"to\" = "+userID+";");
                output = new ArrayList<Request>();
            }

            @Override
            protected void middle(ResultSet rs) throws SQLException {
                int type = rs.getInt("type");

                switch (type){
                    case 1:
                    case 2:
                    case 3:
                        output.add(new Request(rs.getInt("id"), type, rs.getInt("from"), rs.getInt("to"), 0, null, null, null));
                        return;
                    default:
                        Log.e("getRequestsTo", "Request Type not implemented!");
                }

            }

            @Override
            protected void postRead() {

            }

            @Override
            protected ArrayList<Request> endBackground() {
                return output;
            }
        }

        /*
        Pass user id to befriend
        returns true if succefully posted
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
            protected void postRead() {

            }

            @Override
            protected Integer endBackground() {
                return out;
            }
        }

        /*
        Pass in yes or no to response, and request number
        Works with request type
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
        Respond to a generic notification
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
        returns true is successful
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
            return false;
        }

        /*
        Decline the trade defined b requestID
        returns true if successful
         */
        public static boolean declineTrade(int requestID) throws TimeoutException, NotLoggedInException {
            return false;
        }

        /*
        int: request number, string: location name
        returns true if successfully posted response
        */
        public static boolean respondLocation(int requestID, String location) throws TimeoutException, NotLoggedInException {
            return false;
        }

        /*
        Accept the location of trade defined by requestID
        returns true if successful
         */
        public static boolean acceptLocation(int requestID) throws TimeoutException, NotLoggedInException{
            return false;
        }

        /*
        request a middle man by ID
        returns true if successful
         */
        public static boolean requestMiddleMan(int requestID, int middleManID) throws TimeoutException, NotLoggedInException{
            return false;
        }

        /*
        Middle man responds to a request
        Returns true is successfully posted
         */
        public static boolean middlemanResponse(int requestID, Boolean answer) throws TimeoutException, NotLoggedInException{
            return false;
        }
    }

    public static class User{
        public User(int i, String n){
            id = i;
            name = n;
        }

        public int id(){return id;}
        public String name(){return name;}

        private int id;
        private String name;
    }

    public static class Item{
        public Item(int i, String n){
            id = i;
            name = n;
        }

        public int id(){return id;}
        public String name(){return name;}

        private int id;
        private String name;
    }

    /* TYPES
    friendship requested = 1
    friendship Accepted = 2
    friendship Rejected = 3

    Trade Requested = 10
    Trade Denied = 11
    Request Location = 12 (assumed accepted)
    Location Denied - new location = 13
    Location Accepted = 14
    Trade Completed = 15

    MiddleMan Requested = 20
    MiddleMan Accepted = 21
    MiddleMan Rejected = 22
    */
    public static class Request{
        private int requestID, type, fromID, toID, extraInt;
        private int[] extra1, extra2;
        private String extrastring;

        public Request(int id, int ty, int frm, int to, int ei,int[] e1, int[] e2, String es){
            requestID = id;
            type = ty;
            fromID = frm;
            toID = to;
            extra1 = e1;
            extra2 = e2;
            extrastring = es;
            extraInt = ei;
        }

        public String toString(){
            return "requestID: " + requestID + ", type: " + type + ", fromID: " + fromID +
                    ", toID: " + toID + " , extraInt: " + extraInt + ", extra1: " + extra1 + ", extra2: " + extra2 +
                    ", extrastring: \"" + extrastring + "\".";
        }
    }

    public static class NotLoggedInException extends Exception{
        public NotLoggedInException(){
            super("User isn't logged in");
        }
    }
