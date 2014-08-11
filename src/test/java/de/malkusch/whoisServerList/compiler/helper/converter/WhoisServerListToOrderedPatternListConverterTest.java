package de.malkusch.whoisServerList.compiler.helper.converter;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import de.malkusch.whoisServerList.api.v1.model.WhoisServer;

public final class WhoisServerListToOrderedPatternListConverterTest {
    
    private WhoisServerListToOrderedPatternListConverter converter;
    
    @Before
    public void setConverter() {
        converter = new WhoisServerListToOrderedPatternListConverter();
    }

    @Test
    public void testNull() {
        assertNull(converter.convert(null));
    }
    
    @Test
    public void testNullPattern() {
        List<WhoisServer> servers
                = asList(new WhoisServer[] { buildServer(null) });
        
        assertTrue(converter.convert(servers).isEmpty());
    }
    
    @Test
    public void testEmptyList() {
        assertTrue(converter.convert(new ArrayList<WhoisServer>()).isEmpty());
    }
    
    @Test
    public void testSorting() {
        List<WhoisServer> servers = asList(new WhoisServer[] {
                buildServer("test1"),
                
                buildServer("test3"),
                buildServer("test3"),
                buildServer("test3"),
                
                buildServer("test2"),
                buildServer("test2")
        });
        
        List<Pattern> patterns = converter.convert(servers);
        
        assertTrue(patterns.size() == 3);
        assertEquals("test3", patterns.get(0).pattern());
        assertEquals("test2", patterns.get(1).pattern());
        assertEquals("test1", patterns.get(2).pattern());
    }
    
    private WhoisServer buildServer(final String pattern) {
        WhoisServer server = new WhoisServer();
        if (pattern != null) {
            server.setAvailablePattern(Pattern.compile(pattern));
            
        }
        return server;
    }

}
