package de.malkusch.whoisServerList.compiler.list.xml;

import javax.annotation.concurrent.NotThreadSafe;

import de.malkusch.whoisServerList.api.v1.model.DomainList;
import de.malkusch.whoisServerList.api.v1.model.Source;
import de.malkusch.whoisServerList.compiler.list.DomainListFactory;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;

/**
 * Builds the domain list from the existing whois server list.
 *
 * @author markus@malkusch.de
 *
 * @see <a href="https://github.com/whois-server-list/whois-server-list">Whois
 *      Server List</a>
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
public final class XMLDomainListFactory implements DomainListFactory {

    @Override
    public Source getSource() {
        return Source.XML;
    }

    @Override
    public DomainList buildList() throws BuildListException {
        de.malkusch.whoisServerList.api.v1.DomainListFactory factory
                = new de.malkusch.whoisServerList.api.v1.DomainListFactory();
        return factory.build();
    }
}
