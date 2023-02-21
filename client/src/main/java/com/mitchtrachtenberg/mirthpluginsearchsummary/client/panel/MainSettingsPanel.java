/*
 * Copyright 2021 Kaur Palang
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mitchtrachtenberg.mirthpluginsearchsummary.client.panel;

import com.mitchtrachtenberg.mirthpluginsearchsummary.shared.MyConstants;
import com.mirth.connect.client.ui.AbstractSettingsPanel;
import com.mirth.connect.client.ui.components.MirthCheckBox;
import com.mirth.connect.client.ui.components.MirthPasswordField;
import com.mirth.connect.client.ui.components.MirthTextField;
import net.miginfocom.swing.MigLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import com.mirth.connect.client.ui.AbstractSettingsPanel;
import com.mirth.connect.client.ui.PlatformUI;
//import com.mirth.connect.client.ui.components.MirthCheckBox;
//import com.mirth.connect.client.ui.components.MirthPasswordField;
//import com.mirth.connect.client.ui.components.MirthTextField;
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

//import net.miginfocom.swing.MigLayout;

public class MainSettingsPanel extends AbstractSettingsPanel {

    public MainSettingsPanel() {
        // The name of our tab in the Settings menu
        super(MyConstants.SETTINGS_TABNAME_MAIN);
        addTask("doSearch", "Do Search", "Do Search.", "", new ImageIcon(com.mirth.connect.client.ui.Frame.class.getResource("images/table.png")));
        addTask("doChannel", "Do Channel", "Do Channel.", "", new ImageIcon(com.mirth.connect.client.ui.Frame.class.getResource("images/table.png")));
        initComponents();
    }

    public void doSearch(){
        System.out.println("DO SEARCH!");
    }
    
    public void doChannel(){
        System.out.println("DO CHANNEL!");
	
        //createAndShowGUI();
    }

    private void initComponents() {
        setBackground(Color.YELLOW);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setLayout(new BorderLayout());

        
    	List<Channel> channels =  null;
    	try {
            channels = PlatformUI.MIRTH_FRAME.mirthClient.getAllChannels();
            System.out.println(channels);
        } catch (Exception e){
            System.out.println(e);
        }
    	
    	String channelStr = "<html>";
        for (Channel channel: channels){
          Connector s = channel.getSourceConnector();
          List<Connector> destList = channel.getDestinationConnectors();
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
	  channelStr += ("<h4>PreprocessingScript: </h4>");
          Transformer t = s.getTransformer();
          channelStr += ("<h2>" + s.getName() + " (" + s.getClass() + ")</h2>");
          Filter f = s.getFilter();
	  channelStr += Summarize.generate_summary(channel);
          channelStr += ("<div>IN: " + t.getInboundDataType() +" OUT: "+ t.getOutboundDataType() + "</div>");
          List<Rule> rules = f.getElements();
          channelStr += ("<h3>Filters:</h3>");
	  try {
            for (Rule rule: rules){
	      if (rule.getType().toString() == "Rule Builder"){
		      channelStr += ("<div>");
		      channelStr += ("Accept if " + ((RuleBuilderRule)rule).getField().toString() + " " + ((RuleBuilderRule)rule).getCondition().toString() + " " + ((RuleBuilderRule)rule).getValues().toString() + "</div>");
	      } 
	      /*
	      if (rule.getType().toString().contains( "Java")){
			channelStr += ("<pre>Accept if script returns true:\n"+((JavaScriptRule)rule).getScript() + "</pre>");
	      } 
	      */
            }
	  } catch (Exception e) {
		  System.out.println(e.toString());
	  }
          channelStr += ("<h3>Transformers:</h3>");
          List<Step> el = t.getElements();
	  try {
          for (Step fte: el){
              //channelStr += ("<div>" + fte.getClass() + fte + "</div>");
              channelStr += ("<div>" + fte.getType() + "</div>");
	      try {
		/*
		if (fte.getType().toString().contains("Mapper")){
		      channelStr += "<div>Set variable [" + ((MapperStep)fte).getVariable() + "] to [value in [" + ((MapperStep)fte).getMapping() + " in [" + ((MapperStep)fte).getScope().toString() + "]</div>";
		}
		*/
		if (fte.getType().toString().contains("Message")){
		      channelStr += "<div>Set [" + ((MessageBuilderStep)fte).getMessageSegment() + " from mapping: " + ((MessageBuilderStep)fte).getMapping() + "</div>" ;
		}		
	      } catch (Exception e2) {
		System.out.println(e2.toString());
	      }

          }
	} catch (Exception e) {
		System.out.println(e.toString());
	}
          for (Connector d: destList){
              channelStr += ("<h2>" + d.getName() + "</h2>");
              Transformer dt = d.getTransformer();
              Filter df = d.getFilter();
              channelStr += ("<h3>IN: " + dt.getInboundDataType() + " OUT: " +dt.getOutboundDataType() + "</h3>");
              List<Rule> frules = df.getElements();
              for (Rule frule: frules){
		  //channelStr += ("<div>" + frule.getClass() + "  "  + "</div>");//channelStr += 
              	channelStr += ("<div>" + frule.getType() + "  "  + "</div>");//channelStr += 
                channelStr += ("<h3>" + frule + "</h3>");
              }
              List<Step> del = dt.getElements();
              for (Step fte: del){
              	channelStr += ("<div>" + fte.getClass() + fte + "</div>");
              	channelStr += ("<div>" + fte.getType() + "</div>");
                channelStr += ("<h3>" + fte + "</h3>");
              }
          }
        }
        channelStr += "</html>";


        //HTML Area for channel
        JEditorPane text = new JEditorPane();
        text.setContentType("text/html");
        HTMLEditorKit kit = (HTMLEditorKit) text.getEditorKitForContentType("text/html");
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {color:#000; font-family:times; margin: 4px; }");
        styleSheet.addRule("h1 {color: blue;}");
        styleSheet.addRule("h2 {color: blue;}");
        styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");
        kit.setStyleSheet(styleSheet);
    	text.setText(channelStr);
    	JScrollPane scroll = new JScrollPane(text);
        
        //List Area to choose channel
        JList list = new JList();
        String[] channelNameList;
        channelNameList = new String[20];
        int i = 0;
        for (Channel channel: channels){
        	channelNameList[i++] = channel.getName();
        //channelNameList[0] = "alpha";
        //channelNameList[1] = "beta";
        list.setListData(channelNameList);
        
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {


                   String selectedItem = (String) list.getSelectedValue();
                   // add selectedItem to your second list.
                   text.setText("<html><h1>" + selectedItem + "</h1></html>");

                 }
            }
        };
        list.addMouseListener(mouseListener);
        
        JScrollPane listScroll = new JScrollPane(list);
        JLabel label = new JLabel("Hello World");
        JButton button = new JButton("Execute task");
        button.addActionListener(new ActionListener() {
            @Override 
            public void actionPerformed(ActionEvent e) {
                SwingWorker<String, Void> worker 
                    = new SwingWorker<String, Void>() {

                    @Override
                    public String doInBackground() {
                        String myString = "<html><h1>From swing worker</h1></html>";
                        return myString;
                    }
                    @Override
                    public void done() {
                        try {
                            text.setText(get());
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        } catch (ExecutionException ex) {
                            ex.printStackTrace();
                        }
                    }
                };
                worker.execute();
            }
        });
        //Split pane to house list and HTML display scrolled panes
        JSplitPane split = new JSplitPane(
        		JSplitPane.HORIZONTAL_SPLIT,
                listScroll, 
                scroll);

        this.add(split,BorderLayout.CENTER);
        this.add(button,BorderLayout.SOUTH);
        //p.add(split,BorderLayout.CENTER);
        //p.add(button,BorderLayout.SOUTH);
 
        //Display the window.
        //this.pack();
        //this.setVisible(true);
        }
    }
/*
        // this will print the HTML output
        } catch (Exception e) {
        	System.out.println(e.toString());
        } finally {
        	
        };
*/
    
    
    @Override
    public void doRefresh() {

    }

    @Override
    public boolean doSave() {
        return true;
    }
}
