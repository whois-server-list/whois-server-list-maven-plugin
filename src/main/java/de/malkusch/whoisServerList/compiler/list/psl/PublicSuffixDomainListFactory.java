package de.malkusch.whoisServerList.compiler.list.psl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.helper.DomainUtil;
import de.malkusch.whoisServerList.compiler.list.DomainListFactory;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.list.listObjectBuilder.TopLevelDomainBuilder;
import de.malkusch.whoisServerList.compiler.model.DomainList;
import de.malkusch.whoisServerList.compiler.model.Source;
import de.malkusch.whoisServerList.compiler.model.domain.Domain;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;
import de.malkusch.whoisServerList.publicSuffixList.PublicSuffixList;
import de.malkusch.whoisServerList.publicSuffixList.PublicSuffixListFactory;
import de.malkusch.whoisServerList.publicSuffixList.rule.Rule;

/**
 * This factory builds the domain list from the Public Domain Suffix List.
 *
 * @author markus@malkusch.de
 *
 * @see <a href="https://publicsuffix.org/">https://publicsuffix.org/</a>
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
public final class PublicSuffixDomainListFactory implements DomainListFactory {

    /**
     * The builded Top Level Domains.
     */
    private final Map<String, TopLevelDomain> topLevelDomains = new HashMap<>();

    /**
     * The top level domain builder.
     */
    private final TopLevelDomainBuilder tldBuilder
        = new TopLevelDomainBuilder(Source.PSL);

    /**
     * The Public Suffix List.
     */
    private final PublicSuffixList suffixList;

    /**
     * Initializes with a public suffix list.
     *
     * @param suffixList  the suffix list
     */
    public PublicSuffixDomainListFactory(final PublicSuffixList suffixList) {
        this.suffixList = suffixList;
    }

    /**
     * Initializes with the bundled suffix list.
     */
    public PublicSuffixDomainListFactory() {
        this(new PublicSuffixListFactory().build());
    }

    @Override
    public Source getSource() {
        return Source.PSL;
    }

    @Override
    public DomainList buildList()
            throws BuildListException, InterruptedException {

        this.topLevelDomains.clear();

        for (Rule rule : this.suffixList.getRules()) {
            if (rule.isExceptionRule()) {
                continue;

            }
            if (rule.getPattern().contains(Rule.WILDCARD)) {
                continue;

            }

            String name = DomainUtil.normalize(rule.getPattern());

            if (StringUtils.isEmpty(name)) {
                throw new BuildListException(
                        "Public Suffix List provided an empty rule");

            }

            String[] labels = de.malkusch.whoisServerList.publicSuffixList.util
                        .DomainUtil.splitLabels(name);

            TopLevelDomain topLevelDomain
                = getTopLevelDomain(labels[labels.length - 1]);

            if (ArrayUtils.getLength(labels) > 1) {
                Domain domain = new Domain();
                domain.setName(name);
                domain.setSource(getSource());

                topLevelDomain.getDomains().add(domain);

            }
        }

        DomainList list = new DomainList();
        list.setDomains(
                new ArrayList<TopLevelDomain>(this.topLevelDomains.values()));

        return list;
    }

    /**
     * Returns the top level domain.
     *
     * If the top level domain does not exist in {@link #topLevelDomains} it
     * will be created and stored and that map.
     *
     * @param name  the domain name, not null
     * @return the top level domain, not null
     * @throws InterruptedException if the thread was interrupted
     */
    private TopLevelDomain getTopLevelDomain(final String name)
            throws InterruptedException {

        try {
            TopLevelDomain domain = this.topLevelDomains.get(name);
            if (domain == null) {
                this.tldBuilder.setName(name);
                domain = this.tldBuilder.build();
                this.topLevelDomains.put(name, domain);

            }
            return domain;

        } catch (WhoisServerListException e) {
            throw new RuntimeException(e);

        }
    }

}
