# nicole v 1.0
Nicole is a JSF-JavaScript library which enables you to integrate your own JavaScript-code into JSF in a clean and consistent way. It takes care of the clientId-prefix hell and enables you to access parameters that were defined on the serverside in your JavaScript-Code.

#Installation
Build the project with maven (e.g. mvn clean install) and include nicole in the pom.xml of your project:
```html
 <dependency>
      <artifactId>nicole</artifactId>
      <groupId>de.openknowledge</groupId>
      <version>1.0</version>
  </dependency>
```

#Usage
As soon as a nicole-module is included in a jsf page, the required JavaScript-Library gets injected automatically. 
Nicoles' namespace is "http://openknowledge.de/nicole".

<b>IMPORTANT:</b> Scriptfiles that contain the JavaScript-code for your modules need to be included at the very bottom of the page. The easiest way to achieve this is to mark the script-tag with target="body".

<b>JSF-Page</b>
```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:jsf="http://xmlns.jcp.org/jsf" xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:nicole="http://openknowledge.de/nicole">
<head jsf:id="head">
</head>
<body jsf:id="body">

    <nicole:module modulename="SimpleExample">
        <nicole:jsparameter name="serverTime" value="#{backingBean.currentTime}"/>
    </nicole:module>
    
<h:outputScript library="ok" name="simpleExample.js" target="body"/>
</body>
</html>
```
<b>JavaScript-Module</b>

```javascript
Nicole.module("SimpleExample", function () {
    //this.parameter() gets a property which was defined in jsf
    //second parameter is optional
    var serverTime = this.parameter("serverTime", "int");
    console.log(new Date(serverTime));
});
```

<b>IMPORTANT:</b> The modulename of your JavaScript-Module must match the modulename that was defined in the JSF-Page. Otherwise you will get a "There is no js-module for the declared module"-Error. 

<b>Usage of jQuery:</b>
Nicole is also designed to work with jquery. When your JavaScript-Modules depend on jQuery, it is important that jQuery is included BEFORE nicole. To achieve this, include jQuery with target head or without any target.

```html
...
<h:outputScript library="scripts" name="jquery.min.js" target="head"/>
or
<h:outputScript library="scripts" name="jquery.min.js"/>
```
You can also include the nicole scripts by hand:
```html
...
<h:outputScript library="scripts" name="dist/nicole.min.js" />
or for development.
<h:outputScript library="scripts" name="dist/nicole.js" />
```

#Basic API
Nicoles' JavaScript engine has a few basic helper functions that make the interaction with JSF easier.

- <b>this.elm(domElId)</b> and <b>this.$elm(domElId)</b>: this function returns the DOM-Element (resp. the jQuery wrapped DOM-Element) for the given element-id. The function is more than a wrapper for document.getElementById (resp. $(domElementId) when jQuery is present) because it takes care of JSFs' generated ID-prefixes (clientId). This is especially useful when writing composite-components. Example:
```html
<composite:interface>
    <composite:attribute name="value" type="java.util.Date"/>
    <composite:attribute name="jsFormat" type="java.lang.String"/>
    <composite:attribute name="javaFormat" type="java.lang.String"/>
</composite:interface>
<composite:implementation>
    <input type="text" jsf:id="datePicker" jsf:value="#{cc.attrs.value}">
        <f:convertDateTime pattern="#{cc.attrs.javaFormat}"/>
    </input>
    <nicole:module modulename="DatePicker">
        <nicole:jsparameter name="format" value="#{cc.attrs.jsFormat}"/>
    </nicole:module>
</composite:implementation>
```

```javascript
Nicole.module("DatePicker", function () {
    //we don't care about the clientId-prefix that is generated by jsf here. 
    //Nicole automatically adds the id-prefix so that we access the dom-element
    //by using a simple id
    this.$elm("datePicker").datepicker({
        dateFormat: this.parameter("format")
    });
});
```
- <b>this.parameter(paramName,[dataType])</b>: this function gives access to the parameters that where defined in JSF (see the "format" example above). This way you can use server-side defined parameters in your JavaScript-Code. The second parameter is optional. It allows you to cast a parameter into different types. Possible values are "int", "float","bool"
- <b>this.parameterHasValue(parameterName)</b>: checks if the given parameter name is present and has a value 

#Advanced API

- <b>this.ajax</b>: this function is a wrapper for JSFs' ajax api. It allows you to use simple ids instead of the full-qualified ids that you would normally have to use. Example:

```javascript
Nicole.module("SomeModuleWithAjax", function () {

   this.$elm("someButton").on("click",function(){
       this.ajax("elementIdA", {
          execute: ["elementIdA","elementIdB"],
          render: ["elementIdC"],
          begin : function(){
          },
          complete : function(){
          },
          success: function () {
          });
   });
  
});

 ```                      



#Compatability
Nicole works with all browsers >= IE 9

