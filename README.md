
[![Build Status](https://travis-ci.org/TAI-EPAM/page-objects-generator.svg?branch=master)](https://travis-ci.org/TAI-EPAM/page-objects-generator)
[![codecov](https://codecov.io/gh/TAI-EPAM/page-objects-generator/branch/master/graph/badge.svg)](https://codecov.io/gh/TAI-EPAM/page-objects-generator)
[![Maintainability](https://api.codeclimate.com/v1/badges/e527808dc8bd7806783f/maintainability)](https://codeclimate.com/github/TAI-EPAM/page-objects-generator/maintainability)
# page-objects-generator
1. [About POG](#about-pog)
2. [How to setup project and run examples](#how-to-setup-project-and-run-examples)
3. [JSON structure](#json-structure)
4. [Search Rule](#search-rule)
5. [Creating searchRule](#creating-searchrule)
6. [How to add group](#how-to-add-group)
7. [Creating custom Validator](#creating-custom-validator)
8. [What technologies we used](#what-technologies-we-used)
## About POG
This program is needed for creating java files, which are based on JSON file with rules and list of URL.
In general, POG is used in testing web sites and definitions different types of elements on web pages. For instance, button, dropdown list and etc.
By using POG tester-engineer can generate java file for JDI-framework and can do work faster.

Structure of project:
1) First step is user has to download JSON file;
2) After that program create list of Search Rules;
3) Then JSON Schema Validator checks JSON file on validness: as result throw exceptions if file has some problem, otherwise all list of Search Rules goes further;
4) After successful validation method FormTypeValidator splits every Rules on three types: Common, Complex, Form;
5) Then every types is checked by business logic on accordance. This is another place, where we can see exception;
6) If we have list of valid JSON files, program takes list of URL and WebPageBuilder and creates WebPage or generate exception;
7) Then program checks WebPages and puts acceptable WebPages in list of JavaClassBuildable;
8) JavaClassBuildable decides which type of java file has to be build and puts them in JavaClass;
9) After all these manipulations program gives command for JavaWriter and java files are written in file.
## How to setup project and run examples
###Download application
You can download .zip archive by the following link: https://github.com/TAI-EPAM/page-objects-generator/archive/master.zip<br/>
or you can use console and clone repository:
```text
git clone https://github.com/TAI-EPAM/page-objects-generator.git
```
###Run examples
After downloading you can open project in IDE which you prefer.
You can find "example" folder in tests, which contains [ExampleTest](https://github.com/TAI-EPAM/page-objects-generator/blob/master/src/test/java/com/epam/page/object/generator/example/ExampleTest.java) file.
Each test need to run in isolation from others, because folder with generating .java source files are cleared before each test. 
In [ExampleTest](https://github.com/TAI-EPAM/page-objects-generator/blob/master/src/test/java/com/epam/page/object/generator/example/ExampleTest.java) you can change outputDir where will be generate .java source files and packageName from which will be started structure of the project.
```text
private String outputDir = "src/test/resources/";
private String packageName = "test";
```
At the end of each test you can find generating .java source file in folder which located by the path: outputDir + packageName. <br/>
For example run exampleTestCommonElement. We can see our .java source files by the following path: "src/test/resources/test".<br/>
<p align="center"><img src ="https://user-images.githubusercontent.com/13944502/34299793-e66f4ec8-e735-11e7-91b3-48a7d0787e66.png" /></p>

## JSON structure
Page object generator uses JSON files to from form search rules for getting elements from web pages.
There are three types of JSON structure that can be processed by POG, for each of search rule type:
### Common element
```json
{
  "elements": [
    {
      "type": "button",
      "uniqueness": "value",
      "selector": {
        "type": "xpath",
        "value": "//input[@type='submit']"
      }
    }
  ]
}
```
As seen on given example, JSON for common element consist of this parameters:
1. `type` - type of web element to search, from list SearchRuleType class.
2. `uniqueness` - attribute of web element that must be unique on that page.
3. `selector` have two parameters:

   * `type` - the type of selector, `xpath` or `css` by which we search element on page
   * `value` - the value that element must correspond to search result by described type.
   
### Complex element
Complex elements, such as dropdown menus, containing list of simple elements processed by
`ComplexSearchRule` :
```json
{
  "elements": [
    {
      "type": "dropdown",
      "innerSearchRules": [
        {
          "title": "root",
          "uniqueness": "text",
          "selector":{
            "type": "xpath",
            "value": "//button[@class='dropbtn']"
          }
        }
      ]
    }
  ]
}
```
1. `type` - type of web element to search, from list SearchRuleType class.
2. `innerSearchRule` - list of search rules for elements containing in complex element. Inner 
search rules have this parameters:
   * `title` - contains name that will be used to build annotation for found element. 
   It is required to have one inner search element with this field have value `"root"`.
   * `uniquueness` - attribute of web element that must be unique on that page.
   * `selector` have two parameters:
   
      * `type` - the type of selector, `xpath` or `css` by which we search element on page
      * `value` - the value that element must correspond to search result by described type.
### Form and Section
Form with section (such as login form) requires specific kind of json. 
```json
{
  "elements": [
    {
      "type": "form",
      "section": "HtmlForm",
      "selector": {
        "type": "css",
        "value": ".w3-example form"
      },
      "innerSearchRules": [
        {
          "type": "textfield",
          "uniqueness": "name",
          "selector": {
            "type": "css",
            "value": "input[type=text]"
          }
        },
        {
          "type": "button",
          "uniqueness": "value",
          "selector": {
            "type": "css",
            "value": "input[type=submit]"
          }
        }
      ]
    }
  ]
}
```
1. `type` -  must have value `"form"`
2. `section` - describes type of form.
3. `selector` have two parameters:
      
      * `type` - the type of selector, `xpath` or `css` by which we search element on page
      * `value` - the value that element must correspond to search result by described type.
4. `innerSearchRule` - list of search rules for elements containing in complex element. Inner 
search rules have this parameters:
   * `type` - contains name that will be used to build annotation for found element. 
   It is required to have one inner search element with this field have value `"root"`.
   * `uniquueness` - attribute of web element that must be unique on that page.
   * `selector` have two parameters:
 
      * `type` - the type of selector, `xpath` or `css` by which we search element on page
      * `value` - the value that element must correspond to search result by described type.
## Search Rule
## Creating searchRule
## How to add group
## Creating custom Validator
## What technologies we used



