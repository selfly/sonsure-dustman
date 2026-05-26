package com.sonsure.dustman.test.common.utils;

import com.sonsure.dustman.common.utils.FileIOUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FileIOUtils 单元测试
 */
public class FileIOUtilsTest {

    @Test
    public void testToByteArray() throws IOException {
        byte[] data = "hello world".getBytes(StandardCharsets.UTF_8);
        byte[] result = FileIOUtils.toByteArray(new ByteArrayInputStream(data));
        assertArrayEquals(data, result);
    }

    @Test
    public void testToByteArray_Empty() throws IOException {
        byte[] result = FileIOUtils.toByteArray(new ByteArrayInputStream(new byte[0]));
        assertEquals(0, result.length);
    }

    @Test
    public void testWriteByteArrayToFile() throws IOException {
        File tempFile = File.createTempFile("dustman-test-", ".tmp");
        try {
            byte[] data = "test data".getBytes(StandardCharsets.UTF_8);
            FileIOUtils.writeByteArrayToFile(tempFile, data);
            assertTrue(tempFile.length() > 0);
            assertArrayEquals(data, FileIOUtils.toByteArray(new FileInputStream(tempFile)));
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void testWriteByteArrayToFile_Append() throws IOException {
        File tempFile = File.createTempFile("dustman-test-", ".tmp");
        try {
            byte[] data1 = "hello ".getBytes(StandardCharsets.UTF_8);
            byte[] data2 = "world".getBytes(StandardCharsets.UTF_8);
            FileIOUtils.writeByteArrayToFile(tempFile, data1, false);
            FileIOUtils.writeByteArrayToFile(tempFile, data2, true);
            byte[] result = FileIOUtils.toByteArray(new FileInputStream(tempFile));
            assertEquals("hello world", new String(result, StandardCharsets.UTF_8));
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void testCopy() throws IOException {
        byte[] data = "test data for copy".getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        long count = FileIOUtils.copy(input, output, 1024);
        assertEquals(data.length, count);
        assertArrayEquals(data, output.toByteArray());
    }

    @Test
    public void testReadLines_InputStream() throws IOException {
        String content = "line1\nline2\nline3";
        InputStream input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        List<String> lines = FileIOUtils.readLines(input, StandardCharsets.UTF_8);
        assertEquals(3, lines.size());
        assertEquals("line1", lines.get(0));
        assertEquals("line2", lines.get(1));
        assertEquals("line3", lines.get(2));
    }

    @Test
    public void testReadLines_Reader() throws IOException {
        String content = "a\nb\nc";
        Reader reader = new StringReader(content);
        List<String> lines = FileIOUtils.readLines(reader);
        assertEquals(3, lines.size());
        assertEquals("a", lines.get(0));
        assertEquals("b", lines.get(1));
        assertEquals("c", lines.get(2));
    }

    @Test
    public void testReadLines_Empty() throws IOException {
        Reader reader = new StringReader("");
        List<String> lines = FileIOUtils.readLines(reader);
        assertTrue(lines.isEmpty());
    }

    @Test
    public void testToBufferedReader_AlreadyBufferedReader() {
        BufferedReader br = new BufferedReader(new StringReader("test"));
        assertSame(br, FileIOUtils.toBufferedReader(br));
    }

    @Test
    public void testToBufferedReader_NotBufferedReader() {
        Reader reader = new StringReader("test");
        BufferedReader br = FileIOUtils.toBufferedReader(reader);
        assertNotNull(br);
        assertNotSame(reader, br);
    }
}
