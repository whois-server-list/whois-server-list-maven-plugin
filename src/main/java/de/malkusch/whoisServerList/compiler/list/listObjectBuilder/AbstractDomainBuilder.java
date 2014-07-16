package de.malkusch.whoisServerList.compiler.list.listObjectBuilder;

import net.jcip.annotations.NotThreadSafe;

import org.apache.commons.lang3.StringUtils;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.model.Source;
import de.malkusch.whoisServerList.compiler.model.domain.Domain;

/**
 * Abstract builder for {@code Domain} objects.
 *
 * @author markus@malkusch.de
 * @param <T> the Domain type for the builded domains
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
public abstract class AbstractDomainBuilder<T extends Domain>
        extends ListObjectBuilder<T> {

    /**
     * The domain name.
     */
    private String name;

    /**
     * Sets the object source.
     *
     * @param source the source
     */
    AbstractDomainBuilder(final Source source) {
        super(source);
    }

    /**
     * Completes a builded domain object.
     *
     * @param domain the domain
     * @throws WhoisServerListException If building the object failed
     */
    protected void completeDomain(final T domain)
            throws WhoisServerListException {
    }

    @Override
    protected final void complete(final T domain)
            throws WhoisServerListException {

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

}
