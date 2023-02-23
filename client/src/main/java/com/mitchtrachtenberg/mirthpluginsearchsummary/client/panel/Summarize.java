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
	    String propStr;
	    try {
		if(p != null){
		    propStr = escapeHTML(ObjectXMLSerializer.getInstance().serialize(p));
		    channelStr += "<h4>Connector properties</h4>\n";
		    channelStr += ("<div><pre>" + propStr +  "</pre>\n</div>\n");
		}
	    } catch (Exception e){
		System.out.println(e);
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
	    try {
		List<Rule> ruleList = f.getElements();
		List<Step> stepList = t.getElements();
		String sl = " " + stepList;
		sl = sl.replace(",","\n");
		String rl = " " + ruleList;
		rl = rl.replace(",","\n");
		String[] stepStrList = sl.split("Step\\[");
		String[] ruleStrList = rl.split("Rule\\[");
		System.out.println("Step");
		System.out.println(stepStrList);
		System.out.println("Rule");
		System.out.println(ruleStrList);
		channelStr += "<h4>Connector transformers</h4>\n";
		channelStr += "<div><ul>\n";
		int i = 1; // the 0th entry is before the first step
		for (Step s: stepList){
		    channelStr += ("<li>" + s.getName());
		    channelStr += ("<pre>" + stepStrList[i++] + "</pre></li>\n");
		}
		channelStr += "</ul></div>\n";
		channelStr += ("<h4>Connector filters </h4>");
		channelStr += "<div><ul>";
		i=1; // the 0th entry is before the first rule
		for (Rule r: ruleList){
		    String ruleOpStr = "";
		    try {
			ruleOpStr = r.getOperator().name();
		    } catch (Exception e){
			System.out.println("Problem with getOperator: null? " + r);
		    }
		    channelStr += ("<li>" + ruleOpStr + " " + r.getName() );
		    channelStr += ("<pre> " + ruleStrList[i++] + "</pre>\n</li>\n");
		}
	    } catch (Exception e) {
		System.out.println("Problem with rule or step list");
		System.out.println(e);
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

