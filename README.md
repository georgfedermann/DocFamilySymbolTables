# hit2assext
## Introduction

hit2assext stands for: extension classes for the HIT to Assentis DocFamily transformer project.

This project is an extension to the document designer DocDesign of the 
output management system Assentis DocFamily. DocDesign is an XSL-FO design
tool. It produces XSLT output that can be rendered to various output formats
like PDF etc.

XSLT is a functional language. DocDesign ships with a concept DocumentVariable
which loosens the stern declarative character of XSLT by introducing writable
variables.

This project takes the idea a step further by introducing one dimensional variables
that can be used like lists or fields.

The motiviation for this project is to provide support for the hit2ass project,
which transforms HIT/CLOU text components to Assentis DocFamily Workspaces.  
Hit/CLOU used an imperative language making a transformation to declarative
XSLT demanding. DocFamily offers great support by shipping with aforementioned
document variables. But there're still uncovered grounds:
* lists / fields
* tables
* while loops

While this project clearly is intended to support the hit2ass project it can
be used independent from the hit2ass project. By adding this extension to
your DocFamily installation, you can use the new concepts as described in this
manual to realize requirements you might have been unable to solve before, or
at higher cost.

This project uses well documented hook points within DocFamily to provide
extension classes that add new features to DocDesign and the rendering engine
DocBase.

Thanks for your feedback, questions and bug reports.

## Setting up hit2assext

To use the hit2assext project, follow the following steps:
* checkout the project
* build the project  
  ```
  mvn clean package assembly:single
  ```
* Deploy the resulting jar file to the lib folders of your DocBase render engines, including the desktop server shipping with DocDesign  
  ```cp target/HitAssExtensions-jar-with-dependencies.jar DocDesignInstallationDir/tomcat/webapps/DocBase2/WEB-INF/lib```
* Restart DocDesign or just the desktop server

To use the new features in a DocDesign workspace you have to prepare your DocDesign workspace by following proceedings. These steps can be applied both to a newly created workspace as well to an existing project to which you want to add the features provided by this project.
* To use the DocFamily extension mechanism you firstly have to provide a Java class implementing
the new features on the classpath of the DocBase service, which we have already done above. Next,
you have to make this class known to the system. This works by declaring a new namespace which will
be used to refer to methods of the given class within XPath expressions. You provide the new namespace
in the Document element of your DocDesign workspace. Let's add a new namespace together: Open your workspace
in DocDesign, left-click the Document element in the tree view, and choose the _XML Namespace_ tab in the
property editor on the far right. Click _Add_ and enter the following values as prefix and URL respectively:
as prefix we enter _hit2assext_ and as URL we give the full qualified class name of our extension class, which is
`org.poormanscastle.products.hit2assext.RenderSessionManager'
* Right at the start of your PageContent element, add a DocumentVariable. This step will initialize a render session manager which will handle your symbol tables containing the list / field variables, and will also hold a uuid identifying the render session in a multithreaded environment which DocBase provides. So, add a new DocumentVariable element and configure its name and value as stated here:  
  ```Name: 'renderSessionUuid'  
  Value: hit2assext:createRenderSessionContext()```  
  **Nota bene**: both values, name and value, are XPath expressions, so the single quotes are mandatory for the name,
or else DocBase will try to evaluate the given value as an XPath. This will break your code.  
* At this point you have configured your system to create a RenderSessionManager at render time. The RenderSessionManager will manage the symbol tables for your new list variables and provide access to your lists and their respective values. **Nota bene**: It's mandatory to clean this up after rendering or else you'll introduce a resource leak, a memory leak in this case. You do the clean up by adding a _Dynamic Content_ element at the very end of your _Page Content_, containing the following XPath: `hit2assext:cleanUpRenderSessionContext(var:read('renderSessionUuid'))
**Nota bene** var:read('renderSessionUuid') is a standard DocDesign XSLT extension and reads the value of the document variable we've declared together earlier.

After you've completed this section you're ready to reap the benefits of the hit2assext project.

## Testing your setup
This section leads you through a quick smoke test so you know whether you did every right so far.
**Nota bene** This step requires that you worked through the previous sections.  

You can test the successful installation by creating a new **Simple Document** in DocDesign and configuring the hit2assext system as described above. Then add a new **Dynamic Content** element to your document. **Nota bene**: The new element has to be located in between the elements initializing the RenderSessionManager and cleaning it up again.  
In the new **Dynamic Content** element enter the following XPath: `hit2assext:testConfiguration()`  When publishing your document without XML document, it should print the text **Hello, World!** If you see this output, you're good. If you see no output and/or get error message, go through above steps again. Please, also have a look at the section about maintenance and logging below in this document. Turning on logging might also help you in persueing error causes.

## Working with hit2assext

This section describes what you can do with the hit2assext module and the syntax of the commands involved. It's a list of all available commands with information what they are meant to do and how they have to be used, including the syntax.

### Create RenderSessionContext
#### Abstract
Rendering of documents always happens in the render engine DocBase. Since DocBase is a multithreaded environment, multiple documents can be created concurrently, i.e. more or less at the same time. The DocDesign extension mechanism works with a static context. Thus, the hit2assext symbol tables exist in a static context, and therefore visible to all render threads that exist at a given time. To separate symbol tables for each render thread, render sessions are introduced. For each rendering process, one of those render sessions has to be created, and also has to be destroyed when the rendering is completed. The symbol table exists within the render session and is called RenderSessionContext. Creation and deletion of those render session objects is the responsibility of the DocDesign template designer. When a render session is created, the hit2assext system reports back the unique render session id by which the render session object can be referenced. It's important to keep this id or the render session cannot be referenced, e.g. to create lists, manipulate lists, and to delete the render session altogether when not needed any more. Therefore, the best practice is to create the render session within a DocDesign DocumentVariable element and store the uuid within the respective document variable.  
####Sample code
In DocDesign, add a new DocumentVariable element to the very beginning of your PageContent, and in the property editor of this DocumentVariable element, wihtin the XML section, set the name to the value  
`'renderSessionUuid'`  
Set the value of the document variable to this value:  
`hit2assext:createRenderSessionContext()`  
__Nota bene__:
* both values are XPath expressions. Do not forget to enter the single quotes around the variable name. 
* The _function_ hit2assext:createRenderSessionContext() creates a new render session and returns the id of the newly created render session. It also creates a new symbol table within this session, called _render session context_.
* The variable `renderSessionUuid` will now hold the unique id of the newly created render session. You can use the id to refer to the render session, as shown below.

### Cleanup render session
####Abstract
When the render process for the given document (! - not batch process!) has completed (successfully or erroneous), it's mandatory to clean up the render session to avoid a memory leak.
####Sample code
In DocDesign, add a new _Dynamic Content_ element at the very end of your _Page Content_.  
In the property editor of your _Dynamic Content_ element, go to Common->XML and enter the following XPath expression:  
`hit2assext:cleanUpRenderSessionContext(var:read('renderSessionUuid'))`  

__Nota bene:__
* After the clean up of the session context, the session context will not be available any more. Any attempt to access the render session or the content of its symbol table after clean up will potentially and probably lead to erroneous behavior, error logs and rendering malfunctions.
* The syntax `var:read('renderSessionUuid')` uses the standard _DocDesign_ _Document Variable_ mechanism which is available using the namespace `var`. `'renderSessionUuid'` again is the name of the variable we have chosen when creating the render session in the previous section.
 
###Test configuration
####Abstract
To perform a quick smoke test of your hit2assext configuration you can add a _Dynamic Element_ to your _DocDesign_ workspace as quoted below. If everything works it will print the text __'Hello, World!'__.
#### Sample code
Somewhere in your _Page Content_ between the creation and deletion of your render session, add a _Dynamic Content_ element to your _Page Content_ and set its XPath expression to the following value:  
`hit2assext:testConfiguration()`  
__Nota bene:__  
* This function will print the static text __'Hello, World!'__. If this text does not appear within your document on publishing the document, your hit2assext configuration does not work. Here are some steps you can try to resolve your problem:
 * Check if the hit2assext JAR file is on the class path of your _DocBase_ installations (including the _DocDesign_ desktop server)
 * Check if you registered the hit2assext namespace with your DocDesign Document's namespaces
 * Check the hit2assext logs in your log files, including the DocDesign desktop server logs

###Create a new list variable
####Abstract
The main merit of the hit2assext system is the introduction of list variables into DocDesign. This sections shows how to create a new list variable and register it with the hit2assext render session context.
####Sample code
Within your _Page Content_ between the creation and deletion of your render session, add a _Dynamic Content_ element to your _Page Content_ and set its XPath expression to the following value:  
`hit2assext:createList(var:read('renderSessionUuid'), 'abraxas')`  
__Nota bene__:
* This statement will create a new list named __abraxas__ inside the render session referenced by its unique id as stored in the _DocDesign_ _document variable_ __`'renderSessionUuid'`__.

###Add a value to the end of a hit2assext list
####Abstract
Adding a value to the end of a list is one way to interact with hit2assext lists. After successful completion of this statement the length of the list will have increased by one, and the last item in the list will be the value added using this statement.
####Syntax
`hit2assext:addListValue( renderSessionUuid, listVariableName, newValue )`
####Sample code
Within your _Page Content_ after the creation and before the deletion of your render session, add a _Dynamic Content_ element to your _Page Content_ and set its XPath expression to the following value:  
`hit2assext:addListValue(var:read('renderSessionUuid'), 'abraxas', 'John')`  
__Nota bene__:  
* This statement adds the value __'John'__ to the end of the list __'abraxas'__ available in the render session identified by the unique id as stored in the _DocDesign_ _Document variable_ __'renderSessionUuid'__.
* Please note the recurring idiom of using `var:read('renderSessionUuid')` to refer to the unique id of the render session.
 
###Set a list value at a specified index
####Abstract
Setting the value at a specified list location is another way to interact with hit2assext lists. After successful completion of this statement the length of the list will be left unchanged, and the previous item at the specified location will have been exchanged with the given value.  
####Syntax
`hit2assext:setListValueAt( renderSessionUuid, listVariableName, index, newValue )`
####Sample code
Within your _Page Content_ after the creation and before the deletion of your render session, add a _Dynamic Content_ element to your _Page Content_ and set its XPath expression to the following value:  
`hit2assext:addListValue(var:read('renderSessionUuid'), 'abraxas', 3, 'John')`  
__Nota bene__:  
* This statement replaces the current element in the specified list at the specified index with the specified object newValue.
* There will be exceptions if the specified list does not exist, or if the specified index is out of range for the given list, i.e. if a index is given that exceeds the length of the list.
* Since HIT/CLOU uses 1-based counting - as opposed to Java's 0-based counting - the 1st item of a hit2assext list has index 1.

###Retrieve a value from a hit2assext list variable
####Abstract
Using this statement you can access the elements of hit2assext lists using and index. The 1st element will have the index 0, the 2nd element the index 1, the nth element will have the index (n-1), the last element will have the index (listLength-1), where listLength is the number of elements found in the given list.
####Syntax
`hit2assext:getListValueAt( renderSessionUuid, listVariableName, index )`
####Sample code
Within your _Page Content_ after the creation and before the deletion of your render session, add a _Dynamic Content_ element to your _Page Content_ and set its XPath expression to the following value:  
`hit2assext:getListValueAt(var:read('renderSessionUuid'), 'abraxas', 0)`  
__Nota bene__:  
* This statement will retrieve the 1st element from the list __'abraxas'__ available in the render session with the unique id stored in the document variable __'renderSissionUuid'__
* If the given list has no such element (in this case: if the list is empty) this will yield in a RuntimeException thrown in your render engine!


## Maintenance and debugging

Please note: the hit2assext system does some helpful logging on the INFO level. If you want to track its behavior and maybe get some helpful debug information, set the logging level of the org.poormanscastle packages to INFO by adding this line to your __log4j.properties__ file (which in case of the _DocDesign_ desktop server you can find here: DocDesignInstallationFolder/data/resources/log4j.properties)  
`log4j.logger.org.poormanscastle=DEBUG`

# DISCLAIMER
This product comes as is without no warranty whatsoever. Use it at your own discretion. If you need help with your HIT/CLOU migration project, be it that you migrate to DocFamily or any other output management product, I will be happy to help. At a price.  

Again, thank you for any feedback, questions and bug reports.
