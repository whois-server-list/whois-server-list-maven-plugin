package de.malkusch.whoisServerList.compiler.helper.converter;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.junit.Test;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.helper.converter.DocumentToStringIteratorConvertor;
import de.malkusch.whoisServerList.compiler.helper.converter.InputStreamToDocumentConverter;
import de.malkusch.whoisServerList.compiler.list.iana.IanaDomainListFactory;

public class DocumentToStringIteratorConvertorTest {

    @Test
    public void testConvert() throws IOException, WhoisServerListException {
        String[] expectedTLDs = { ".ac", ".academy", ".axa", ".vn", ".vodka",
                ".测试", ".परीक्षा", ".在线", ".한국", ".ভারত", ".موقع", ".বাংলা",
                ".москва", ".테스트", ".טעסט", ".భారత్", ".ලංකා", ".ભારત",
                ".भारत", ".آزمایشی", ".பரிட்சை", ".δοκιμή", ".إختبار", ".台灣",
                ".мон", ".الجزائر", ".المغرب", ".السعودية", ".سودان",
                ".مليسيا", ".شبكة", ".გე", ".ไทย", ".سورية", ".تونس", ".みんな",
                ".مصر", ".இந்தியா", ".xxx", ".yachts", ".zw" };

        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/compiler.properties"));

        InputStream ianaList = getClass().getResourceAsStream(
                "/iana-list-excerpt.html");

        InputStreamToDocumentConverter documentConverter = new InputStreamToDocumentConverter(
                properties
                        .getProperty(IanaDomainListFactory.PROPERTY_LIST_CHARSET));

        DocumentToStringIteratorConvertor<InputStream> converter = new DocumentToStringIteratorConvertor<>(
                properties
                        .getProperty(IanaDomainListFactory.PROPERTY_LIST_TLD_XPATH),
                documentConverter);

        ArrayList<String> tlds = new ArrayList<>();
        for (String tld : converter.convert(ianaList)) {
            tlds.add(tld);

        }

        assertArrayEquals(expectedTLDs, tlds.toArray());
    }

}
