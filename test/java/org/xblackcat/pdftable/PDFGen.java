package org.xblackcat.pdftable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

/**
 * 25.04.2016 14:50
 *
 * @author xBlackCat
 */
public class PDFGen {
    private final Random rnd = new Random();
    private final static PDTextStyle DEFAULT_STYLE = new PDTextStyle(Color.black, PDType1Font.TIMES_ROMAN, 12);

    public static URL getResource(String resourceName) throws MissingResourceException {
        URL url = PDFGen.class.getResource(resourceName);
        if (url == null) {
            url = PDFGen.class.getClassLoader().getResource(resourceName);
        }
        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        }
        if (url == null) {
            url = ClassLoader.getSystemResource(resourceName);
        }
        if (url == null) {
            throw new MissingResourceException("Can not find resource " + resourceName, PDFGen.class.getName(), resourceName);
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
    private static PDFont[] FONTS = new PDFont[]{
            PDType1Font.TIMES_ROMAN,
            PDType1Font.TIMES_BOLD,
            PDType1Font.TIMES_ITALIC,
            PDType1Font.TIMES_BOLD_ITALIC,
            PDType1Font.HELVETICA,
            PDType1Font.HELVETICA_BOLD,
            PDType1Font.HELVETICA_OBLIQUE,
            PDType1Font.HELVETICA_BOLD_OBLIQUE,
            PDType1Font.COURIER,
            PDType1Font.COURIER_BOLD,
            PDType1Font.COURIER_OBLIQUE,
            PDType1Font.COURIER_BOLD_OBLIQUE
    };

    public static void loadData() throws IOException {
        WORDS = loadListFromResource("/words.txt").stream().toArray(String[]::new);
    }

    private final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("'r:/test-'yyyy-MM-dd-HH-mm-ss'.pdf'");
    private final DateTimeFormatter pattern2 = DateTimeFormatter.ofPattern("'r:/test-multifont-'yyyy-MM-dd-HH-mm-ss'.pdf'");
    private final DateTimeFormatter pattern3 = DateTimeFormatter.ofPattern("'r:/test-multifont-multiheader-'yyyy-MM-dd-HH-mm-ss'.pdf'");

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

    private PDStyledString buildWordMultiFont(ToIntFunction<Random> wordsCount) {
        return buildWordMultiFont(wordsCount, rnd -> (rnd.nextFloat() * 6) + 7);
    }

    private PDStyledString buildWordMultiFont(ToIntFunction<Random> wordsCount, ToDoubleFunction<Random> fontSize) {
        PDStyledString.Builder builder = new PDStyledString.Builder(DEFAULT_STYLE);
        int words = wordsCount.applyAsInt(this.rnd);
        nextPart(builder, "", fontSize);
        while (words-- > 0) {
            nextPart(builder, " ", fontSize);
        }

        return builder.toStyledString();
    }

    private void nextPart(PDStyledString.Builder bldr, String s, ToDoubleFunction<Random> fontSize) {
        final String text = s + WORDS[this.rnd.nextInt(WORDS.length)];
        final PDTextStyle style = nextStyle(fontSize);
        bldr.append(text, style);
    }

    private PDTextStyle nextStyle(ToDoubleFunction<Random> fontSize) {
        PDFont font = FONTS[rnd.nextInt(FONTS.length)];
        return new PDTextStyle(nextColor(), font, (float) fontSize.applyAsDouble(this.rnd));
    }

    private Color nextColor() {
        return new Color(rnd.nextInt(0x1000000));
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

    public DataGroup[] tableDataMultiFont() {
        int cols = 6;
        int rows = 20;

        Collection<DataGroup> data = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            PDStyledString[] row = new PDStyledString[cols];
            for (int j = 0; j < cols; j++) {
                row[j] = buildWordMultiFont(rnd -> rnd.nextInt(5) + 3);
            }

            data.add(new DataGroup(row));
        }

        return data.stream().toArray(DataGroup[]::new);
    }

    public DataGroup[] tableDataMultiFontWithHeader() {
        int cols = 6;
        int rows = 10;

        Collection<DataGroup> data = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            PDStyledString[] row = new PDStyledString[cols];
            for (int j = 0; j < cols; j++) {
                row[j] = buildWordMultiFont(rnd -> rnd.nextInt(3) + 1, rnd -> (rnd.nextFloat() * 4) + 10);
            }

            data.add(new DataGroup(row, tableDataMultiFont()));
        }

        return data.stream().toArray(DataGroup[]::new);
    }

    public DataGroup[] tableDataMultiFontWithHeaders() {
        int cols = 6;
        int rows = 10;

        Collection<DataGroup> data = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            PDStyledString[] row = new PDStyledString[cols];
            for (int j = 0; j < cols; j++) {
                row[j] = buildWordMultiFont(rnd -> rnd.nextInt(2) + 1, rnd -> (rnd.nextFloat() * 4) + 14);
            }

            data.add(new DataGroup(row, tableDataMultiFontWithHeader()));
        }

        return data.stream().toArray(DataGroup[]::new);
    }

    public void generatePDF() throws IOException {
        DataGroup[] data = tableData();

        final PDTextStyle style = new PDTextStyle(PDType1Font.TIMES_ROMAN, 8);
        PDFTable table = new PDFTable(
                new DefaultPDPageProvider(PDRectangle.A4),
                new DefaultPDRowProvider(
                        PDBorderStyle.fullBorderOf(PDLineStyle.ofColor(4, Color.GREEN)),
                        PDTableTextCell.DEFAULT_PADDING,
                        (cellObj, col, row, page) -> {
                            String[] r = (String[]) cellObj;
                            return new PDStyledString(r[col], style);
                        },
                        PDBorderStyle.fullBorderOf(PDLineStyle.ofColor(Color.blue)),
                        null,
                        100, 100, 50, 50, 50, 50
                ),
                null,
                null
        );

        PDDocument doc = new PDDocument();

        PDFTable.Drawer drawer = table.applyToDocument(doc);
        drawer.drawTable(data);
        try (OutputStream os = new FileOutputStream(pattern.format(LocalDateTime.now()))) {
            doc.save(os);
            os.flush();
        }

    }

    public void generatePDFMultiFont() throws IOException {
        DataGroup[] data = tableDataMultiFont();

        PDFTable table = new PDFTable(
                new DefaultPDPageProvider(PDRectangle.A4, new PDInsets(40, 50, 30, 50)),
                new DefaultPDRowProvider(
                        PDBorderStyle.leftRightBorderOf(PDLineStyle.ofColor(4, Color.GREEN)),
                        PDTableTextCell.DEFAULT_PADDING,
                        (cellObj, col, row, page) -> ((PDStyledString[]) cellObj)[col],
                        PDBorderStyle.topBottomBorderOf(PDLineStyle.ofColor(Color.blue)),
                        null,
                        100, 100, 50, 50, 50, 50
                ),
                PDBorderStyle.fullBorderOf(PDLineStyle.ofColor(2, Color.red)),
                null
        );

        PDDocument doc = new PDDocument();

        PDFTable.Drawer drawer = table.applyToDocument(doc);
        drawer.drawTable(data);
        try (OutputStream os = new FileOutputStream(pattern2.format(LocalDateTime.now()))) {
            doc.save(os);
            os.flush();
        }

    }

    public void generatePDFMultiFontWithHeader() throws IOException {
        DataGroup[] data = tableDataMultiFontWithHeader();

        IPDRowProvider rowProvider = new DefaultLevelPDRowProvider(
                new DefaultPDRowProvider(
                        PDBorderStyle.fullBorderOf(PDLineStyle.ofColor(6, Color.YELLOW)),
                        PDTableTextCell.DEFAULT_PADDING,
                        (cellObj, col, row, page) -> ((PDStyledString[]) cellObj)[col],
                        PDBorderStyle.leftRightBorderOf(PDLineStyle.ofColor(Color.MAGENTA)),
                        null,
                        200, 300, 200, 80
                ),
                new DefaultPDRowProvider(
                        PDBorderStyle.leftRightBorderOf(PDLineStyle.ofColor(4, Color.GREEN)),
                        PDTableTextCell.DEFAULT_PADDING,
                        (cellObj, col, row, page) -> ((PDStyledString[]) cellObj)[col],
                        PDBorderStyle.fullBorderOf(PDLineStyle.ofColor(Color.blue)),
                        null,
                        200, 200, 100, 100, 100, 80
                )
        );
        PDFTable table = new PDFTable(
                new DefaultPDPageProvider(
                        new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()),
                        new PDInsets(40, 50, 30, 50)
                ),
                rowProvider,
                PDBorderStyle.fullBorderOf(PDLineStyle.ofColor(2, Color.red)),
                null
        );

        PDDocument doc = new PDDocument();

        PDFTable.Drawer drawer = table.applyToDocument(doc);
        drawer.drawTable(data);
        try (OutputStream os = new FileOutputStream(pattern2.format(LocalDateTime.now()))) {
            doc.save(os);
            os.flush();
        }

    }

    public void generatePDFMultiFontWithHeaders() throws IOException {
        DataGroup[] data = tableDataMultiFontWithHeaders();

        IPDRowProvider rowProvider = new DefaultLevelPDRowProvider(
                new DefaultPDRowProvider(
                        PDBorderStyle.fullBorderOf(PDLineStyle.ofColor(8, Color.GRAY)),
                        PDTableTextCell.DEFAULT_PADDING,
                        (cellObj, col, row, page) -> ((PDStyledString[]) cellObj)[col],
                        PDBorderStyle.leftRightBorderOf(PDLineStyle.ofColor(Color.orange)),
                        Color.DARK_GRAY,
                        500, 280
                ),
                new DefaultPDRowProvider(
                        PDBorderStyle.fullBorderOf(PDLineStyle.ofColor(6, Color.YELLOW)),
                        PDTableTextCell.DEFAULT_PADDING,
                        (cellObj, col, row, page) -> ((PDStyledString[]) cellObj)[col],
                        PDBorderStyle.leftRightBorderOf(PDLineStyle.ofColor(Color.MAGENTA)),
                        Color.pink,
                        200, 300, 200, 80
                ) {
                    @Override
                    public PDTableRowDef getRowCellInfo(Object rowObject, int level, int groupRow, int row, int page) {
                        PDTableRowDef cellInfo = super.getRowCellInfo(rowObject, level, groupRow, row, page);
                        if (groupRow % 2 == 0) {
                            return cellInfo;
                        } else {
                            return new PDTableRowDef(cellInfo.getBorderStyle(), Color.orange, cellInfo.getCellDefs());
                        }
                    }
                },
                new DefaultPDRowProvider(
                        PDBorderStyle.leftRightBorderOf(PDLineStyle.ofColor(4, Color.GREEN)),
                        PDTableTextCell.DEFAULT_PADDING,
                        (cellObj, col, row, page) -> ((PDStyledString[]) cellObj)[col],
                        PDBorderStyle.fullBorderOf(PDLineStyle.ofColor(Color.blue)),
                        null,
                        200, 200, 100, 100, 100, 80
                ) {
                    @Override
                    public PDTableRowDef getRowCellInfo(Object rowObject, int level, int groupRow, int row, int page) {
                        PDTableRowDef cellInfo = super.getRowCellInfo(rowObject, level, groupRow, row, page);
                        if (groupRow % 2 == 0) {
                            return cellInfo;
                        } else {
                            return new PDTableRowDef(cellInfo.getBorderStyle(), Color.lightGray, cellInfo.getCellDefs());
                        }
                    }
                }
        );
        final PDRectangle pageSize = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
        PDFTable table = new PDFTable(
                new IPDPageProvider() {
                    @Override
                    public PDPageContentStream buildPage(PDDocument doc, int pageNum) throws IOException {
                        final PDPage page = new PDPage(pageSize);
                        doc.addPage(page);

                        return new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, false);
                    }

                    @Override
                    public PDInsets getDrawMargins(int pageNum) {
                        return new PDInsets(40, 20 + 30 * (pageNum % 2), 30, 50 - 30 * (pageNum % 2));
                    }

                    @Override
                    public PDRectangle getPageSize(int pageNum) {
                        return pageSize;
                    }
                },
                rowProvider,
                PDBorderStyle.fullBorderOf(PDLineStyle.ofColor(2, Color.red)),
                null
        );

        PDDocument doc = new PDDocument();

        PDFTable.Drawer drawer = table.applyToDocument(doc);
        drawer.drawTable(data);
        try (OutputStream os = new FileOutputStream(pattern3.format(LocalDateTime.now()))) {
            doc.save(os);
            os.flush();
        }

    }

    public static void main(String[] args) throws IOException {
        loadData();

//        new PDFGen().generatePDF();
//        new PDFGen().generatePDFMultiFont();
//        new PDFGen().generatePDFMultiFontWithHeader();
        new PDFGen().generatePDFMultiFontWithHeaders();
    }
}
