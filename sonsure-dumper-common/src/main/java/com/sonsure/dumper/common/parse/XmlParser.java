/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.parse;

import com.sonsure.dumper.common.exception.SonsureCommonsException;
import com.sonsure.dumper.common.exception.SonsureException;
import com.sonsure.dumper.common.utils.ClassUtils;
import com.sonsure.dumper.common.utils.PropertyUtils;
import com.sonsure.dumper.common.utils.StrUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by liyd on 17/9/6.
 */
public final class XmlParser {

    /**
     * 解析xml
     *
     * @param is           the is
     * @param propertyFile the property file
     * @return xml node
     */
    public static XmlNode parse(InputStream is, String propertyFile) {

        Map<String, String> properties = null;
        if (StrUtils.isNotBlank(propertyFile)) {
            properties = PropertyUtils.getProperties(propertyFile);
        }
        Document document = readXml(is, properties);
        return parse(document.asXML(), properties);
    }

    /**
     * 解析xml
     *
     * @param is the is
     * @return xml node
     */
    public static XmlNode parse(InputStream is) {
        Document document = readXml(is);
        Element rootElement = document.getRootElement();
        return parseElement(rootElement, null);
    }

    /**
     * 解析xml
     *
     * @param xmlContent the xml content
     * @return xml node
     */
    public static XmlNode parse(String xmlContent) {

        return parse(xmlContent, null);
    }

    /**
     * 解析xml
     *
     * @param xmlContent the xml content
     * @param properties the properties
     * @return xml node
     */
    public static XmlNode parse(String xmlContent, Map<String, String> properties) {
        String xml = replaceXmlProperties(xmlContent, properties);
        Document document = readXml(xml);
        Element rootElement = document.getRootElement();
        return parseElement(rootElement, properties);
    }

    /**
     * 解析xml元素
     *
     * @param element    the element
     * @param properties the properties
     * @return xml node
     */
    public static XmlNode parseElement(Element element, Map<String, String> properties) {

        XmlNode xmlNode = new XmlNode(element.getName());

        Iterator<Attribute> iterator = element.attributeIterator();
        while (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            xmlNode.addAttribute(attribute.getName(), attribute.getValue());
        }

        Iterator<Element> iter = null;
        if ("include".equals(element.getName())) {
            Attribute attr = element.attribute("file");
            try (InputStream is = ClassUtils.getDefaultClassLoader().getResourceAsStream(attr.getValue())) {
                if (is == null) {
                    throw new SonsureCommonsException("加载资源失败:" + attr.getValue());
                }
                Document document = readXml(is, properties);
                Element rootElement = document.getRootElement();
                iter = rootElement.elementIterator();
            } catch (IOException e) {
                throw new SonsureCommonsException("加载资源失败:" + attr.getValue(), e);
            }
        } else {
            iter = element.elementIterator();
        }

        while (iter.hasNext()) {
            Element next = iter.next();
            xmlNode.addChildNode(parseElement(next, properties));
        }
        xmlNode.setText(element.getTextTrim());
        return xmlNode;
    }

    /**
     * 解析xml元素
     *
     * @param element the element
     * @return xml node
     */
    public static XmlNode parseElement(Element element) {
        return parseElement(element, null);
    }

    /**
     * 解析xml
     *
     * @param is the is
     * @return document document
     */
    public static Document readXml(InputStream is) {
        return readXml(is, null);
    }

    /**
     * 解析xml
     *
     * @param is         the is
     * @param properties the properties
     * @return document document
     */
    public static Document readXml(InputStream is, Map<String, String> properties) {

        try {
            SAXReader saxReader = SAXReader.createDefault();
            Document document = saxReader.read(new InputSource(is));
            if (properties == null || properties.isEmpty()) {
                return document;
            }
            String xml = document.asXML();
            xml = replaceXmlProperties(xml, properties);
            return readXml(xml);
        } catch (Exception e) {
            throw new SonsureException(e);
        }
    }

    /**
     * Replace xml properties string.
     *
     * @param xml        the xml
     * @param properties the properties
     * @return the string
     */
    public static String replaceXmlProperties(String xml, Map<String, String> properties) {

        if (properties == null) {
            return xml;
        }
        String theXml = xml;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String value = StrUtils.replace(entry.getValue(), "&amp", "&");
            value = StrUtils.replace(value, "&", "&amp;");
            theXml = StrUtils.replace(theXml, String.format("${%s}", entry.getKey()), value);
        }
        return theXml;
    }

    /**
     * 读取xml流为doc
     *
     * @param content the content
     * @return document
     */
    public static Document readXml(String content) {
        try {
            return DocumentHelper.parseText(content);
        } catch (DocumentException e) {
            throw new SonsureException("读取xml文件失败", e);
        }
    }
}
