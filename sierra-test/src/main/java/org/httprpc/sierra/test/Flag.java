/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.httprpc.sierra.test;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.Icon;

public class Flag {
    private Icon icon;
    private String name;
    private String description;

    public Flag(String iconName, String name, String description) {
        var icon = new FlatSVGIcon(getClass().getResource(String.format("flags/%s", iconName)));

        this.icon = icon.derive(30, 30);

        this.name = name;
        this.description = description;
    }

    public Icon getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
