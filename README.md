# nicole v 1.0
Nicole is a JSF-JavaScript library which enables you to integrate your own JavaScript-code into a JSF-frontend in a clean and consistent way. 

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

<b>IMPORTANT:</b> The modulename of your JavaScript-Module must match the modulename that was defined in the JSF-Page.

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

#API


#Compatability
Nicole works with all browsers >= IE 9

