# Jdbc查询自定义类型转换器

## 说明

一般情况下不需要用到，Spring自带的RowMapper就能够满足大部分场景。

**诞生背景：**

应用只需一个轻量级或演示环境，数据库在切换到`SQLite`时发现查询不能正常处理代码中的`LocalDateTime`类型(数据库为datetime类型),报：

    org.springframework.beans.ConversionNotSupportedException: Failed to convert property value of type 'java.sql.Timestamp' to required type 'java.time.LocalDateTime' for property 'gmtLastLogin'; nested exception is java.lang.IllegalStateException: Cannot convert value of type 'java.sql.Timestamp' to required type 'java.time.LocalDateTime' for property 'gmtLastLogin': no matching editors or conversion strategy found

很明显类型不一致导致的问题，这方面`SQLite`和其它数据库有点不大一样，当然调整一下代码是很容易解决的，但想着一套代码能够在不同的数据库间平滑切换，这不是上上之选，所以引入了自定义的类型转换。

__PS:该类型转换只对查询结果为Bean对象时生效，如果不是Bean对象直接Object转不转都一样。__

## 接口说明

包含2个方法，即Database->Java,Java->Database的相互转换。

    /**
    * @author selfly
    */
    public interface JdbcTypeConverter {

    /**
        * Db 2 java type object.
        *
        * @param dialect      the dialect
        * @param requiredType the required type
        * @param value        the value
        * @return the object
        */
        default Object db2JavaType(String dialect, Class<?> requiredType, Object value) {
        return value;
        }

    /**
        * Java 2 db type object.
        *
        * @param dialect the dialect
        * @param value   the value
        * @return the object
        */
        default Object java2DbType(String dialect, Object value) {
        return value;
        }

    }

## 实现

以下是`SQLite`对于使用`LocalDateTime`的兼容实现，供参考。

    /**
     * @author selfly
     */
    public class SqliteCompatibleLocalDateTimeConverter implements JdbcTypeConverter {

        private static final String LOCAL_DATE = "LocalDate";
        private static final String LOCAL_TIME = "LocalTime";
        private static final String LOCAL_DATE_TIME = "LocalDateTime";

        private static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
        private static final String PATTERN_DATE = "yyyy-MM-dd";
        private static final String PATTERN_TIME = "HH:mm:ss";

        private final Set<String> types = new HashSet<>();

        public SqliteCompatibleLocalDateTimeConverter() {
            types.add(LOCAL_DATE);
            types.add(LOCAL_TIME);
            types.add(LOCAL_DATE_TIME);
        }

        @Override
        public Object db2JavaType(String dialect, Class<?> requiredType, Object value) {
            if (!DatabaseDialect.SQLITE.belong(dialect)) {
                return value;
            }
            if (types.contains(requiredType.getSimpleName()) && value instanceof Timestamp) {
                final LocalDateTime localDateTime = ((Timestamp) value).toLocalDateTime();
                if (LOCAL_DATE.equals(requiredType.getSimpleName())) {
                    return localDateTime.toLocalDate();
                } else if (LOCAL_TIME.equals(requiredType.getSimpleName())) {
                    return localDateTime.toLocalTime();
                } else {
                    return localDateTime;
                }
            }
            return value;
        }

        @Override
        public Object java2DbType(String dialect, Object value) {
            if (!DatabaseDialect.SQLITE.belong(dialect)) {
                return value;
            }
            if (value instanceof LocalDateTime) {
                return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern(PATTERN_DATETIME));
            } else if (value instanceof LocalDate) {
                return ((LocalDate) value).format(DateTimeFormatter.ofPattern(PATTERN_DATE));
            } else if (value instanceof LocalTime) {
                return ((LocalTime) value).format(DateTimeFormatter.ofPattern(PATTERN_TIME));
            } else {
                return value;
            }
        }
    }

默认配置已添加该`JdbcTypeConverter`，当数据库为`SQLite`时会自动生效，如果是手动声明`JdbcEngine`，需要显示添加：

    JdbcTemplateEngineConfigImpl jdbcTemplateEngineConfig = new JdbcTemplateEngineConfigImpl();
    jdbcTemplateEngineConfig.setDataSource(getDataSource());
    final List<JdbcTypeConverter> jdbcTypeConverters = Collections.singletonList(new SqliteCompatibleLocalDateTimeConverter());
    jdbcTemplateEngineConfig.setJdbcTypeConverters(jdbcTypeConverters);
    defaultJdbcEngine = new JdbcEngineImpl(jdbcTemplateEngineConfig);