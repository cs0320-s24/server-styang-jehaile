# Project details:

## Project name:
Server
## Project description:
This project handles: setting up a web API server that provides access to multiple data sources,
using proxy and other patterns to mediate access to that data,
serializing and deserializing JSON messages,
the difference between unit and integration testing, and how to practice the latter
the value of “mocking” data. The server that this project sets up allows a user to
load, search, and view local files, and also to find census data based on state and county
via the American Community Survey API.
## Team members:
Simon Yang and Jowet Haile

## Estimated time: 
30 hours
## Link to repo: 
https://github.com/cs0320-s24/server-styang-jehaile.git
# Design choices – high level design
Our program has 5 main components: Server and ServerTestSuite in addition to CSVParser, Search Utility and Exceptions class from the previous sprint. These main packages also have subpackages which we will go through below.
From Sprint 1: CSV README - (INSERT)

## Server Package:

The server package is split into the CSV and Broadband packages which handle retrieving, converting, sending and other interaction with the csv files and broadband percentage census API respectively.

## CSV Package
The CSVPackage includes the following files:
### CSVDataSource 
This class handles the interactions with the csv data files to load, view and 
search through the files and is returned to the user through their respective handler classes which are also a part of this package. The 4 methods in this class include: loadCSVData, searchCSVData, viewCSVData and isLoaded. LoadCSVData takes in the filename and a boolean indicating whether the file contains headers. This method then uses the CSVParser to parse the data and if successfully parsed it sets the boolean indicating that a file was parsed and loaded to true. The searchCSV method then interacts with the search class after the data is parsed and loaded to search based on the values passed into the method from the request the user makes. We allow the user to search for a value through the whole dataset, through header names or through column indices. This method then returns to the rows where the value was found as a list of list of strings and handles a failed search throwing errors which are caught when this method is called in the search handler class. Similarly the viewCSV file gets the contents of a csv file from the parser and returns it to the user after a file was loaded by the user. The last method of this class is a boolean method that determines whether the data was loaded. This class is essential to separating the functionality of the CSV file parsing and search so that it can be easily used to be converted in the handlers.
### LoadCSVHandler
This class is used to handle the user request to load a csv file. This calls on the data source class which will parse the file and load it. This class implements route to override the handle method to handle the query of the file name requested by the user. If the data was successfully loaded this class displays a success message to the user by serializing it to a JSON string. Similarly if the data failed to load a failure message in the form of a JSON string is presented to the user. 
### ViewCSVHandler
This class utilizes the data source class which returns the contents of a csv file to convert this into json data viewable by the user through the url. This class also implements route handles serializing the file into JSON or a failure message indicating to the user that they may be trying to view an unloaded file.
### SearchCSVHandler
This class also implements route and uses the data source class to return the java data of the rows containing values searched by the user. The handle method of this class querying the request from the user which should indicate the value to search for, through which header and/or column index. If the file is loaded and searching the csv file is successful then this class serializes the java data into JSONs viewable by the user successfully. This class also serializes a response to the user if their attempt to search was unsuccessful for example if they are searching for a column that does not exist. If the user searches for a value in the data set that is not present the program behaves by providing no matches or an empty list.
### Broadband Package
The Broadband package contains the following classes:
#### BroadbandHandler Package
The BroadbandHandler package contains the following classes, responsible for receiving API requests and returning census data.
##### BroadbandDataSourceInterface
The BroadbandDataSourceInterface class is an interface containing the method ‘getBroadbandData’. The presence of this interface makes caching possible, as CachingBroadbandDataSource can be used as a proxy for and other instance of BroadbandDataSourceInterface. BroadbandDataSourceInterface instances are passed to the BroadbandHandler, allowing for versatility in retrieving API (or mock) data. This was especially useful during testing, when I created a MockBroadbandSource class that implemented the BroadbandDataSourceInterface and allowed for ease of data injection and unit testing.
##### BroadbandDataSource
The BroadbandDataSource class is an instance of BroadbandDataSourceInterface. At a high level, the class is responsible for building and requesting queries from the census API, passing them to APIUtilities class to be deserialized, and returning them as a BroadbandData object. BroadbandDataSource makes one call to the Census API upon initiation to build a HashMap from state String to state code String. This design choice limits the amount of queries made, and creates a convenient, runtime-efficient way to find the state code corresponding to a queried state. From there, the class’s getBroadbandData method uses inputted state and county names to make a census call and consolidate its response into a BroadbandData object to be used by the BroadbandDataHandler.

##### BroadbandHandler
The BroadbandHandler class facilitates API requests and is responsible for interfacing with DataSource classes to call for and return data to the web API. BroadbandHandler takes in an instance of BroadbandDataSourceInterface, which it calls in its handle method to retrieve data based on the query that handle receives. By using a BroadbandDataSourceInterface to get data (rather than making API calls itself), the handler separates functionality and makes the code more versatile for a variety of usages. 

##### CachingBroadbandDataSource
The CachingBroadBandDataSource class is a proxy class used to wrap a BroadbandDataSourceInterface and allow for caching functionality. The class is configurable by developers, allowing them to change eviction policies by changing the CacheBuilder passed into its cache field. To change the maximum size of the cache, developers should change ‘maximumSize’ value in CachingBroadbandDataSource’s constructor. Similarly, they can modify the expireAfterWrite field and add any other policies by changing the CacheBuilder’s fields/values after investigating the Guava caching library. The class receives getBroadbandData requests from BroadbandHandler and determines if it has the request stored. If not it calls the getBroadbandData method within the BroadbandDataSourceInterface that it wraps. This minimizes queries and maximizes efficiency for users and developers.

#### BroadbandData 
The BroadbandData class represents a piece of data retrieved by an instance of BroadbandDataSourceInterface. The class stores a state name (String), county name (String), census data regarding broadband access (double), and the time that the data was retrieved (String). 
#### BroadbandAPIUtilities
The BroadbandAPIUtilities class is responsible for the deserialization of data retrieved by instances of BroadbandDataSourceInterface. By consolidating deserialization methods, this class makes the project more readable and easy-to-use. 
## Server
The server class is the higher level class that handles user interaction with the program by starting spark and the port such that the user can navigate to the url and began making requests. The main method in the server class facilitates this interaction by calling on the 4 handler classes which serve as endpoints, the broadbandhandler, loadcsvhandler, searchcsvhandler, and viewcsvhandler to perform the interaction between the datasource, api and user. The main method catches errors thrown by classes called within it.
## ServerTestSuite
This package contains four classes which test the csv and broadband with both integration and unit test. It also contains a MockBroadbandSource class which is used to make tests easily configurable. Finally, it contains request classes for each of BroadbandRequest, LoadCSVRequest, SearchCSVRequest, and ViewCSVRequest. These make it possible to query handlers without explicitly creating a Spark request.

# Errors/Bugs

We have thoroughly tested and interacted with our program such that we believe there are no issues significantly hindering the functionality and performance of this program. However in the ServerCSVTestingSuite we ran into a bug where two tests are failing when we run the entire test suite but passing individually and when the same behavior is emulated in through running the server url port the expected behavior matches the actual. This behavior is occurring because the thread the server is run on is being affected by the performance time of the other tests when they are all run together influencing what response is displayed. Since the test is searching and viewing a file before it is loaded it should display a failure message to load the file which our program does when the user interacts with it.   We tried increasing the time the server thread sleeps in between tests, creating a boolean that is set while the server is running and other ways to stop this interaction when all the tests are run together.
# Tests
Our test suite is split into four classes to check the unit test and the integration test of the broadband package and the csv package.


##  ServerCSVIntegrationTestSuite
These tests confirm that the API server works as expected using JUnit integration tests. I set up the server thread and tear it down after the tests. 
### testLoadCSV
This test is checking that that the load handler class is correctly loading the data by checking the numerical code return
### testViewCSV
This tests that the view csv endpoint is working properly by checking the response code.
### testSearchCSV
This test checks that the search csv endpoint is working properly by checking the response code.
### testViewBeforeLoad
This test ensures that a user can not view a file that has been loaded by checking the string
### testSearchBeforeLoadCSV
This test ensures that a user can not search a file that has been loaded by checking the string
### testContentsofViewCSV
This tests the contents of the view csv by checking that it contains the correct values by checking the string
### testContentsofViewCSVLargeData
This tests contents of the view csv of a large data file to make sure it is can process large sets by checking that it contains the correct values by checking the string
### testSearchCSVInvalid
This tests that if a value is search in a data file and the value isnt present then it returns an empty match “[]”
### testSearchCSVHeaders
This tests that a user can search through a csv file for data using the header name
### testSearchCSVColumnIndex
This tests that a user can search through a csv file for data using the column index

## ServerBroadbandIntegrationTest 
These tests confirm that the API server works as expected using JUnit integration tests
### testBroadbandRequestSuccess
This test checks the broadband request and response works successfully by testing the resposne code, and specific county percentage.
### testBroadbandRequestFailureNoState
This test checks that if a broadband request was made without entering a state name the json states error and instructs the user to input a state name.
### testBroadbandRequestFailureNoCounty
This test checks that if a broadband request was made without entering a county name the json states error and instructs the user to input a county name.
### testBroadbandRequestFailureNoCountyNoState
This test checks that if a broadband request was made without entering a county or state name the json states error and instructs the user to input a county name and state name
### testBroadbandRequestEmptyState
This tests that if a user enters a broadband request without a state but a county, like &state=&county=providence
That the user ensures they are putting in a valid state by displaying the associated json message
### testBroadbandRequestEmptyCounty
This tests that if a user enters a broadband request without a county but a state, like &state=georgia&county=
That the user ensures they are putting in a valid county by displaying the associated json message
### testBroadbandRequestEmptyCountyEmptyState
This tests that if a user enters a broadband request without a state and a county, like &state=&county=
That the user ensures they are putting in a valid county and state by displaying the associated json message


## ServerBroadbandUnitTest 
These tests confirm that our program works even if a user wanted to use an API datasource locally that our program would still maintain functionality

### testCachingFunctionalityTimeExpiration
This test checks the time expiration limit on the cacher, by checking that if a user loads a request then waits for a time limit that when they cache again that it is not the same as initial response.
### testCachingFunctionalityTimeNonExpiration
This test checks the time expiration limit on the cacher, by checking that if a user loads a request then waits for a time limit that when they cache again that it is not the same as initial response.
Tests that two requests made to the server, the first one is not present int he cache anymore after the second one was cached
### testGetResponseWebSource
This ensures that the response from the api websource is correct.
### testGetResponseNoSuchElement
Tests that if a invalid state or county name is inputted then no such element is asserted
### testBroadbandHandlerSuccess
Tests that the broadband handler returns the correct response.
### testBroadbandHandlerFailureNoCounty
Tests that the broadbandhandler displays a failure message when requested without entering a county
### testBroadbandHandlerFailureNoState
Tests that the broadbandhandler displays a failure message when requested without entering a state
### testBroadbandHandlerFailureNoCountyNoState
Tests that the broadbandhandler displays a failure message when requested without entering a county and a state
### testBroadbandHandlerFailureNullState
Test that the broadband handler displays a failure message when a null state is requested
### testBroadbandHandlerFailureNullCounty
Test that the broadband handler displays a failure message when a null county is requested
### testBroadbandHandlerFailureNullStateNullCounty
Test that the broadband handler displays a failure message when a null state and null county is requested

## ServerCSVUnitTest
These tests confirm that our program works even if a user wanted locally interact.

### testLoadCSVHandlerSuccess
Tests that the loadcsv handler works correctly
### testLoadCSVHandlerFailureMalformed
Tests that the loadcsv handler displays a message that malformed data cant be parsed and loaded
### testloadcsvhandlerfailurenofilename
Tests that the loadcsv handler displays a message that if no file name is provided it cant be parsed and loaded
### testloadcsvhandlerfailurefilenotfound
Tests that the loadcsv handler displays a message that if a file is not found it cant be parsed and loaded
### testloadcsvhandlernullheaders
Tests that the loadcsv handler displays a message that if null headers( are passed in it be parsed and loaded
### testloadcsvhandlermalformedheaders
Tests that the loadcsv handler displays a message that if malformed headers are passed in it be parsed and loaded
### testloadcsvhandlerfailureinaccessible
Tests that the loadcsv handler displays a message that if an inaccessible file name is passed in it be parsed and loaded
### testsearchcsvsuccess
Test that there is a good success response from the search handler
### testsearchcsvfailurecolumdoesnotexist
Tests that the searchcsv handler displays a failure message that if column doesnt exist are passed in it be parsed and loaded
### testsearchcsvfailurecolumindexwrong input
Tests that the searchcsv handler displays a failure message that if column index is incorrect a file cant be passed in it be parsed and loaded
### testsearchbeforeload
Test that a file cant be search if it is not loaded
### testviewsuccess
Test a successful viewing of a csv file using the view handler
### testviewfailurenoload
Test that a file cant be view if not loaded

# How to:
## Run server:
- Enter “mvn package” in the command line
- Type ./run
- Follow the url to the  http://localhost:1234
- Choose which endpoint you want to use by typing either “/loadcsv” /viewcsv” “/searchcsv” or “/broadband” 
- For /loadcsv:
Type “?fileName = replace with target file name”
- For /viewcsv: no parameters are required if a file has already been loaded.
- For /searchcsv:
Type “?fileName = replace with target file name”
Type “&toSearch = replace with target string to search for”
Optional if wanting to search by headers: “&headerName= replace with target header name”
Optional if wanting to search by column index: “&columnIndex= replace with target column to search through”
- For /broadband:
“?state= replace with the state name”
“&county= replace with county name”

## Run tests:
- Navigate to test class that you will be running
- Find green arrow at the beginning of test class, to the left of the code
- Press green arrow to run class of tests


