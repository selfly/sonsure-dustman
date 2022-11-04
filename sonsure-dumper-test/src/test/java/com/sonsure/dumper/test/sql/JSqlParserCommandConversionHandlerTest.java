/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.sql;

import com.sonsure.dumper.core.command.sql.CommandConversionHandler;
import com.sonsure.dumper.core.command.sql.JSqlParserCommandConversionHandler;
import com.sonsure.dumper.core.exception.SonsureJdbcException;
import com.sonsure.dumper.core.mapping.DefaultMappingHandler;
import com.sonsure.dumper.test.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class JSqlParserCommandConversionHandlerTest {


    private CommandConversionHandler commandConversionHandler;

    public JSqlParserCommandConversionHandlerTest() {
        DefaultMappingHandler mappingHandler = new DefaultMappingHandler();
        Map<String, Class<?>> customClassMapping = new HashMap<>();
        customClassMapping.put("RelTag", RelTag.class);
        customClassMapping.put("Tag", Tag.class);
        customClassMapping.put("User", User.class);
        customClassMapping.put("UserInfo", UserInfo.class);
        customClassMapping.put("Content", Content.class);
        customClassMapping.put("Series", Series.class);
        customClassMapping.put("RelSeries", RelSeries.class);
        mappingHandler.setCustomClassMapping(customClassMapping);
        commandConversionHandler = new JSqlParserCommandConversionHandler(mappingHandler);
    }

    @Test
    public void commandToSql1() throws Exception {
        String command = "select t1.*,t2.contentNum,t2.contentType from Tag t1 inner join (select count(*) as contentNum,rt.tagId,rt.contentType from RelTag rt where rt.contentType = ? group by rt.tagId order by contentNum desc limit 0,?) t2 on t1.tagId = t2.tagId";
        String sql = commandConversionHandler.convert(command, null);
        String result = "SELECT t1.*, t2.contentNum, t2.CONTENT_TYPE FROM TAG t1 INNER JOIN (SELECT count(*) AS contentNum, rt.TAG_ID, rt.CONTENT_TYPE FROM REL_TAG rt WHERE rt.CONTENT_TYPE = ? GROUP BY rt.TAG_ID ORDER BY contentNum DESC LIMIT 0, ?) t2 ON t1.TAG_ID = t2.TAG_ID";
        System.out.println(sql.toLowerCase());
        System.out.println(result.toLowerCase());
        Assert.assertEquals(result.toLowerCase(), sql.toLowerCase());

    }

    @Test
    public void commandToSql2() throws Exception {
        String command2 = "select t1.*,count(*) as contentNum from User t1 inner join Content t2 on t1.userId = t2.userId and t2.gmtCreate > ? and t2.gmtCreate < ? and t2.status in ( ?, ?, ?) group by t2.userId order by contentNum desc limit 0,?";
        String sql2 = commandConversionHandler.convert(command2, null);
        String result2 = "select t1.*, count(*) as contentnum from user t1 inner join content t2 on t1.user_id = t2.user_id and t2.gmt_create > ? and t2.gmt_create < ? and t2.status in (?, ?, ?) group by t2.user_id order by contentnum desc limit 0, ?";
        Assert.assertEquals(sql2.toLowerCase(), result2);
        System.out.println(sql2.toLowerCase());
    }

    @Test
    public void commandToSql3() throws Exception {
        String command3 = "select loginName,password from UserInfo where userInfoId = ?";
        String sql3 = commandConversionHandler.convert(command3, null);
        String result3 = "select login_name, password from user_info where user_info_id = ?";
        Assert.assertEquals(sql3.toLowerCase(), result3);
        System.out.println(sql3.toLowerCase());
    }

    @Test
    public void commandToSql4() throws Exception {
        String command4 = "select t1.*,t2.loginName,t2.userAge from Tag t1,UserInfo t2";
        String sql4 = commandConversionHandler.convert(command4, null);
        String result4 = "select t1.*, t2.login_name, t2.user_age from tag t1, user_info t2";
        Assert.assertEquals(sql4.toLowerCase(), result4);
        System.out.println(sql4.toLowerCase());
    }

    @Test
    public void commandToSql5() {

        String command5 = "Insert into Tag(tagName,tagType) select loginName,userAge from UserInfo";
        String sql5 = commandConversionHandler.convert(command5, null);
        String result5 = "insert into tag (tag_name, tag_type) select login_name, user_age from user_info";
        System.out.println(sql5.toLowerCase());
        System.out.println(result5.toLowerCase());
        Assert.assertEquals(sql5.toLowerCase(), result5);

    }

    @Test
    public void commandToSql6() throws Exception {
        String command6 = "update UserInfo set loginName = ?,password = ? where userInfoId = ?";
        String sql6 = commandConversionHandler.convert(command6, null);
        String result6 = "update user_info set login_name = ?, password = ? where user_info_id = ?";
        Assert.assertEquals(sql6.toLowerCase(), result6);
        System.out.println(sql6.toLowerCase());
    }

    @Test
    public void commandToSql7() throws Exception {
        String command7 = "delete from UserInfo where loginName = ? and password = ? and userInfoId = ?";
        String sql7 = commandConversionHandler.convert(command7, null);
        String result7 = "delete from user_info where login_name = ? and password = ? and user_info_id = ?";
        Assert.assertEquals(sql7.toLowerCase(), result7);
        System.out.println(sql7.toLowerCase());
    }

    @Test
    public void commandToSql8() {
        String command = "select t1.* from Series t1 inner join RelSeries t2 inner JOIN Content t3 on t1.seriesId = t2.seriesId and t2.contentId = t3.contentId group by t1.seriesId order by sum(t3.clickCount) desc";
        String sql = commandConversionHandler.convert(command, null);
        String result = "select t1.* from series t1 inner join rel_series t2 inner join content t3 on t1.series_id = t2.series_id and t2.content_id = t3.content_id group by t1.series_id order by sum(t3.click_count) desc";
        Assert.assertEquals(sql.toLowerCase(), result);
        System.out.println(sql.toLowerCase());
        System.out.println(result);

    }

    @Test
    public void commandToSql9() {
        String command = "select username,password from User where parentId is null";
        String sql = commandConversionHandler.convert(command, null);
        String result = "SELECT USERNAME, PASSWORD FROM USER WHERE parentId IS NULL";
        Assert.assertEquals(sql.toLowerCase(), result.toLowerCase());
    }

    @Test
    public void commandToSql10() {
        String command = "select t.* from UserInfo t where t.userInfoId = :userInfoId and userAge = :userAge and password = ?";
        String sql = commandConversionHandler.convert(command, null);
        String result = "SELECT t.* FROM USER_INFO t WHERE t.USER_INFO_ID = :userInfoId AND userAge = :userAge AND password = ?";
        Assert.assertEquals(sql.toLowerCase(), result.toLowerCase());
    }

    @Test
    public void commandToSql11() {
        String command = "insert into UserInfo (username,testUserId) values (?,`{{SEQ_TEST_USER.NEXTVAL}}`)";
        String sql = commandConversionHandler.convert(command, null);
        String result = "INSERT INTO USER_INFO (username, testUserId) VALUES (?, SEQ_TEST_USER.NEXTVAL)";
        Assert.assertEquals(sql.toLowerCase(), result.toLowerCase());
    }

    @Test
    public void commandToSql12() {
        String command = "select orderNumber,resourceUrl,resourceIcon,sysResourceId,resourceName,routingUrl,parentId,resourceType from UserInfo where user_info_id in ( select 1 from RelTag) ";
        String sql = commandConversionHandler.convert(command, null);
        String result = "SELECT orderNumber, resourceUrl, resourceIcon, sysResourceId, resourceName, routingUrl, parentId, resourceType FROM USER_INFO WHERE user_info_id IN (SELECT 1 FROM REL_TAG)";
        Assert.assertEquals(sql.toLowerCase(), result.toLowerCase());
    }

    @Test
    public void commandToSql13() {
        String command = "update Content set commentCount = commentCount+1 where contentId is null";
        String sql = commandConversionHandler.convert(command, null);
        String result = "UPDATE CONTENT SET COMMENT_COUNT = COMMENT_COUNT + 1 WHERE CONTENT_ID IS NULL";
        Assert.assertEquals(sql.toLowerCase(), result.toLowerCase());
    }

    @Test
    public void commandToSql14() throws Exception {
        try {
            String command3 = "select loginName,password from UnUserInfo where userInfoId = ?";
            String sql3 = commandConversionHandler.convert(command3, null);
        } catch (SonsureJdbcException e) {
            Assert.assertEquals(e.getCause().getMessage(), "没有找到对应的class:UnUserInfo");
        }
    }

    @Test
    public void commandToSql15() throws Exception {
        DefaultMappingHandler mh = new DefaultMappingHandler();
        mh.setFailOnMissingClass(false);
        JSqlParserCommandConversionHandler cch = new JSqlParserCommandConversionHandler(mh);
        String command3 = "select loginName, password from UnUserInfo where userInfoId = ?";
        String sql3 = cch.convert(command3, null);
        Assert.assertEquals(command3.toLowerCase(), sql3.toLowerCase());
    }

    @Test
    public void commandToSql16() throws Exception {
        DefaultMappingHandler mh = new DefaultMappingHandler();
        mh.setFailOnMissingClass(false);
        JSqlParserCommandConversionHandler cch = new JSqlParserCommandConversionHandler(mh);
        String command3 = "SELECT * FROM articles WHERE MATCH (tags) AGAINST ('哈哈' IN BOOLEAN MODE)";
        String sql3 = cch.convert(command3, null);
        Assert.assertEquals(command3.toLowerCase(), sql3.toLowerCase());
    }


}
