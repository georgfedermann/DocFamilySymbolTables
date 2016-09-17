package org.poormanscastle.products.hit2assext.domain;

/**
 * Created by georg on 7/15/16.
 */
public final class RenderSessionContexts {

    /**
     * for compatibility reasons with DocFamily 5.10.3 this project is built with Java 7 and
     * thus extensions methods are not supported in the RenderSessionContext interface and the
     * static factory method is implemented in its own type.
     *
     * @return a new RenderSessionContext to work with.
     */
    public static RenderSessionContext createDefaultRenderSessionContext() {
        return new RenderSessionContextImpl();
    }

}
