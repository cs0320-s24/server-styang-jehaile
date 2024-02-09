# Project Details
I discussed by project details below in the design choices. 
# Design Choices
For a high level overview: I created 2 classes that handle the main functionality of my program: Searcher and
CSVParser. I instaniate these classes in my Main class to ensure that this is user friendly. To facilate tests I created
3 additional classes: A Pizza class, a PizzaCreator and a StringCreator class. 

I wrote header comments that go into more details if anything is confusing but here is a more general description of my program:

CSVParser takes in a creatorfromrow instance and a reader, I use a bufferreader to go through the file passed in. 
I only have one method in my CSVParser class which parses the data.

Searcher takes in a CSV parser, the value to search for, a boolean indicating whether there
headers present in the data set, an integer which should indicate the column index and a string which holds the 
column header. I choose to take in a separate variables to check with a boolean if the user wants to use
more specific searching through a header or column index so that I could differentiate the different search patterns in my 
method using an if statement. Further, I have another method in search which converts header names into their corresponding
index value, which is called in my search method. My search method uses for loops to go through the data returned by parser.

My Main class implements the user interaction by prompting a user for the values needed to search. Then returning the row with
that value

My classes for testing are the String Creator which I use in my main class to just convert everything to strings. Then I made
a Pizza class and a PizzaCreator which creates objects of type Pizza by implementing the interface CreatorFromRow.

My test suite is where I handle all my test cases. 


# Errors/Bugs
I believe I am correctly catching errors, however I could not figure out how to test that the correct errors
were being thrown, I tried using the commented out test testParseNoFile to ensure that it was correctly being thrown,
which I know it is because when I run the test the error filenotfoundexception occurs but my test still fails. 
Another issue I was running into while testing was showing that my data works with double quoted values. I saw success
with this when using my main and user input but could not show how it was working through my tests. I used the regex provided in
the handout. I will do my best to fix these two bugs before the deadline but just wanted to record them here incase I run out of time. 

# Tests
I have 12 tests which should be a pretty comprehensive evaluation of my program: 
test parseempty (empty file)
test parsenonempty (nonempty file)
test parse valuesmall (ensure that a small data set is parsed correctly by matching data values)
test parsepizzaobjects (ensures that my instance of create and parse is working together to correctly create pizza objects)
test parseNoFile is commented out but I intended to check that errors were correctly being caught
test searchbasicsmall
test ColNameBasic
testColNumBasic
testLargeData
testMalformed
testMultiRow
testQoutes is also commented out but was supposed to test when the value passed in is in double qoutes

# How to...
I think the only thing a little unclear may be the fact search takes in a header, column index integer and a column name as a string.
If the user does not want to use any of these or only some, for the column index use a negative value, the boolean should be false
and the column name can either be anything. This works because if there is no header value in the data to the one passed in
the column indexer is just set to a negative value. I believe everything else is pretty straight-forward. 