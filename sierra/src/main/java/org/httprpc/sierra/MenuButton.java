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

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.RootPaneContainer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Displays a popup menu when pressed.
 */
public class MenuButton extends JButton {
    private HorizontalAlignment popupHorizontalAlignment = HorizontalAlignment.LEADING;
    private VerticalAlignment popupVerticalAlignment = VerticalAlignment.BOTTOM;

    /**
     * Constructs a new menu button.
     */
    public MenuButton() {
        this(null, null);
    }

    /**
     * Constructs a new menu button.
     *
     * @param text
     * The button text.
     */
    public MenuButton(String text) {
        this(text, null);
    }

    /**
     * Constructs a new menu button.
     *
     * @param icon
     * The button icon.
     */
    public MenuButton(Icon icon) {
        this(null, icon);
    }

    /**
     * Constructs a new menu button.
     *
     * @param text
     * The button text.
     *
     * @param icon
     * The button icon.
     */
    public MenuButton(String text, Icon icon) {
        super(text, icon);

        var popupMenu = new JPopupMenu();

        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            MouseListener mouseListener = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent event) {
                    // No-op
                }
            };

            Component getGlassPane() {
                return ((RootPaneContainer)getTopLevelAncestor()).getRootPane().getGlassPane();
            }

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent event) {
                var glassPane = getGlassPane();

                glassPane.addMouseListener(mouseListener);

                glassPane.setVisible(true);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent event) {
                var glassPane = getGlassPane();

                glassPane.removeMouseListener(mouseListener);

                glassPane.setVisible(false);

                setSelected(false);
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent event) {
                // No-op
            }
        });

        setComponentPopupMenu(popupMenu);

        setModel(new JToggleButton.ToggleButtonModel() {
            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);

                if (selected) {
                    var size = getSize();
                    var popupMenuSize = popupMenu.getPreferredSize();

                    var x = switch (popupHorizontalAlignment.getLocalizedValue(MenuButton.this)) {
                        case LEFT -> 0;
                        case RIGHT -> size.width - popupMenuSize.width;
                        case CENTER -> (size.width - popupMenuSize.width) / 2;
                        default -> throw new UnsupportedOperationException();
                    };

                    var y = switch (popupVerticalAlignment) {
                        case TOP -> -popupMenuSize.height;
                        case BOTTOM -> size.height;
                        case CENTER -> (size.height - popupMenuSize.height) / 2;
                    };

                    popupMenu.show(MenuButton.this, x, y);
                }
            }
        });
    }

    /**
     * Returns the popup's horizontal alignment.
     *
     * @return
     * The popup's horizontal alignment.
     */
    public HorizontalAlignment getPopupHorizontalAlignment() {
        return popupHorizontalAlignment;
    }

    /**
     * Sets the popup's horizontal alignment, relative to the button.
     *
     * @param popupHorizontalAlignment
     * The popup's horizontal alignment.
     */
    public void setPopupHorizontalAlignment(HorizontalAlignment popupHorizontalAlignment) {
        if (popupHorizontalAlignment == null) {
            throw new IllegalArgumentException();
        }

        this.popupHorizontalAlignment = popupHorizontalAlignment;
    }

    /**
     * Returns the popup's vertical alignment.
     *
     * @return
     * The popup's vertical alignment.
     */
    public VerticalAlignment getPopupVerticalAlignment() {
        return popupVerticalAlignment;
    }

    /**
     * Sets the popup's vertical alignment, relative to the button.
     *
     * @param popupVerticalAlignment
     * The popup's vertical alignment.
     */
    public void setPopupVerticalAlignment(VerticalAlignment popupVerticalAlignment) {
        if (popupVerticalAlignment == null) {
            throw new IllegalArgumentException();
        }

        this.popupVerticalAlignment = popupVerticalAlignment;
    }

    @Override
    public Component add(Component component) {
        if (component == null) {
            throw new IllegalArgumentException();
        }

        return getComponentPopupMenu().add(component);
    }

    @Override
    public void remove(Component component) {
        if (component == null) {
            throw new IllegalArgumentException();
        }

        getComponentPopupMenu().remove(component);
    }
}
