package de.malkusch.whoisServerList.compiler.merger;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import org.junit.Test;

import de.malkusch.whoisServerList.api.v1.model.domain.Domain;
import de.malkusch.whoisServerList.api.v1.model.domain.Domain.State;
import de.malkusch.whoisServerList.api.v1.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.DomainListCompiler;
import de.malkusch.whoisServerList.compiler.test.TestUtil;

public class TopLevelDomainMergerTest {

    @Test
    public void testMerge() throws MalformedURLException, InterruptedException {
        Properties properties = DomainListCompiler.getDefaultProperties();
        TopLevelDomainMerger merger = new TopLevelDomainMerger(properties);

        TopLevelDomain left = new TopLevelDomain();
        left.setCountryCode("DE");
        left.setChanged(TestUtil.getYesterday());
        left.setState(State.NEW);
        left.setRegistratonService(new URL("http://www.example.net"));
        Domain leftSubdomain = new Domain();
        leftSubdomain.setName("left.example.net");
        left.getDomains().add(leftSubdomain);

        TopLevelDomain right = new TopLevelDomain();
        right.setCountryCode(null);
        right.setChanged(new Date());
        right.setRegistratonService(new URL("http://www.example.com"));
        right.setState(State.ACTIVE);
        Domain rightSubdomain = new Domain();
        rightSubdomain.setName("right.example.net");
        right.getDomains().add(rightSubdomain);

        TopLevelDomain expected = new TopLevelDomain();
        expected.setCountryCode("DE");
        expected.setChanged(right.getChanged());
        expected.setRegistratonService(new URL("http://www.example.com"));
        expected.setState(State.ACTIVE);
        expected.getDomains().add(leftSubdomain);
        expected.getDomains().add(rightSubdomain);

        assertEquals(expected, merger.merge(left, right));
    }

    @Test
    public void testMergeState() throws InterruptedException {
        Properties properties = DomainListCompiler.getDefaultProperties();
        TopLevelDomainMerger merger = new TopLevelDomainMerger(properties);

        {
            TopLevelDomain left = new TopLevelDomain();
            left.setState(State.NEW);

            TopLevelDomain right = new TopLevelDomain();
            right.setState(State.ACTIVE);

            TopLevelDomain expected = new TopLevelDomain();
            expected.setState(State.NEW);

            assertEquals(expected, merger.merge(left, right));
        }
        {
            TopLevelDomain left = new TopLevelDomain();
            left.setChanged(TestUtil.getYesterday());
            left.setState(State.NEW);

            TopLevelDomain right = new TopLevelDomain();
            right.setChanged(new Date());
            right.setState(State.ACTIVE);

            TopLevelDomain expected = new TopLevelDomain();
            expected.setChanged(right.getChanged());
            expected.setState(State.ACTIVE);

            assertEquals(expected, merger.merge(left, right));
        }
        {
            TopLevelDomain left = new TopLevelDomain();
            left.setChanged(new Date());
            left.setState(State.NEW);

            TopLevelDomain right = new TopLevelDomain();
            right.setChanged(TestUtil.getYesterday());
            right.setState(State.ACTIVE);

            TopLevelDomain expected = new TopLevelDomain();
            expected.setChanged(left.getChanged());
            expected.setState(State.NEW);

            assertEquals(expected, merger.merge(left, right));
        }
    }

}
