# 执行sql脚本

组件提供了执行sql脚本的接口，用来执行初始化的建表语句等。

其也是通过`NativeExecutor`来完成的。示例代码：

    String script = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("script.sql"));
    byte[] bytes = FileIOUtils.toByteArray(resourceAsStream);
    jdbcDao.nativeExecutor()
            .command(script)
            .forceNative()
            .executeScript();

具体调用可参考测试用例中的代码。





