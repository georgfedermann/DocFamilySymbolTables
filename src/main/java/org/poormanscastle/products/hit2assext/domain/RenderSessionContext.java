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

    /**
     * create a new list which can be referenced using the given name.
     *
     * @param name
     */
    void addListVariable(String name);

    /**
     * appends the given value at the end of the list identified by the given name.
     * Nota bene: before adding values to a list, the list must have been created
     * using method addListVariable(String) beforehand (or else it will be created
     * at this time automatically - for your convenience and because our client asked
     * us not to be nitpickers about this).
     *
     * @param listName
     * @param value
     */
    void addListValue(String listName, Object value);

    /**
     * sets the value of the given list at the given index to the given value.
     * Nota bene: before adding values to a list, the list must have been created
     * using method addListVariable(String) beforehand.
     *
     * @param listName
     * @param index
     * @param value
     */
    Object setListValueAt(String listName, Integer index, Object value);

    /**
     * this method can be used to retrieve the value at the specified index from
     * the list referenced by the given name.
     *
     * @param listName
     * @param index
     * @return
     */
    Object getListValueAt(String listName, int index);

    /**
     * create an new scalar variable which can be referenced using the given name.
     *
     * @param variableName
     */
    void addScalarVariable(String variableName);

    /**
     * this method can be used to set the value of a scalar variable.
     *
     * @param variableName
     * @param value
     */
    void setScalarVariableValue(String variableName, Object value);

    /**
     * this method can be used to retrieve the value of a scalar variable.
     * @param variableName
     * @return
     */
    Object getScalarVariableValue(String variableName);

}
