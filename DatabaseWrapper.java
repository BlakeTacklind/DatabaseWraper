
    public static class DatabaseWrapper{
        private static Connection conn;
        private static int userid;
        final private static String url = "jdbc:postgresql://serenity.isozilla.com:5432/"; /*+
                "parcelexchange?sslfactory=org.postgresql.ssl.NonValidatingFactory" +
                "&ssl=true";*/
        final private static String username = "parcelexchange";
        final private static String password = "Mabc0NDkYRf1yVyIfhRd";

        public static void setId(int i){userid = i;}

        private static void start() throws SQLException{
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            Log.v("Test", "Test 3");

            Properties props = new Properties();
            props.setProperty("user", username);
            props.setProperty("password", password);
            props.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
            props.setProperty("ssl", "true");
            props.setProperty("loginTimeout", "5");
            props.setProperty("socketTimeout", "15");

            Log.v("Test", "Test m");

            conn = DriverManager.getConnection(url, props);

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
	private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
     	protected Long doInBackground(URL... urls) {

     	}

     	protected void onProgressUpdate(Integer... progress) {

     	}

     	protected void onPostExecute(Long result) {

     	}
 	}
 	*/

        /*
        Give your user name
        prints error if failed
        */
        public static Boolean LogIn(String name){
            if(conn == null){
                Log.e("Test", "No connection!");
            }
            else{
                Log.v("Test", "Connection!");
            }

            LoginSequence l = new LoginSequence();

            Log.v("Test", "Test 5");
            l.execute(name);

            Log.v("Test", "Test 4");
            while (!l.done);

            Log.v("Test","Test 6 " + userid);
            if (userid == 0)
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
                userid = 0;
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
                        userid = rs.getInt("userid");
                    }

                    Log.v("Login", "Login as " + userid);

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

        /*
        Add the user by string
        return true if success
        */
        public static boolean addUser(String name){

            AddUserSequence s = new AddUserSequence();
            s.execute(name);

            while(userid == 0);

            if (userid == -1)
                return false;

            return true;
        }
        private static class AddUserSequence extends AsyncTask<String, Integer, Integer>{

            protected Integer doInBackground(String... name) {
                userid = 0;

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
                           userid = rs.getInt("adduser");
                       }

                       Log.v("AddUser", "UserId: " + userid);


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


        /*
        Delete user (self)
        returns true if successful
        */
        public static boolean deleteSelf(){
            if (userid == 0){
                Log.e("RemoveUser", "Must be logged in to remove user");
                return false;
            }

            DeleteSelfSequence d = new DeleteSelfSequence();
            d.execute();

            while (d.returned == -1);

            if(d.returned == 0)
                return false;

            userid = 0;

            return true;
        }
        private static class DeleteSelfSequence extends AsyncTask<Integer, Integer, Integer> {
            protected static int returned;
            protected Integer doInBackground(Integer... urls) {
                returned = -1;

                String sql;
                sql = "SELECT \"removeuser\" ('" + userid +"')";

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

    	/*
	    return your list of friends

    	SELECT * FROM users WHERE userid = ANY(SELECT user1 FROM friendship WHERE user2 = self) OR userid = ANY(SELECT user2 FROM friendship WHERE user1 = self) ORDER BY username;
	    */
        public static ArrayList<User> getFriends(){
            getFriendsSequnece f = new getFriendsSequnece();
            f.execute();

            while (!f.done());

            return f.output;
        }
        private static class getFriendsSequnece extends AsyncTask<Integer, Integer, Integer>{
            protected ArrayList<User> output;

            private Boolean done;
            public Boolean done(){return done;}

            public getFriendsSequnece(){
                super();
                done = false;
            }

            @Override
            protected Integer doInBackground(Integer... params) {

                String sql;
                sql = "SELECT * FROM users WHERE " +
                        "userid = ANY(SELECT user1 FROM friendship WHERE user2 = "+userid+") OR " +
                        "userid = ANY(SELECT user2 FROM friendship WHERE user1 = "+userid+") " +
                        "ORDER BY username;";

                Log.v("Test",sql);

                Statement st = null;
                ResultSet rs = null;
                try {
                    start();
                    st = conn.createStatement();
                    rs = st.executeQuery(sql);
                    output = new ArrayList<User>();

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

                return null;
            }
        }


        /*
        SELECT * FROM users WHERE userid = ANY(SELECT user1 FROM friendship WHERE user2 = self) OR userid = ANY(SELECT user2 FROM friendship WHERE user1 = self) INTERSECT SELECT * FROM users WHERE userid = ANY(SELECT user1 FROM friendship WHERE user2 = int) OR userid = ANY(SELECT user2 FROM friendship WHERE user1 = int) ORDER BY username;
        */
        public static ArrayList<User> getMutualFriends(int id){
            return null;
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
