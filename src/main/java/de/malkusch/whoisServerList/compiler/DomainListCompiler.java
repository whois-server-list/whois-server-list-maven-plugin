package de.malkusch.whoisServerList.compiler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.PropertyKey;
import javax.annotation.concurrent.Immutable;
import javax.cache.Cache;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.compiler.filter.DomainListFilter;
import de.malkusch.whoisServerList.compiler.helper.VersionUtil;
import de.malkusch.whoisServerList.compiler.list.DomainListFactory;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.list.iana.IanaDomainListFactory;
import de.malkusch.whoisServerList.compiler.list.mdwhois.MDWhoisDomainListFactory;
import de.malkusch.whoisServerList.compiler.list.phois.PhoisDomainListFactory;
import de.malkusch.whoisServerList.compiler.list.phpwhois.PhpWhoisDomainListFactory;
import de.malkusch.whoisServerList.compiler.list.psl.PublicSuffixDomainListFactory;
import de.malkusch.whoisServerList.compiler.list.whoisrb.WhoisrbDomainListFactory;
import de.malkusch.whoisServerList.compiler.list.xml.XMLDomainListFactory;
import de.malkusch.whoisServerList.compiler.merger.DomainListMerger;

/**
 * Compiles a list of top level domains and its whois server.
 *
 * The sources for the compiled list are:
 *
 * <ul>
 *   <li>Whois Server List</li>
 *   <li>Root Zone Database</li>
 *   <li>Public Suffix List</li>
 *   <li>Ruby Whois</li>
 *   <li>Marco d'Itri's</li>
 *   <li>php-whois</li>
 *   <li>phpWhois</li>
 * </ul>
 *
 * @author markus@malkusch.de
 *
 * @see <a href="https://github.com/whois-server-list/whois-server-list">Whois
 *      Server List</a>
 * @see <a href="http://www.iana.org/domains/root/db">Root Zone Database</a>
 * @see <a href="https://publicsuffix.org/">Public Suffix List</a>
 * @see <a href="http://whoisrb.org/">Ruby Whois</a>
 * @see <a href="https://github.com/rfc1036/whois">Marco d'Itri's Whois client</a>
 * @see <a href="https://github.com/regru/php-whois">php-whois</a>
 * @see <a href="https://github.com/phpWhois/phpWhois">phpWhois</a>
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@Immutable
public final class DomainListCompiler {

    /**
     * The domain list factories for different sources.
     *
     * The list is ordered after the list dominance. The first
     * list is the most dominant list. The next lists can't overwrite
     * existing values.
     */
    private final DomainListFactory[] listFactories;

    /**
     * The domain list merger.
     */
    private final DomainListMerger merger;

    /**
     * The domain list filter.
     */
    private final DomainListFilter filter;
    
    /**
     * The Ruby Whois's list URI.
     */
    @PropertyKey
    private static final String PROPERTY_WHOIS_RB_URI = "whoisrb.uri";
    
    /**
     * The URI for Marco d'Itri's list.
     */
    @PropertyKey
    private static final String PROPERTY_MD_WHOIS_URI = "mdwhois.uri";
    
    /**
     * The URI for php-whois' list.
     */
    @PropertyKey
    private static final String PROPERTY_PHOIS_URI = "phois.uri";
    
    /**
     * The URI for phpWhois' list.
     */
    @PropertyKey
    private static final String PROPERTY_PHPWHOIS_URI = "phpwhois.uri";
    
    /**
     * The delay in seconds before script execution starts.
     */
    @PropertyKey
    private static final String PROPERTY_PHPWHOIS_DELAY = "phpwhois.delay";

    /**
     * Returns the default properties for the compiler.
     *
     * @return the default properties
     */
    public static Properties getDefaultProperties() {
        try {
            Properties properties = new Properties();
            properties.load(DomainListCompiler.class.getResourceAsStream(
                    "/compiler.properties"));

            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructs the compiler with the default properties.
     *
     * @param cache  the query cache, not null
     * @see #getDefaultProperties()
     */
    public DomainListCompiler(@Nonnull final Cache<String, String> cache) {
        this(getDefaultProperties(), cache);
    }

    /**
     * Constructs the compiler.
     *
     * @param properties  the compiler properties
     * @param cache       the query cache, not null
     */
    public DomainListCompiler(final Properties properties,
            @Nonnull final Cache<String, String> cache) {

        try {
            HttpClient httpClient = HttpClients.createDefault();
            
            this.listFactories = new DomainListFactory[] {
                new XMLDomainListFactory(),
                new IanaDomainListFactory(properties),
                new PublicSuffixDomainListFactory(),
                new MDWhoisDomainListFactory(httpClient, new URI(properties.getProperty(PROPERTY_MD_WHOIS_URI))),
                new WhoisrbDomainListFactory(httpClient, new URI(properties.getProperty(PROPERTY_WHOIS_RB_URI))),
                new PhoisDomainListFactory(httpClient, new URI(properties.getProperty(PROPERTY_PHOIS_URI))),
                new PhpWhoisDomainListFactory(
                        httpClient,
                        new URI(properties.getProperty(PROPERTY_PHPWHOIS_URI)),
                        Integer.parseInt(properties.getProperty(PROPERTY_PHPWHOIS_DELAY))),
            };
    
            this.merger = new DomainListMerger(properties);
    
            this.filter = new DomainListFilter(properties, cache);
            
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Compiles and returns the merged list from all list sources.
     *
     * The date of the compiled list is set to now. The patch part of
     * the version is incremented by 1. E.g. version 1.0.0 will be 1.0.1.
     *
     * @return the compiled list
     *
     * @throws BuildListException   If building a list failed
     * @throws InterruptedException If the thread was interrupted
     */
    public DomainList compile()
            throws BuildListException, InterruptedException {

        DomainList compiledList = new DomainList();

        for (DomainListFactory listFactory : listFactories) {
            compiledList = merger.merge(compiledList, listFactory.buildList());

        }

        compiledList.setDate(new Date());

        compiledList.setVersion(
                VersionUtil.incrementVersion(compiledList.getVersion()));

        compiledList = filter.filter(compiledList);

        return compiledList;
    }

}
