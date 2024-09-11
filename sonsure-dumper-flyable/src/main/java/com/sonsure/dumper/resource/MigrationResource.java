package com.sonsure.dumper.resource;

import com.sonsure.dumper.common.parse.GenericTokenParser;
import com.sonsure.dumper.common.spring.Resource;
import com.sonsure.dumper.common.utils.EncryptUtils;
import com.sonsure.dumper.common.utils.FileIOUtils;
import com.sonsure.dumper.common.utils.TextUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

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

    private final Resource resource;

    private final String prefix;

    private final String version;

    private final String group;

    private final String description;

    private String checksum;

    private byte[] resourceBytes;

    private final Map<String, String> variables;

    public MigrationResource(Resource resource) {
        this.resource = resource;
        this.variables = new HashMap<>(8);
        String filename = resource.getFilename();
        this.prefix = StringUtils.substring(filename, 0, 1);
        int versionIndex = StringUtils.indexOf(filename, VERSION_DELIMITER);
        this.version = StringUtils.substring(filename, 1, versionIndex);

        int groupIndex = StringUtils.indexOf(filename, GROUP_DELIMITER, versionIndex + VERSION_DELIMITER.length());
        this.group = StringUtils.substring(filename, versionIndex + VERSION_DELIMITER.length(), groupIndex);

        int suffixIndex = StringUtils.lastIndexOf(filename, SUFFIX_DELIMITER);
        this.description = StringUtils.substring(filename, groupIndex + GROUP_DELIMITER.length(), suffixIndex);

        this.init(resource);
    }

    @SneakyThrows
    private void init(Resource rs) {
        try (InputStream is = rs.getInputStream()) {
            this.resourceBytes = FileIOUtils.toByteArray(is);
            String minify = TextUtils.minify(new String(this.resourceBytes));
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
        return resource.getFilename();
    }
}
