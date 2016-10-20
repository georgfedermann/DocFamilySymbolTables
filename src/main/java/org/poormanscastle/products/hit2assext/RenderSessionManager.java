package org.poormanscastle.products.hit2assext;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.poormanscastle.products.hit2assext.domain.RenderSessionContext;
import org.poormanscastle.products.hit2assext.domain.RenderSessionContexts;

import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.Value;

/**
 * A RenderSessionManager manages the RenderSessionContext instances.
 * There exists exactly one RenderSessionManager within application scope. The application is the render
 * engine, i.e. the DocBase instance doing the rendering. E.g. the DocDesign desktop server or the
 * DocBase engine running somewhere as a service.
 * The DocFamily extension mechanism requires the methods of extension classes to be static.
 * Therefore the RenderSessionManager implements no interface.
 * Created by georg on 7/15/16.
 */
public final class RenderSessionManager {

    private final static Logger logger = Logger.getLogger(RenderSessionManager.class);

    private final static Map<String, RenderSessionContext> contextMap = new HashMap<>();

    /**
     * Implements the HIT/CLOU substring feature.
     * The substring can be defined by using a bracket notation: Let myVar="Hello, World!".
     * Then, myVar[1,5] is "Hello". HIT/CLOU used one based counting: The first character
     * has index 1. The index of the last character is equal to the length of the string.
     * The length of a substring, thus, is ( endIndex - startIndex + 1 ).
     *
     * @param inputString string from which a substring shall be extracted
     * @param startIndex  start substringing here
     * @param endIndex    stop substringing here
     * @return the resulting substring
     */
    public static String getSubstring(String inputString, int startIndex, int endIndex) {
        checkNotNull(inputString, "Input to getSubstring() was null.");
        checkArgument(startIndex > 00 && endIndex > 0x0, StringUtils.join("startIndex ", startIndex,
                " and endIndex ", endIndex, " must both be positive numbers."));
        checkArgument(startIndex <= endIndex, StringUtils.join("startIndex ", startIndex,
                " must be smaller or equal to endIndex ", endIndex));
        checkArgument(endIndex <= inputString.length(), "endIndex ", endIndex,
                " cannot be greated then input string length ", inputString.length());

        return inputString.substring(startIndex - 1, endIndex);
    }

    /**
     * This method can be used to test the configuration.
     * It returns the text "Hello, World!".
     *
     * @return the string "Hello, World!" without the quotes.
     */
    public static String testConfiguration() {
        if (logger.isInfoEnabled()) {
            logger.info("Running testConfiguration()");
        }
        return "Hello, World!";
    }

    /**
     * create a new RenderSessionContext and register it with the RenderSessionManager.
     * This method is intended to be called during render time by the render engine, e.g.
     * DocDesign's desktop server, or a productive DocBase service, etc.
     *
     * @return a new RenderSessionContext instance.
     */
    public static String createRenderSessionContext() {
        RenderSessionContext context = RenderSessionContexts.createDefaultRenderSessionContext();
        contextMap.put(context.getUuid(), context);
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Created new RenderSessionContext with uuid ", context.getUuid()));
        }
        return context.getUuid();
    }

    /**
     * Using this method, the render engine can signal the RenderSessionManager that
     * the render session is not needed any more. Use this at the end of a DocBase
     * render session or else we have a memory leak.
     * TODO you could implement a garbage collector that discards RenderSession older than 5 seconds to avoid memory leaks.
     *
     * @param uuid the unique of the render session which shall be discarded.
     */
    public static void cleanUpRenderSessionContext(String uuid) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Cleaning up RenderSessionContext with uuid ", uuid));
        }
        Object foundObject = contextMap.remove(uuid);
        if (foundObject == null) {
            logger.warn(StringUtils.join("No RenderSession for uuid ", uuid, " was found."));
        }
    }

    /**
     * creates a new, empty list object. a list is a symbol that can hold several values
     * which can be addressed using the name of the variable and an index given in brackets [].
     *
     * @param renderSessionContextUuid the render session that will be the scope of the new list
     * @param listName                 the name of the new list
     */
    public static void createList(String renderSessionContextUuid, String listName) {
        List<Object> list = new LinkedList<>();
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Creating new list with name ", listName, " in RenderSessionContext with uuid ", renderSessionContextUuid, "."));
        }
        contextMap.get(renderSessionContextUuid).addListVariable(listName);
    }

    /**
     * appends the given value at the end of the list identified by the given name
     * belonging to the given RenderSessionContext.
     * Nota bene: before adding values to a list, the list must have been created
     * using method addListVariable(String) beforehand.
     *
     * @param renderSessionContextUuid the render session in which this list exists
     * @param listName                 the name of the list
     * @param value                    the new value to be added to the list
     */
    public static void addListValue(String renderSessionContextUuid, String listName, Object value) {
        contextMap.get(renderSessionContextUuid).addListValue(listName, value);
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Adding value ", value, " to list with name ", listName, " in RenderSessionContext with uuid ", renderSessionContextUuid, "."));
        }
    }

    public static void appendList(String renderSessionContextUuid, String sourceListName, String targetListName) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Append list ", sourceListName, " to the end of ", targetListName, "."));
        }
        contextMap.get(renderSessionContextUuid).appendList(sourceListName, targetListName);
    }

    /**
     * this method can be used to retrieve the value at the specified index from
     * the list referenced by the given name.
     *
     * @param renderSessionContextUuid the render session in which the given list exists
     * @param listName                 the name of the list
     * @param index                    the index of the value of interest
     * @return the value of interest
     */
    public static Object getListValueAt(String renderSessionContextUuid, String listName, int index) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Received call: getListValueAt('", renderSessionContextUuid, "', ",
                    listName, "[", index, "]"));
        }
        Object value = contextMap.get(renderSessionContextUuid).getListValueAt(listName, index - 1);
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Looking up value ", listName, "[", index - 1, "]=", value));
        }
        return value;
    }

    /**
     * this method can be used to set the list item at the specified index. The previous
     * item at this index will be replaced with the specified value.
     *
     * @param renderSessionContextUuid the render session in which the given list exists
     * @param listName                 the name of the list
     * @param index                    the index of the list that shall be set to the new value
     * @param value                    the new value that shall be stored in the given list
     */
    public static void setListValueAt(String renderSessionContextUuid, String listName, int index, Object value) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Received call: setListValueAt('", renderSessionContextUuid, "', ",
                    listName, "[", index - 1, "]=", value));
        }
        Object oldValue = contextMap.get(renderSessionContextUuid).setListValueAt(listName, index - 1, value);
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Replacing ", listName, "[", index - 1, "]=", oldValue, " with newValue ", value));
        }
    }

    public static int getXmlSequence(String renderSessionContextUuid) {
        int lastQueriedXmlSequence = contextMap.get(renderSessionContextUuid).getLastQueriedXmlSequence();
        int val = contextMap.get(renderSessionContextUuid).getXmlSequence();
        if (logger.isInfoEnabled() && lastQueriedXmlSequence != val) {
            logger.info(StringUtils.join("Received (potentially multiple) call: getXmlSequence('", renderSessionContextUuid, "')=", val));
        }
        return val;
    }

    public static void incrementXmlSequence(String renderSessionContextUuid) {
        int oldXmlSequence = contextMap.get(renderSessionContextUuid).getXmlSequence();
        int newXmlSequence = contextMap.get(renderSessionContextUuid).incrementXmlSequence();
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Received call: incrementXmlSequence('", renderSessionContextUuid, "') ",
                    oldXmlSequence, "->", newXmlSequence));
        }
    }

    public static void createScalarVariable(String renderSessionContextUuid, String variableName, Object value) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Creating new scalar variable with name ", variableName, " in RenderSessionContext with uuid ", renderSessionContextUuid, ". Delegating to RenderSessionManager.setScalarVariable()"));
        }
        RenderSessionManager.setScalarVariableValue(renderSessionContextUuid, variableName, value);
    }

    public static void setScalarVariableValue(String renderSessionContextUuid, String variableName, Object value) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Received call: setScalarVariableValue(", renderSessionContextUuid, ", ", variableName, ", ", value, ")"));
        }
        // check that arguement is not an empty list
        if (value instanceof List && ((List) value).size() == 0) {
            throw new IllegalArgumentException(StringUtils.join("hit2assext:ERROR setScalarVariable('", renderSessionContextUuid, "', '",
                    variableName, "', value) received empty List. Maybe the line with the given number is missing in the user data XML? "));
        }
        try {
            if (value instanceof List && ((List) value).get(0) instanceof NodeInfo) {
                value = ((NodeInfo) ((List) value).get(0)).atomize();
                if (logger.isInfoEnabled()) {
                    logger.info(StringUtils.join("Autoconverting the 1st element of List<NodeInfo> to atomic value: ", ((Value) value).getStringValue()));
                }
            } else if (value instanceof NodeInfo) {
                value = ((NodeInfo) value).atomize();
                if (logger.isInfoEnabled()) {
                    logger.info(StringUtils.join("Autoconverting the NodeInfo to atomic value: ", ((Value) value).getStringValue()));
                }
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("No need to convert to atomic value");
                }
            }
        } catch (XPathException e) {
            if (logger.isInfoEnabled()) {
                logger.error("Could not transform saxon node type to saxon value type.");
            }
        }
        contextMap.get(renderSessionContextUuid).setScalarVariableValue(variableName, value);
    }

    public static Object getScalarVariableValue(String renderSessionContextUuid, String variableName) {
        Object value = contextMap.get(renderSessionContextUuid).getScalarVariableValue(variableName);
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Received call: getScalarVariableValue(", renderSessionContextUuid, ", ",
                    variableName, ")->", value.toString()));
        }
        return value;
    }

    public static void printLogStatement(String renderSessionContextUuid, String logMessage) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Received call: printLogStatement(", renderSessionContextUuid, ", ", logMessage));
        }
    }

    /**
     * Using this method the length of a list that has been registered in the RenderSessionContext
     * for the given renderSessionContextUuid can be retrieved. The list must have been registered
     * beforehand using the createList() method.
     *
     * @param renderSessionContextUuid the render session in which the given list exists
     * @param listName                 the name of the list
     * @return the length of the list corresponding to the given render session and listName
     */
    public static Integer getListLength(String renderSessionContextUuid, String listName) {
        Integer length = contextMap.get(renderSessionContextUuid).getListLength(listName);
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Received call: getListLength('", renderSessionContextUuid, "', '",
                    listName, "')->", length));
        }
        return length;
    }

    /**
     * Using this method the template composer can reach into the Java runtime of the render engine
     * and query for the Java system properties.
     *
     * @param systemPropertyName the name of the system property of interest
     * @return the value of the given system property
     */
    public static String getSystemProperty(String systemPropertyName) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Received call: getSystemProperty('", systemPropertyName, "')"));
        }
        return System.getProperty(systemPropertyName);
    }

    /**
     * accepts date in the format T.M.JJJJ and converts it to ISO8601, e.b. 2016-10-20.
     * This format yields the advantage that it is supported by XPath date functions.
     * The hard-to-read method name shall support readability.
     *
     * @param dateString a string conveying a date in the format T.M.JJJJ. This means the date
     *                   contains 3 value separated by "." dots where the first item represents
     *                   a day in month, the second a month, the third a year. In fact it does
     *                   not matter how many digits each data item has, as long as they are
     *                   numeric values and meaningful in the context of dates. Month counting is
     *                   1-based, i.d. January is represented by the 1, February by 2, and so
     *                   on through to December represented by 16.
     * @return a String representing the same date as the input string in the ISO8601 format.
     */
    public static String convert_TMJJJJ_DateToIso8601Format(String dateString) {
        StringBuilder result = new StringBuilder();
        String[] dateItems = dateString.split("\\.");
        checkArgument(dateItems.length == 3, StringUtils.join("Invalid dateString ", dateString,
                " - it should be in the format T.M.JJJJ"));
        boolean flag = false;
        for (int counter = 0; counter < 3; counter++) {
            result.append(StringUtils.join(flag ? "-" : "", StringUtils.leftPad(dateItems[2 - counter], 2, "0")));
            flag = true;
        }
        return result.toString();
    }

}
