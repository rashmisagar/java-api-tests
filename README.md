# API Testing with RestAssured and TestNG

This project contains a suite of API tests using the RestAssured library and TestNG framework.

## Test Class

The `ProductAPITests` class contains all the test methods. The tests interact with the API to create, update, search, and delete products.

## Use of POJO Classes in Tests

In the tests, I used POJO classes to represent the data and to convert them to JSON to be sent in the body of a POST or PATCH request and convert the JSON response back into an instance of the class to easily access the properties.

## Setup and Cleanup

The `setup()` method sets the base URI for RestAssured and runs once before any of the test methods.

The `cleanup()` method is intended to delete all test data and close the MongoDB connection. However, it is currently commented out and does nothing. This method runs once after all the test methods and to be put as for further addtions.


## Running the Tests

The tests can be run through the `testng.xml` file. This file is a TestNG configuration file that specifies which test methods to run.

Here are the steps to run the tests:

1. Start the server.
2. Open the project in your IDE
3. Load the project dependencies from `pom.xml`
4. Locate the `testng.xml` file in the project directory.
5. Right-click on the `testng.xml` file and select `Run 'testng.xml'`. This will run all the test methods specified in the file.
6. After the tests have run, you can view the results in the Test Runner tab of your IDE.


## Additional Thoughts and Considerations

### Test Coverage
While the current tests cover the basic CRUD operations, there are many other aspects of the application that could be tested. For example, we could add tests for edge cases, such as creating a product with a very long name or a very large price. We could also add tests for the application's performance and security.

### Test Data Management
In the current solution, test data is created and deleted as part of the tests. This is a good practice as it ensures that the tests are not dependent on the existing data in the database. However, it would be beneficial to have a more robust system for managing test data, such as a separate test database or a way to reset the database to a known state before each test run.

### Continuous Integration
The tests are currently run manually, which can be time-consuming. Integrating the tests into a continuous integration (CI) pipeline would allow them to be run automatically whenever changes are made to the code. This would help catch any regressions early and ensure that the application is always in a releasable state.

### Test Reporting
While the current solution logs the results of each test to the console, it would be beneficial to have a more detailed test report. This could include information such as the number of tests passed and failed, the time taken for each test, and any error messages. There are many tools available that can generate such reports, such as Allure or Extent Reports.

### Code Review and Refactoring
Regular code reviews and refactoring can help maintain the quality of the test code. This includes following best practices for writing clean, readable, and maintainable code.
