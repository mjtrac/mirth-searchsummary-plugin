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
import java.awt.Desktop;
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
    	text.setText("<html><div>&lt;-- Click Do Channel to open browser.<br/>Use the nonexistent controls to search only channels with<br/>particular connector types, filters or transformers\nand to switch output between collapsible and full display.</div><div>TODO: Handle iteration by extracting children in rules and steps.</br>Add response transformer handling.</br>Add special handling for particular connector types to highlight relevant properties.<br/>For example, port, host, file, scheme, template </div></html");
    	JScrollPane scroll = new JScrollPane(text);
        
        //List Area to choose channel
        JList list = new JList();
        String[] channelNameList;
        channelNameList = new String[20];
        int i = 0;
        //for (Channel channel: channels){
        //	channelNameList[i++] = channel.getName();
        channelNameList[0] = "alpha";
        channelNameList[1] = "beta";
        list.setListData(channelNameList);
        
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
			text.setText("<html>Not implemented.</html>");
		}
	    }
        };
        list.addMouseListener(mouseListener);
        
        JScrollPane listScroll = new JScrollPane(list);
        JLabel label = new JLabel("Hello World");
        JButton button = new JButton("Channels to browser");
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
	/*
        //p.add(split,BorderLayout.CENTER);
        //p.add(button,BorderLayout.SOUTH);
 
        //Display the window.
        //this.pack();
        //this.setVisible(true);

        // this will print the HTML output
        } catch (Exception e) {
        	System.out.println(e.toString());
        } finally {
        	
        };
	*/
        
    }
    
    
    @Override
    public void doRefresh() {

    }

    @Override
    public boolean doSave() {
        return true;
    }
}
