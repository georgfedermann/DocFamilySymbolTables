package org.poormanscastle.products.hit2assext.domain;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class RenderSessionContextImpl implements RenderSessionContext {

    private final Map<String, List<Object>> listMap = new HashMap<>();

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
    public String toString() {
        return "RenderSessionContextImpl{" +
                "creationDateTime=" + creationDateTime +
                ", uuid='" + uuid + '\'' +
                '}';
    }

    @Override
    public void addListVariable(String name) {
        listMap.put(name, new LinkedList<>());
    }

    @Override
    public void addListValue(String listName, Object value) {
        listMap.get(listName).add(value);
    }

    @Override
    public Object getListValueAt(String listName, int index) {
        return listMap.get(listName).get(index);
    }
}
