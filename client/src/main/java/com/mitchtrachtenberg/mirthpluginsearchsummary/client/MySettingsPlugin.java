/*
 * Copyright 2021 Kaur Palang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mitchtrachtenberg.mirthpluginsearchsummary.client;

import com.kaurpalang.mirth.annotationsplugin.annotation.ClientClass;
import com.mitchtrachtenberg.mirthpluginsearchsummary.client.panel.MainSettingsPanel;
import com.mitchtrachtenberg.mirthpluginsearchsummary.shared.MyConstants;
import com.mirth.connect.client.ui.AbstractSettingsPanel;
import com.mirth.connect.plugins.SettingsPanelPlugin;
import com.mirth.connect.model.converters.ObjectXMLSerializer;

@ClientClass
public class MySettingsPlugin extends SettingsPanelPlugin {

    private MainSettingsPanel mainSettingsPanel;

    public MySettingsPlugin(String name) {
        super(name);
    }

    @Override
    public AbstractSettingsPanel getSettingsPanel() {
        return this.mainSettingsPanel;
    }

    @Override
    public String getPluginPointName() {
        return MyConstants.PLUGIN_POINTNAME;
    }

    @Override
    public void start() {
        System.out.println("MySettingsPlugin start()");
        this.mainSettingsPanel = new MainSettingsPanel();
    }

    @Override
    public void stop() {

    }

    @Override
    public void reset() {

    }
}
