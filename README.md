# Database Wrapper for Dank Exchange project
### by Blake Tacklind blake@tacklind.com
##### package: com.dank.exchange.backend

---
## Datatypes

#### User
* Name: users unique name

#### Item
* Name: items name

#### Request
* FromUser: User the request is from
* ToUser: User the request is for
* FromItems: ArrayList of Items the From User has and wants to trade
* ToItems: ArrayList of Items the To User has and wants to trade
* Location: String for the location of a trade
* Middleman: User for the optional middle man trade

---
## Public functions

Give your user name<br>
returns true if successful
```Java
boolean logIn(String name) throws TimeoutException 
```

Add the new user by String<br>
return true if success
```Java
boolean addUser(String name) throws TimeoutException 
```

Delete currently logged in user<br>
returns true if successful
```Java
boolean deleteSelf() throws TimeoutException, NotLoggedInException 
```

set middle man status of logged in user<br>
returns true if successful
```Java
boolean setMiddleMan(boolean canMiddleMan) throws TimeoutException, NotLoggedInException 
```

return your list of friends<br>
may return null if failed
```Java
ArrayList<User> getFriends() throws TimeoutException, NotLoggedInException 
```

returns mutual friends of logged in user and user given<br>
may return null if failed
```Java
ArrayList<User> getMutualFriends(User friend) throws TimeoutException, NotLoggedInException 
```

Get List of middle men mutual to logged in user and friend<br>
returns list of mutual middle men
```Java
ArrayList<User> getMutualMiddleMen(User friend) throws TimeoutException, NotLoggedInException
```

Give a user<br>
returns contents of their knapsack
```Java
ArrayList<Item> getKnapsack(User user) throws TimeoutException 
```

add item named string to knapsack<br>
returns true if posted
```Java
boolean addToKnapsack(String itemName) throws TimeoutException, NotLoggedInException 
```

remove item from knapsack<br>
returns true if removed successfully
```Java
boolean removeFromKnapsack(Item item) throws TimeoutException, NotLoggedInException 
```

Get requests TO the logged in user<br>
Returns the list of requests
```Java
ArrayList<Request> getRequestsTo() throws TimeoutException, NotLoggedInException 
```

Get requests FROM the logged in user<br>
Returns the list of requests
```Java
ArrayList<Request> getRequestsFrom() throws TimeoutException, NotLoggedInException
```

Pass username to send friend request from current logged in user<br>
returns true if successfully posted
```Java
boolean requestFriendship(String name) throws TimeoutException, NotLoggedInException 
```

Pass in yes or no to response, and request number<br>
returns true if successfully posted response
```Java
boolean respondFriendship(Request request, boolean response) throws TimeoutException, NotLoggedInException 
```

clears a notification.<br>
Works with friendship Accepted, friendship Rejected, Trade Denied, Middle Man Notification, and Trade Cancelled<br>
returns true if successful
```Java
boolean clearRequest(Request request) throws TimeoutException, NotLoggedInException 
```

Removes friendship relation<br>
(should also remove current requests between users?)<br>
returns true if successful
```Java
boolean removeFriendship(User friend) throws TimeoutException, NotLoggedInException 
```

User to trade with, my item(s) to trade, their item(s) to trade<br>
(temporarily removes items to be traded from senders knapsack?)<br>
returns true if successfully posted request
```Java
boolean requestTrade(User friend, ArrayList<Item> myItems, ArrayList<Item> theirItems) throws TimeoutException, NotLoggedInException 
```

Decline the trade request<br>
returns true if successful
```Java
boolean declineTrade(Request request) throws TimeoutException, NotLoggedInException 
```

Pass in: Request to respond to and location name in string<br>
returns true if successfully posted response
```Java
boolean respondLocation(Request request, String location) throws TimeoutException, NotLoggedInException 
```

Accept the location of trade request<br>
returns true if successful
```Java
boolean acceptLocation(Request request) throws TimeoutException, NotLoggedInException
```

Completes the trade, Items exchange hands<br>
returns true if successful
```Java
boolean completeTrade(Request request) throws TimeoutException, NotLoggedInException 
```

Canceled a trade that has been accepted by both parties - Can be done by either<br>
returns true if successful
```Java
boolean cancelTrade(Request request) throws TimeoutException, NotLoggedInException 
```

---
#### Empty Bodies

Start a trade with a middleman<br>
returns true if successful
```Java
boolean middleManTrade(User them, User middleman, ArrayList<Item> items, boolean toThem) throws TimeoutException, NotLoggedInException
```
