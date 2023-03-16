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
        addTask("doSearch", "Do Search", "Do Search.", "", new ImageIcon(com.mirth.connect.client.ui.Frame.class.getResource("images/table.png")));

        addTask("doChannel", "Browse channel docs", "Browse channel docs.", "", new ImageIcon(com.mirth.connect.client.ui.Frame.class.getResource("images/table.png")));

    }

    public void doSearch(){

        System.out.println("DO SEARCH PRESSED!");
        SwingWorker<String,Void> worker = new SwingWorker<String, Void>() {
            @Override
            public String doInBackground() {
                System.out.println("DO SEARCH DO IN BACKGROUND");
                System.out.println("JTF getText: " + jtf.getText());
                System.out.println("JTA getText: " + jta.getText());
                StringBuilder sb = new StringBuilder();
                try {
                    for (Channel c : PlatformUI.MIRTH_FRAME.mirthClient.getAllChannels()) {
                        sb.append(Search.generate_search(c,jtf.getText()));
                    }
                } catch (Exception e){
                    System.out.println(e.toString());
                }
                jta.setText(jtf.getText() + sb.toString());
                System.out.println("JTF getText: " + jtf.getText());
                System.out.println("JTA getText: " + jta.getText());
                return ("hello");
            }
            @Override
            public void done() {
                System.out.println("DO SEARCH'S WORKER DONE.");
            }

        };
        worker.execute();
    }
    
    public void doChannel(){
        System.out.println("DO CHANNEL PRESSED, using Summarize.useDocumentHelper");
	    System.out.println("DO CHANNEL PRESSED, STARTING WORKER");
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
		@Override
		public String doInBackground() {
		    try {
			System.out.println("CALLING SUMMARIZE.generate_all");
			String channelStr = Summarize.generate_all(
								   PlatformUI.MIRTH_FRAME.mirthClient.getAllChannels());
			System.out.println("BACK FROM SUMMARIZE.generate_all");
			Path tempFile = Files.createTempFile(null, ".html");
			System.out.println("WROTE " + tempFile);
			System.out.println("Desktop.getDesktop()");
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
		    System.out.println("DO CHANNEL'S WORKER DONE.");
		}
	    };
	worker.execute();
    }

    private void initComponents() {
        setBackground(Color.YELLOW);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setLayout(new BorderLayout());


        
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
        
        JScrollPane listScroll = new JScrollPane(list);
        JLabel label = new JLabel("Hello World");
	JPanel panel = new JPanel(new MigLayout());

	panel.add(new JLabel("<html><h2>Search across all channels</h2></html>"), "wrap" );
	//panel.add(new JCheckBox("HTML output?"),"wrap");
	panel.add(new JLabel("Search string: " ),"wrap");
	this.jtf = new JTextField("",30);
	panel.add(jtf);

        JButton button = new JButton("Search");
        button.addActionListener(new ActionListener() {
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

	panel.add(button, "wrap");
	panel.add(new JLabel("Search results:"), "wrap");
	this.jta = new JTextArea(10,128);
	panel.add(jta, "span 2 12, grow");
        //Split pane to house list and
	//control panel widgets.
	//HTML display scrolled panes
        JSplitPane split = new JSplitPane(
        		JSplitPane.HORIZONTAL_SPLIT,
                listScroll, 
			//        scroll);
			panel);

	this.add(split);
	this.setVisible(true);
        
    }
    
    
    @Override
    public void doRefresh() {

    }

    @Override
    public boolean doSave() {
        return true;
    }
}
