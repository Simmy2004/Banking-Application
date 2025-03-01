# Project Assignment POO  - J. POO Morgan - Phase One

![](https://s.yimg.com/ny/api/res/1.2/aN0SfZTtLF5hLNO0wIN3gg--/YXBwaWQ9aGlnaGxhbmRlcjt3PTcwNTtoPTQyNztjZj13ZWJw/https://o.aolcdn.com/hss/storage/midas/b23d8b7f62a50a7b79152996890aa052/204855412/fit.gif)
## Implementation:
* The input is being parsed command by command with a special class - CommandFactory. Here I used
the Factory Design pattern which came hand in hand with the Command Design Pattern. The CommandFactory
will return a Command type (interface) by checking which of the commands are requested in the input.
* Every class that implements the Command interface will have to implement its own execute() method, that
solves the specific problem that is requested. The execute() method will return an ObjectNode, because
most of the commands will have to put errors in an ObjectNode.
* There is also a Database, which will consists in most Maps, for quick transition between different situations encountered such as 
email->user, iban->account and so on. The database will have all the users and information about them, for quick access.
* The ExchangeGraph class is made for solving the inter currencies transactions, by applying a dfs on all the possible
exchanges, to find the multiplier needed.
* I also dealt with the transactions, by using again the Command Pattern. Transactions are kept in each user, and all of them
have their own implementation of printDetails() as requested.
* The Split Payment problem was solved using the Observer Design Pattern, by notifying all the accounts included when the payment has a final decision (rejected or approved).

####

## Transactions package:
### Transaction interface:
* Made for using the Command Pattern, with possiblity to extend to even more specific transaction types (see Spending type).
* Creates custom implementation for every printDetails() method, for every transaction. Also have a field of isSpending() that will be
usefull for separating normal transactions with spendings.

### Spending interface:
* Made for dealing with spendingReport() command, that requests specific payment information. All the classes that implements this Spending, will always
implement Transaction, because the printDetails() method is a must-have for giving information about the current spendings of a User.
The isSpending() field from the Transaction will return true if the Spending interface is implemented.

### All the transaction types:
* There are 21 classes of type Transaction, that will give out the requested information about transactions found in the user's
history. Every one of them will come with their personal implementation of the printDetails(). The transactions are added
throughout the execution, in User's ArrayList, when the Command type will be executed.

## Commands package:
### Command interface:
* Made for using the Command Design Pattern. Creates custom execution logic for every command that implements it.
####
### All the command types:
* There are 29 classes of type Command, that will execute the wanting command. There commands are initialized with the use of
the CommandFactory that will return from a switch case the specific command implementing class. Every execution will have access
to the Database for modifications, and will most of the time return an ObjectNode for printing errors encountered in a JSON format.
* Also, every execution will be responsible for adding the transaction types in each of user's array lists.
####
## Accounts package:
* There are 3 types of Accounts: a basic one, that can make payments, a SavingsAccount that is only made for adding InterestRates and a business one, where multiple users can manage the same account based on their role.
* Also, 2 cards are implemented, one extending other: The simple Card, that will not change its IBAN after each payment, and the
OneTimeCard, that will have an actual implementation over the pay() that will change the card number after each payment done.
####
## Design Patterns:
* The ExchangeRateGraph is made as a static Singleton, that can be used anywhere in the code. It is made as a singleton, because no more than one instance is needed.
* The commands are processed into different individual Command instances by the CommandFactory, implemented as a Factory Design Pattern.
* The commands are using the Command Design Pattern. Creates custom execution logic for every command that implements it.
* Again, the transactions are using another Command Design Pattern, for custom details for every type.
* 2 Strategy Design patterns are used: One for selecting every Commerciant special cashBackStrategy, and another one for selecting and calculating the commission based on what service plan the user has.
* Account class implements the SplitObserver interface, for an Observer Design Pattern. The subscribers are the accounts included in a Split Payment. They will get notified every time every account in the split payment has made its decision by rejecting or accepting the payment.
When the update happens, every account subscribed will be deducted the amount needed and the transaction type will be added to the list.
