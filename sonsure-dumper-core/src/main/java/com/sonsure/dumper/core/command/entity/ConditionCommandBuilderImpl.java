package com.sonsure.dumper.core.command.entity;

import com.sonsure.dumper.core.command.CommandContext;
import com.sonsure.dumper.core.command.CommandContextBuilderContext;
import com.sonsure.dumper.core.command.CommandParameter;
import com.sonsure.dumper.core.config.JdbcEngineConfig;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.management.CommandField;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liyd
 */
public class ConditionCommandBuilderImpl extends AbstractCommandContextBuilder {

    private final Context conditionContext;

    public ConditionCommandBuilderImpl(Context conditionContext) {
        super(conditionContext);
        this.conditionContext = conditionContext;
        this.conditionContext.setSubBuilderContext(true);
    }

    public void addWhereField(String logicalOperator, String name, String fieldOperator, Object value, CommandField.Type type) {
        this.conditionContext.addWhereField(logicalOperator, name, fieldOperator, value, type);
    }

    public void addWhereFields(List<CommandField> commandFields) {
        this.conditionContext.addWhereFields(commandFields);
    }

    @Override
    public CommandContext doBuild(JdbcEngineConfig jdbcEngineConfig) {
        final boolean isNamedParameter = this.conditionContext.isNamedParameter();
        List<CommandField> whereFields = this.conditionContext.getWhereFields();
        if (whereFields.isEmpty()) {
            return null;
        }

        StringBuilder whereCommand = new StringBuilder(" ");
        List<CommandParameter> commandParameters = new ArrayList<>();
        for (CommandField commandField : whereFields) {

            //在前面处理，有单独where or and 的情况
            if (StringUtils.isNotBlank(commandField.getLogicalOperator())) {
                //没有where不管如何and or等操作符都换成where
                if (whereCommand.length() < 5) {
                    whereCommand.append("where ");
                } else {
                    whereCommand.append(commandField.getLogicalOperator()).append(" ");
                }
            }
            //只有where or and 的情况
            if (StringUtils.isBlank(commandField.getFieldName())) {
                continue;
            }
            final String filedCommandName = this.getFiledCommandName(commandField, jdbcEngineConfig);
            if (commandField.getType() == CommandField.Type.WHERE_APPEND) {
                whereCommand.append(filedCommandName);
                if (commandField.getValue() != null) {
                    if (commandField.getValue() instanceof Object[]) {
                        Object[] values = (Object[]) commandField.getValue();
                        for (int i = 0; i < values.length; i++) {
                            commandParameters.add(new CommandParameter(commandField.getFieldName() + i, values[i]));
                        }
                    } else if (commandField.getValue() instanceof Map) {
                        final Map<String, Object> valueMap = (Map<String, Object>) commandField.getValue();
                        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                            commandParameters.add(new CommandParameter(entry.getKey(), entry.getValue()));
                        }
                    } else {
                        throw new SonsureJdbcException("不支持的参数类型");
                    }
                }
            } else if (commandField.getValue() == null) {
                String operator = StringUtils.isBlank(commandField.getFieldOperator()) ? "is" : commandField.getFieldOperator();
                whereCommand.append(this.getTableAliasField(commandField.getTableAlias(), filedCommandName))
                        .append(" ")
                        .append(operator)
                        .append(" null ");
            } else if (commandField.getValue() instanceof Object[]) {
                this.processArrayArgs(commandField, whereCommand, commandParameters, jdbcEngineConfig);
            } else {
                whereCommand.append(this.getTableAliasField(commandField.getTableAlias(), filedCommandName))
                        .append(" ")
                        .append(commandField.getFieldOperator())
                        .append(" ");

                //native 不传参方式
                if (commandField.isNative()) {
                    whereCommand.append(commandField.isFieldOperatorNeedBracket() ? String.format(" ( %s ) ", commandField.getValue()) : String.format(" %s ", commandField.getValue()));
                } else {
                    final String placeholder = this.createParameterPlaceholder(commandField.getFieldName(), isNamedParameter);
                    whereCommand.append(commandField.isFieldOperatorNeedBracket() ? String.format(" ( %s ) ", placeholder) : String.format(" %s ", placeholder));
                    commandParameters.add(new CommandParameter(commandField.getFieldName(), commandField.getValue()));
                }
            }
        }
        //只有where的情况
        if (whereCommand.length() < 8) {
            whereCommand.delete(0, whereCommand.length());
        }
        CommandContext commandContext = new CommandContext();
        commandContext.setCommand(whereCommand.toString());
        commandContext.addCommandParameters(commandParameters);
        return commandContext;

    }


    /**
     * 处理数组参数
     */
    protected void processArrayArgs(CommandField commandField, StringBuilder whereCommand, List<CommandParameter> parameters, JdbcEngineConfig jdbcEngineConfig) {
        final String filedCommandName = this.getFiledCommandName(commandField, jdbcEngineConfig);
        String aliasField = this.getTableAliasField(commandField.getTableAlias(), filedCommandName);
        Object[] args = (Object[]) commandField.getValue();
        if (commandField.isFieldOperatorNeedBracket()) {
            whereCommand.append(aliasField).append(" ").append(commandField.getFieldOperator()).append(" (");
            for (int i = 0; i < args.length; i++) {
                if (commandField.isNative()) {
                    whereCommand.append(args[i]);
                } else {
                    final String name = commandField.getFieldName() + i;
                    String placeholder = this.createParameterPlaceholder(name, this.conditionContext.isNamedParameter());
                    whereCommand.append(placeholder);
                    parameters.add(new CommandParameter(name, args[i]));
                }
                if (i != args.length - 1) {
                    whereCommand.append(",");
                }
            }
            whereCommand.append(") ");
        } else {
            if (ArrayUtils.getLength(args) > 1) {
                whereCommand.append(" (");
            }
            for (int i = 0; i < args.length; i++) {
                whereCommand.append(aliasField).append(" ").append(commandField.getFieldOperator());
                if (commandField.isNative()) {
                    whereCommand.append(String.format(" %s ", args[i]));
                } else {
                    final String name = commandField.getFieldName() + i;
                    String placeholder = this.createParameterPlaceholder(name, this.conditionContext.isNamedParameter());
                    whereCommand.append(" ").append(placeholder).append(" ");
                    parameters.add(new CommandParameter(name, args[i]));
                }
                if (i != args.length - 1) {
                    whereCommand.append(" or ");
                }
            }
            if (ArrayUtils.getLength(args) > 1) {
                whereCommand.append(") ");
            }
        }
    }

    public static class Context extends CommandContextBuilderContext {

        private final List<CommandField> whereFields;

        public Context() {
            this.whereFields = new ArrayList<>();
        }

        public void addWhereField(String logicalOperator, String name, String fieldOperator, Object value, CommandField.Type type) {
            boolean analyseTableAlias = CommandField.Type.isAnalyseTableAlias(type);
            CommandField commandField = this.createCommandClassField(name, analyseTableAlias, CommandField.Type.MANUAL_FIELD);
            commandField.setLogicalOperator(logicalOperator);
            commandField.setFieldOperator(fieldOperator);
            commandField.setValue(value);
            commandField.setType(type);
            this.getWhereFields().add(commandField);
        }

        public void addWhereFields(List<CommandField> commandFields) {
            this.getWhereFields().addAll(commandFields);
        }

//        public void addWhereField(String logicalOperator, String name, String fieldOperator, Object value) {
//            this.addWhereField(logicalOperator, name, fieldOperator, value, null);
//        }


        public List<CommandField> getWhereFields() {
            return whereFields;
        }
    }

}
