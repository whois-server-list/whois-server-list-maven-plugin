package de.malkusch.whoisServerList.compiler.list.listObjectBuilder;

import net.jcip.annotations.NotThreadSafe;

import org.apache.commons.lang3.StringUtils;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.model.Source;
import de.malkusch.whoisServerList.compiler.model.domain.Domain;

/**
 * Builder for {@code Domain} objects.
 *
 * @author markus@malkusch.de
 * @param <T> the Domain type for the builded domains
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
public class DomainBuilder<T extends Domain>
        extends ListObjectBuilder<T> {

    /**
     * The domain name.
     */
    private String name;

    /**
     * The domain type.
     */
    private final Class<T> domainType;

    /**
     * Sets the object source.
     *
     * @param source      the source
     * @param domainType  the domain type for the builded objects
     */
    public DomainBuilder(final Source source, final Class<T> domainType) {
        super(source);

        this.domainType = domainType;
    }

    /**
     * Completes a builded domain object.
     *
     * @param domain the domain
     * @throws WhoisServerListException If building the object failed
     * @throws InterruptedException If the thread was interrupted
     */
    protected void completeDomain(final T domain)
            throws WhoisServerListException, InterruptedException {
    }

    @Override
    protected final void complete(final T domain)
            throws WhoisServerListException, InterruptedException {

        if (this.name == null) {
            throw new IllegalStateException(
                    "Can't build domains without a name.");

        }
        domain.setName(StringUtils.lowerCase(this.name));
        completeDomain(domain);
    }

    /**
     * Sets the domain name.
     *
     * @param name the domain name
     */
    public final void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the domain name for the builded objects.
     *
     * @return the domain name.
     */
    public final String getName() {
        return this.name;
    }

    @Override
    protected final Class<? extends T> getObjectType() {
        return domainType;
    }

}
