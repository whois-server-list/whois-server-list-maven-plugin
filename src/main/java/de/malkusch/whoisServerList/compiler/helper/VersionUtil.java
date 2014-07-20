package de.malkusch.whoisServerList.compiler.helper;

import com.cedarsoft.version.Version;

/**
 * Version utility class.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public final class VersionUtil {

    /**
     * Private ultility constructor.
     */
    private VersionUtil() {
    }

    /**
     * Increments the patch part of a version.
     *
     * Example: 1.0.0 becomes 1.0.1
     *
     * @param version  the version
     * @return the incremented version
     */
    public static String incrementVersion(final String version) {
        Version parsedVersion = Version.parse(version);
        Version nextVersion = new Version(
                parsedVersion.getMajor(),
                parsedVersion.getMinor(),
                parsedVersion.getBuild() + 1);
        return nextVersion.format();
    }

}
