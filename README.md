# LYA_AutomatedTesting project 
was created for purpose of automated testing for the given site during the testing task execution.

## Task description
Create an automated tests script - walkthrough that makes sure that there are:

1) a minimum of 10 artists on the starting page

2) the "Maifeld Derby" is visible, if the filter is selected

3) buy an available merch and verify the shipping costs to germany in the "shipping" step

4) go back, buy additional tickets, delete everything without tickets and verify that now there is no shipping step needed.

## Used tools
- JDK 1.8 64 bit;
- Selenium WebDriver;
- JUnit 4 as test framework;
- Firefox browser should be installed;

## Developer environment
- Eclipse IDE;
- Ant.

## Additional details
The automated project uses java.util.logging library for logging and takes a screenshot for last step of test execution automatically.

Due to the given implementation of DOM model (for some site pages) the test script uses javascript executor for forced clicking on some controls. Also ther are a several usability bugs/improvements should be resolved from the site developer's side for best results of automated test execution (see references in the test code).

Result of test execution consists of two parts: first part - for the 1 task point and second part - for the 2 - 4 points.
Video record with test execution here: https://github.com/ytarankov/LYA_AutomatedTesting/blob/master/Test_smoke_execution_20160512_lq.zip  - file was uploaded with low quality due to the set limit by github for uploaded file size.

## TODO
Some methods and the structure of test - to be refactored. 
