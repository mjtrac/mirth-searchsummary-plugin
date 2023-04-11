package com.mitchtrachtenberg.mirthpluginsearchsummary.client.panel;

import java.io.*;
import java.util.List;
import java.lang.String;
import java.util.HashMap;

import com.mirth.connect.model.ChannelProperties;
import com.mirth.connect.model.Channel;
import com.mirth.connect.model.ChannelExportData;
import com.mirth.connect.model.Connector;
import com.mirth.connect.model.converters.ObjectXMLSerializer;
import com.mirth.connect.donkey.model.channel.ConnectorProperties;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.XMLConstants;
import javax.xml.xpath.*;
import javax.xml.xpath.XPathConstants;

public class Summarize {
    static String get_xml(String xmlString, String searchString) {
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
            NodeList nodes = (NodeList) xPath.evaluate(
                    searchString,
                    doc,
                    XPathConstants.NODESET);

            int nodeListLen = nodes.getLength();
            for (int i = 0; i < nodeListLen; i++) {
                Node n = nodes.item(i);
                Node p = nodes.item(i);
                String path = "";
                while (p != null) {
                    String nodeName = p.getNodeName();
                    if (nodeName.equals("connector")) {
                        // find the sequence number and or name child node, and add its text
                        nodeName += ("(" + generateName(p) + ")");
                    }
                    path = nodeName + "/" + path;
                    p = p.getParentNode();
                }
                path = path.replaceAll("#document/", "");
                path = path.replaceAll("com.mirth.connect.plugins.", "");
                path = path.replaceAll("com.mirth.connect.connectors.", "...");
                sb.append(path);
                sb.append(" = ");
                sb.append(n.getTextContent());
                sb.append("\n");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        String retString = escapeHTML(sb.toString());
        return( retString );
    }

    static String generate(boolean htmlqq, List<Channel> channels) {
        StringBuilder sb = new StringBuilder();
        if(htmlqq) {
            sb.append("<html>\n");
            sb.append("<head>\n");
            sb.append(genHTMLAccordionScripts());
            sb.append("</head>\n");
            sb.append("<body>\n");
            sb.append("<div id=\"accordion\">\n");
        }
        for (Channel channel : channels) {
            sb.append(Summarize.genChannel(htmlqq,channel));
        }
        //close accordion div
        if(htmlqq) {
            sb.append("</div>\n");
            sb.append("</body>\n");
            sb.append("</html>\n");
        }
        return (sb.toString());
    }


    static String genHTMLAccordionScripts() {
        return "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.6.3/jquery.min.js\"></script>" +
                "<link rel=\"stylesheet\" href=\"https://ajax.googleapis.com/ajax/libs/jqueryui/1.13.2/themes/smoothness/jquery-ui.css\">" +
                "<script src=\"https://ajax.googleapis.com/ajax/libs/jqueryui/1.13.2/jquery-ui.min.js\"></script>" +
                "<script>  $( function() {\n" +
                "$( \"#accordion\" ).accordion({\n" +
                "active: \"false\",\n" +
                "heightStyle: \"content\",\n" +
                "collapsible: \"true\"\n" +
                "});\n" +
                "} );\n" +
                "</script>\n<script>\n" +
                "$( function() {\n" +
                "$( \".accordion2\" ).accordion({\n" +
                "heightStyle: \"content\",\n" +
                "active: \"false\",\n" +
                "collapsible: \"true\"\n" +
                "});\n" +
                "} );\n" +
                "</script>\n<script>\n" +
                "$( function() {\n" +
                "$( \".accordion3\" ).accordion({\n" +
                "heightStyle: \"content\",\n" +
                "active: \"false\",\n" +
                "collapsible: \"true\"\n" +
                "});\n" +
                "} );\n" +
                "</script>\n";
    }

    static String genChannel(boolean htmlqq, Channel channel) {
        // this creates the h3 header and a div for the channel contents
        String description = channel.getDescription();
        int indent = 0;
        int descriptionLength = 0;
        if (description != null) {
            descriptionLength = description.length();
        } else {
            description = "";
        }
        String deployScript = channel.getDeployScript();
        int deployScriptLength = deployScript.length();
        String undeployScript = channel.getUndeployScript();
        int undeployScriptLength = undeployScript.length();
        String preprocessingScript = channel.getPreprocessingScript();
        int preprocessingScriptLength = preprocessingScript.length();
        String postprocessingScript = channel.getPostprocessingScript();
        int postprocessingScriptLength = postprocessingScript.length();
        // The following variable are either start and end tags or empty strings and newlines,
        // depending on whether we've requested html output (htmlqq)
        String h3s = "<h3>\n";
        String h3e = "</h3>\n";
        String h4s = "<h4>\n";
        String h4e = "</h4>\n";
        String divs = "<div>\n";
        String dive = "</div>\n";
        String pres = "<pre>";
        String pree = "</pre>\n";
        String uls = "<ul>";
        String ule = "</ul>\n";
        if (!htmlqq){
            h3s = "";
            h3e = "\n";
            h4s = "";
            h4e = "\n";
            divs = "";
            dive = "\n";
            pres = "";
            pree = "\n";
            uls = "";
            ule = "\n";
        }
        ChannelProperties props = channel.getProperties();
        ChannelExportData ed = channel.getExportData();
        StringBuilder channelStr = new StringBuilder();
        channelStr.append(h3s);
        channelStr.append(channel.getName());
        channelStr.append(h3e);

        channelStr.append(divs); //creates inner div for accordion
        indent++;

        // ACCORDION LEVEL 2
        channelStr.append(htmlqq ? "<div class=\"accordion2\">\n" : "\n");
        indent++;

        //DESCRIPTION
        channelStr.append(h4s);
        channelStr.append("Description: ");
        channelStr.append(descriptionLength);
        channelStr.append(" chars ");
        channelStr.append(h4e);
        channelStr.append(divs);
        channelStr.append(pres);
        channelStr.append(htmlqq ? escapeHTML(description) : description);
        channelStr.append(pree);
        channelStr.append(dive);

        //PREPROCESS
        channelStr.append(h4s);
        channelStr.append("PreprocessingScript: ");
        channelStr.append(preprocessingScriptLength);
        channelStr.append(" chars ");
        channelStr.append(h4e);
        channelStr.append(divs);
        channelStr.append(pres);
        channelStr.append(escapeHTML(preprocessingScript));
        channelStr.append(pree);
        channelStr.append(dive);

        //POSTPROCESS
        channelStr.append(h4s);
        channelStr.append("PostprocessingScript: ");
        channelStr.append(postprocessingScriptLength);
        channelStr.append(" chars ");
        channelStr.append(h4e);
        channelStr.append(divs);
        channelStr.append(pres);
        channelStr.append(escapeHTML(postprocessingScript));
        channelStr.append(pree);
        channelStr.append(dive);

        //DEPLOY
        channelStr.append(h4s);
        channelStr.append("DeployScript: ");
        channelStr.append(deployScriptLength);
        channelStr.append(" chars ");
        channelStr.append(h4e);
        channelStr.append(divs);
        channelStr.append(pres);
        channelStr.append(escapeHTML(deployScript));
        channelStr.append(pree);
        channelStr.append(dive);

        //UNDEPLOY
        channelStr.append(h4s);
        channelStr.append("UndeployScript: ");
        channelStr.append(undeployScriptLength);
        channelStr.append(" chars ");
        channelStr.append(h4e);
        channelStr.append(divs);
        channelStr.append(pres);
        channelStr.append(escapeHTML(undeployScript));
        channelStr.append(pree);
        channelStr.append(dive);

        //CHANNEL PROPERTIES
        try {
            if (props != null) {
                String propStrXML = ObjectXMLSerializer.getInstance().serialize(props);
                //String propStrResult = get_xml(propStrXML,"//*[not(@*)]");
                String propStrResult = get_xml(propStrXML, "//*[count(*)=0]");

                //CHANNEL PROPERTIES
                channelStr.append(h4s);
                channelStr.append("Channel properties");
                channelStr.append(h4e);
                channelStr.append(divs);
                channelStr.append(pres);
                channelStr.append(propStrResult);
                channelStr.append(pree);
                channelStr.append(dive);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        //EXPORT DATA
        try {
            if (ed != null) {
                String edStrXML = ObjectXMLSerializer.getInstance().serialize(ed);
                //String propStrResult = get_xml(propStrXML,"//*[not(@*)]");
                String edStrResult = get_xml(edStrXML, "//*[count(*)=0]");
                channelStr.append(h4s);
                channelStr.append("Channel export data");
                channelStr.append(h4e);
                channelStr.append(divs);
                channelStr.append(pres);
                channelStr.append(edStrResult);
                channelStr.append(pree);
                channelStr.append(dive);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        //CONNECTORS LOOP
        List<Connector> connList = channel.getDestinationConnectors();
        connList.add(0, channel.getSourceConnector());
        indent++;
        for (Connector conn : connList) {
            channelStr.append(h4s);
            channelStr.append("*CONNECTOR* ");
            channelStr.append(conn.getName());
            channelStr.append( " (" );
            channelStr.append(conn.getTransportName());
            channelStr.append( ")" );
            channelStr.append(h4e);

            channelStr.append(htmlqq ? "<div class=\"accordion3\">\n" : "\n"); // acc3 starts

            //CONNECTOR PROPERTIES
            ConnectorProperties p = conn.getProperties();
            try {
                if (p != null) {
                    String propStrXML = ObjectXMLSerializer.getInstance().serialize(p);
                    //String propStrResult = get_xml(propStrXML,"//*[not(@*)]");
                    String propStrResult = get_xml(propStrXML, "//*[count(*)=0]");
                    channelStr.append(h4s);
                    channelStr.append("Connector properties");
                    channelStr.append(h4e);
                    channelStr.append(divs);
                    channelStr.append(pres);
                    indent++;
                    channelStr.append(propStrResult);
                    indent--;
                    channelStr.append(pree);
                    channelStr.append(dive);
                }
            } catch (Exception e) {
                System.out.println(e);
            }

            //CONNECTOR TRANSFORMER AND FILTER ELEMENTS
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
                Double filterCount = generateElementListCount(doc, "filter");
                Double transformerCount = generateElementListCount(doc, "transformer");
                Double responseTransformerCount = generateElementListCount(doc, "responseTransformer");
                channelStr.append(h4s);
                channelStr.append("Connector filter elements (");
                channelStr.append(filterCount.intValue());
                channelStr.append(")");
                channelStr.append(h4e);
                channelStr.append(divs);
                indent++;
                channelStr.append(uls);
                channelStr.append(generateElementList(doc, "filter", htmlqq));
                channelStr.append(ule);
                channelStr.append(dive);
                indent--;

                channelStr.append(h4s);
                channelStr.append("Connector transformer elements (");
                channelStr.append(transformerCount.intValue());
                channelStr.append(")");
                channelStr.append(h4e);
                channelStr.append(divs);
                indent++;
                channelStr.append(uls);
                channelStr.append(generateElementList(doc, "transformer", htmlqq));
                channelStr.append(ule);
                channelStr.append(dive);
                indent--;
                //channelStr.append( "</div>\n" ); //ends accordion3 div

                channelStr.append(h4s);
                channelStr.append("Connector response transformer elements (");
                channelStr.append(responseTransformerCount.intValue());
                channelStr.append(")");
                channelStr.append(h4e);
                channelStr.append(divs);
                indent++;
                channelStr.append(uls);
                channelStr.append(generateElementList(doc, "responseTransformer", htmlqq));
                channelStr.append(ule);
                channelStr.append(dive);
                indent--;

                channelStr.append(dive); //ends accordion3 div

            } catch (Exception e) {
                System.out.println("Problem with connector html gen");
                System.out.println(e);
            }
        } // end for loop
        indent--;
        channelStr.append(dive); //ends accordion2 div for scripts, connectors
        indent--;
        channelStr.append(dive); //ends accordion div for channel
        indent--;
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


    static Double generateElementListCount(Document doc, String eltype) {
        Double numChildren = 0.0;
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            numChildren = (Double) xPath.evaluate(
                    String.format("count(//%s/elements//sequenceNumber)", eltype),
                    doc,
                    XPathConstants.NUMBER);
        } catch (Exception e) {
            System.out.println(numChildren);

            System.out.println(e.getMessage());
        }
        return (numChildren);
    }

    static String generateName(Node n) {
        String name = "?";
        try {
            NodeList nodes = n.getChildNodes();
            Node nameNode;
            for (int x = 0; x < nodes.getLength(); x++) {
                if (nodes.item(x).getNodeName().equals("name")) {
                    nameNode = nodes.item(x);
                    name = nameNode.getNodeValue() + "," + nameNode.getFirstChild().getNodeValue();
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(name);

            System.out.println(e.getMessage());
        }
        return (name);
    }

    static String generateElementList(Document doc, String eltype, boolean html) {

        StringBuilder retString = new StringBuilder();
        try {

            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList parents = (NodeList) xPath.evaluate(
                    String.format("//%s/elements", eltype),
                    doc,
                    XPathConstants.NODESET);
            for (int i = 0; i < parents.getLength(); i++) {
                retString.append(getElements(parents.item(i), html));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return (retString.toString());
    }

    // getElements: return text describing transformer elements,
    // including those elements on the children list of iterators
    // Call on each descendant of ..../transformer|filterI'm /elements
    static StringBuilder getElements(Node transformerOrChildrenNode, boolean htmlqq) throws javax.xml.xpath.XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        StringBuilder retString = new StringBuilder();
        String listitemstart = "";
        String listitemend = "";
	String uls = "";
	String ule = "\n";
        if (htmlqq) {
            listitemstart = "<li>";
            listitemend = "</li>";
	    uls = "<ul>";
	    ule = "</ul>\n";
        }
        if (transformerOrChildrenNode == null) {
            retString.append("Got null child.\n");
            return retString;
        }

        if (transformerOrChildrenNode.getNodeName().contains("Iterat")) {
            transformerOrChildrenNode = (Node) xPath.evaluate("./properties/children", transformerOrChildrenNode, XPathConstants.NODE);
        }
        if (transformerOrChildrenNode.getNodeName().equals("elements")
                || transformerOrChildrenNode.getNodeName().equals("children")) {
            Node f = transformerOrChildrenNode.getFirstChild();
            Node x = f;
            do {
                if (x == null) break;
                if (x.getNodeName().contains("#text")) {
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
                            listitemstart,
                            dataMap.get("sequenceNumber"),
                            dataMap.get("enabled").equals("true") ? "" : "!DISABLED!",
                            abbreviateHL7(dataMap.get("mapping")),
                            dataMap.get("defaultValue"),
                            dataMap.get("scope").toLowerCase(),
                            dataMap.get("variable"),
                            listitemend
                    ));
                } else if (x.getNodeName().contains("RuleBuilderRule")) {
                    String op = dataMap.get("operator");
                    if (op == null) op = " ";
                    String fmt_temp = "%s%s %s %s Filter rule: The value of %s %s %s.%s\n";
                    retString.append(String.format(
                            fmt_temp,
                            listitemstart,
                            op,
                            dataMap.get("sequenceNumber"),
                            dataMap.get("enabled").equals("true") ? "" : "!DISABLED!",
                            abbreviateHL7(dataMap.get("field")),
                            abbreviateHL7(dataMap.get("condition")),
                            dataMap.get("values"),
                            listitemend
                    ));
                } else if (x.getNodeName().contains("MessageBuilderStep")) {
                    String fmt_temp = "%s %s %s MessageBuilder: Put the value of %s (\"%s\" if empty) into %s.\n%s";
                    retString.append(String.format(
                            fmt_temp,
                            listitemstart,
                            dataMap.get("sequenceNumber"),
                            dataMap.get("enabled").equals("true") ? "" : "!DISABLED!",
                            abbreviateHL7(dataMap.get("mapping")),
                            dataMap.get("defaultValue"),
                            abbreviateHL7(dataMap.get("messageSegment")),
                            listitemend
                    ));
                } else if (x.getNodeName().contains("Java")) {
                    String fmt_temp = "%s%s %s JavaScript:\n%s\n%s";
                    retString.append(String.format(
                            fmt_temp,
                            listitemstart,
                            dataMap.get("sequenceNumber"),
                            dataMap.get("enabled").equals("true") ? "" : "!DISABLED!",
                            "<pre>" + dataMap.get("script") + "</pre>",
                            listitemend
                    ));
                    // raw data is in script, above
                    // retString.append(dataMap);

                } else if (x.getNodeName().contains("External")) {
                    String fmt_temp = "%s%s %s JavaScript from path:\n%s\n%s";
                    retString.append(String.format(
                            fmt_temp,
                            listitemstart,
                            dataMap.get("sequenceNumber"),
                            dataMap.get("enabled").equals("true") ? "" : "!DISABLED!",
                            dataMap.get("scriptPath"),
                            listitemend
                    ));


                } else if (x.getNodeName().contains("IteratorStep")) {
                    retString.append(listitemstart);
                    retString.append((String) xPath.evaluate("./sequenceNumber", x, XPathConstants.STRING));
                    retString.append("  Loop: ");
                    retString.append((String) xPath.evaluate("./name", x, XPathConstants.STRING));
                    //retString.append( " \n" );
                    //start another <ul> in this item retString.append(listitemend);
                    retString.append(uls);
                    retString.append(getElements((Node) xPath.evaluate("./properties/children", x, XPathConstants.NODE), htmlqq));
                    retString.append(ule);
                    //retString.append( "] (end loop)\n" );
                    retString.append(listitemend);
                } else if (x.getNodeName().contains("IteratorRule")) {
                    retString.append((String) xPath.evaluate("./sequenceNumber", x, XPathConstants.STRING));
                    retString.append(listitemstart);
                    retString.append("  Loop: ");
                    retString.append((String) xPath.evaluate("./name", x, XPathConstants.STRING));
                    retString.append(" (do bracketed items in each loop pass) [\n");
                    retString.append(listitemend);
                    retString.append(getElements((Node) xPath.evaluate("./properties/children", x, XPathConstants.NODE), htmlqq));
                    retString.append(listitemstart);
                    retString.append("] (end loop)\n");
                    retString.append(listitemend);
                } else {
                    retString.append(listitemstart);
                    retString.append(String.format(
                            "%s %s:",
                            dataMap.get("sequenceNumber"),
                            x.getNodeName()
                    ));
                    retString.append(dataMap);
                    retString.append(listitemend);
                }
                if (x == f.getLastChild()) break;
                x = x.getNextSibling();
                if (x == null) break;

            } while (true);

        }
        return retString;
    }

    static String abbreviateHL7(String inString) {
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
        ) {
            return stringArray[0] + " " + stringArray[stringArray.length - 1].replace("']", "").replace("\\\"]", "").replace(".toString()", "").substring(1);
        } else {
            return inString;
        }
    }
}
