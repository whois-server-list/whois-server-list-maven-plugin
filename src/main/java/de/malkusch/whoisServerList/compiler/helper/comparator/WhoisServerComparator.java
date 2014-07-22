package de.malkusch.whoisServerList.compiler.helper.comparator;

import java.util.Comparator;

import de.malkusch.whoisServerList.api.v1.model.WhoisServer;

/**
 * Compares WhoisServer by its host name.
 *
 * @author markus@malkusch.de
 *
 * @see WhoisServer#getHost()
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public final class WhoisServerComparator implements Comparator<WhoisServer> {

    @Override
    public int compare(final WhoisServer o1, final WhoisServer o2) {
        return o1.getHost().compareTo(o2.getHost());
    }

}
