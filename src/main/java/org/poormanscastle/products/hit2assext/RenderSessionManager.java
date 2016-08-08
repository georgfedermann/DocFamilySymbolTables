package org.poormanscastle.products.hit2assext;

import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.Value;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.poormanscastle.products.hit2assext.domain.RenderSessionContext;
import org.poormanscastle.products.hit2assext.domain.RenderSessionContexts;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A RenderSessionManager manages the RenderSessionContext instances.
 * <p>
 * There exists exactly one RenderSessionManager within application scope. The application is the render
 * engine, i.e. the DocBase instance doing the rendering. E.g. the DocDesign desktop server or the
 * DocBase engine running somewhere as a service.
 * <p>
 * The DocFamily extension mechanism requires the methods of extension classes to be static.
 * Therefore the RenderSessionManager implements no interface.
 * <p>
 * Created by georg on 7/15/16.
 */
public final class RenderSessionManager {

    private final static Logger logger = Logger.getLogger(RenderSessionManager.class);

    private final static Map<String, RenderSessionContext> contextMap = new HashMap<>();

    /**
     * This method can be used to test the configuration.
     * It returns the text "Hello, World!".
     *
     * @return
     */
    public static String testConfiguration() {
        if (logger.isInfoEnabled()) {
            logger.info("Running testConfiguration()");
        }
        return "Hello, World!";
    }

    /**
     * create a new RenderSessionContext and register it with the RenderSessionManager.
     * <p>
     * This method is intended to be called during render time by the render engine, e.g.
     * DocDesign's desktop server, or a productive DocBase service, etc.
     *
     * @return
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
     * the render session is not needed any more.
     *
     * @param uuid
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
     * @param renderSessionContextUuid
     * @param listName
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
     * @param renderSessionContextUuid
     * @param listName
     * @param value
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
     * @param renderSessionContextUuid
     * @param listName
     * @param index
     * @return
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
     * @param renderSessionContextUuid
     * @param listName
     * @param index
     * @param value
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
        int val = contextMap.get(renderSessionContextUuid).getXmlSequence();
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Received call: getXmlSequence('", renderSessionContextUuid, "')=", val));
        }
        return val;
    }

    public static void incrementXmlSequence(String renderSessionContextUuid) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Received call: incrementXmlSequence('", renderSessionContextUuid, "')"));
        }
        contextMap.get(renderSessionContextUuid).incrementXmlSequence();
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
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Received call: getScalarVariableValue(", renderSessionContextUuid, ", ", variableName, ")"));
        }
        return contextMap.get(renderSessionContextUuid).getScalarVariableValue(variableName);
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
     * @param renderSessionContextUuid
     * @param listName
     * @return the length of the list corresponding to the given render session and listName
     */
    public static Integer getListLength(String renderSessionContextUuid, String listName) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Received call: getListLength('", renderSessionContextUuid, "', '",
                    listName, "')"));
        }
        return contextMap.get(renderSessionContextUuid).getListLength(listName);
    }

    /**
     * Using this method the template composer can reach into the Java runtime of the render engine
     * and query for the Java system properties.
     *
     * @param systemPropertyName
     * @return
     */
    public static String getSystemProperty(String systemPropertyName) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtils.join("Received call: getSystemProperty('", systemPropertyName, "')"));
        }
        return System.getProperty(systemPropertyName);
    }

}
