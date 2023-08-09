package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandContextBuilderContext;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.management.CommandField;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liyd
 */
public class OrderByCommandBuilderImpl extends AbstractCommandContextBuilder {

    private final Context orderByContext;

    public OrderByCommandBuilderImpl(Context orderByContext) {
        super(orderByContext);
        this.orderByContext = orderByContext;
    }

    public void addOrderByField(String... fields) {
        this.orderByContext.addOrderByField(fields);
    }

    public void asc() {
        this.orderByContext.setOrderByType("asc");
    }

    public void desc() {
        this.orderByContext.setOrderByType("desc");
    }

    @Override
    public CommandContext doBuild(JdbcEngineConfig jdbcEngineConfig) {

        CommandContext commandContext = this.createCommandContext();
        List<CommandField> orderByFields = this.orderByContext.getOrderByFields();
        if (orderByFields == null || orderByFields.isEmpty()) {
            return commandContext;
        }
        StringBuilder sb = new StringBuilder(" order by ");
        for (CommandField orderByField : orderByFields) {
            final String filedCommandName = this.getFiledCommandName(orderByField, jdbcEngineConfig);
            String aliasField = this.getTableAliasField(orderByField.getTableAlias(), filedCommandName);
            sb.append(aliasField).append(" ").append(orderByField.getFieldOperator()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);

        commandContext.setCommand(sb.toString());
        return commandContext;
    }

    public static class Context extends CommandContextBuilderContext {

        private final List<CommandField> orderByFields;

        public Context() {
            this.orderByFields = new ArrayList<>();
        }

        public void addOrderByField(String... fields) {
            final List<CommandField> orderByFields = this.getOrderByFields();
            for (String field : fields) {
                orderByFields.add(this.createCommandClassField(field, true, CommandField.Type.MANUAL_FIELD));
            }
        }

        public void setOrderByType(String type) {
            final List<CommandField> orderByFields = this.getOrderByFields();
            if (orderByFields.isEmpty()) {
                throw new SonsureJdbcException("请先指定需要排序的属性");
            }
            int size = orderByFields.size();
            for (int i = size - 1; i >= 0; i--) {
                CommandField commandField = orderByFields.get(i);
                if (StringUtils.isNotBlank(commandField.getFieldOperator())) {
                    //已经指定了，跳出
                    break;
                }
                commandField.setFieldOperator(type);
            }
        }

        public List<CommandField> getOrderByFields() {
            return orderByFields;
        }
    }

}
