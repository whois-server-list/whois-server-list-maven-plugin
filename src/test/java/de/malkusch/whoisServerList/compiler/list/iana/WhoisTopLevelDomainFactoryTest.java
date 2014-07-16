package de.malkusch.whoisServerList.compiler.list.iana;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.model.Source;
import de.malkusch.whoisServerList.compiler.model.WhoisServer;
import de.malkusch.whoisServerList.compiler.model.domain.CountryCodeTopLevelDomain;
import de.malkusch.whoisServerList.compiler.model.domain.Domain.State;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

public class WhoisTopLevelDomainFactoryTest {

    private IANATopLevelDomainBuilder builder;

    @Before
    public void setTopLevelDomainFactory() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/compiler.properties"));

        builder = new IANATopLevelDomainBuilder(properties);
    }

    @Test
    public void testBuild() throws WhoisServerListException, ParseException, MalformedURLException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        CountryCodeTopLevelDomain de = new CountryCodeTopLevelDomain();
        de.setCountryCode("DE");
        de.setName("de");
        de.setState(State.ACTIVE);
        de.setSource(Source.IANA);
        de.setCreated(dateFormat.parse("1986-11-05"));
        de.setChanged(dateFormat.parse("2012-04-19"));
        de.setRegistratonService(new URL("http://www.denic.de/"));
        WhoisServer deServer = new WhoisServer();
        deServer.setHost("whois.denic.de");
        deServer.setSource(Source.IANA);
        de.getWhoisServers().add(deServer);

        builder.setName("de");
        assertEquals(de, builder.build());


        TopLevelDomain tld = new TopLevelDomain();
        tld.setName("网络");
        tld.setState(State.ACTIVE);
        tld.setSource(Source.IANA);
        tld.setCreated(dateFormat.parse("2014-01-09"));
        tld.setChanged(dateFormat.parse("2014-01-20"));
        tld.setRegistratonService(new URL("http://www.cnnic.cn"));
        WhoisServer tldServer = new WhoisServer();
        tldServer.setHost("whois.ngtld.cn");
        tldServer.setSource(Source.IANA);
        tld.getWhoisServers().add(tldServer);

        builder.setName("网络");
        assertEquals(tld, builder.build());


        TopLevelDomain tld2 = new TopLevelDomain();
        tld2.setName("テスト");
        tld2.setState(State.INACTIVE);
        tld2.setSource(Source.IANA);
        tld2.setCreated(dateFormat.parse("2007-10-19"));
        tld2.setChanged(dateFormat.parse("2013-10-31"));
        tld2.setRegistratonService(new URL("http://www.iana.org/domains/idn-test/"));

        builder.setName("テスト");
        assertEquals(tld2, builder.build());
    }

}
