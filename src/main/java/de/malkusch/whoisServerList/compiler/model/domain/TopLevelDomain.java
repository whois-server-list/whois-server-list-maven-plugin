package de.malkusch.whoisServerList.compiler.model.domain;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Top level domain.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public class TopLevelDomain extends Domain {

    /**
     * The registration service.
     */
    private URL registrationService;

    /**
     * The sub domains.
     */
    private List<Domain> domains = new ArrayList<>();

    /**
     * Returns the sub domains.
     *
     * @return the sub domains, not null
     */
    public final List<Domain> getDomains() {
        return domains;
    }

    /**
     * Sets the sub domains.
     *
     * @param domains  the sub domains, not null
     */
    public final void setDomains(final List<Domain> domains) {
        this.domains = domains;
    }

    /**
     * Returns the URL of the registration service.
     *
     * @return the registration service, maybe null
     */
    public final URL getRegistratonService() {
        return registrationService;
    }

    /**
     * Sets the URL of the registration service.
     *
     * @param registrationService the registration service, may be null
     */
    public final void setRegistratonService(final URL registrationService) {
        this.registrationService = registrationService;
    }

}
