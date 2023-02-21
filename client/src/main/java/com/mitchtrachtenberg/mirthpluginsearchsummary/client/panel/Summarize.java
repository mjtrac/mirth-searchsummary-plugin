package com.mitchtrachtenberg.mirthpluginsearchsummary.client.panel;

import java.util.List;

import com.mirth.connect.model.Channel;
import com.mirth.connect.model.Connector;
import com.mirth.connect.model.Filter;
import com.mirth.connect.model.Rule;
import com.mirth.connect.model.Step;
import com.mirth.connect.model.FilterTransformerElement;
import com.mirth.connect.model.Transformer;
import com.mirth.connect.donkey.model.channel.ConnectorProperties;
import com.mirth.connect.plugins.*;
import com.mirth.connect.plugins.rulebuilder.RuleBuilderRule;
//import com.mirth.connect.plugins.javascriptrule.JavaScriptRule;
//import com.mirth.connect.plugins.mapper.MapperStep;

import com.mirth.connect.plugins.messagebuilder.MessageBuilderStep;
import com.mitchtrachtenberg.mirthpluginsearchsummary.shared.MyConstants;
import com.mirth.connect.donkey.model.*;
import com.mirth.connect.model.Channel;

public class Summarize {
    static String generate_summary(Channel channel){
	String channelStr = "";
	channelStr += ("<h1>" + channel.getName() + "</h1>\n");
        channelStr += ("<pre>Description:" + channel.getDescription() + "</pre>");
	channelStr += ("<h4>PreprocessingScript: </h4>");
	channelStr += ("<pre>" + channel.getPreprocessingScript() + "</pre>");
	channelStr += ("<h4>PostprocessingScript: </h4>");
	channelStr += ("<pre>" + channel.getPostprocessingScript() + "</pre>");
	channelStr += ("<h4>DeployScript: </h4>");
	channelStr += ("<pre>" + channel.getDeployScript() + "</pre>");
	channelStr += ("<h4>UndeployScript: </h4>");
	channelStr += ("<pre>" + channel.getUndeployScript() + "</pre>");
        List<Connector> connList = channel.getDestinationConnectors();
	connList.add(0,channel.getSourceConnector());
	for (Connector conn: connList){
	    channelStr += ("<h4>C O N N E C T O R " + conn.getName() + "</h4>");
	    Transformer t = conn.getTransformer();
	    Transformer rt = conn.getResponseTransformer();
	    Filter f = conn.getFilter();
	    String tn = conn.getTransportName();
	    ConnectorProperties p = conn.getProperties();
	    List<Rule> ruleList = f.getElements();
	    List<Step> stepList = t.getElements();
	    try {List<Step> respStepList = rt.getElements();}
	    except (Exception e) {System.out.println("Null rt");}
	    channelStr += ("<div>Props<pre>" + p +  "</pre></div>");
	    channelStr += ("<div>Transport<pre>" + tn +  "</pre></div>");
    
	    channelStr += ("<div>Transformer<pre>" + t + stepList + "</pre></div>");
	    channelStr += "<ul>";
	    for (Step s: stepList){
		channelStr += ("<li>" + s.getName() + "</li>");
	    }
	    channelStr += "</ul>";
	    channelStr += ("<div>RespTrans<pre>" + rt + "</pre></div>");
	    channelStr += ("<div>Filter<pre>" + f +  ruleList + "</pre></div>");	}
	return channelStr;
    }
}
