package org.xblackcat.pdftable;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * 11.04.2016 12:07
 *
 * @author xBlackCat
 */
public class PDFUtilsTest {
    @Test
    public void splitTest() throws IOException {
        String text = "I am trying to create a PDF file with a lot of text contents in the document. I am using PDFBox";

        {
            final PDTableCell strings = PDFUtils.toFixedWidthCell(300, new PDTextPart(text, PDType1Font.HELVETICA, 12));
            Assert.assertArrayEquals(
                    new String[]{
                            "I am trying to create a PDF file with a lot of text",
                            "contents in the document. I am using PDFBox"
                    },
                    Stream.of(strings.getLines()).map(ITextable::getText).toArray(String[]::new)
            );
        }
        {
            final PDTableCell strings = PDFUtils.toFixedWidthCell(300, new PDTextPart(text, PDType1Font.HELVETICA, 18));
            Assert.assertArrayEquals(
                    new String[]{

                            "I am trying to create a PDF file with",
                            "a lot of text contents in the",
                            "document. I am using PDFBox"
                    },
                    Stream.of(strings.getLines()).map(ITextable::getText).toArray(String[]::new)
            );
        }
        {
            final PDTableCell strings = PDFUtils.toFixedWidthCell(300, new PDTextPart(text, PDType1Font.HELVETICA_BOLD, 18));
            Assert.assertArrayEquals(
                    new String[]{

                            "I am trying to create a PDF file",
                            "with a lot of text contents in the",
                            "document. I am using PDFBox"
                    },
                    Stream.of(strings.getLines()).map(ITextable::getText).toArray(String[]::new)
            );
        }
        {
            String textML = "I am trying to create a PDF file with a lot of text contents in the document.\nI am using PDFBox";

            final PDTableCell strings = PDFUtils.toFixedWidthCell(300, new PDTextPart(textML, PDType1Font.HELVETICA_BOLD, 18));
            Assert.assertArrayEquals(
                    new String[]{

                            "I am trying to create a PDF file",
                            "with a lot of text contents in the",
                            "document.",
                            "I am using PDFBox"
                    },
                    Stream.of(strings.getLines()).map(ITextable::getText).toArray(String[]::new)
            );
        }
        {
            String text1 = "I am trying to create a PDF file with a lot of text contents in the document with one very_long_unbreakable_word_to_split_it. I am using PDFBox";
            final PDTableCell strings = PDFUtils.toFixedWidthCell(300, new PDTextPart(text1, PDType1Font.HELVETICA_BOLD, 18));
            Assert.assertArrayEquals(
                    new String[]{

                            "I am trying to create a PDF file",
                            "with a lot of text contents in the",
                            "document with one",
                            "very_long_unbreakable_word_to_sp",
                            "lit_it. I am using PDFBox"
                    },
                    Stream.of(strings.getLines()).map(ITextable::getText).toArray(String[]::new)
            );
        }
    }
}
