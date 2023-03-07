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
		    path = p.getNodeName() +  "/" + path;
		    p = p.getParentNode();
		}
		path = path.replaceAll("#document/","");
		path = path.replaceAll("com.mirth.connect.plugins.","");
		path = path.replaceAll("com.mirth.connect.connectors.","...");
		
		sb.append( path + " = " + n.getTextContent()+ "\n");
		
	    }
	} catch (Exception e){
	    System.out.println(e);
	}
	String retString = escapeHTML(sb.toString());
	return(retString);
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
	channelStr += ("<h3>" + channel.getName() + " " + channel.getId() + "</h3>\n");
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
	sb.append("active: \"false\",\n");
	sb.append("heightStyle: \"content\",\n");
	sb.append("collapsible: \"true\"\n");
	sb.append("});\n");
	sb.append("} );\n");
	sb.append("</script>\n<script>\n");
	sb.append("$( function() {\n");
	sb.append("$( \".accordion2\" ).accordion({\n");
	sb.append("heightStyle: \"content\",\n");
	sb.append("active: \"false\",\n");
	sb.append("collapsible: \"true\"\n");
	sb.append("});\n");
	sb.append("} );\n");
	sb.append("</script>\n<script>\n");
	sb.append("$( function() {\n");
	sb.append("$( \".accordion3\" ).accordion({\n");
	sb.append("heightStyle: \"content\",\n");
	sb.append("active: \"false\",\n");
	sb.append("collapsible: \"true\"\n");
	sb.append("});\n");
	sb.append("} );\n");
	//open accordion div
	sb.append("</script></head><body><div id=\"accordion\">");
	System.out.println("In generate_all post header");

        for (Channel channel: channels){
	    sb.append(Summarize.generate_summary(channel));
	}
	//close accordion div
	sb.append("</div></body></html>");
	return(sb.toString());
    }
    static String generate_summary(Channel channel){
        // this creates an h3 header and a div for the channel contents
	String description = channel.getDescription();
	int descriptionLength = 0;
	if (description != null) descriptionLength = description.length();
	String deployScript = channel.getDeployScript();
	int deployScriptLength = deployScript.length();
	String undeployScript = channel.getUndeployScript();
	int undeployScriptLength = undeployScript.length();
	String preprocessingScript = channel.getPreprocessingScript();
	int preprocessingScriptLength = preprocessingScript.length();
	String postprocessingScript = channel.getPostprocessingScript();
	int postprocessingScriptLength = postprocessingScript.length();

	ChannelProperties props = channel.getProperties();
	ChannelExportData ed = channel.getExportData();
	StringBuilder channelStr = new StringBuilder();
	channelStr.append( "<h3>" + channel.getName() + "</h3>\n");
	channelStr.append( "<div>\n" );//creates inner div for accordion
	channelStr.append( "<div class=\"accordion2\">\n" );
        channelStr.append( "<h4>Description: " );
        channelStr.append( String.valueOf(descriptionLength) );
	channelStr.append( " chars </h4>\n<div>\n<pre>" );
	channelStr.append( escapeHTML(description) );
	channelStr.append( "</pre>\n</div>\n" );
	channelStr.append( "<h4>PreprocessingScript: " );
	channelStr.append( String.valueOf(preprocessingScriptLength) );
	channelStr.append( " chars </h4>\n" );
	channelStr.append( "<div>\n<pre>" );
	channelStr.append( escapeHTML(preprocessingScript) );
	channelStr.append( "</pre>\n</div>\n" );
	channelStr.append( "<h4>PostprocessingScript: " );
	channelStr.append( String.valueOf(postprocessingScriptLength) );
	channelStr.append( " chars</h4>\n" );
	channelStr.append( "<div>\n<pre>" );
	channelStr.append( escapeHTML(postprocessingScript) );
	channelStr.append( "</pre>\n</div>\n" );
	channelStr.append( "<h4>DeployScript: " );
	channelStr.append( String.valueOf(deployScriptLength) );
	channelStr.append( " chars</h4>\n" );
	channelStr.append("<div>\n<pre>" );
	channelStr.append( escapeHTML(deployScript) );
	channelStr.append( "</pre>\n</div>\n" );
	channelStr.append( "<h4>UndeployScript: " );
	channelStr.append( undeployScriptLength + " chars" );
	channelStr.append( "</h4>\n" );
	channelStr.append("<div>\n<pre>" );
	channelStr.append( escapeHTML(undeployScript) );
	channelStr.append( "</pre>\n</div>\n" );
	try {
		if(props != null){
		    String propStrXML = ObjectXMLSerializer.getInstance().serialize(props);
		    //String propStrResult = get_xml(propStrXML,"//*[not(@*)]");
		    String propStrResult = get_xml(propStrXML,"//*[count(*)=0]");
		    
		    channelStr.append( "<h4>Channel properties</h4>\n" );
		    channelStr.append( "<div><pre>" );
		    channelStr.append( propStrResult );
		    channelStr.append( "</pre>\n</div>\n" );
		}
	} catch (Exception e){
	    System.out.println(e);
	}
	try {
		if(ed != null){
		    String edStrXML = ObjectXMLSerializer.getInstance().serialize(ed);
		    //String propStrResult = get_xml(propStrXML,"//*[not(@*)]");
		    String edStrResult = get_xml(edStrXML,"//*[count(*)=0]");
		    
		    channelStr.append( "<h4>Channel export data</h4>\n" );
		    channelStr.append( "<div><pre>" );
		    channelStr.append( edStrResult );
		    channelStr.append( "</pre>\n</div>\n" );
		}
	} catch (Exception e){
	    System.out.println(e);
	}
        List<Connector> connList = channel.getDestinationConnectors();
	connList.add(0,channel.getSourceConnector());
	for (Connector conn: connList){
	    channelStr.append( "<h4><b>CONNECTOR</b> " + conn.getName() + " (" + conn.getTransportName() + ")</h4>\n");
	    channelStr.append( "<div>\n" ); // conn contents division starts
	    channelStr.append( "<div class=\"accordion3\">\n" ); // acc3 starts
	    ConnectorProperties p = conn.getProperties();
	    try {
		if(p != null){
		    String propStrXML = ObjectXMLSerializer.getInstance().serialize(p);
		    //String propStrResult = get_xml(propStrXML,"//*[not(@*)]");
		    String propStrResult = get_xml(propStrXML,"//*[count(*)=0]");
		    channelStr.append( "<h4>Connector properties</h4>\n" );
		    channelStr.append( "<div><pre>" + propStrResult +  "</pre>\n</div>\n" );
		}
	    } catch (Exception e){
		System.out.println(e);
	    }
	    try {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// optional, but recommended
		// process XML securely,
		//avoid attacks like XML External Entities (XXE)
		dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

		// parse XML string
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is;
		is = new InputSource(new StringReader(
			     ObjectXMLSerializer.getInstance().serialize(conn)));
		Document doc = db.parse(is);
		doc.getDocumentElement().normalize();
		Double filterCount= generateElementListCount(doc,"filter",true);
		Double transformerCount= generateElementListCount(doc,"transformer",true);
		channelStr.append( "<h4>Connector filter elements ");
		channelStr.append( filterCount.intValue());
		channelStr.append( "</h4>" );
		channelStr.append( "<div>" );
		channelStr.append( "<ul>" );
		channelStr.append( generateElementList(doc,"filter",true) );
		channelStr.append( "</ul></div>\n" );
		channelStr.append( "<h4>Connector transformer elements " );
		channelStr.append( transformerCount.intValue());
		channelStr.append( "</h4>");
		channelStr.append( "<div>" );
		channelStr.append( "<ul>" );

		channelStr.append( generateElementList(doc,"transformer",true) );
		channelStr.append( "</ul></div>\n" );
		channelStr.append( "</div>\n" ); //ends accordion3 div
		channelStr.append( "</div>\n" ); //ends inner div with conn contents
	    } catch (Exception e) {
		//System.out.println("Problem with rule or step list");
		//System.out.println(e);
	    }
	} // end for loop
	channelStr.append("</div>\n" ); //ends accordion2 div for scripts, connectors
	channelStr.append( "</div>\n" ); //ends accordion div for channel
	return channelStr.toString();
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
    
    
    static Double generateElementListCount(Document doc, String eltype, boolean html){
	Double numChildren = 0.0;
        try {
             HashMap<String,String> strings = new HashMap<>();
	     XPath xPath = XPathFactory.newInstance().newXPath();
	     numChildren = (Double) xPath.evaluate(
			     String.format("count(//%s/elements//sequenceNumber)",eltype),
			     doc,
			     XPathConstants.NUMBER);
         } catch (Exception e) {
	    System.out.println(numChildren);
	    
            System.out.println(e.getMessage());
        }
	return (numChildren);
    }
    
    static String generateElementList(Document doc, String eltype, boolean html){

        StringBuilder retString = new StringBuilder();
        try {

             HashMap<String,String> strings = new HashMap<>();
	     XPath xPath = XPathFactory.newInstance().newXPath();
	     NodeList parents = (NodeList) xPath.evaluate(
			     String.format("//%s/elements",eltype),
			     doc,
			     XPathConstants.NODESET);
	     for (int i = 0; i < parents.getLength(); i++) {
		 retString.append(getElements(parents.item(i),true));
                }
         } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return( retString.toString() );
    }

    // getElements: return text describing transformer elements,
    // including those elements on the children list of iterators
    // Call on each descendant of ..../transformer|filterI'm /elements
    static StringBuilder getElements(Node transformerOrChildrenNode, boolean html) throws javax.xml.xpath.XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        StringBuilder retString = new StringBuilder();
	String start = "";
	String end = "";
	if (html == true){
	    start = "<li>";
	    end = "</li>";
	}
        if (transformerOrChildrenNode == null) {
            retString.append( "Got null child.\n" );
            return retString;
        }

        if (transformerOrChildrenNode.getNodeName().contains("Iterat")) {
            transformerOrChildrenNode = (Node) xPath.evaluate("./properties/children", transformerOrChildrenNode, XPathConstants.NODE);
        }
        if (transformerOrChildrenNode.getNodeName().equals( "elements" )
                || transformerOrChildrenNode.getNodeName().equals ("children")) {
            Node f = transformerOrChildrenNode.getFirstChild();
            Node x = f;
            do {
                if (x == null) break;
                if (x.getNodeName().contains("#text")){
                    x = x.getNextSibling();
                    if (x == null) break;
                    else continue;//no op for #text nodes
                }
                NodeList ns = (NodeList) xPath.evaluate(
                        "./*",
                        x,
                        XPathConstants.NODESET);
		// put all data in a HashMap for the string to be formatted
                HashMap<String, String> dataMap = new HashMap<>();
                for (int m = 0; m < ns.getLength(); m++) {
                    dataMap.put(ns.item(m).getNodeName(), ns.item(m).getTextContent());
                }
		// format the string as appropriate for the step type
                if (x.getNodeName().contains("MapperStep")) {
		    String fmt_temp = "%s%s %s Mapper: Put the value of %s (\"%s\" if empty) in %s map with key %s.%s\n";
                    retString.append(String.format(
			fmt_temp,
			start,
			dataMap.get("sequenceNumber"),
			dataMap.get("enabled").equals("true") ? "" : "!DISABLED!",
			abbreviateHL7(dataMap.get("mapping")),
			dataMap.get("defaultValue"),
			dataMap.get("scope").toLowerCase(),
			dataMap.get("variable"),
			end
                    ));
                } else if (x.getNodeName().contains("RuleBuilderRule")) {
		    String op = dataMap.get("operator");
		    if (op == null) op = " ";
		    String fmt_temp = "%s%s %s %s Filter rule: The value of %s %s %s.%s\n";
                    retString.append( String.format(
			fmt_temp,
			start,
			op,
			dataMap.get("sequenceNumber"),
			dataMap.get("enabled").equals("true") ? "" : "!DISABLED!",
			abbreviateHL7(dataMap.get("field")),
			abbreviateHL7(dataMap.get("condition")),
			dataMap.get("values"),
			end
                    ));
                } else if (x.getNodeName().contains("MessageBuilderStep")) {
		    String fmt_temp = "%s %s %s MessageBuilder: Put the value of %s (\"%s\" if empty) into %s.\n%s";
		    retString.append( String.format(
			fmt_temp,
			start,
			dataMap.get("sequenceNumber"),
			dataMap.get("enabled").equals("true") ? "" : "!DISABLED!",
			abbreviateHL7(dataMap.get("mapping")),
			dataMap.get("defaultValue"),
			abbreviateHL7(dataMap.get("messageSegment")),
			end
                    ));
                } else if (x.getNodeName().contains("Java")){
		    String fmt_temp = "%s%s %s JavaScript:\n%s\n%s"; 
                    retString.append(String.format(
		        fmt_temp,
			start,
			dataMap.get("sequenceNumber"),
			dataMap.get("enabled").equals("true") ? "" : "!DISABLED!",
			"<pre>"+dataMap.get("script")+"</pre>",
			end
		    ));
		    // raw data is in script, above
                    // retString.append(dataMap);

                } else if (x.getNodeName().contains("External")){
		    String fmt_temp = "%s%s %s JavaScript from path:\n%s\n%s";
                     retString.append(String.format(
			  fmt_temp,
			  start,
			  dataMap.get("sequenceNumber"),
			  dataMap.get("enabled").equals("true") ? "" : "!DISABLED!",
			  dataMap.get("scriptPath"),
			  end
		    ));
		     

                } else if (x.getNodeName().contains("IteratorStep")) {
		    retString.append(start);
                    retString.append((String) xPath.evaluate("./sequenceNumber",x,XPathConstants.STRING));
                    retString.append("  Loop: " );
                    retString.append( (String) xPath.evaluate("./name",x,XPathConstants.STRING) );
                    //retString.append( " \n" );
		    //start another <ul> in this item retString.append(end);
		    retString.append("<ul>");
                    retString.append( getElements((Node) xPath.evaluate("./properties/children", x, XPathConstants.NODE),html));
		    retString.append("</ul>");
                    //retString.append( "] (end loop)\n" );
		    retString.append(end);
                } else if (x.getNodeName().contains("IteratorRule")) {
                    retString.append((String) xPath.evaluate("./sequenceNumber",x,XPathConstants.STRING));
		    retString.append(start);
                    retString.append("  Loop: " );
                    retString.append( (String) xPath.evaluate("./name",x,XPathConstants.STRING) );
                    retString.append( " (do bracketed items in each loop pass) [\n" );
		    retString.append(end);
                    retString.append( getElements((Node) xPath.evaluate("./properties/children", x, XPathConstants.NODE),html));
		    retString.append(start);
                    retString.append( "] (end loop)\n" );
		    retString.append(end);
                } else {
		    retString.append(start);
                    retString.append(String.format(
		     "%s %s:",
		     dataMap.get("sequenceNumber"),
		     x.getNodeName()
		    ));
                    retString.append(dataMap);
		    retString.append(end);
                }
                if ( x == f.getLastChild()) break;
                x = x.getNextSibling();
                if (x == null)break;

            } while (true);

        }
        return retString;
    }
    static String abbreviateHL7(String inString){
	// typical HL7 usage:
	//   msg['PID']['PID.5']['PID.5.2'].toString()
	// will be abbreviated to:
	//   msg PID.5.2
	// to reduce needless intimidation.
	// Split string at [ and abbreviate if at least three occurrences
	// and first and second occurrence begin with same three letters.
	// abbreviation takes material before the first occurrence,
	// (likely msg or tmp)
	// and adds the last occurrence and removes .toString()
	// (likely SEG.n.m or SEG.n.m.o)
	
        String[] stringArray = inString.split("\\Q[\\E");
	
	if ((stringArray.length >= 4)
	    //&&
	    //(stringArray[1].substring(0,3) == stringArray[2].substring(0,3))
	    ){
	    return stringArray[0] + " " + stringArray[stringArray.length - 1].replace("']","").replace("\\\"]","").replace(".toString()","").substring(1);
        } else {
            return inString;
        }
    }
}
