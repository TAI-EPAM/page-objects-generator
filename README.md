
[![Build Status](https://travis-ci.org/TAI-EPAM/page-objects-generator.svg?branch=master)](https://travis-ci.org/TAI-EPAM/page-objects-generator)
[![codecov](https://codecov.io/gh/TAI-EPAM/page-objects-generator/branch/master/graph/badge.svg)](https://codecov.io/gh/TAI-EPAM/page-objects-generator)
[![Maintainability](https://api.codeclimate.com/v1/badges/e527808dc8bd7806783f/maintainability)](https://codeclimate.com/github/TAI-EPAM/page-objects-generator/maintainability)
# page-objects-generator
This is simple library which can generate pageobjects for any site
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
## JSON structure
## Search Rule
## Creating searchRule
## How to add group
## Creating custom Validator
## What technologies we used

