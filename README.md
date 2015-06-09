# Database Wrapper for Dank Exchange project
### by Blake Tacklind blake@tacklind.com
##### package: com.dank.exchange.backend

---
## Datatypes

#### User
* ID: users unique ID
* Name: users unique name

#### Item
* ID: items unique ID
* Name: items name

#### Request
* ID: request unique ID
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

Add the user by string<br>
return true if success
```Java
boolean addUser(String name) throws TimeoutException 
```

Delete user (self)<br>
returns true if successful
```Java
boolean deleteSelf() throws TimeoutException, NotLoggedInException 
```

return your list of friends<br>
may return null if failed
```Java
ArrayList<User> getFriends() throws TimeoutException, NotLoggedInException 
```

returns mutual friends of logged in user and user with id<br>
may return null if failed
```Java
ArrayList<User> getMutualFriends(int id) throws TimeoutException, NotLoggedInException 
```

Give a user id (or name, not preferable)<br>
returns contents of knapsack
```Java
ArrayList<Item> getKnapsack(int id) throws TimeoutException 
```

add item named string to knapsack<br>
returns true if posted
```Java
boolean addToKnapsack(String itemName) throws TimeoutException, NotLoggedInException 
```

remove item with id from knapsack<br>
returns true if removed successfully
```Java
boolean removeFromKnapsack(int itemNumber) throws TimeoutException, NotLoggedInException 
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

Pass username to befriend<br>
returns true if successfully posted
```Java
boolean requestFriendship(String name) throws TimeoutException, NotLoggedInException 
```

Pass in yes or no to response, and request number<br>
returns true if successfully posted response
```Java
boolean respondFriendship(int requestID, boolean response) throws TimeoutException, NotLoggedInException 
```

clears a notification.<br>
Works with friendship Accepted, friendship Rejected, Trade Denied, Middle Man Notification, and Trade Cancelled<br>
returns true if successful
```Java
boolean clearRequest(int requestID) throws TimeoutException, NotLoggedInException 
```

Removes friendship relation<br>
(should also remove current requests between users?)<br>
returns true if successful
```Java
boolean removeFriendship(int friendID) throws TimeoutException, NotLoggedInException 
```

Pass: id, my item(s) to trade, their item(s) to trade<br>
(temporarily removes items to be traded from senders knapsack?)<br>
returns true if successfully posted request
```Java
boolean requestTrade(int id, ArrayList<Item> myItems, ArrayList<Item> theirItems) throws TimeoutException, NotLoggedInException 
```

Decline the trade defined by requestID<br>
returns true if successful
```Java
boolean declineTrade(int requestID) throws TimeoutException, NotLoggedInException 
```

int: request number, string: location name<br>
returns true if successfully posted response
```Java
boolean respondLocation(int requestID, String location) throws TimeoutException, NotLoggedInException 
```

Accept the location of trade defined by requestID<br>
returns true if successful
```Java
boolean acceptLocation(int requestID) throws TimeoutException, NotLoggedInException
```

Completes the trade Items exchange hands<br>
returns true if successful
```Java
boolean completeTrade(int requestID) throws TimeoutException, NotLoggedInException 
```

Canceled a trade that has been accepted by both parties - Can be done by either<br>
returns true if successful
```Java
boolean cancelTrade(int requestID) throws TimeoutException, NotLoggedInException 
```

---
#### Not implemented - yet

set middle man status of logged in user as input<br>
returns true if successful
```Java
boolean setMiddleMan(boolean canMiddleMan) throws TimeoutException, NotLoggedInException 
```

Get List of middle men mutual to logged in user and friendID<br>
returns list of mutual middle men
```Java
ArrayList<User> getMutualMiddleMen(int friendID) throws TimeoutException, NotLoggedInException
```

Start a trade with a middleman<br>
returns true if successful
```Java
boolean middleManTrade(int theirID, int middleManID, ArrayList<Item> items, boolean toThem) throws TimeoutException, NotLoggedInException
```
