package de.malkusch.whoisServerList.compiler.helper;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class VersionUtilTest {

    @Parameter(0)
    public String version;

    @Parameter(1)
    public String nextVersion;

    @Parameters
    public static Collection<String[]> getTestCases() {
        return Arrays.asList(new String[][] {
                {"0.0.0", "0.0.1"},
                {"0.0.1", "0.0.2"},
                {"0.0.9", "0.0.10"},
                {"0.0.10", "0.0.11"},
                {"1.0.0", "1.0.1"},
                {"1.1.0", "1.1.1"},
        });
    }

    @Test
    public void testIncrementVersion() {
        assertEquals(nextVersion, VersionUtil.incrementVersion(version));
    }

}
