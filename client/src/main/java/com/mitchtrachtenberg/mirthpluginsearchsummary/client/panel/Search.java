package com.mitchtrachtenberg.mirthpluginsearchsummary.client.panel;

import java.io.*;
import java.util.List;
import java.lang.String;
import java.util.HashMap;

import com.mirth.connect.model.ChannelProperties;
import com.mirth.connect.model.Channel;
import com.mirth.connect.model.ChannelExportData;
import com.mirth.connect.model.Connector;
import com.mirth.connect.model.Filter;
import com.mirth.connect.model.Rule;
import com.mirth.connect.model.Step;
import com.mirth.connect.model.FilterTransformerElement;
import com.mirth.connect.model.Transformer;
import com.mirth.connect.model.converters.ObjectXMLSerializer;
import com.mirth.connect.donkey.model.channel.ConnectorProperties;
import com.mirth.connect.plugins.*;
//import com.mirth.connect.connectors.*;
import com.mirth.connect.plugins.rulebuilder.RuleBuilderRule;
//import com.mirth.connect.plugins.javascriptrule.JavaScriptRule;
//import com.mirth.connect.plugins.mapper;
//import com.mirth.connect.plugins.messagebuilder;
//import com.mirth.connect.connectors.vm;

import com.mitchtrachtenberg.mirthpluginsearchsummary.shared.MyConstants;
import com.mirth.connect.donkey.model.*;
import com.mirth.connect.model.Channel;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.XMLConstants;
import javax.xml.xpath.*;
import javax.xml.xpath.XPathConstants;

public class Search {
    static String generateName(Node n){
	String name = "?";
        try {
	    NodeList nodes = n.getChildNodes();
	    Node nameNode;
	    for (int x = 0; x < nodes.getLength(); x++){
		if (nodes.item(x).getNodeName().equals("name")){
		    nameNode = nodes.item(x);
		    name = nameNode.getFirstChild().getNodeValue();
		    break;
		}
	    }
         } catch (Exception e) {
	    System.out.println(name);
	    
            System.out.println(e.getMessage());
        }
	return (name);
    }

    static String get_xml(String xmlString, String searchString){
	StringBuilder sb = new StringBuilder();
	try {
	    
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          // optional, but recommended
          // process XML securely, avoid attacks like XML External Entities (XXE)
	    dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

          // parse XML string
	    DocumentBuilder db = dbf.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(xmlString));
	    Document doc = db.parse(is);
	    doc.getDocumentElement().normalize();
	    XPath xPath = XPathFactory.newInstance().newXPath();
	    NodeList nodes = (NodeList)xPath.evaluate(
						      searchString,
						      doc,
						      XPathConstants.NODESET);
	    //System.out.println(nodes.size());
	    int nodeListLen = nodes.getLength();
	    for (int i = 0; i < nodeListLen ; i++){
		Node n = nodes.item(i);
		Node p = nodes.item(i);
		short nType = n.getNodeType();
		String path="";
		while (p != null) {
		    String nodeName = p.getNodeName();
		    if (nodeName == "connector" || nodeName=="channel"){
			nodeName += ("(" + generateName(p)+")");// find the sequence number and or name child node, and add its text
		    }
		    path = nodeName +  "/" + path;
		    p = p.getParentNode();
		}
		path = path.replaceAll("#document/","");
		//path = path.replaceAll("com.mirth.connect.plugins.","");
		//path = path.replaceAll("com.mirth.connect.connectors.","...");
		
		sb.append( path + " = " + n.getTextContent()+ "\n");
	    }
						      
	} catch (Exception e){
	    System.out.println(e);
	}
	return(sb.toString());
    }
    
    
    static String valueSearch(Channel channel, String searchString){
	StringBuilder sb = new StringBuilder();
     	try {
	    String chXML = ObjectXMLSerializer.getInstance().serialize(channel);
	    String searchOn = "//*[contains(normalize-space(text()),'"+searchString+"')]";
	    String matches = get_xml(chXML, searchOn);
	    if (matches.length() > 0){
		sb.append(matches);
	    }
	} catch (Exception e){
	    return (e.toString());
	}
	return(sb.toString());
    }
    static String keySearch(Channel channel, String searchString){
	StringBuilder sb = new StringBuilder();
     	try {
	    String chXML = ObjectXMLSerializer.getInstance().serialize(channel);
	    String searchOn = "//"+searchString;
	    String matches = get_xml(chXML, searchOn);
	    if (matches.length() > 0){
		sb.append(matches);
	    }
	} catch (Exception e){
	    return (e.toString());
	}
	return(sb.toString());
    }
}
