package org.poormanscastle.products.hit2assext.domain;

/**
 * Created by georg on 7/15/16.
 */
public final class RenderSessionContexts {

    public static RenderSessionContext createDefaultRenderSessionContext() {
        return new RenderSessionContextImpl();
    }

}
