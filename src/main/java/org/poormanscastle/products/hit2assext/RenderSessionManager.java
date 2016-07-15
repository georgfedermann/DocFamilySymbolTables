package org.poormanscastle.products.hit2assext;

import org.poormanscastle.products.hit2assext.domain.RenderSessionContext;
import org.poormanscastle.products.hit2assext.domain.RenderSessionContexts;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A RenderSessionManager manages the RenderSessionContext instances.
 * <p>
 * There exists exactly one RenderSessionManager with application scope. The application is the render
 * engine, i.e. the DocBase instance doing the rendering. Like the DocDesign desktop server or the
 * DocBase engine running somewhere as a service.
 * <p>
 * Created by georg on 7/15/16.
 */
public final class RenderSessionManager {

    private final static Map<String, RenderSessionContext> contextMap = new HashMap<>();

    /**
     * This method can be used to test the configuration.
     * It returns the text "Hello, World!".
     *
     * @return
     */
    public static String testConfiguration() {
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
        return context.getUuid();
    }

    /**
     * Using this method, the render engine can signal the RenderSessionManager that
     * the render session is not needed any more.
     *
     * @param uuid
     */
    public static void cleanUpRenderSessionContext(String uuid) {
        contextMap.remove(uuid);
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
        return contextMap.get(renderSessionContextUuid).getListValueAt(listName, index);
    }

}
