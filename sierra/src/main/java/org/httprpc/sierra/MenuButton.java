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
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.FocusEvent;

/**
 * Displays a popup menu when pressed.
 */
public class MenuButton extends JButton {
    private JPopupMenu popupMenu = null;

    private HorizontalAlignment popupHorizontalAlignment = HorizontalAlignment.LEADING;
    private VerticalAlignment popupVerticalAlignment = VerticalAlignment.BOTTOM;

    private PopupMenuListener popupMenuListener = new PopupMenuListener() {
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent event) {
            // No-op
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent event) {
            // No-op
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent event) {
            ignorePress = true;
        }
    };

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
                    var size = getSize();
                    var popupMenuSize = popupMenu.getPreferredSize();

                    int x;
                    int y;
                    switch (popupHorizontalAlignment) {
                        case LEADING:
                        case TRAILING: {
                            if (getComponentOrientation().isLeftToRight() ^ popupHorizontalAlignment == HorizontalAlignment.TRAILING) {
                                x = 0;
                            } else {
                                x = size.width - popupMenuSize.width;
                            }

                            break;
                        }

                        case CENTER: {
                            x = (size.width - popupMenuSize.width) / 2;
                            break;
                        }

                        default: {
                            throw new UnsupportedOperationException();
                        }
                    }

                    switch (popupVerticalAlignment) {
                        case TOP: {
                            y = -popupMenuSize.height;
                            break;
                        }

                        case BOTTOM: {
                            y = size.height;
                            break;
                        }

                        case CENTER: {
                            y = (size.height - popupMenuSize.height) / 2;
                            break;
                        }

                        default: {
                            throw new UnsupportedOperationException();
                        }
                    }

                    popupMenu.show(MenuButton.this, x, y);
                }

                ignorePress = false;
            }

            @Override
            public void setRollover(boolean rollover) {
                super.setRollover(rollover);

                ignorePress = false;
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
        if (this.popupMenu != null) {
            this.popupMenu.removePopupMenuListener(popupMenuListener);
        }

        if (popupMenu != null) {
            popupMenu.addPopupMenuListener(popupMenuListener);
        }

        this.popupMenu = popupMenu;
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

    /**
     * Processes a focus event.
     * {@inheritDoc}
     */
    @Override
    protected void processFocusEvent(FocusEvent event) {
        super.processFocusEvent(event);

        ignorePress = (event.getID() == FocusEvent.FOCUS_LOST && event.isTemporary());
    }
}
