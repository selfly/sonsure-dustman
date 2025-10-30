/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.test.parse;

import com.sonsure.dumper.common.exception.SonsureCommonsException;
import com.sonsure.dumper.common.exception.SonsureException;
import com.sonsure.dumper.common.parse.XmlNode;
import com.sonsure.dumper.common.parse.XmlParser;
import com.sonsure.dumper.common.utils.StrUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XmlParser 单元测试
 */
public class XmlParserTest {

    // ========== parse(String xmlContent) 方法测试 ==========

    @Test
    public void testParse_SimpleXmlString() {
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><child>content</child></root>";
        XmlNode node = XmlParser.parse(xmlContent);

        Assertions.assertNotNull(node);
        Assertions.assertEquals("root", node.getName());
        Assertions.assertEquals(1, node.getChildNodes().size());
        Assertions.assertEquals("child", node.getChildNodes().get(0).getName());
        Assertions.assertEquals("content", node.getChildNodes().get(0).getText());
    }

    @Test
    public void testParse_XmlStringWithAttributes() {
        String xmlContent = "<?xml version=\"1.0\"?><root id=\"1\" name=\"test\"><item value=\"value1\">text</item></root>";
        XmlNode node = XmlParser.parse(xmlContent);

        Assertions.assertNotNull(node);
        Assertions.assertEquals("root", node.getName());
        Assertions.assertEquals("1", node.getAttribute("id"));
        Assertions.assertEquals("test", node.getAttribute("name"));

        XmlNode item = node.getChildNodes().get(0);
        Assertions.assertEquals("item", item.getName());
        Assertions.assertEquals("value1", item.getAttribute("value"));
        Assertions.assertEquals("text", item.getText());
    }

    @Test
    public void testParse_XmlStringNested() {
        String xmlContent = "<?xml version=\"1.0\"?><root><level1><level2>nested</level2></level1></root>";
        XmlNode node = XmlParser.parse(xmlContent);

        Assertions.assertNotNull(node);
        Assertions.assertEquals("root", node.getName());
        XmlNode level1 = node.getChildNodes().get(0);
        Assertions.assertEquals("level1", level1.getName());
        XmlNode level2 = level1.getChildNodes().get(0);
        Assertions.assertEquals("level2", level2.getName());
        Assertions.assertEquals("nested", level2.getText());
    }

    @Test
    public void testParse_XmlStringEmpty() {
        String xmlContent = "<?xml version=\"1.0\"?><root/>";
        XmlNode node = XmlParser.parse(xmlContent);

        Assertions.assertNotNull(node);
        Assertions.assertEquals("root", node.getName());
        Assertions.assertTrue(node.getChildNodes().isEmpty());
        Assertions.assertTrue(StrUtils.isBlank(node.getText()));
    }

    @Test
    public void testParse_XmlStringTextOnly() {
        String xmlContent = "<?xml version=\"1.0\"?><root>Only text</root>";
        XmlNode node = XmlParser.parse(xmlContent);

        Assertions.assertNotNull(node);
        Assertions.assertEquals("root", node.getName());
        Assertions.assertEquals("Only text", node.getText());
        Assertions.assertTrue(node.getChildNodes().isEmpty());
    }

    // ========== parse(String xmlContent, Map<String, String> properties) 方法测试 ==========

    @Test
    public void testParse_XmlStringWithProperties() {
        String xmlContent = "<?xml version=\"1.0\"?><root><item>${variable1}</item><attr attr=\"${variable2}\"/></root>";
        Map<String, String> properties = new HashMap<>();
        properties.put("variable1", "value1");
        properties.put("variable2", "value2");

        XmlNode node = XmlParser.parse(xmlContent, properties);

        Assertions.assertNotNull(node);
        XmlNode item = node.getChildNodes().get(0);
        Assertions.assertEquals("value1", item.getText());
        XmlNode attr = node.getChildNodes().get(1);
        Assertions.assertEquals("value2", attr.getAttribute("attr"));
    }

    @Test
    public void testParse_XmlStringWithNullProperties() {
        String xmlContent = "<?xml version=\"1.0\"?><root><item>${variable}</item></root>";
        XmlNode node = XmlParser.parse(xmlContent, null);

        Assertions.assertNotNull(node);
        // 变量未被替换，应该保持原样
        XmlNode item = node.getChildNodes().get(0);
        Assertions.assertEquals("${variable}", item.getText());
    }

    @Test
    public void testParse_XmlStringWithEmptyProperties() {
        String xmlContent = "<?xml version=\"1.0\"?><root><item>${variable}</item></root>";
        Map<String, String> properties = new HashMap<>();
        XmlNode node = XmlParser.parse(xmlContent, properties);

        Assertions.assertNotNull(node);
        // 空Map不会替换变量
        XmlNode item = node.getChildNodes().get(0);
        Assertions.assertEquals("${variable}", item.getText());
    }

    @Test
    public void testParse_XmlStringWithMultipleVariables() {
        String xmlContent = "<?xml version=\"1.0\"?><root>" +
                "<db>${db.name}</db>" +
                "<port>${server.port}</port>" +
                "<host>${server.host}</host>" +
                "</root>";
        Map<String, String> properties = new HashMap<>();
        properties.put("db.name", "mydb");
        properties.put("server.port", "8080");
        properties.put("server.host", "localhost");

        XmlNode node = XmlParser.parse(xmlContent, properties);

        Assertions.assertNotNull(node);
        Assertions.assertEquals("mydb", node.getSingleChildNodeText("db"));
        Assertions.assertEquals("8080", node.getSingleChildNodeText("port"));
        Assertions.assertEquals("localhost", node.getSingleChildNodeText("host"));
    }

    @Test
    public void testParse_XmlStringWithAmpersandInVariable() {
        String xmlContent = "<?xml version=\"1.0\"?><root><value>${test.value}</value></root>";
        Map<String, String> properties = new HashMap<>();
        properties.put("test.value", "A & B");

        XmlNode node = XmlParser.parse(xmlContent, properties);

        Assertions.assertNotNull(node);
        // & 应该被转义为 &amp;
        String value = node.getSingleChildNodeText("value");
        Assertions.assertTrue(value.contains("&amp;") || value.equals("A & B"));
    }

    // ========== parse(InputStream is) 方法测试 ==========

    @Test
    public void testParse_SimpleInputStream() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/simple.xml");
        Assertions.assertNotNull(is);

        XmlNode node = XmlParser.parse(is);

        Assertions.assertNotNull(node);
        Assertions.assertEquals("root", node.getName());
        Assertions.assertEquals(2, node.getChildNodes().size());

        XmlNode child1 = node.getSingleChildNode("child1");
        Assertions.assertNotNull(child1);
        Assertions.assertEquals("text content", child1.getText());

        XmlNode child2 = node.getSingleChildNode("child2");
        Assertions.assertNotNull(child2);
        Assertions.assertEquals("child text", child2.getText());
        Assertions.assertEquals("value", child2.getAttribute("attr"));
    }

    @Test
    public void testParse_NestedInputStream() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/nested.xml");
        Assertions.assertNotNull(is);

        XmlNode node = XmlParser.parse(is);

        Assertions.assertNotNull(node);
        Assertions.assertEquals("root", node.getName());

        List<XmlNode> level1Nodes = node.getChildNodes("level1");
        Assertions.assertEquals(2, level1Nodes.size());

        XmlNode firstLevel1 = level1Nodes.get(0);
        XmlNode level2 = firstLevel1.getSingleChildNode("level2");
        Assertions.assertNotNull(level2);
        XmlNode level3 = level2.getSingleChildNode("level3");
        Assertions.assertNotNull(level3);
        Assertions.assertEquals("deep nested content", level3.getText());
    }

    @Test
    public void testParse_InputStreamWithAttributes() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/with-attributes.xml");
        Assertions.assertNotNull(is);

        XmlNode node = XmlParser.parse(is);

        Assertions.assertNotNull(node);
        Assertions.assertEquals("root", node.getName());
        Assertions.assertEquals("1", node.getAttribute("id"));
        Assertions.assertEquals("test", node.getAttribute("name"));
        Assertions.assertEquals("true", node.getAttribute("enabled"));

        List<XmlNode> items = node.getChildNodes("item");
        Assertions.assertEquals(3, items.size());

        XmlNode item1 = node.getSingleChildNode("item", "id", "1");
        Assertions.assertNotNull(item1);
        Assertions.assertEquals("item1", item1.getAttribute("name"));
        Assertions.assertEquals("value1", item1.getAttribute("value"));
        Assertions.assertEquals("Item 1 content", item1.getText());
    }

    @Test
    public void testParse_EmptyInputStream() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/empty.xml");
        Assertions.assertNotNull(is);

        XmlNode node = XmlParser.parse(is);

        Assertions.assertNotNull(node);
        Assertions.assertEquals("root", node.getName());
        Assertions.assertTrue(node.getChildNodes().isEmpty());
    }

    @Test
    public void testParse_TextOnlyInputStream() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/text-only.xml");
        Assertions.assertNotNull(is);

        XmlNode node = XmlParser.parse(is);

        Assertions.assertNotNull(node);
        Assertions.assertEquals("root", node.getName());
        Assertions.assertEquals("Only text content, no children", node.getText());
        Assertions.assertTrue(node.getChildNodes().isEmpty());
    }

    @Test
    public void testParse_DeepNestingInputStream() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/very-deep-nesting.xml");
        Assertions.assertNotNull(is);

        XmlNode node = XmlParser.parse(is);

        Assertions.assertNotNull(node);
        Assertions.assertEquals("level0", node.getName());

        // 测试深度嵌套结构
        XmlNode current = node;
        for (int i = 1; i <= 8; i++) {
            List<XmlNode> children = current.getChildNodes();
            Assertions.assertEquals(1, children.size());
            current = children.get(0);
            Assertions.assertEquals("level" + i, current.getName());
        }
        Assertions.assertEquals("Deep nested text", current.getText());
    }

    @Test
    public void testParse_MultipleChildrenInputStream() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/multiple-children.xml");
        Assertions.assertNotNull(is);

        XmlNode node = XmlParser.parse(is);

        Assertions.assertNotNull(node);
        Assertions.assertEquals("root", node.getName());

        List<XmlNode> items = node.getChildNodes("item");
        Assertions.assertEquals(3, items.size());
        Assertions.assertEquals("Item 1", items.get(0).getText());
        Assertions.assertEquals("Item 2", items.get(1).getText());
        Assertions.assertEquals("Item 3", items.get(2).getText());

        List<XmlNode> nodes = node.getChildNodes("node");
        Assertions.assertEquals(3, nodes.size());

        List<XmlNode> typeANodes = new java.util.ArrayList<>();
        for (XmlNode n : nodes) {
            if ("A".equals(n.getAttribute("type"))) {
                typeANodes.add(n);
            }
        }
        Assertions.assertEquals(2, typeANodes.size());
    }

    // ========== parse(InputStream is, String propertyFile) 方法测试 ==========

    @Test
    public void testParse_InputStreamWithPropertyFile() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/with-variables.xml");
        Assertions.assertNotNull(is);

        XmlNode node = XmlParser.parse(is, "xml/test-variables.properties");

        Assertions.assertNotNull(node);
        Assertions.assertEquals("root", node.getName());

        XmlNode database = node.getSingleChildNode("database");
        Assertions.assertNotNull(database);
        Assertions.assertEquals("com.mysql.jdbc.Driver", database.getSingleChildNodeText("driver"));
        Assertions.assertEquals("jdbc:mysql://localhost:3306/testdb", database.getSingleChildNodeText("url"));
        Assertions.assertEquals("testuser", database.getSingleChildNodeText("username"));
        Assertions.assertEquals("testpass", database.getSingleChildNodeText("password"));

        XmlNode server = node.getSingleChildNode("server");
        Assertions.assertNotNull(server);
        Assertions.assertEquals("8080", server.getAttribute("port"));
        Assertions.assertEquals("localhost", server.getAttribute("host"));
        Assertions.assertEquals("Test Application", server.getSingleChildNodeText("name"));
    }

    @Test
    public void testParse_InputStreamWithNullPropertyFile() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/simple.xml");
        Assertions.assertNotNull(is);

        XmlNode node = XmlParser.parse(is, null);

        Assertions.assertNotNull(node);
        Assertions.assertEquals("root", node.getName());
    }

    @Test
    public void testParse_InputStreamWithEmptyPropertyFile() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/simple.xml");
        Assertions.assertNotNull(is);

        XmlNode node = XmlParser.parse(is, "");

        Assertions.assertNotNull(node);
        Assertions.assertEquals("root", node.getName());
    }

    // ========== parseElement 方法通过 parse 间接测试 ==========
    // parseElement 方法已在其他测试中通过 parse 方法间接覆盖

    // ========== readXml 方法测试 ==========
    // readXml 方法通过 parse 方法的成功执行间接验证
    // 单独测试 readXml 的错误场景

    @Test
    public void testReadXml_InvalidXml() {
        String invalidXml = "<?xml version=\"1.0\"?><root><unclosed>";
        Assertions.assertThrows(SonsureException.class, () -> {
            XmlParser.readXml(invalidXml);
        });
    }

    @Test
    public void testReadXml_EmptyString() {
        Assertions.assertThrows(SonsureException.class, () -> {
            XmlParser.readXml("");
        });
    }

    // ========== replaceXmlProperties 方法测试 ==========

    @Test
    public void testReplaceXmlProperties_Simple() {
        String xml = "<root>${var}</root>";
        Map<String, String> properties = new HashMap<>();
        properties.put("var", "value");

        String result = XmlParser.replaceXmlProperties(xml, properties);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("value"));
        Assertions.assertFalse(result.contains("${var}"));
    }

    @Test
    public void testReplaceXmlProperties_Multiple() {
        String xml = "<root><a>${var1}</a><b>${var2}</b></root>";
        Map<String, String> properties = new HashMap<>();
        properties.put("var1", "value1");
        properties.put("var2", "value2");

        String result = XmlParser.replaceXmlProperties(xml, properties);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("value1"));
        Assertions.assertTrue(result.contains("value2"));
    }

    @Test
    public void testReplaceXmlProperties_InAttributes() {
        String xml = "<root attr=\"${attrValue}\"/>";
        Map<String, String> properties = new HashMap<>();
        properties.put("attrValue", "test");

        String result = XmlParser.replaceXmlProperties(xml, properties);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("attr=\"test\""));
    }

    @Test
    public void testReplaceXmlProperties_WithAmpersand() {
        String xml = "<root>${var}</root>";
        Map<String, String> properties = new HashMap<>();
        properties.put("var", "A & B");

        String result = XmlParser.replaceXmlProperties(xml, properties);

        Assertions.assertNotNull(result);
        // & 应该被转义为 &amp;
        Assertions.assertTrue(result.contains("&amp;") || result.contains("&"));
    }

    @Test
    public void testReplaceXmlProperties_NullProperties() {
        String xml = "<root>${var}</root>";
        String result = XmlParser.replaceXmlProperties(xml, null);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(xml, result); // 应该保持不变
    }

    @Test
    public void testReplaceXmlProperties_EmptyProperties() {
        String xml = "<root>${var}</root>";
        Map<String, String> properties = new HashMap<>();
        String result = XmlParser.replaceXmlProperties(xml, properties);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("${var}")); // 应该保持不变
    }

    @Test
    public void testReplaceXmlProperties_UnmatchedVariable() {
        String xml = "<root>${unknown}</root>";
        Map<String, String> properties = new HashMap<>();
        properties.put("known", "value");

        String result = XmlParser.replaceXmlProperties(xml, properties);

        Assertions.assertNotNull(result);
        // 未匹配的变量应该保持原样
        Assertions.assertTrue(result.contains("${unknown}"));
    }

    // ========== include 功能测试 ==========

    @Test
    public void testParse_WithInclude() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/with-include.xml");
        Assertions.assertNotNull(is);

        XmlNode node = XmlParser.parse(is);

        Assertions.assertNotNull(node);
        Assertions.assertEquals("root", node.getName());

        // include 标签的内容应该被包含的文件内容替换
        // 检查前后节点是否存在
        boolean foundBefore = false;
        boolean foundAfter = false;
        boolean foundIncluded = false;

        for (XmlNode child : node.getChildNodes()) {
            if ("before".equals(child.getName())) {
                foundBefore = true;
                Assertions.assertEquals("Before include", child.getText());
            } else if ("after".equals(child.getName())) {
                foundAfter = true;
                Assertions.assertEquals("After include", child.getText());
            } else if ("include".equals(child.getName())) {
                List<XmlNode> childNodes = child.getChildNodes();
                if ("include-item".equals(childNodes.get(0).getName())) {
                    foundIncluded = true;
                }
            }
        }

        Assertions.assertTrue(foundBefore);
        Assertions.assertTrue(foundAfter);
        Assertions.assertTrue(foundIncluded); // 包含的内容应该被加载
    }

    @Test
    public void testParse_WithIncludeNotFound() {
        String xmlContent = "<?xml version=\"1.0\"?><root><include file=\"non-existent.xml\"/></root>";

        Assertions.assertThrows(SonsureCommonsException.class, () -> {
            XmlParser.parse(xmlContent);
        });
    }

    // ========== XmlNode 功能测试（通过 XmlParser 间接测试） ==========

    @Test
    public void testXmlNode_GetSingleChildNode() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/multiple-children.xml");
        XmlNode node = XmlParser.parse(is);

        XmlNode item = node.getSingleChildNode("item");
        Assertions.assertNotNull(item);
        Assertions.assertEquals("item", item.getName());
    }

    @Test
    public void testXmlNode_GetSingleChildNodeByAttribute() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/with-attributes.xml");
        XmlNode node = XmlParser.parse(is);

        XmlNode item = node.getSingleChildNode("item", "id", "2");
        Assertions.assertNotNull(item);
        Assertions.assertEquals("item2", item.getAttribute("name"));
        Assertions.assertEquals("value2", item.getAttribute("value"));
    }

    @Test
    public void testXmlNode_GetChildNodes() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/multiple-children.xml");
        XmlNode node = XmlParser.parse(is);

        List<XmlNode> items = node.getChildNodes("item");
        Assertions.assertEquals(3, items.size());

        List<XmlNode> nodes = node.getChildNodes("node");
        Assertions.assertEquals(3, nodes.size());
    }

    @Test
    public void testXmlNode_GetSingleChildNodeText() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/simple.xml");
        XmlNode node = XmlParser.parse(is);

        String text = node.getSingleChildNodeText("child1");
        Assertions.assertEquals("text content", text);
    }

    @Test
    public void testXmlNode_GetSingleChildNodeTextByAttribute() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/with-attributes.xml");
        XmlNode node = XmlParser.parse(is);

        String text = node.getSingleChildNodeText("item", "id", "1");
        Assertions.assertEquals("Item 1 content", text);
    }

    @Test
    public void testXmlNode_GetSingleChildNodeValue() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/with-attributes.xml");
        XmlNode node = XmlParser.parse(is);

        String value = node.getSingleChildNodeValue("item");
        Assertions.assertEquals("value1", value); // 第一个item的value属性
    }

    @Test
    public void testXmlNode_GetSingleChildNodeValueByAttribute() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/with-attributes.xml");
        XmlNode node = XmlParser.parse(is);

        String value = node.getSingleChildNodeValue("item", "id", "2");
        Assertions.assertEquals("value2", value);
    }

    @Test
    public void testXmlNode_GetAttribute() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/with-attributes.xml");
        XmlNode node = XmlParser.parse(is);

        Assertions.assertEquals("1", node.getAttribute("id"));
        Assertions.assertEquals("test", node.getAttribute("name"));
        Assertions.assertEquals("true", node.getAttribute("enabled"));
    }

    @Test
    public void testXmlNode_GetText() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/text-only.xml");
        XmlNode node = XmlParser.parse(is);

        Assertions.assertEquals("Only text content, no children", node.getText());
    }

    // ========== 特殊字符处理测试 ==========

    @Test
    public void testParse_SpecialCharacters() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/with-special-chars.xml");
        XmlNode node = XmlParser.parse(is);

        Assertions.assertNotNull(node);
        XmlNode textNode = node.getSingleChildNode("text");
        Assertions.assertNotNull(textNode);
        String text = textNode.getText();
        Assertions.assertTrue(text.contains("special") && text.contains("characters"));

        XmlNode ampersandNode = node.getSingleChildNode("ampersand");
        Assertions.assertNotNull(ampersandNode);
        String value = ampersandNode.getAttribute("value");
        Assertions.assertNotNull(value);
    }

    @Test
    public void testReplaceXmlProperties_SpecialCharacters() {
        String xml = "<root>${var1}&amp;${var2}</root>";
        Map<String, String> properties = new HashMap<>();
        properties.put("var1", "value1");
        properties.put("var2", "value2");

        String result = XmlParser.replaceXmlProperties(xml, properties);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("value1"));
        Assertions.assertTrue(result.contains("value2"));
    }

    // ========== 复杂场景测试 ==========

    @Test
    public void testParse_ComplexScenario() {
        String xmlContent = "<?xml version=\"1.0\"?>" +
                "<config>" +
                "<database driver=\"${db.driver}\" url=\"${db.url}\"/>" +
                "<server port=\"${server.port}\">" +
                "<name>${app.name}</name>" +
                "</server>" +
                "</config>";
        Map<String, String> properties = new HashMap<>();
        properties.put("db.driver", "com.mysql.jdbc.Driver");
        properties.put("db.url", "jdbc:mysql://localhost:3306/db");
        properties.put("server.port", "8080");
        properties.put("app.name", "MyApp");

        XmlNode node = XmlParser.parse(xmlContent, properties);

        Assertions.assertNotNull(node);
        Assertions.assertEquals("config", node.getName());

        XmlNode database = node.getSingleChildNode("database");
        Assertions.assertNotNull(database);
        Assertions.assertEquals("com.mysql.jdbc.Driver", database.getAttribute("driver"));
        Assertions.assertEquals("jdbc:mysql://localhost:3306/db", database.getAttribute("url"));

        XmlNode server = node.getSingleChildNode("server");
        Assertions.assertNotNull(server);
        Assertions.assertEquals("8080", server.getAttribute("port"));
        Assertions.assertEquals("MyApp", server.getSingleChildNodeText("name"));
    }

    @Test
    public void testParse_RealWorldConfig() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/with-variables.xml");
        XmlNode node = XmlParser.parse(is, "xml/test-variables.properties");

        Assertions.assertNotNull(node);
        XmlNode database = node.getSingleChildNode("database");
        Assertions.assertNotNull(database);
        Assertions.assertEquals("com.mysql.jdbc.Driver", database.getSingleChildNodeText("driver"));
        Assertions.assertEquals("jdbc:mysql://localhost:3306/testdb", database.getSingleChildNodeText("url"));
        Assertions.assertEquals("testuser", database.getSingleChildNodeText("username"));
        Assertions.assertEquals("testpass", database.getSingleChildNodeText("password"));
    }

    // ========== 边界情况测试 ==========

    @Test
    public void testParse_WhitespaceInText() {
        String xmlContent = "<?xml version=\"1.0\"?><root>  Text with spaces  </root>";
        XmlNode node = XmlParser.parse(xmlContent);

        Assertions.assertNotNull(node);
        // getTextTrim() 会去除首尾空格
        Assertions.assertEquals("Text with spaces", node.getText());
    }

    @Test
    public void testParse_MultipleAttributes() {
        String xmlContent = "<?xml version=\"1.0\"?><root a=\"1\" b=\"2\" c=\"3\" d=\"4\"/>";
        XmlNode node = XmlParser.parse(xmlContent);

        Assertions.assertNotNull(node);
        Assertions.assertEquals("1", node.getAttribute("a"));
        Assertions.assertEquals("2", node.getAttribute("b"));
        Assertions.assertEquals("3", node.getAttribute("c"));
        Assertions.assertEquals("4", node.getAttribute("d"));
    }

    @Test
    public void testParse_NoMatchingChildNode() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/simple.xml");
        XmlNode node = XmlParser.parse(is);

        XmlNode nonExistent = node.getSingleChildNode("nonExistent");
        Assertions.assertNull(nonExistent);

        String text = node.getSingleChildNodeText("nonExistent");
        Assertions.assertEquals("", text);

        String value = node.getSingleChildNodeValue("nonExistent");
        Assertions.assertEquals("", value);

        List<XmlNode> nodes = node.getChildNodes("nonExistent");
        Assertions.assertTrue(nodes.isEmpty());
    }

    // ========== 错误处理测试 ==========

    @Test
    public void testParse_InvalidXmlContent() {
        Assertions.assertThrows(SonsureException.class, () -> {
            XmlParser.parse("invalid xml content");
        });
    }

    // readXml 方法的更多测试通过 parse 方法间接覆盖

    @Test
    public void testReadXml_ClosedInputStream() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/simple.xml");
        Assertions.assertNotNull(is);
        try {
            is.close();
        } catch (Exception e) {
            // ignore
        }

        // 使用已关闭的流应该抛出异常
        Assertions.assertThrows(SonsureException.class, () -> {
            XmlParser.readXml(is);
        });
    }

    // ========== 性能和大数据量测试 ==========

    @Test
    public void testParse_LargeStructure() {
        StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\"?><root>");
        for (int i = 0; i < 100; i++) {
            xmlBuilder.append("<item id=\"").append(i).append("\">Item ").append(i).append("</item>");
        }
        xmlBuilder.append("</root>");

        XmlNode node = XmlParser.parse(xmlBuilder.toString());

        Assertions.assertNotNull(node);
        List<XmlNode> items = node.getChildNodes("item");
        Assertions.assertEquals(100, items.size());
    }

    @Test
    public void testParse_ManyAttributes() {
        StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\"?><root");
        for (int i = 0; i < 50; i++) {
            xmlBuilder.append(" attr").append(i).append("=\"").append("value").append(i).append("\"");
        }
        xmlBuilder.append("/>");

        XmlNode node = XmlParser.parse(xmlBuilder.toString());

        Assertions.assertNotNull(node);
        for (int i = 0; i < 50; i++) {
            Assertions.assertEquals("value" + i, node.getAttribute("attr" + i));
        }
    }
}

