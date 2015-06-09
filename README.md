# Database Wrapper for Dank Exchange project
### by Blake Tacklind <blake@tacklind.com
### package: com.dank.exchange.backend

---
## Datatypes

#### User
ID: users unique ID
Name: users unique name

#### Item
ID: items unique ID
Name: items name

#### Request
ID: request unique ID
FromUser: User the request is from
ToUser: User the request is for
FromItems: ArrayList of Items the From User has and wants to trade
ToItems: ArrayList of Items the To User has and wants to trade
Location: String for the location of a trade
Middleman: User for the optional middle man trade

---
## Public functions

Give your user name
prints error if failed
Boolean logIn(String name) throws TimeoutException 

Add the user by string
return true if success
boolean addUser(String name) throws TimeoutException 

Delete user (self)
returns true if successful
boolean deleteSelf() throws TimeoutException, NotLoggedInException 

return your list of friends
may return null if failed
ArrayList<User getFriends() throws TimeoutException, NotLoggedInException 

returns mutual friends of logged in user and user with id
may return null if failed
ArrayList<User getMutualFriends(int id) throws TimeoutException, NotLoggedInException 

Give a user id (or name, not preferable)
returns contents of knapsack
ArrayList<Item getKnapsack(int id) throws TimeoutException 

add item named string to knapsack
returns true if posted
boolean addToKnapsack(String itemName) throws TimeoutException, NotLoggedInException 

remove item with id from knapsack
returns true if removed successfully
boolean removeFromKnapsack(int itemNumber) throws TimeoutException, NotLoggedInException 

Get requests TO the logged in user
Returns the list of requests
ArrayList<Request getRequestsTo() throws TimeoutException, NotLoggedInException 

Get requests FROM the logged in user
Returns the list of requests
ArrayList<Request getRequestsFrom() throws TimeoutException, NotLoggedInException

Pass username to befriend
returns true if successfully posted
boolean requestFriendship(String name) throws TimeoutException, NotLoggedInException 

Pass in yes or no to response, and request number
returns true if successfully posted response
boolean respondFriendship(int requestID, boolean response) throws TimeoutException, NotLoggedInException 

clears a notification.
Works with friendship Accepted, friendship Rejected, Trade Denied, Middle Man Notification, and Trade Cancelled
returns true if successful
boolean clearRequest(int requestID) throws TimeoutException, NotLoggedInException 

Removes friendship relation
(should also remove current requests between users?)
returns true if successful
boolean removeFriendship(int friendID) throws TimeoutException, NotLoggedInException 

Pass: id, my item(s) to trade, their item(s) to trade
(temporarily removes items to be traded from senders knapsack?)
returns true if successfully posted request
boolean requestTrade(int id, ArrayList<Item myItems, ArrayList<Item theirItems) throws TimeoutException, NotLoggedInException 

Decline the trade defined by requestID
returns true if successful
boolean declineTrade(int requestID) throws TimeoutException, NotLoggedInException 

int: request number, string: location name
returns true if successfully posted response
boolean respondLocation(int requestID, String location) throws TimeoutException, NotLoggedInException 

Accept the location of trade defined by requestID
returns true if successful
boolean acceptLocation(int requestID) throws TimeoutException, NotLoggedInException

Completes the trade Items exchange hands
returns true if successful
boolean completeTrade(int requestID) throws TimeoutException, NotLoggedInException 

Canceled a trade that has been accepted by both parties - Can be done by either
returns true if successful
boolean cancelTrade(int requestID) throws TimeoutException, NotLoggedInException 
