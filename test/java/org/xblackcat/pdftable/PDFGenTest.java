package org.xblackcat.pdftable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 25.04.2016 14:50
 *
 * @author xBlackCat
 */
public class PDFGenTest {
    private final Random rnd = new Random();

    public static URL getResource(String resourceName) throws MissingResourceException {
        URL url = PDFGenTest.class.getResource(resourceName);
        if (url == null) {
            url = PDFGenTest.class.getClassLoader().getResource(resourceName);
        }
        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        }
        if (url == null) {
            url = ClassLoader.getSystemResource(resourceName);
        }
        if (url == null) {
            throw new MissingResourceException("Can not find resource " + resourceName, PDFGenTest.class.getName(), resourceName);
        }

        return url;
    }

    /**
     * Returns stream of specified resource or {@code null} if resource is not exists.
     *
     * @param resourceName resource name to open
     * @return resource stream or null
     * @throws IOException              if input stream can not be opened
     * @throws MissingResourceException if specified resource can not be found.
     */
    public static InputStream getResourceAsStream(String resourceName) throws IOException, MissingResourceException {
        return getResource(resourceName).openStream();
    }

    public static List<String> loadListFromResource(String resourceName) throws IOException, MissingResourceException {
        InputStream stream = getResourceAsStream(resourceName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        List<String> strings = new LinkedList<>();

        String s;
        while ((s = reader.readLine()) != null) {
            strings.add(s.trim());
        }
        return strings;
    }

    private static String[] WORDS;

    @BeforeClass
    public static void loadData() throws IOException {
        WORDS = loadListFromResource("/words.txt").stream().toArray(String[]::new);
    }

    private final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("'r:/test-'yyyy-MM-dd-HH-mm'.pdf'");

    private String buildWord() {
        StringBuilder str = new StringBuilder();

        str.append(WORDS[rnd.nextInt(WORDS.length)]);
        int words = rnd.nextInt(10) + 3;
        while (words-- > 0) {
            str.append(' ');
            str.append(WORDS[rnd.nextInt(WORDS.length)]);
        }

        return str.toString();
    }

    public DataGroup[] tableData() {
        int cols = 6;
        int rows = 60;

        Collection<DataGroup> data = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            String[] row = new String[cols];
            for (int j = 0; j < cols; j++) {
                row[j] = buildWord();
            }

            data.add(new DataGroup(row));
        }

        return data.stream().toArray(DataGroup[]::new);
    }

    @Test
    public void generatePDF() throws IOException {
        DataGroup[] data = tableData();

        PDFTable table = new PDFTable(
                new DefaultPDPageProvider(PDRectangle.A4),
                new DefaultPDRowProvider(
                        PDTableCell.DEFAULT_PADDING,
                        (cellObj, col, row, page) -> {
                            String[] r = (String[]) cellObj;
                            return PDTextLine.of(r[col], PDType1Font.TIMES_ROMAN, 8);
                        }, 100, 100, 50, 50, 50, 50
                )
        );

        PDDocument doc = new PDDocument();

        PDFTable.Drawer drawer = table.drawTable(doc);
        drawer.drawTable(data);
        try (OutputStream os = new FileOutputStream(pattern.format(LocalDateTime.now()))) {
            doc.save(os);
            os.flush();
        }

    }
}
