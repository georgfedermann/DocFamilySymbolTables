package org.poormanscastle.products.hit2assext.domain;

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

}
