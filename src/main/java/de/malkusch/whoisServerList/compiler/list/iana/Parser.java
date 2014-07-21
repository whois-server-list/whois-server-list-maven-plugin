package de.malkusch.whoisServerList.compiler.list.iana;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.StringUtils;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.helper.converter.InputStreamToBufferedReaderConverter;
import de.malkusch.whoisServerList.compiler.model.domain.Domain.State;

/**
 * Whois result parser.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
final class Parser implements Closeable {

    /**
     * Value for the domain state {@link State#NEW}.
     */
    public static final String STATE_ACTIVE = "ACTIVE";

    /**
     * Value for the domain state {@link State#INACTIVE}.
     */
    public static final String STATE_INACTIVE = "INACTIVE";

    /**
     * Value for the domain state {@link State#NEW}.
     */
    public static final String STATE_NEW = "NEW";

    /**
     * Parsable whois result keys.
     */
    private String[] keys;

    /**
     * The whois result date format.
     */
    private final DateFormat dateFormat;

    /**
     * The parsed whois result.
     */
    private final Map<String, String> result = new HashMap<>();

    /**
     * The reader for the whois result stream.
     */
    private BufferedReader reader;

    /**
     * Found URLs during parsing.
     */
    private final List<URL> urls = new ArrayList<>();

    /**
     * Initializes the parser.
     */
    Parser() {
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }

    /**
     * Sets the whois result keys.
     *
     * The result map will only contain values for these keys.
     *
     * @param keys  the parsable whois result keys, not null
     */
    void setKeys(final String... keys) {
        this.keys = keys;
    }

    /**
     * Closes the stream which was used in {@code parse}.
     *
     * @throws IOException If closing failed
     */
    @Override
    public void close() throws IOException {
        reader.close();
    }

    /**
     * Starts parsing.
     *
     * Closing the parser will close this reader as well.
     *
     * @param reader  the whois result stream
     * @throws IOException If reading from the stream failed
     * @throws InterruptedException If the thread was interrupted
     */
    void parse(final BufferedReader reader)
            throws IOException, InterruptedException {

        this.reader = reader;

        String regex = String.format("^(%s):\\s+(\\S.*\\S)\\s*$",
                StringUtils.join(keys, "|"));
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        Pattern commentPattern = Pattern.compile("^% ");

        Pattern urlPattern = Pattern.compile(
                "(https?://\\S+)(\\s|$)", Pattern.CASE_INSENSITIVE);

        String line;
        while ((line = reader.readLine()) != null) {
            if (Thread.interrupted()) {
                throw new InterruptedException();

            }
            if (commentPattern.matcher(line).find()) {
                continue;

            }

            Matcher urlMatcher = urlPattern.matcher(line);
            while (urlMatcher.find()) {
                urls.add(new URL(urlMatcher.group(1)));

            }

            Matcher matcher = pattern.matcher(line);
            if (!matcher.matches()) {
                continue;

            }
            String key = matcher.group(1);
            String value = matcher.group(2);
            result.put(key, value);

        }
    }

    /**
     * Starts parsing.
     *
     * Closing the parser will close this stream as well.
     *
     * @param stream   the whois result stream
     * @param charset  the character encoding of the whois stream
     * @throws IOException If reading from the stream failed
     * @throws InterruptedException If the thread was interrupted
     */
    void parse(final InputStream stream, final Charset charset)
            throws IOException, InterruptedException {

        InputStreamToBufferedReaderConverter converter
                = new InputStreamToBufferedReaderConverter(charset);
        parse(converter.convert(stream));
    }

    /**
     * Returns the value from a whois result for a key.
     *
     * @param key  the whois result key, not null
     * @return the whois result value, or null
     */
    String getString(final String key) {
        return result.get(key);
    }

    /**
     * Returns the value as a {@code State} from a whois result for a key.
     *
     * @param key  the whois result key, not null
     * @return the whois result value as state, or null
     * @throws WhoisServerListException If the whois result returned an
     *                                  unexpected state
     */
    State getState(final String key) throws WhoisServerListException {
        String state = getString(key);
        if (state == null) {
            return null;

        }
        switch (state) {

        case STATE_ACTIVE:
            return State.ACTIVE;

        case STATE_NEW:
            return State.NEW;

        case STATE_INACTIVE:
            return State.INACTIVE;

        default:
            throw new WhoisServerListException(String.format(
                    "unexpected state %s", state));

        }
    }

    /**
     * Returns the value as a {@code Date} from a whois result for a key.
     *
     * @param key  the whois result key, not null
     * @return the whois result value as Date, or null
     * @throws WhoisServerListException If the whois result returned an
     *                                  unexpected date format
     */
    Date getDate(final String key) throws WhoisServerListException {
        try {
            String date = getString(key);
            if (date == null) {
                return null;

            }
            return dateFormat.parse(date);

        } catch (ParseException e) {
            throw new WhoisServerListException(e);

        }
    }

    /**
     * Return URLs which were found during parsing.
     *
     * @return the URLs, not null
     */
    List<URL> getURLs() {
        return urls;
    }

}
