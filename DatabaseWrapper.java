
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

            Log.v("Test", "Test m");

            conn = DriverManager.getConnection(url, username, password);

            if (conn == null || conn.isClosed()){
                Log.e("Test", "Connection failed!");
            }

            Log.v("Test","Test n");

        }
        private static void stop(){
            Log.v("Stop","Stopping");
            if (conn != null)
                try {
                    conn.close();
                    Log.v("Stop", "Connection Closed");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }

        /*
        Give your user name
        prints error if failed
        */
        public static Boolean logIn(String name) throws TimeoutException {
            if(conn == null){
                Log.e("Test", "No connection!");
            }
            else{
                Log.v("Test", "Connection!");
            }

            logInTask l = new logInTask(name);

            Log.v("Test", "Test 5");
            try {
                l.execute().get(timeOut, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            Log.v("Test","Test 6 " + userID);
            if (userID == 0)
                return false;

            return true;
        }
        private static class LoginSequence extends AsyncTask<String, Integer, Integer>{
            public Boolean done;

            private LoginSequence(){
                super();
                done = false;
            }

            protected Integer doInBackground(String... name) {
                userID = 0;
                Log.v("Test","Test 8");

                String sql;
                sql = "SELECT userid FROM users WHERE username = '" + name[0] + "';";

                Log.v("Test",sql);

                Statement st = null;
                ResultSet rs = null;
                try {
                    start();
                    st = conn.createStatement();
                    rs = st.executeQuery(sql);

                    while(rs.next()){
                        userID = rs.getInt("userid");
                    }

                    Log.v("Login", "Login as " + userID);

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

                done = true;
                return 0;
            }

            protected void onPostExecute(Integer result){
                Log.v("Login", "Post Execute");
            }
        }
        private static class logInTask extends SELECT<String, Integer>{

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
        private static class AddUserSequence extends AsyncTask<String, Integer, Integer>{

            protected Integer doInBackground(String... name) {
                userID = 0;

                String sql;
                sql = "SELECT \"adduser\" ('" + name[0] +"')";

                Log.v("Test",sql);

                Statement st = null;
                ResultSet rs = null;
                try {
                    start();
                    st = conn.createStatement();
                    rs = st.executeQuery(sql);

                       while(rs.next()){
                           userID = rs.getInt("adduser");
                       }

                       Log.v("AddUser", "UserId: " + userID);


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

                return 0;
            }

            protected void onPostExecute(Integer result){
                Log.v("AddUser", "Post Execute");
            }
        }
        private static class addUserTask extends SELECT<String, Integer>{

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
        public static boolean deleteSelf() throws TimeoutException {
            if (userID == 0){
                Log.e("RemoveUser", "Must be logged in to remove user");
                return false;
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
        private static class DeleteSelfSequence extends AsyncTask<Integer, Integer, Integer> {
            protected static int returned;
            protected Integer doInBackground(Integer... urls) {
                returned = -1;

                String sql;
                sql = "SELECT \"removeuser\" ('" + userID +"')";

                Log.v("Test",sql);

                Statement st = null;
                ResultSet rs = null;
                try {
                    start();
                    st = conn.createStatement();
                    rs = st.executeQuery(sql);

                    while(rs.next()){
                        returned = rs.getInt("removeuser");
                    }

                    Log.v("removeUser", "UserId: " + returned);

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

                return 0;
            }

            protected void onProgressUpdate(Integer... progress) {

            }

            protected void onPostExecute(Integer result) {

            }
        }
        private static class deleteSelfTask extends SELECT<Integer, Integer>{
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
        public static ArrayList<User> getFriends() throws TimeoutException {
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
        private static class getFriendsSequnece extends AsyncTask<Integer, Integer, ArrayList<User> >{
            //protected ArrayList<User> output;

            private Boolean done;
            public Boolean done(){return done;}

            public getFriendsSequnece(){
                super();
                done = false;
            }

            @Override
            protected ArrayList<User>  doInBackground(Integer... params) {
                ArrayList<User> output = new ArrayList<User>();
                String sql;
                sql = "SELECT * FROM users WHERE " +
                        "userid = ANY(SELECT user1 FROM friendship WHERE user2 = "+userID+") OR " +
                        "userid = ANY(SELECT user2 FROM friendship WHERE user1 = "+userID+") " +
                        "ORDER BY username;";

                Log.v("Test",sql);

                Statement st = null;
                ResultSet rs = null;
                try {
                    start();
                    st = conn.createStatement();
                    rs = st.executeQuery(sql);

                    while(rs.next()){
                        output.add(new User(rs.getInt("userid"), rs.getString("username")));
                    }

                    done = true;
                    Log.v("getFriends", "Number of friends " + output.size());

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

                return output;
            }
        }
        private static class getFriendsTask extends SELECT<Integer, ArrayList<User>>{
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

        private static abstract class SELECT<IntputType,OutputType> extends AsyncTask<IntputType, Integer, OutputType>{
            private String sql;

            public SELECT(String SQLquerry){
                super();
                sql = SQLquerry;
            }

            protected abstract void middle(ResultSet rs) throws SQLException;
            protected abstract void postRead();
            protected abstract OutputType endBackground();

            protected OutputType doInBackground(IntputType... in){
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
        public static ArrayList<User> getMutualFriends(int id) throws TimeoutException {
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
        private static class getMutualTask extends SELECT<Integer, ArrayList<User>>{
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

        /*    Not needed?
        Give a user id (or name, not preferable)
        */
        public static User getUserDetails(String name){
            return null;
        }

        public static User getUserDetails(int id){
            return null;
        }

        /*
        Give a user id (or name, not preferable)
        returns connents of knapsack
        */
        public static ArrayList<Item> getKnapsack(int id){
            return null;
        }

        public static ArrayList<Item> getKnapsack(String name){
            return null;
        }

        public class Item{
            int id;
            String name;
        }

        /*
        add item named string to knapsack
        returns true if posted
        */
        public static boolean addToKnapsack(String itemName){
            return true;
        }

        /*
        remove item with id from knapsack
        returns true if removed successfully
        */
        public static boolean removeFromKnapsack(int itemNumber){
            return true;
        }

        /*
        Pass user id to befriend
        returns true if succefully posted
        */
        public static boolean requestFriendship(int id){
            return true;
        }

        public static boolean requestFriendship(String name){
            return true;
        }

        /*
        Pass: id, my item(s) to trade, thier item(s) to trade
        returns true if succesfully posted request
        */
        public static boolean requestTrade(int id, ArrayList<Item> myItems, ArrayList<Item> theirItems){
            return true;
        }

        /*
        Pass in yes or no to response, and request number
        returns true if succefully posted response
        */
        public static boolean respond(int requestID, boolean response){
            return true;
        }

        /*
        int: request number, string: location name
        returns true if succefully posted response
        */
        public static boolean respondLocation(int requestID, String location){
            return true;
        }

        /*
        returns a list of request for the current user
        */
        public static ArrayList<Request> getRequests(){
            return null;
        }

        /* TYPES
        friendship = 1
        friendshipAccepted = 2
        friendshipRejected = 3

        TradeRequested = 10
        TradeAccepted-withLocation = 11
        TradeDenied = 12
        LocationDenied = 13
        TradeCompleted = 15

        MiddleManRequested = 20
        */
        public class Request{
            int id;
            int type;
            User from;
            User to;
            //sometimes useful
            ArrayList<Integer> extra1;
            ArrayList<Integer> extra2;
            String extrastring;
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
