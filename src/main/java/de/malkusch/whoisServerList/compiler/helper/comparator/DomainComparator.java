package de.malkusch.whoisServerList.compiler.helper.comparator;

import java.util.Comparator;

import de.malkusch.whoisServerList.api.v1.model.domain.Domain;

/**
 * Compares Domains by its name.
 *
 * @author markus@malkusch.de
 *
 * @see Domain#getName()
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public final class DomainComparator implements Comparator<Domain> {

    @Override
    public int compare(final Domain o1, final Domain o2) {
        return o1.getName().compareTo(o2.getName());
    }

}
