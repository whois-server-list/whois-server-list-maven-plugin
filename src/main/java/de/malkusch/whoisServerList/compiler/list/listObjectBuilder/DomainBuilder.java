package de.malkusch.whoisServerList.compiler.list.listObjectBuilder;

import net.jcip.annotations.NotThreadSafe;
import de.malkusch.whoisServerList.compiler.model.Source;
import de.malkusch.whoisServerList.compiler.model.domain.Domain;

/**
 * Builder for {@code Domain} objects.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
public final class DomainBuilder extends AbstractDomainBuilder<Domain> {

    /**
     * Sets the object source.
     *
     * @param source the source
     */
    public DomainBuilder(final Source source) {
        super(source);
    }

    @Override
    protected Class<Domain> getObjectType() {
        return Domain.class;
    }

}
