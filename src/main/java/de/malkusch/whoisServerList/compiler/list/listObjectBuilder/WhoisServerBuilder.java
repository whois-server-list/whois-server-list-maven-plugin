package de.malkusch.whoisServerList.compiler.list.listObjectBuilder;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.concurrent.NotThreadSafe;
import de.malkusch.whoisServerList.compiler.model.Source;
import de.malkusch.whoisServerList.compiler.model.WhoisServer;

/**
 * Builder for WhoisServer.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
public final class WhoisServerBuilder extends ListObjectBuilder<WhoisServer> {

    /**
     * The host name for builded objects.
     */
    private String host;

    /**
     * Sets the source for this factory.
     *
     * All created objects of this factory will have this source.
     *
     * @param source  the source
     */
    public WhoisServerBuilder(final Source source) {
        super(source);
    }

    @Override
    protected void complete(final WhoisServer server) {
        if (this.host == null) {
            throw new IllegalStateException("host name may not be null.");

        }
        server.setHost(StringUtils.lowerCase(this.host));
    }

    /**
     * Sets the host name for builded objects.
     *
     * @param host  the host name, not null
     */
    public void setHost(final String host) {
        this.host = host;
    }

    @Override
    protected Class<WhoisServer> getObjectType() {
        return WhoisServer.class;
    }

}
