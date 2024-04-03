/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dumper.common.parse;

import com.sonsure.dumper.common.exception.SonsureException;
import com.sonsure.dumper.common.spring.ClassPathResource;
import com.sonsure.dumper.common.spring.Resource;
import com.sonsure.dumper.common.utils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by liyd on 17/9/6.
 */
public final class XmlParser {

    /**
     * 解析xml
     *
     * @param resource     the resource
     * @param propertyFile the property file
     * @return xml node
     */
    public static XmlNode parse(Resource resource, String propertyFile) {

        Map<String, String> properties = null;
        if (StringUtils.isNotBlank(propertyFile)) {
            properties = PropertyUtils.getProperties(propertyFile);
        }
        Document document = readXml(resource, properties);
        return parse(document.asXML(), properties);
    }

    /**
     * 解析xml
     *
     * @param resource the resource
     * @return xml node
     */
    public static XmlNode parse(Resource resource) {
        Document document = readXml(resource);
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
        if (StringUtils.equals("include", element.getName())) {
            Attribute attr = element.attribute("file");
            Document document = readXml(new ClassPathResource(attr.getValue()), properties);
            Element rootElement = document.getRootElement();
            iter = rootElement.elementIterator();
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
     * @param resource the resource
     * @return document
     */
    public static Document readXml(Resource resource) {
        return readXml(resource, null);
    }

    /**
     * 解析xml
     *
     * @param resource   the resource
     * @param properties the properties
     * @return document
     */
    public static Document readXml(Resource resource, Map<String, String> properties) {

        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new InputSource(resource.getInputStream()));
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
            String value = StringUtils.replace(entry.getValue(), "&amp", "&");
            value = StringUtils.replace(value, "&", "&amp;");
            theXml = StringUtils.replace(theXml, String.format("${%s}", entry.getKey()), value);
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
