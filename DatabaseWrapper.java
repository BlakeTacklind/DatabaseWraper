

public static class DatabaseWrapper{
	protected static Connection conn;
	protected static int userid;
    final private static String url = "jdbc:postgresql://serenity.isozilla.com:5432/" +
            "parcelexchange?sslfactory=org.postgresql.ssl.NonValidatingFactory" +
            "&ssl=true";

	private static void start(){
		try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connector c = new Connector();
        c.execute();

        return;
	}

	private static void stop(){
		if (conn != null)
			try {
            	conn.close();
        	} catch (SQLException e) {
            	e.printStackTrace();
        	}

		Log.v("Stop", "Connection Closed?");
	}
	
	private static class Connector extends AsyncTask<Integer, Integer, Integer>{
		protected Boolean done;

		protected Connector(){
			done=false;
		}

		protected Integer doInBackground(Integer... in) {
        	
        	try {
            	conn = DriverManager.getConnection(url, "parcelexchange", "Mabc0NDkYRf1yVyIfhRd");
        	} catch (SQLException e) {
            	e.printStackTrace();
        	}

	        if (conn == null)
    	        Log.e("connectToDataBase", "Connection was null");

       		return 0;
     	}

     	protected void onPostExecute(Integer result) {
        	done = true;
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
		start();
		LoginSequence l = new LoginSequence();
		
		l.execute(name);

		while (!l.done);

		if (userid == 0)
			return false;

		return true;
	}

	private static class LoginSequence extends AsyncTask<String, Integer, Integer>{
        public Boolean done;

        public LoginSequence(){
        	super();
        	done = false;
        }

        protected Integer doInBackground(String... name) {
        	userid = 0;
            if(conn!=null) {

                String sql;
                sql = "SELECT username FROM users WHERE username = '" + name[0] + "';";

                Statement st = null;
                try {
                    st = conn.createStatement();
                    ResultSet rs = null;
                    rs = st.executeQuery(sql);

                    while(rs.next()){
                        userid = rs.getInteger("userid");
                    }

                    rs.close();
                    st.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return 0;
        }

        protected void onPostExecute(Integer result){
            stop();
            done = true;
        }
    }
	
	//Logged in user id is stored global variable
	
	/*
	Add the user by string
	return true if success
	*/
	public static boolean addUser(String name){
		return true;
	}
	
	/*
	Deletes self
	returns true if successful
	*/
	public static boolean deleteSelf(){
		return true;
	}
	
	/*
	return your list of friends
	
	SELECT * FROM users WHERE userid = ANY(SELECT user1 FROM friendship WHERE user2 = self) OR userid = ANY(SELECT user2 FROM friendship WHERE user1 = self) ORDER BY username;
	*/

	public static ArrayList<User> getFriends(){
		return null;
	}
	
	/*
	SELECT * FROM users WHERE userid = ANY(SELECT user1 FROM friendship WHERE user2 = self) OR userid = ANY(SELECT user2 FROM friendship WHERE user1 = self) INTERSECT SELECT * FROM users WHERE userid = ANY(SELECT user1 FROM friendship WHERE user2 = int) OR userid = ANY(SELECT user2 FROM friendship WHERE user1 = int) ORDER BY username;
	*/
	public static ArrayList<User> getMutualFriends(int id){
		return null;
	}
	
	public class User{
		int id;
		String name;
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