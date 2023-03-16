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
import net.miginfocom.swing.MigLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;

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

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JCheckBox;
import javax.swing.JTextField;


import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import java.util.ArrayList;

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
import com.mirth.connect.model.converters.ObjectXMLSerializer;
import com.mirth.connect.plugins.*;
import com.mirth.connect.plugins.rulebuilder.RuleBuilderRule;
//import com.mirth.connect.plugins.javascriptrule.JavaScriptRule;
//import com.mirth.connect.plugins.mapper.MapperStep;
import com.mirth.connect.plugins.messagebuilder.MessageBuilderStep;
import com.mitchtrachtenberg.mirthpluginsearchsummary.shared.MyConstants;
import com.mirth.connect.donkey.model.*;

import java.io.*;
//import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;

//import net.miginfocom.swing.MigLayout;
import javax.swing.JTextField;
import javax.swing.JTextArea;

public class MainSettingsPanel extends AbstractSettingsPanel {
    JTextField jtf;
    JTextArea jta;

    public MainSettingsPanel() {
        // The name of our tab in the Settings menu
        super(MyConstants.SETTINGS_TABNAME_MAIN);
        initComponents();

        addTask("doChannel", "Browse channel docs", "Browse channel docs.", "", new ImageIcon(com.mirth.connect.client.ui.Frame.class.getResource("images/table.png")));

    }

    public void doSearch(){

        SwingWorker<String,Void> worker = new SwingWorker<String, Void>() {
            @Override
            public String doInBackground() {
                StringBuilder sb = new StringBuilder();
                try {
                    for (Channel c : PlatformUI.MIRTH_FRAME.mirthClient.getAllChannels()) {
                        sb.append(Search.generate_search(c,jtf.getText()));
                    }
                } catch (Exception e){
                    System.out.println(e.toString());
                }
                jta.setText(sb.toString());
                return("");
            }
            @Override
            public void done() {
            }

        };
        worker.execute();
    }
    
    public void doChannel(){
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
		@Override
		public String doInBackground() {
		    try {
			String channelStr = Summarize.generate_all(
			    PlatformUI.MIRTH_FRAME.mirthClient.getAllChannels());
			Path tempFile = Files.createTempFile(null, ".html");
			Desktop desk = Desktop.getDesktop();
			Files.write(tempFile,
				    channelStr.getBytes(
							StandardCharsets.UTF_8
							)
				    );
			desk.browse(tempFile.toUri());
		    } catch (Exception e){
			System.out.println(e);
		    }
		    return "Done";
		}
		@Override
		public void done() {
		}
	    };
	worker.execute();
    }


    private void initChannelList(){
        //List Area to choose channel
        JList list = new JList();
	java.util.ArrayList channelNameArrayList = new ArrayList();
        //String[] channelNameList;
        //channelNameList = new String[200];
        int i = 0;
	try {
	    for (Channel channel: PlatformUI.MIRTH_FRAME.mirthClient.getAllChannels()){
		channelNameArrayList.add(channel.getName());
	    }
	    String[] channelNameList = new String[channelNameArrayList.size()];
	    list.setListData(channelNameArrayList.toArray());

	} catch (Exception e){
	    System.out.println(e.toString());
	}        
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
		       System.out.println("List doubleclick not implemented.");
		}
	    }
        };
        list.addMouseListener(mouseListener);
        return;
    }
    private void initHTMLEdit() {
    }

    private void initComponents() {
        setBackground(Color.YELLOW);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setLayout(new BorderLayout());


        
	JPanel panel = new JPanel(new MigLayout());

	// Search components
	JLabel search_header = new JLabel("<html><h2>Search across all channels</h2></html>"); 
	JLabel search_label = new JLabel("Search text: ");
	JLabel search_results_label = new JLabel("Search results:");
	this.jtf = new JTextField("",30);
        JButton search_button = new JButton("Search");
	this.jta = new JTextArea(10,100);
	JScrollPane results_scroll = new JScrollPane(this.jta);

	// search on return key in text or on button press
        search_button.addActionListener(new ActionListener() {
            @Override 
            public void actionPerformed(ActionEvent e) {
		doSearch();
            }
        });
        this.jtf.addActionListener(new ActionListener() {
            @Override 
            public void actionPerformed(ActionEvent e) {
		doSearch();
            }
        });

	// Lay out components, having text area take all excess space
	panel.add(search_header, "wrap" );

	panel.add(search_label,"wrap");

	panel.add(jtf, "split 2");
	panel.add(search_button, "wrap");

	panel.add(search_results_label, "wrap");
	    
	panel.add(results_scroll, "push, grow");

        //Split pane to house channel list if desired
        /*
	  JSplitPane split = new JSplitPane(
        		JSplitPane.HORIZONTAL_SPLIT,
			listScroll, 
			panel);
	*/

	this.add(panel);
	this.setVisible(true);
        
    }
    
    
    @Override
    public void doRefresh() {
	// rebuild channel list if included
    }

    @Override
    public boolean doSave() {
        return true;
    }
}
