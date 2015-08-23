package de.malkusch.whoisServerList.compiler.list.listObjectBuilder;

import javax.annotation.concurrent.NotThreadSafe;

import de.malkusch.whoisServerList.api.v1.model.Source;
import de.malkusch.whoisServerList.api.v1.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.helper.DomainUtil;

/**
 * Factory for TopLevelDomain.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
public class TopLevelDomainBuilder
        extends DomainBuilder<TopLevelDomain> {

    /**
     * Sets the source for this factory.
     *
     * All created objects of this factory will have this source.
     *
     * @param source  the source
     */
    public TopLevelDomainBuilder(final Source source) {
        super(source, TopLevelDomain.class);
    }

    /**
     * Completes a created domain.
     *
     * Subclasses may overwrite this method to complete created domains.
     *
     * @param domain the incomplete top level domain
     * @throws WhoisServerListException If completing failed
     * @throws InterruptedException If the thread was interrupted
     */
    protected void completeTopLevelDomain(final TopLevelDomain domain)
            throws WhoisServerListException, InterruptedException {
    }

    @Override
    protected final void completeDomain(final TopLevelDomain domain)
            throws WhoisServerListException, InterruptedException {

        domain.setCountryCode(DomainUtil.getCountryCode(domain.getName()));

        completeTopLevelDomain(domain);
    }

}
