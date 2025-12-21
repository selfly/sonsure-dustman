package com.sonsure.dustman.flyable;

import com.sonsure.dustman.jdbc.annotation.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author selfly
 */
@Getter
@Setter
public class FlyableHistory {

    @Id
    private Long flyableId;
    private String migrationGroup;
    private String version;
    private String description;
    private String script;
    private String checksum;
    private Long executionTime;
    private LocalDateTime gmtInstalled;
    private Boolean success;
}
