package org.poormanscastle.products.hit2assext.domain;

import org.joda.time.DateTime;

/**
 * A RenderSessionContext is available for the time interval during which a document template is rendered to a document.
 * <p>
 * To create instances of this type use the static factory methods in RenderSessionContexts.
 * <p>
 * A RenderSessionContext is created when DocBase starts rendering a document from a deployment package and
 * gets deleted when the rendering of the document is completed.
 * <p>
 * The RenderSessionContext contains all symbols used during rendering, like names of lists.
 * <p>
 * Created by georg on 7/14/16.
 */
public interface RenderSessionContext {

    DateTime getCreationDateTime();

    long getAgeInSeconds();

    /**
     * the uuid identifies the given RenderSessionContext instance.
     *
     * @return
     */
    String getUuid();

    void addListVariable(String name);

}
