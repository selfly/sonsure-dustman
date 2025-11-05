package com.sonsure.dustman.flyable;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author selfly
 */
@Getter
@Setter
public class FlyableHistory {

    private Long flyableHistoryId;
    private String migrationGroup;
    private String version;
    private String description;
    private String script;
    private String checksum;
    private Long executionTime;
    private LocalDateTime gmtInstalled;
    private Boolean success;
}
