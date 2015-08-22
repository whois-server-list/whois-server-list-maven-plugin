package de.malkusch.whoisServerList.compiler.list;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.ArrayUtils;

import de.malkusch.whoisServerList.api.v1.model.domain.Domain;
import de.malkusch.whoisServerList.api.v1.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.list.listObjectBuilder.DomainBuilder;
import de.malkusch.whoisServerList.compiler.list.listObjectBuilder.TopLevelDomainBuilder;
import de.malkusch.whoisServerList.compiler.list.listObjectBuilder.WhoisServerBuilder;
import de.malkusch.whoisServerList.publicSuffixList.util.DomainUtil;

/**
 * Abstract domain list factory.
 *
 * @author markus@malkusch.de
 *
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
public abstract class AbstractDomainListFactory implements DomainListFactory {

    /**
     * The builded Top Level Domains.
     */
    private final Map<String, TopLevelDomain> topLevelDomains = new HashMap<>();

    /**
     * The top level domain builder.
     */
    private final TopLevelDomainBuilder tldBuilder;
    
    /**
     * The domain builder.
     */
    private final DomainBuilder<Domain> domainBuilder;
    
    /**
     * The whois server builder.
     */
    private final WhoisServerBuilder whoisServerBuilder;
    
    /**
     * Initialize
     */
    public AbstractDomainListFactory() {
        tldBuilder    = new TopLevelDomainBuilder(getSource());
        domainBuilder = new DomainBuilder<>(getSource(), Domain.class);
        whoisServerBuilder = new WhoisServerBuilder(getSource());
    }
    
    /**
     * Adds a suffix and its whois server to the list top level domain list.
     * 
     * @param suffix The suffix may have a leading "."
     * @param host   The whois server.
     */
    protected final void addSuffix(String suffix, @Nullable String host) throws WhoisServerListException, InterruptedException {
        suffix = suffix.replaceAll("^\\.", "");
        
        String[] labels = DomainUtil.splitLabels(suffix);
        
        TopLevelDomain topLevelDomain
            = getTopLevelDomain(labels[labels.length - 1]);
        Domain domain = topLevelDomain;

        if (ArrayUtils.getLength(labels) > 1) {
            domainBuilder.setName(suffix);
            domain = domainBuilder.build();

            topLevelDomain.getDomains().add(domain);
        }
        
        if (host != null) {
            whoisServerBuilder.setHost(host);
            domain.getWhoisServers().add(whoisServerBuilder.build());
        }
    }
    
    /**
     * Gets all top level domains.
     * 
     * @return All top level domains.
     */
    protected final Collection<TopLevelDomain> getDomains() {
        return topLevelDomains.values();
    }

    /**
     * Returns the top level domain.
     *
     * If the top level domain does not exist in {@link #topLevelDomains} it
     * will be created and stored and that map.
     *
     * @param name  the domain name, not null
     * @return the top level domain, not null
     */
    private TopLevelDomain getTopLevelDomain(final String name) {

        return this.topLevelDomains.computeIfAbsent(name, key -> {
            try {
                this.tldBuilder.setName(key);
                TopLevelDomain domain = this.tldBuilder.build();
                return domain;

            } catch (WhoisServerListException | InterruptedException e) {
                throw new IllegalStateException(e);
            }
        });
    }
    
}
