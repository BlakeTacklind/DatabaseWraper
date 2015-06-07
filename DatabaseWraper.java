class DatabaseWraper{
	private static Connection conn;
	private static int userid;

	public static void start(){

	}

	public static void stop(){

	}
	
	/*
	Give your user name
	prints error if failed
	*/
	public static void LogIn(String name){

	}
	//Logged in user id is stored global variable
	
	/*
	Add the user by string
	return true if success
	*/
	public static boolean addUser(String name){

	}
	
	/*
	Deletes self
	returns true if successful
	*/
	public static boolean deleteSelf(){

	}
	
	/*
	return your list of friends
	
	SELECT * FROM users WHERE userid = ANY(SELECT user1 FROM friendship WHERE user2 = self) OR userid = ANY(SELECT user2 FROM friendship WHERE user1 = self) ORDER BY username;
	*/

	public static ArrayList<User> getFriends(){

	}
	
	/*
	SELECT * FROM users WHERE userid = ANY(SELECT user1 FROM friendship WHERE user2 = self) OR userid = ANY(SELECT user2 FROM friendship WHERE user1 = self) INTERSECT SELECT * FROM users WHERE userid = ANY(SELECT user1 FROM friendship WHERE user2 = int) OR userid = ANY(SELECT user2 FROM friendship WHERE user1 = int) ORDER BY username;
	*/
	public static ArrayList<User> getMutualFriends(int id){

	}
	
	public class User{
		int id;
		String name;
	}
	
	/*    Not needed?
	Give a user id (or name, not preferable)
	*/
	public static User getUserDetails(String name){

	}
	public static User getUserDetails(int id){

	}
	
	/*
	Give a user id (or name, not preferable)
	returns connents of knapsack
	*/
	public static ArrayList<Item> getKnapsack(int id){

	}

	public static ArrayList<Item> getKnapsack(String name){

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

	}
	
	/*
	remove item with id from knapsack
	returns true if removed successfully
	*/
	public static boolean removeFromKnapsack(int itemNumber){

	}
	
	/*
	Pass user id to befriend
	returns true if succefully posted
	*/
	public static boolean requestFriendship(int id){

	}

	public static boolean requestFriendship(String name){
	
	}
	
	/*
	Pass: id, my item(s) to trade, thier item(s) to trade
	returns true if succesfully posted request
	*/
	public static boolean requestTrade(int id, ArrayList<Item> myItems, ArrayList<Item> theirItems){

	}
	
	/*
	Pass in yes or no to response, and request number
	returns true if succefully posted response
	*/
	public static boolean respond(int requestID, boolean response){

	}
	
	/*
	int: request number, string: location name
	returns true if succefully posted response
	*/
	public static boolean respondLocation(int requestID, String location){

	}
	
	/*
	returns a list of request for the current user
	*/
	public static ArrayList<Request> getRequests(){

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
		ArrayList<int> extra1;
		ArrayList<int> extra2;
		String extrastring;
	}

}