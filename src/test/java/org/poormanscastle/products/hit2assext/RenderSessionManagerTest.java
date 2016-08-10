package org.poormanscastle.products.hit2assext;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by georg on 8/10/16.
 */
public class RenderSessionManagerTest {

    @Test
    public void getXmlSequence() throws Exception {
        String sessionId = RenderSessionManager.createRenderSessionContext();
        assertEquals(1, RenderSessionManager.getXmlSequence(sessionId));
        assertEquals(1, RenderSessionManager.getXmlSequence(sessionId));
        assertEquals(1, RenderSessionManager.getXmlSequence(sessionId));
        RenderSessionManager.incrementXmlSequence(sessionId);
        assertEquals(2, RenderSessionManager.getXmlSequence(sessionId));
        assertEquals(2, RenderSessionManager.getXmlSequence(sessionId));
        assertEquals(2, RenderSessionManager.getXmlSequence(sessionId));
        RenderSessionManager.incrementXmlSequence(sessionId);
        assertEquals(3, RenderSessionManager.getXmlSequence(sessionId));
        assertEquals(3, RenderSessionManager.getXmlSequence(sessionId));
        assertEquals(3, RenderSessionManager.getXmlSequence(sessionId));
        RenderSessionManager.incrementXmlSequence(sessionId);
        assertEquals(4, RenderSessionManager.getXmlSequence(sessionId));
        assertEquals(4, RenderSessionManager.getXmlSequence(sessionId));
        assertEquals(4, RenderSessionManager.getXmlSequence(sessionId));
        RenderSessionManager.incrementXmlSequence(sessionId);
        assertEquals(5, RenderSessionManager.getXmlSequence(sessionId));
        assertEquals(5, RenderSessionManager.getXmlSequence(sessionId));
        assertEquals(5, RenderSessionManager.getXmlSequence(sessionId));
    }

    @Test
    public void testConfiguration() throws Exception {
        assertEquals("Hello, World!", RenderSessionManager.testConfiguration());
    }

}