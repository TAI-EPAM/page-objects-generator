
[![Build Status](https://travis-ci.org/TAI-EPAM/page-objects-generator.svg?branch=master)](https://travis-ci.org/TAI-EPAM/page-objects-generator)
[![codecov](https://codecov.io/gh/TAI-EPAM/page-objects-generator/branch/master/graph/badge.svg)](https://codecov.io/gh/TAI-EPAM/page-objects-generator)
[![Maintainability](https://api.codeclimate.com/v1/badges/e527808dc8bd7806783f/maintainability)](https://codeclimate.com/github/TAI-EPAM/page-objects-generator/maintainability)
# page-objects-generator
1. [About POG](#about-pog)
2. [How to setup project and run examples](#how-to-setup-project-and-run-examples)
3. [Search Rule](#search-rule)
4. [JSON structure](#json-structure)
5. [Creating SearchRule](#creating-searchrule)
6. [Creating SearchRuleBuilder](#creating-search-rule-builder)
7. [How to add group](#how-to-add-group)
8. [Creating custom Validator](#creating-custom-validator)
9. [What technologies we used](#what-technologies-we-used)
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
### Download application
You can download .zip archive by the following link: https://github.com/TAI-EPAM/page-objects-generator/archive/master.zip<br/>
or you can use console and clone repository:
```text
git clone https://github.com/TAI-EPAM/page-objects-generator.git
```
### Run examples
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

## Search Rule
Search rule is set of rules using for searching web components on web pages. User could set search
rules in JSON files in special structure which you could see in [JSON structure](#json-structure)
section. There are 3 different types of search rule now: common, complex and form.
Respectively they all have their own JSON structure.
## JSON structure
Page object generator uses JSON files to form search rules for getting elements from web pages.
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
## Creating SearchRule
To create a new search rule, you should implement the `SearchRule` interface which extends from 
`Validatable` interface. Which include the following methods that you should override.

[SearchRule](https://github.com/TAI-EPAM/page-objects-generator/blob/master/src/main/java/com/epam/page/object/generator/model/searchrule/SearchRule.java) :
```java
public interface SearchRule extends Validatable {
    
    Selector getSelector();
    
    List<WebElement> getWebElements(Elements elements);
    
    void fillWebElementGroup(List<WebElementGroup> webElementGroups, Elements elements);
}
```
[Validatable](https://github.com/TAI-EPAM/page-objects-generator/blob/master/src/main/java/com/epam/page/object/generator/model/searchrule/Validatable.java) :
```java
public interface Validatable {
    
    void accept(ValidatorVisitor validatorVisitor);
    
    List<ValidationResult> getValidationResults();
    
    boolean isValid();
    
    boolean isInvalid();
}
```
If you don't follow any of given SearchRules (e.g. you want to use another unique element), then 
you can create your own SearchRule. Consider the creation a new SearchRule on the following example.

`MySearchRule` :
```java
public class MySearchRule implements SearchRule {
    private String uniqueness;
    private SearchRuleType type;
    private Selector selector;
    private ClassAndAnnotationPair classAndAnnotation;
    private XpathToCssTransformer transformer;
    private SelectorUtils selectorUtils;

    private List<ValidationResult> validationResults = new ArrayList<>();

    public MySearchRule(String uniqueness, SearchRuleType type, Selector selector,
                            ClassAndAnnotationPair classAndAnnotation,
                            XpathToCssTransformer transformer,
                            SelectorUtils selectorUtils) {
        this.uniqueness = uniqueness;
        this.type = type;
        this.selector = selector;
        this.classAndAnnotation = classAndAnnotation;
        this.transformer = transformer;
        this.selectorUtils = selectorUtils;
    }

    public String getUniqueness() {
        return uniqueness;
    }

    public SearchRuleType getType() {
        return type;
    }

    private String getRequiredValue(Element element) {
        return uniqueness.equals("MyElement")
            ? element.text()
            : element.attr(uniqueness);
    }

    public ClassAndAnnotationPair getClassAndAnnotation() {
        return classAndAnnotation;
    }

    public Selector getTransformedSelector() {
        if (!uniqueness.equalsIgnoreCase("MyElement") && selector.isXpath()) {
            try {
                return transformer.getCssSelector(selector);
            } catch (XpathToCssTransformerException e) {
                e.printStackTrace();
            }
        }
        return selector;
    }

    @Override
    public Selector getSelector() {
        return selector;
    }

    @Override
    public List<WebElement> getWebElements(Elements elements) {
        List<WebElement> webElements = new ArrayList<>();
        for (Element element : elements) {
            webElements.add(new CommonWebElement(element, getRequiredValue(element)));
        }
        return webElements;
    }

    @Override
    public void fillWebElementGroup(List<WebElementGroup> webElementGroups, Elements elements) {
        webElementGroups.add(new CommonWebElementGroup(this, getWebElements(elements),
            selectorUtils));
    }

    @Override
    public void accept(ValidatorVisitor validatorVisitor) {
        ValidationResult visit = validatorVisitor.visit(this);
        validationResults.add(visit);
    }

    @Override
    public List<ValidationResult> getValidationResults() {
        return validationResults;
    }

    @Override
    public boolean isValid() {
        return validationResults.stream().allMatch(ValidationResult::isValid);
    }

    @Override
    public boolean isInvalid() {
        return validationResults.stream()
            .anyMatch(validationResultNew -> !validationResultNew.isValid());
    }
}
```
Also for the implementation of full functionality necessary to create new [SearchRuleBuilder](#creating-search-rule-builder)
(for building custom SearchRule) and [WebElementGroup](#how-to-add-group)
(which contains SearchRule and Elements found by them) creation of which is described below.
## Creation SearchRuleBuilder
## How to add group
## Creating custom Validator
## What technologies we used
### Jsoup
Jsoup is a Java library for working with real-world HTML. It provides a very convenient API 
for extracting and manipulating data, using the best of DOM, CSS, and jquery-like methods.

**Usage in project**

* Extract DOM from website and find elements by the rules with CSS selector.

*More information about Jsoup you can read [here](https://jsoup.org/).*
### Xsoup
Xsoup is Java library which can parse DOM by using Xpath element. Xsoup parser based on Jsoup.

**Usage in project**

* Parse DOM and find elements by the rules with Xpath selector.

*More information about Xsoup you can read [here](https://github.com/code4craft/xsoup).*
### JavaPoet
JavaPoet is a Java API for generating `.java` source files.

**Usage in project**

* Generate `.java` source files.

*More information about JavaPoet you can read [here](https://github.com/square/javapoet).*
### JSON Schema Validator
JSON Schema is a declarative language for validating the format and structure of a JSON Object. 
It allows us to specify the number of special primitives to describe exactly what a valid JSON Object will look like.

**Usage in project**

* Validation json files.

*More information about JSON Schema Validator you can read 
[here](https://github.com/everit-org/json-schema).*
### Apache Commons Lang
Apache Commons Lang, a package of Java utility classes for the classes 
that are in java.lang's hierarchy, or are considered to be so standard as to 
justify existence in java.lang.

**Usage in project**

* String handling.

*More information about Apache Commons Lang you can read 
[here](https://commons.apache.org/proper/commons-lang/).*
### Apache Commons Lang
Apache Commons Lang, a package of Java utility classes for the classes 
that are in java.lang's hierarchy, or are considered to be so standard as to 
justify existence in java.lang.

**Usage in project**

* String handling.

*More information about Apache Commons Lang you can read 
[here](https://commons.apache.org/proper/commons-lang/).*
### Mockito
Mockito is a mocking framework that allows write tests with a clean and simple API. 
Tests are very readable and they produce clean verification errors.

**Usage in project**

* Use for unit testing.

*More information about Mockito you can read [here](http://site.mockito.org/).*
### JUnit
JUnit is a test framework which uses annotations to identify methods that specify a test.

**Usage in project**

* Use for unit testing.

*More information about JUnit you can read [here](http://junit.org/junit5/).*
### Log4j
Log4j is a reliable, fast and flexible logging framework (APIs) written in Java, 
which is distributed under the Apache Software License. 
Log4j is highly configurable through external configuration files at runtime. 
It views the logging process in terms of levels of priorities and offers mechanisms 
to direct logging information to a great variety of destinations, 
such as a database, file, console, etc.

**Usage in project**

* Add logs in the project.

*More information about JUnit you can read [here](https://logging.apache.org/log4j/2.x/index.html).*