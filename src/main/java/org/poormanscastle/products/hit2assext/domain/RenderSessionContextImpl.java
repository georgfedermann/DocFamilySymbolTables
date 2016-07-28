package org.poormanscastle.products.hit2assext.domain;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class RenderSessionContextImpl implements RenderSessionContext {

    private final static Logger logger = Logger.getLogger(RenderSessionContextImpl.class);

    private final Map<String, List<Object>> listMap = new HashMap<>();

    private final Map<String, Object> scalarMap = new HashMap<>();

    private int xmlSequence = 1;

    /**
     * remember when this session item was created. If the clean up does not work for some reason
     * old sessions can be identified as obsolete by their age (e.g. older than 20s) and be
     * garbage collected.
     */
    private final DateTime creationDateTime;

    /**
     * an id, identifying the given session instance.
     */
    private final String uuid;

    RenderSessionContextImpl() {
        creationDateTime = new DateTime();
        uuid = UUID.randomUUID().toString();
    }

    @Override
    public DateTime getCreationDateTime() {
        return creationDateTime;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public long getAgeInSeconds() {
        return new Duration(creationDateTime, new DateTime()).getStandardSeconds();
    }

    @Override
    public void addListVariable(String name) {
        listMap.put(name, new LinkedList<>());
    }

    @Override
    public void addListValue(String listName, Object value) {
        // fun fact: if in HIT/CLOU a variable is accessed which has not been declared before,
        // it gets created at the time of first access.
        if (listMap.get(listName) == null) {
            addListVariable(listName);
        }
        listMap.get(listName).add(value);
    }

    @Override
    public Object setListValueAt(String listName, Integer index, Object value) {
        return listMap.get(listName).set(index, value);
    }

    @Override
    public Object getListValueAt(String listName, int index) {
        List<?> list = listMap.get(listName);
        if (list == null) {
            logger.error(StringUtils.join("The given listName ", listName, " has not been initialized. Please use method RenderSessionManager.createList(String renderSessionContextUuid, String listName) to create the list before referencing it."));
            return StringUtils.join("hitassext:ERROR: no list with name ", listName);
        } else if (index < 0 || index >= list.size()) {
            logger.error(StringUtils.join("Index ", index, " invalid for list ", listName, ".size()=", list.size()));
            return StringUtils.join("hitassext:ERROR: IndexOutOfBounds");
        } else {
            return listMap.get(listName).get(index);
        }
    }

    @Override
    public void addScalarVariable(String variableName) {
        scalarMap.put(variableName, "");
    }

    @Override
    public void setScalarVariableValue(String variableName, Object value) {
        scalarMap.put(variableName, value);
    }

    @Override
    public Object getScalarVariableValue(String variableName) {
        Object value = scalarMap.get(variableName);
        if (value == null) {
            logger.error(StringUtils.join("No variable exists for variableName ", variableName));
            return StringUtils.join("hitassext:ERROR: No variable exists for variableName ", variableName);
        } else {
            return value;
        }
    }

    @Override
    synchronized public int getXmlSequence() {
        return xmlSequence;
    }

    @Override
    synchronized public void incrementXmlSequence() {
        xmlSequence++;
    }

    @Override
    public String toString() {
        return "RenderSessionContextImpl{" +
                "creationDateTime=" + creationDateTime +
                ", uuid='" + uuid + '\'' +
                '}';
    }

}
