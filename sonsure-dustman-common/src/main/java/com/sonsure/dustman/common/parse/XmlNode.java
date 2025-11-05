/*
 * Copyright (c) 2020. www.sonsure.com Inc. All rights reserved.
 * You may obtain more information at
 *
 *   http://www.sonsure.com
 *
 * Designed By Selfly Lee (selfly@live.com)
 */

package com.sonsure.dustman.common.parse;

import com.sonsure.dustman.common.utils.StrUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liyd on 16/12/27.
 */
public class XmlNode {

    private String name;

    private String text;

    private Map<String, String> attributes;

    private List<XmlNode> childNodes;

    public XmlNode() {
        this(null);
    }

    public XmlNode(String nodeName) {
        this.name = nodeName;
        this.attributes = new HashMap<String, String>();
        this.childNodes = new ArrayList<XmlNode>();
    }

    /**
     * 添加属性
     *
     * @param name  the name
     * @param value the value
     */
    public void addAttribute(String name, String value) {
        this.attributes.put(name, value);
    }

    /**
     * 获取属性
     *
     * @param attributeName the attribute name
     * @return attribute
     */
    public String getAttribute(String attributeName) {
        return attributes == null ? "" : attributes.get(attributeName);
    }

    /**
     * 根据节点名称获取唯一子节点
     *
     * @param nodeName the node name
     * @return single child node
     */
    public XmlNode getSingleChildNode(String nodeName) {
        for (XmlNode childNode : this.childNodes) {
            if (nodeName.equals(childNode.getName())) {
                return childNode;
            }
        }
        return null;
    }

    /**
     * 根据节点名称+某个属性，获取子节点
     *
     * @param nodeName the node name
     * @param attrName the attr name
     * @param attrVal  the attr val
     * @return single child node
     */
    public XmlNode getSingleChildNode(String nodeName, String attrName, String attrVal) {
        for (XmlNode childNode : this.childNodes) {
            if (nodeName.equals(childNode.getName()) && attrVal.equals(childNode.getAttribute(attrName))) {
                return childNode;
            }
        }
        return null;
    }

    /**
     * 获取子节点
     *
     * @param nodeName the node name
     * @return child nodes
     */
    public List<XmlNode> getChildNodes(String nodeName) {

        List<XmlNode> nodes = new ArrayList<>();
        for (XmlNode childNode : this.childNodes) {
            if (nodeName.equals(childNode.getName())) {
                nodes.add(childNode);
            }
        }
        return nodes;
    }

    /**
     * 根据节点名称,获取子节点内容
     *
     * @param nodeName the node name
     * @return single child node text
     */
    public String getSingleChildNodeText(String nodeName) {
        XmlNode childNode = getSingleChildNode(nodeName);
        return childNode == null ? "" : childNode.getText();
    }

    /**
     * 根据节点名称+某个属性,获取子节点内容
     *
     * @param nodeName the node name
     * @param attrName the attr name
     * @param attrVal  the attr val
     * @return single child node text
     */
    public String getSingleChildNodeText(String nodeName, String attrName, String attrVal) {
        XmlNode childNode = getSingleChildNode(nodeName, attrName, attrVal);
        return childNode == null ? "" : childNode.getText();
    }

    /**
     * 根据节点名称,获取子节点value属性值
     *
     * @param nodeName the node name
     * @return single child node value
     */
    public String getSingleChildNodeValue(String nodeName) {
        XmlNode childNode = getSingleChildNode(nodeName);
        return childNode == null ? "" : childNode.getAttribute("value");
    }

    /**
     * 根据节点名称+某个属性,获取子节点value属性值
     *
     * @param nodeName the node name
     * @param attrName the attr name
     * @param attrVal  the attr val
     * @return single child node text
     */
    public String getSingleChildNodeValue(String nodeName, String attrName, String attrVal) {
        XmlNode childNode = getSingleChildNode(nodeName, attrName, attrVal);
        return childNode == null ? "" : childNode.getAttribute("value");
    }

    /**
     * 添加子节点
     *
     * @param xmlNode the xml node
     */
    public void addChildNode(XmlNode xmlNode) {
        childNodes.add(xmlNode);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<XmlNode> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<XmlNode> childNodes) {
        this.childNodes = childNodes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return StrUtils.reflectionToString(this);
    }
}
