package com.mitchtrachtenberg.mirthpluginsearchsummary.client.panel;

import java.io.*;
import java.util.List;
import java.lang.String;
import java.util.HashMap;

import com.mirth.connect.model.Channel;
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

public class Summarize {

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
	    System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
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
		    path = p.getNodeName() + " " + String.valueOf(p.getNodeType()) + "/" + path;
		    p = p.getParentNode();
		}
		path = path.replaceAll("#document/","");
		path = path.replaceAll("com.mirth.connect.plugins.","");
		
		sb.append( path + " = " + n.getTextContent()+ "\n");
		
	    }
	} catch (Exception e){
	    System.out.println(e);
	}
	return(sb.toString());
    }
    
    static String map_to_string(HashMap m){
	HashMap<String, String> test1 = new HashMap();
        test1.put("a","b");
	test1.put("c","d");
	System.out.println(test1);
	return(test1.toString());
    }
    
    static String gen_sample_summary(Channel channel){
	String channelStr = "<div id=\"accordion\">";//open accordion div
	channelStr += ("<h3>" + channel.getName() + "</h3>\n");
	channelStr += ("<div>");//creates inner div
        channelStr += ("<p>Description:</p>");
        channelStr += ("<p>Properties:</p>");
	channelStr += ("</div>");//close inner div
	return (channelStr);
    }
    static String generate_all(List<Channel> channels){
	System.out.println("In generate_all");
	StringBuilder sb = new StringBuilder();
    	sb.append("<html>");
	sb.append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.6.3/jquery.min.js\"></script>");
	sb.append("<link rel=\"stylesheet\" href=\"https://ajax.googleapis.com/ajax/libs/jqueryui/1.13.2/themes/smoothness/jquery-ui.css\">");
	sb.append("<script src=\"https://ajax.googleapis.com/ajax/libs/jqueryui/1.13.2/jquery-ui.min.js\"></script>");
	sb.append("<script>  $( function() {\n");
	sb.append("$( \"#accordion\" ).accordion({\n");
	sb.append("heightStyle: \"content\"\n");
	sb.append("});\n");
	sb.append("} );\n");
	sb.append("</script>\n<script>\n");
	sb.append("$( function() {\n");
	sb.append("$( \".accordion2\" ).accordion({\n");
	sb.append("heightStyle: \"content\"\n");
	sb.append("});\n");
	sb.append("} );\n");
	sb.append("</script>\n<script>\n");
	sb.append("$( function() {\n");
	sb.append("$( \".accordion3\" ).accordion({\n");
	sb.append("heightStyle: \"content\"\n");
	sb.append("});\n");
	sb.append("} );\n");
	//open accordion div
	sb.append("</script></head><body><div id=\"accordion\">");
	System.out.println("In generate_all post header");

        for (Channel channel: channels){
	    System.out.println("Calling generate_summary on " + channel);
	    sb.append(Summarize.generate_summary(channel));
	}
	//close accordion div
	sb.append("</div></body></html>");
	return(sb.toString());
    }
    static String generate_summary(Channel channel){
        // this creates an h3 header and a div for the channel contents
	String channelStr = ("<h3>" + channel.getName() + "</h3>\n");
	channelStr += "<div>\n";//creates inner div for accordion
	channelStr += "<div class=\"accordion2\">\n";
        channelStr += ("<h4>Description:</h4>\n<div>\n<pre>" + escapeHTML(channel.getDescription()) + "</pre>\n</div>\n");
	channelStr += ("<h4>PreprocessingScript: </h4>\n");
	channelStr += ("<div>\n<pre>" + escapeHTML(channel.getPreprocessingScript()) + "</pre>\n</div>\n");
	channelStr += ("<h4>PostprocessingScript: </h4>\n");
	channelStr += ("<div>\n<pre>" + escapeHTML(channel.getPostprocessingScript()) + "</pre>\n</div>\n");
	channelStr += ("<h4>DeployScript: </h4>\n");
	channelStr += ("<div>\n<pre>" + escapeHTML(channel.getDeployScript()) + "</pre>\n</div>\n");
	channelStr += ("<h4>UndeployScript: </h4>\n");
	channelStr += ("<div>\n<pre>" + escapeHTML(channel.getUndeployScript()) + "</pre>\n</div>\n");
        List<Connector> connList = channel.getDestinationConnectors();
	connList.add(0,channel.getSourceConnector());
	for (Connector conn: connList){
	    channelStr += ("<h4>C O N N E C T O R " + conn.getName() + " (" + conn.getTransportName() + ")</h4>\n");
	    channelStr += "<div>\n"; // conn contents division starts
	    channelStr += "<div class=\"accordion3\">\n"; // acc3 starts
	    Transformer t = conn.getTransformer();
	    Filter f = conn.getFilter();
	    ConnectorProperties p = conn.getProperties();
	    try {
		if(p != null){
		    System.out.println("p != null");
		    String propStrXML = ObjectXMLSerializer.getInstance().serialize(p);
		    String propStrResult = get_xml(propStrXML,"//*");
		    //propStr = escapeHTML(propStrXML);
		    //try extracting using dom4j
		    channelStr += "<h4>Connector properties</h4>\n";
		    channelStr += ("<div><pre>" + propStrResult +  "</pre>\n</div>\n");
		}
	    } catch (Exception e){
		System.out.println(e);
	    }


	    try {
		List<Rule> ruleList = f.getElements();
		List<Step> stepList = t.getElements();
		String sl = " " + stepList;
		sl = sl.replace(",","\n");
		String rl = " " + ruleList;
		rl = rl.replace(",","\n");
		String[] stepStrList = sl.split("Step\\[");
		String[] ruleStrList = rl.split("Rule\\[");
		channelStr += "<h4>Connector transformer elements</h4>\n";
		channelStr += "<div>\n";
		// retrieve XML entries associated with the transformers
		channelStr += "<pre>" + get_xml(ObjectXMLSerializer.getInstance().serialize(t),"//elements//*") + "</pre>";
		
		channelStr += "<ul>\n";
		int i = 1; // the 0th entry is before the first step
		for (Step s: stepList){
		    channelStr += ("<li>" + s.getName());
		    channelStr += ("<pre>" + stepStrList[i++] + "</pre></li>\n");
		}
		channelStr += "</ul></div>\n";
		channelStr += ("<h4>Connector filter elements </h4>");
		channelStr += "<div>";
		// retrieve XML entries associated with the filters
		channelStr += "<pre>" + get_xml(ObjectXMLSerializer.getInstance().serialize(f),"//elements//*") + "</pre>";

		channelStr += "<ul>";
		i=1; // the 0th entry is before the first rule
		for (Rule r: ruleList){
		    String ruleOpStr = "";
		    try {
			ruleOpStr = r.getOperator().name();
		    } catch (Exception e){
			//System.out.println("Problem with getOperator: null? " + r);
		    }
		    channelStr += ("<li>" + ruleOpStr + " " + r.getName() );
		    channelStr += ("<pre> " + ruleStrList[i++] + "</pre>\n</li>\n");
		}
	    } catch (Exception e) {
		//System.out.println("Problem with rule or step list");
		//System.out.println(e);
	    }
	    channelStr += "</ul></div>\n";
	    channelStr += "</div>\n"; //ends accordion3 div
	    channelStr += "</div>\n"; //ends inner div with conn contents
	} // end for loop
	channelStr += "</div>\n"; //ends accordion2 div for scripts, connectors
	channelStr += "</div>\n"; //ends accordion div for channel
	return channelStr;
    }
    static String escapeHTML(String s) {
	StringBuilder out = new StringBuilder(Math.max(16, s.length()));
	for (int i = 0; i < s.length(); i++) {
	    char c = s.charAt(i);
	    if (c > 127 || c == '"' || c == '\'' || c == '<' || c == '>' || c == '&') {
		out.append("&#");
		out.append((int) c);
		out.append(';');
	    } else {
		out.append(c);
	    }
	}
	return out.toString();
    }
    
    
}

