package de.malkusch.whoisServerList.compiler.model;

import java.util.ArrayList;
import java.util.List;

import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

/**
 * List of all top level domains.
 *
 * This is the root element of the data structure.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public final class DomainList {

    /**
     * The top level domains.
     */
    private List<TopLevelDomain> domains = new ArrayList<>();

    /**
     * Returns the top level domains.
     *
     * @return the top level domains
     */
    public List<TopLevelDomain> getDomains() {
        return domains;
    }

    /**
     * Set the top level domains.
     *
     * @param domains  the top level domains
     */
    public void setDomains(final List<TopLevelDomain> domains) {
        this.domains = domains;
    }

}
