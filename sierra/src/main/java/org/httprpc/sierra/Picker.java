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

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyEvent;

/**
 * Abstract base class for picker components.
 */
public abstract class Picker extends JTextField {
    private HorizontalAlignment popupHorizontalAlignment = HorizontalAlignment.LEADING;
    private VerticalAlignment popupVerticalAlignment = VerticalAlignment.BOTTOM;

    private Popup popup = null;
    private JComponent popupComponent = null;

    private HierarchyBoundsListener hierarchyBoundsListener = new HierarchyBoundsListener() {
        @Override
        public void ancestorMoved(HierarchyEvent e) {
            hidePopup();
        }

        @Override
        public void ancestorResized(HierarchyEvent e) {
            hidePopup();
        }
    };

    /**
     * Constructs a new picker.
     *
     * @param columns
     * The column count.
     */
    protected Picker(int columns) {
        super(columns);
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
     * Sets the popup's horizontal alignment, relative to the picker.
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
     * Sets the popup's vertical alignment, relative to the picker.
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

        switch (event.getID()) {
            case FocusEvent.FOCUS_GAINED -> showPopup();
            case FocusEvent.FOCUS_LOST -> {
                if (!inPopup(event.getOppositeComponent())) {
                    hidePopup();
                } else {
                    requestFocus();
                }
            }
        }
    }

    private boolean inPopup(Component component) {
        while (component != null) {
            if (component.equals(popupComponent)) {
                return true;
            }

            component = component.getParent();
        }

        return false;
    }
  
    /**
     * Processes a key event.
     * {@inheritDoc}
     */
    @Override
    protected void processKeyEvent(KeyEvent event) {
        super.processKeyEvent(event);

        if (event.getID() == KeyEvent.KEY_PRESSED) {
            hidePopup();
        }
    }

    /**
     * Indicates that the popup is enabled.
     *
     * @return
     * {@code true} if the popup is enabled; {@code false}, otherwise.
     */
    protected abstract boolean isPopupEnabled();

    /**
     * Returns the popup component.
     *
     * @return
     * The popup component.
     */
    protected abstract JComponent getPopupComponent();

    private void showPopup() {
        if (popup != null || !isPopupEnabled()) {
            return;
        }

        popupComponent = getPopupComponent();

        popupComponent.applyComponentOrientation(getComponentOrientation());

        var size = getSize();
        var popupSize = popupComponent.getPreferredSize();

        var x = switch (popupHorizontalAlignment) {
            case LEADING, TRAILING -> {
                if (getComponentOrientation().isLeftToRight() ^ popupHorizontalAlignment == HorizontalAlignment.TRAILING) {
                    yield 0;
                } else {
                    yield size.width - popupSize.width;
                }

            }
            case CENTER -> (size.width - popupSize.width) / 2;
        };

        var y = switch (popupVerticalAlignment) {
            case TOP -> -(popupSize.height + 2);
            case BOTTOM -> size.height + 2;
            case CENTER -> (size.height - popupSize.height) / 2;
        };

        var location = getLocationOnScreen();

        popup = PopupFactory.getSharedInstance().getPopup(this, popupComponent, location.x + x, location.y + y);

        popup.show();

        addHierarchyBoundsListener(hierarchyBoundsListener);
    }

    private void hidePopup() {
        if (popup == null) {
            return;
        }

        popup.hide();

        popup = null;

        removeHierarchyBoundsListener(hierarchyBoundsListener);
    }
}
