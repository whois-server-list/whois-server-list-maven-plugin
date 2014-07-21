package de.malkusch.whoisServerList.compiler;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import net.jcip.annotations.Immutable;
import de.malkusch.whoisServerList.compiler.helper.VersionUtil;
import de.malkusch.whoisServerList.compiler.list.DomainListFactory;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.list.iana.IanaDomainListFactory;
import de.malkusch.whoisServerList.compiler.list.psl.PublicSuffixDomainListFactory;
import de.malkusch.whoisServerList.compiler.list.xml.XMLDomainListFactory;
import de.malkusch.whoisServerList.compiler.merger.DomainListMerger;
import de.malkusch.whoisServerList.compiler.model.DomainList;

/**
 * Compiles a list of top level domains and its whois server.
 *
 * The sources for the compiled list are:
 *
 * <ul>
 *   <li>Whois Server List</li>
 *   <li>Root Zone Database</li>
 *   <li>Public Suffix List</li>
 * </ul>
 *
 * @author markus@malkusch.de
 *
 * @see <a href="https://github.com/whois-server-list/whois-server-list">Whois
 *      Server List</a>
 * @see <a href="http://www.iana.org/domains/root/db">Root Zone Database</a>
 * @see <a href="https://publicsuffix.org/">Public Suffix List</a>
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
     * @see #getDefaultProperties()
     */
    public DomainListCompiler() {
        this(getDefaultProperties());
    }

    /**
     * Constructs the compiler.
     *
     * @param properties  the compiler properties
     */
    public DomainListCompiler(final Properties properties) {
        this.listFactories = new DomainListFactory[] {
                new XMLDomainListFactory(),
                new IanaDomainListFactory(properties),
                new PublicSuffixDomainListFactory()
        };

        this.merger = new DomainListMerger(properties);
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

        return compiledList;
    }

}
