package org.poormanscastle.products.hit2assext;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by georg on 8/10/16.
 */
public class RenderSessionManagerTest {
    @Test
    public void convert_TMJJJJ_DateToIso8601Format() throws Exception {
        assertEquals("2016-10-20", RenderSessionManager.convert_TMJJJJ_DateToIso8601Format("20.10.2016"));
        assertEquals("2016-01-02", RenderSessionManager.convert_TMJJJJ_DateToIso8601Format("2.1.2016"));
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void getSubstringValid() throws Exception {
        String testString = "Hello, World!";
        assertEquals("Hello", RenderSessionManager.getSubstring(testString, 1, 5));
        assertEquals("World!", RenderSessionManager.getSubstring(testString, 8, 13));
    }

    @Test
    public void getSubStringIndex0() throws Exception {
        String testString = "Hello, World!";
        exception.expect(IllegalArgumentException.class);
        RenderSessionManager.getSubstring(testString, 0, 4);
    }

    @Test
    public void getSubStringStartIndexGreaterThanEndIndex() throws Exception {
        String testString = "Hello, World!";
        exception.expect(IllegalArgumentException.class);
        RenderSessionManager.getSubstring(testString, 5, 1);
    }

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