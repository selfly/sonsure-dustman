# PageHandler使用及扩展

分页的处理，都是通过PageHandler来完成的。

这部分借鉴了[Mybatis-PageHelper](https://github.com/pagehelper/Mybatis-PageHelper)的思路还有部分的代码。

接口定义如下：

    public interface PageHandler {
    
        /**
         * 是否支持当前数据库的分页
         *
         * @param dialect
         * @return
         */
        boolean support(String dialect);
    
        /**
         * 根据查询语句获取count语句
         *
         * @param command 
         * @param dialect the dialect
         * @return count command
         */
        String getCountCommand(String command, String dialect);
    
        /**
         * 根据查询语句获取分页语句
         *
         * @param command    the command
         * @param pagination the pagination
         * @param dialect    the dialect
         * @return page command
         */
        String getPageCommand(String command, Pagination pagination, String dialect);
    }
    
因为关系型数据库的`count(*)`查询基本都是一样的，所以抽象类`AbstractPageHandler`对`getCountCommand`方法进行了实现，在扩展时只需要继承该抽象类即可。

下面以实现`postgresql`的分页为例，实现代码如下：

    public class PostgresqlPageHandler extends AbstractPageHandler {
    
        @Override
        public boolean support(String dialect) {
            return StringUtils.indexOfIgnoreCase(dialect, "postgresql") != -1;
        }
    
        @Override
        public String getPageCommand(String command, Pagination pagination, String dialect) {
            StringBuilder pageSql = new StringBuilder(200);
            pageSql.append(command);
            pageSql.append(" limit ");
            pageSql.append(pagination.getPageSize());
            pageSql.append(" offset ");
            pageSql.append(pagination.getBeginIndex());
            return pageSql.toString();
        }
    }
    
非常的简单，然后将它配置到`jdbcContext`中即可使用：

    JdbcContextImpl jdbcContext = new JdbcContextImpl();
    jdbcContext.setPageHandler(new PostgresqlPageHandler());
    
也可以像下面这样配置到`NegotiatingPageHandler`中，统一使用`NegotiatingPageHandler`：

    NegotiatingPageHandler negotiatingPageHandler = new NegotiatingPageHandler();
    negotiatingPageHandler.addPageHandler(new MysqlPageHandler());
    negotiatingPageHandler.addPageHandler(new OraclePageHandler());
    negotiatingPageHandler.addPageHandler(new PostgresqlPageHandler());
    negotiatingPageHandler.addPageHandler(new SqlServerPageHandler());
    negotiatingPageHandler.addPageHandler(new SqlitePageHandler());
    negotiatingPageHandler.addPageHandler(new H2PageHandler());
    
    jdbcContext.setPageHandler(negotiatingPageHandler);
    
*注意：以上的分页处理实现，默认已经内置在NegotiatingPageHandler中，可以直接拿来用或使用单独的分页实现。*