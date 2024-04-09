package com.sonsure.dumper.core.mapping;

public interface TablePrefixSupportHandler {

    /**
     * Gets table prefix.
     *
     * @param classPackage the class package
     * @return the table prefix
     */
    String getTablePrefix(String classPackage);
}
