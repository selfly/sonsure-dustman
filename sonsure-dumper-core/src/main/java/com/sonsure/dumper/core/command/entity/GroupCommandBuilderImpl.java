//package com.sonsure.dumper.core.command.entity;
//
//import com.sonsure.dumper.core.command.AbstractCommonCommandDetailsBuilder;
//import com.sonsure.dumper.core.command.CommandContextBuilderContext;
//import com.sonsure.dumper.core.command.CommandDetails;
//import com.sonsure.dumper.core.config.JdbcEngineConfig;
//import com.sonsure.dumper.core.management.CommandField;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author liyd
// */
//public class GroupCommandBuilderImpl extends AbstractCommonCommandDetailsBuilder {
//
//    private final Context groupContext;
//
//    public GroupCommandBuilderImpl(Context groupContext) {
//        super(groupContext);
//        this.groupContext = groupContext;
//        this.groupContext.setSubBuilderContext(true);
//    }
//
//    public void addGroupByField(String... fields) {
//        for (String field : fields) {
//            this.groupContext.addGroupByField(this.createCommandClassField(field, true, CommandField.Type.MANUAL_FIELD));
//        }
//    }
//
//    @Override
//    public CommandDetails doBuild(JdbcEngineConfig jdbcEngineConfig) {
//        List<CommandField> groupByFields = this.groupContext.getGroupByFields();
//        if (groupByFields.isEmpty()) {
//            return null;
//        }
//        StringBuilder sb = new StringBuilder(" group by ");
//        for (CommandField groupByField : groupByFields) {
//            final String filedCommandName = this.getFiledCommandName(groupByField, jdbcEngineConfig);
//            String aliasField = this.getTableAliasField(groupByField.getTableAlias(), filedCommandName);
//            sb.append(aliasField).append(",");
//        }
//        sb.deleteCharAt(sb.length() - 1);
//        CommandDetails commandDetails = this.createCommandContext();
//        commandDetails.setCommand(sb.toString());
//        return commandDetails;
//    }
//
//    public static class Context extends CommandContextBuilderContext {
//
//        private final List<CommandField> groupByFields;
//
//        public Context() {
//            this.groupByFields = new ArrayList<>();
//        }
//
//        public void addGroupByField(CommandField commandField) {
//            getGroupByFields().add(commandField);
//        }
//
//        public List<CommandField> getGroupByFields() {
//            return groupByFields;
//        }
//    }
//}
