package com.mitchtrachtenberg.mirthpluginsearchsummary.client.panel;


import com.mirth.connect.model.Channel;
import com.mirth.connect.model.Connector;
import com.mirth.connect.model.Filter;
import com.mirth.connect.model.Rule;
import com.mirth.connect.model.Step;
import com.mirth.connect.model.Transformer;
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
	String x = "<pre>Summary</pre>";
	return x;
    }
}
