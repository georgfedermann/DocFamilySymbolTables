# hit2assext
## Introduction
This project contains extension classes for Assentis DocFamily to
facilitate the integration of the hit2ass project with DocFamily.

DocFamily implements a **DocumentVariable** type that support an imperative
variable concept within XSLT. Which breaks with with the declarative
approach of XSLT (for good) but supports the porting of imperative HIT/CLOU
text components to **DocDesign** _workspaces_.

This project tries to top this blasphemy in adding additional concepts like
list and field variables.

To use hit2ass generated workspaces in DocFamily you will neet to add the hit2assext
jar file to the classpath of your *DocBase* server, including the desktop server
of your *DocDesign* installation.

The generated workspaces will automatically declare a namespace for these
extensions on the Document element. For test purposes you can manually add
the required namespace on the Document Element in DocDesign. The required
namespace is the full qualified class name of the RenderSessionManager type:

Enter a namespace prefix and a namespace:  
hitassext -> org.poormanscastle.products.hit2assext.domain.RenderSessionManager

You can test the successful installation by creating a new *Simple Document* in DocDesign,
configuring the fqcn namespace as described above, and adding a *DynamicValue* element
to the Document's *Page Content*, specifying this XPath:
 hitassext:testConfiguration()
 
 This should print the text "Hello, World!", when you publish the document, using no data XML.
 
 Thanks for your feedback, questions and bug reports.

## Usage
With the hit2assext extension package you get a bunch of new functionality in DocDesign.
The hit2ass transformer will use this extended set of statements to create Workspaces
from HIT/CLOU text components. Here is a listing of the new commands and the syntax how
to use them in DocDesign workspaces.

### Initialize hit2assext in a new workspace
After having put the hit2assext __Java__ jar file into the Assentis desktop server lib folder,
and having restarted the desktop server, you are good to go.  
These are the steps to hook hit2assext into your new DocDesign workspace:
* Configure the hit2assext namespace in the DocDesign Workspace Document, as described above. Do not forget this step, or you will fail
* User the hit2assext RenderSessionManager to manage your RenderSessionContext. You will have to create a RenderSessionContext at the begin of your render session, and clean it up again at the end of the render session. To this end you have to do two steps:
 * Add a DocumentVariable to the very start of your Page Content with the name 'renderSessionUuid' and the value hit2assext:createRenderSessionContext(). In fact you are free to name the variable any way you like. I've chosen the given id here because it's the very id the hit2ass transformer will use when it transforms HIT/CLOU text components to DocFamily workspaces, so get used to that name.
 * Add a DynamicContent element to the very end of your _Page Content_ to clean up the RenderSessionContext you've just created. Do not forget this or you will create a resource leak, cluttering up your _DocBase_ engine's memory as you go along with your batch processes. Name this DynamicContent element 'Cleanup RenderSession' or anything you wish, and enter this XPath: _hit2assext:cleanUpRenderSessionContext(var:read('renderSessionUuid'))_. Please note two things: We're using the DocFamily feature DocumentVariable here to refer back to the name of the RenderSessionContext we've created in the previous bullet point. And: the name of the document variable given in var:read('renderSessionUuid') must be exactly the same as in the document variable definition in the previous bullet point, or your system will crash
* Please note: the hit2assext system does some helpful logging on the INFO level. If you want to track its behavior and maybe get some helpful debug information, set the logging level of the org.poormanscastle packages to INFO by adding this line to your log4j.properties file (which you can find here: DocDesignInstallationFolder/data/resource/log4j.properties)

# DISCLAIMER
This product comes as is without no warranty whatsoever. Use it at your own discretion. If you need help with your HIT/CLOU migration project, be it that you migrate to DocFamily or any other output management product, I will be happy to help. At a price.  

Again, thank you for any feedback, questions and bug reports.
