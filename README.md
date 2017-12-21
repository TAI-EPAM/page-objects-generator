
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
## How to setup project and run examples
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
2. `uniqueness` - field of web element that must be unique on that page.
3. `selector` have two parameters:

   * `type` - the type of selector, `xpath` or `css` by which we search element on page
   * `value` - the value that element must
   
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
`type` - the same as in common element
`innerSearchRule` - list of search rules for elements containing in complex element. Search rules in
that list differs from common search rule only by field `title` instead of `type` 
## Search Rule
## Creating searchRule
## How to add group
## Creating custom Validator
## What technologies we used

