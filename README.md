# hit2assext

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
