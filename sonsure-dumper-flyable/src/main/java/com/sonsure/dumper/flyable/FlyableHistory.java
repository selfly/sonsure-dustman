package com.sonsure.dumper.flyable;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author selfly
 */
@Getter
@Setter
public class FlyableHistory {

    public static final String TYPE_SQL = "SQL";

    private Long flyableHistoryId;
    private String migrationGroup;
    private String version;
    private String type;
    private String description;
    private String script;
    private String checksum;
    private Long executionTime;
    private LocalDateTime gmtInstalled;
    private Boolean success;
}
