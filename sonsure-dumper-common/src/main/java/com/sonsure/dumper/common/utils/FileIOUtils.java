/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.utils;

import com.sonsure.dumper.common.exception.SonsureException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liyd
 */
public class FileIOUtils {

    /**
     * To byte array byte [ ].
     *
     * @param input the input
     * @return the byte [ ]
     * @throws IOException the io exception
     */
    public static byte[] toByteArray(final InputStream input) throws IOException {
        try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            copy(input, output, 1024);
            return output.toByteArray();
        }
    }

    /**
     * Write byte array to file.
     *
     * @param file the file
     * @param data the data
     */
    public static void writeByteArrayToFile(final File file, final byte[] data) {
        writeByteArrayToFile(file, data, false);
    }

    /**
     * Write byte array to file.
     *
     * @param file   the file
     * @param data   the data
     * @param append the append
     */
    public static void writeByteArrayToFile(final File file, final byte[] data, final boolean append) {

        try (OutputStream out = new FileOutputStream(file, append)) {
            out.write(data, 0, data.length);
        } catch (IOException e) {
            throw new SonsureException(e);
        }
    }

    /**
     * Copy long.
     *
     * @param input      the input
     * @param output     the output
     * @param bufferSize the buffer size
     * @return the long
     * @throws IOException the io exception
     */
    public static long copy(final InputStream input, final OutputStream output, int bufferSize)
            throws IOException {
        byte[] buffer = new byte[bufferSize];
        long count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Read lines list.
     *
     * @param input    the input
     * @param encoding the encoding
     * @return the list
     * @throws IOException the io exception
     */
    public static List<String> readLines(final InputStream input, final Charset encoding) throws IOException {
        final InputStreamReader reader = new InputStreamReader(input, encoding);
        return readLines(reader);
    }

    /**
     * Read lines list.
     *
     * @param input the input
     * @return the list
     * @throws IOException the io exception
     */
    public static List<String> readLines(final Reader input) throws IOException {
        final BufferedReader reader = toBufferedReader(input);
        final List<String> list = new ArrayList<>();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
    }

    /**
     * Returns the given reader if it is a {@link BufferedReader}, otherwise creates a BufferedReader from the given
     * reader.
     *
     * @param reader the reader to wrap or return (not null)
     * @return the given reader or a new {@link BufferedReader} for the given reader
     * @throws NullPointerException if the input parameter is null
     */
    public static BufferedReader toBufferedReader(final Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }
}
