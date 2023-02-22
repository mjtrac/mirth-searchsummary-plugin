package com.mitchtrachtenberg.mirthpluginsearchsummary.client.panel;

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

public class Summarize {
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
	StringBuilder sb = new StringBuilder();
    	sb.append("<html>");
	sb.append("<link rel=\"stylesheet\" href=\"C:/Users/mjtra/jquery-ui-1.13.2/jquery-ui-1.13.2/jquery-ui.min.css\">");
	sb.append("<script src=\"C:/Users/mjtra/jquery-ui-1.13.2/jquery-ui-1.13.2/external/jquery/jquery.js\"></script>");
	sb.append("<script src=\"C:/Users/mjtra/jquery-ui-1.13.2/jquery-ui-1.13.2/jquery-ui.min.js\"></script>\n");
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
	//open accordion div
	sb.append("</script></head><body><div id=\"accordion\">");

        for (Channel channel: channels){
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
        channelStr += ("<h4>Description:</h4><div><pre>" + escapeHTML(channel.getDescription()) + "</pre></div>");
	channelStr += ("<h4>PreprocessingScript: </h4>");
	channelStr += ("<div><pre>" + escapeHTML(channel.getPreprocessingScript()) + "</pre></div>\n");
	channelStr += ("<h4>PostprocessingScript: </h4>");
	channelStr += ("<div><pre>" + escapeHTML(channel.getPostprocessingScript()) + "</pre></div>\n");
	channelStr += ("<h4>DeployScript: </h4>");
	channelStr += ("<div><pre>" + escapeHTML(channel.getDeployScript()) + "</pre></div>\n");
	channelStr += ("<h4>UndeployScript: </h4>");
	channelStr += ("<div><pre>" + escapeHTML(channel.getUndeployScript()) + "</pre></div>\n");
        List<Connector> connList = channel.getDestinationConnectors();
	connList.add(0,channel.getSourceConnector());
	for (Connector conn: connList){
	    channelStr += ("<h4>C O N N E C T O R " + conn.getName() + " (" + conn.getTransportName() + ")</h4>\n");
	    channelStr += "<div>\n"; // conn contents division starts
	    Transformer t = conn.getTransformer();
	    Filter f = conn.getFilter();
	    ConnectorProperties p = conn.getProperties();
	    String propStr;
	    try {
		if(p != null){
		    propStr = escapeHTML(ObjectXMLSerializer.getInstance().serialize(p));
		}
	    } catch (Exception e){
	    }
	    /*
	      try {
		if(p != null){
		    propStr = escapeHTML(p.toFormattedString());
		} else {
		    propStr = "No conn props from connector.getProperties()";
		}
		channelStr += ("<div>Props: <pre>" + propStr +  "</pre></div>");
	    } catch (Exception e){
		System.out.println(e);
		System.out.println("Problem with translating connector props.");
		channelStr += ("<div>Props: <pre>Could not get info from " + p +  "</pre></div>");
		}
	    */
	    List<Rule> ruleList = f.getElements();
	    List<Step> stepList = t.getElements();
	    String sl = " " + stepList;
	    sl = sl.replace(",","\n");
	    //try {List<Step> respStepList = rt.getElements();}
	    //catch (Exception e) {System.out.println("Null rt");}
    
	    channelStr += ("<div>Transformer:<pre>" + sl + "</pre></div>\n");
	    channelStr += "<ul>\n";
	    for (Step s: stepList){
		channelStr += ("<li>" + s.getName() + "</li>\n");
	    }
	    channelStr += "</ul>\n";
	    //channelStr += ("<div>RespTrans<pre>" + rt + "</pre></div>\n");
	    channelStr += ("<div>Filter: " + f +  "</div>");
	    channelStr += "<ul>";
	    for (Rule r: ruleList){
		channelStr += ("<li>Rule:" + r.getName() + " " + r.getOperator() +  "</li>");
	    }
	    channelStr += "</ul>\n";
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

