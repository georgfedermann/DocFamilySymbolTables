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

This section describes what you can do with the hit2assext module and the syntax of the commands involved.

## Maintenance and debugging

Please note: the hit2assext system does some helpful logging on the INFO level. If you want to track its behavior and maybe get some helpful debug information, set the logging level of the org.poormanscastle packages to INFO by adding this line to your log4j.properties file (which you can find here: DocDesignInstallationFolder/data/resource/log4j.properties)

# DISCLAIMER
This product comes as is without no warranty whatsoever. Use it at your own discretion. If you need help with your HIT/CLOU migration project, be it that you migrate to DocFamily or any other output management product, I will be happy to help. At a price.  

Again, thank you for any feedback, questions and bug reports.
