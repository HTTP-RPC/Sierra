/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.httprpc.sierra;

import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import java.awt.event.FocusEvent;

/**
 * Presents a popup menu when pressed.
 */
public class MenuButton extends JButton {
    private JPopupMenu popupMenu = null;

    private boolean ignorePress = false;

    /**
     * Constructs a menu button.
     */
    public MenuButton() {
        this(null, null);
    }

    /**
     * Constructs a menu button.
     *
     * @param text
     * The button text.
     */
    public MenuButton(String text) {
        this(text, null);
    }

    /**
     * Constructs a menu button.
     *
     * @param icon
     * The button icon.
     */
    public MenuButton(Icon icon) {
        this(null, icon);
    }

    /**
     * Constructs a menu button.
     *
     * @param text
     * The button text.
     *
     * @param icon
     * The button icon.
     */
    public MenuButton(String text, Icon icon) {
        super(text, icon);

        setModel(new DefaultButtonModel() {
            @Override
            public void setPressed(boolean pressed) {
                super.setPressed(pressed);

                if (popupMenu == null) {
                    return;
                }

                if (pressed && !ignorePress) {
                    popupMenu.show(MenuButton.this, 0, getHeight());
                }
            }
        });
    }

    /**
     * Returns the popup menu.
     *
     * @return
     * The popup menu, or {@code null} if no popup menu is set.
     */
    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    /**
     * Sets the popup menu.
     *
     * @param popupMenu
     * The popup menu, or {@code null} for no popup menu.
     */
    public void setPopupMenu(JPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
    }

    /**
     * Processes a focus event.
     * {@inheritDoc}
     */
    @Override
    protected void processFocusEvent(FocusEvent event) {
        super.processFocusEvent(event);

        if (event.getID() == FocusEvent.FOCUS_LOST) {
            ignorePress = event.isTemporary();
        } else {
            ignorePress = false;
        }
    }
}
