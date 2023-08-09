package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandContextBuilderContext;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.management.CommandField;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liyd
 */
public class GroupCommandBuilderImpl extends AbstractCommandContextBuilder {

    private final Context groupContext;

    public GroupCommandBuilderImpl(Context groupContext) {
        super(groupContext);
        this.groupContext = groupContext;
        this.groupContext.setSubBuilderContext(true);
    }

    public void addGroupByField(String... fields) {
        this.groupContext.addGroupByField(fields);
    }

    @Override
    public CommandContext doBuild(JdbcEngineConfig jdbcEngineConfig) {
        List<CommandField> groupByFields = this.groupContext.getGroupByFields();
        if (groupByFields == null || groupByFields.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder(" group by ");
        for (CommandField groupByField : groupByFields) {
            final String filedCommandName = this.getFiledCommandName(groupByField, jdbcEngineConfig);
            String aliasField = this.getTableAliasField(groupByField.getTableAlias(), filedCommandName);
            sb.append(aliasField).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        CommandContext commandContext = this.createCommandContext();
        commandContext.setCommand(sb.toString());
        return commandContext;
    }

    public static class Context extends CommandContextBuilderContext {

        private final List<CommandField> groupByFields;

        public Context() {
            this.groupByFields = new ArrayList<>();
        }

        public void addGroupByField(String... fields) {
            final List<CommandField> groupByFields = this.getGroupByFields();
            for (String field : fields) {
                groupByFields.add(this.createCommandClassField(field, true, CommandField.Type.MANUAL_FIELD));
            }
        }

        public List<CommandField> getGroupByFields() {
            return groupByFields;
        }
    }
}
