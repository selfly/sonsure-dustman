package com.sonsure.dumper.resource;

import com.sonsure.dumper.common.parse.GenericTokenParser;
import com.sonsure.dumper.common.utility.GenericResource;
import com.sonsure.dumper.common.utils.EncryptUtils;
import com.sonsure.dumper.common.utils.FileIOUtils;
import com.sonsure.dumper.common.utils.StrUtils;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author selfly
 */
@Getter
public class MigrationResource {

    private static final String VERSION_DELIMITER = "__";
    private static final String GROUP_DELIMITER = "_";
    private static final String SUFFIX_DELIMITER = ".";

    private final GenericResource genericResource;

    private final String prefix;

    private final String version;

    private final String group;

    private final String description;

    private String checksum;

    private byte[] resourceBytes;

    private final Map<String, String> variables;

    public MigrationResource(GenericResource genericResource) {
        this.genericResource = genericResource;
        this.variables = new HashMap<>(8);
        String filename = genericResource.getFilename();
        this.prefix = filename.substring(0, 1);
        int versionIndex = filename.indexOf(VERSION_DELIMITER);
        this.version = filename.substring(1, versionIndex);

        int groupIndex = filename.indexOf(GROUP_DELIMITER, versionIndex + VERSION_DELIMITER.length());
        this.group = filename.substring(versionIndex + VERSION_DELIMITER.length(), groupIndex);

        int suffixIndex = filename.lastIndexOf(SUFFIX_DELIMITER);
        this.description = filename.substring(groupIndex + GROUP_DELIMITER.length(), suffixIndex);

        this.init(genericResource);
    }

    @SneakyThrows
    private void init(GenericResource rs) {
        try (InputStream is = rs.getInputStream()) {
            this.resourceBytes = FileIOUtils.toByteArray(is);
            String minify = StrUtils.minify(new String(this.resourceBytes));
            this.checksum = EncryptUtils.getMD5(minify);
        }
    }

    public void addVariable(String name, String value) {
        this.variables.put(name, value);
    }

    public String getResourceContent(Charset charset) {
        String text = new String(this.resourceBytes, charset);
        if (!this.variables.isEmpty()) {
            GenericTokenParser parser = new GenericTokenParser("${", "}", (content, tokenParser) -> variables.get(content));
            text = parser.parse(text);
        }
        return text;
    }

    public String getFilename() {
        return genericResource.getFilename();
    }
}
