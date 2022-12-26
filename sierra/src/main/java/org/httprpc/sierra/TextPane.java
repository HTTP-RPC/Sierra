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
import javax.swing.plaf.ComponentUI;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Displays a string of text.
 */
public class TextPane extends JComponent {
    // Text pane UI
    private class TextPaneUI extends ComponentUI {
        @Override
        public Dimension getMinimumSize(JComponent component) {
            return new Dimension(0, 0);
        }

        @Override
        public Dimension getMaximumSize(JComponent component) {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        @Override
        public Dimension getPreferredSize(JComponent component) {
            // TODO Return constrained text size
            return new Dimension();
        }

        @Override
        public int getBaseline(JComponent component, int width, int height) {
            // TODO Take wrapping and vertical alignment into account
            return -1;
        }

        @Override
        public void paint(Graphics graphics, JComponent component) {
            paint((Graphics2D)graphics);
        }

        private void paint(Graphics2D graphics) {
            if (text == null) {
                return;
            }

            graphics = (Graphics2D)graphics.create();

            // TODO Respect alignment

            graphics.dispose();
        }
    }

    private String text;

    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEADING;
    private VerticalAlignment verticalAlignment = VerticalAlignment.TOP;

    private boolean wrapText;

    /**
     * Constructs a text pane.
     */
    public TextPane() {
        this(null);
    }

    /**
     * Constructs a text pane.
     *
     * @param text
     * The text to display, or {@code null} for no text.
     */
    public TextPane(String text) {
        this(text, false);
    }

    /**
     * Constructs a text pane.
     *
     * @param text
     * The text to display, or {@code null} for no text.
     *
     * @param wrapText
     * {@code true} to wrap text when needed; {@code false}, otherwise.
     */
    public TextPane(String text, boolean wrapText) {
        this.text = text;
        this.wrapText = wrapText;

        setUI(new TextPaneUI());
    }

    /**
     * Returns the text displayed by the component.
     *
     * @return
     * The text displayed by the component.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text displayed by the component.
     *
     * @param text
     * The text to display, or {@code null} for no text.
     */
    public void setText(String text) {
        this.text = text;

        revalidate();
    }

    /**
     * Returns the horizontal alignment.
     *
     * @return
     * The horizontal alignment.
     */
    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    /**
     * Sets the horizontal alignment.
     *
     * @param horizontalAlignment
     * The horizontal alignment.
     */
    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        if (horizontalAlignment == null) {
            throw new IllegalArgumentException();
        }

        this.horizontalAlignment = horizontalAlignment;

        repaint();
    }

    /**
     * Returns the vertical alignment.
     *
     * @return
     * The vertical alignment.
     */
    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    /**
     * Sets the vertical alignment.
     *
     * @param verticalAlignment
     * The vertical alignment.
     */
    public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
        if (verticalAlignment == null) {
            throw new IllegalArgumentException();
        }

        this.verticalAlignment = verticalAlignment;

        repaint();
    }

    /**
     * Indicates that line wrapping is enabled.
     *
     * @return
     * {@code true} if the text will wrap when needed; {@code false},
     * otherwise.
     */
    public boolean getWrapText() {
        return wrapText;
    }

    /**
     * Toggles line wrapping.
     *
     * @param wrapText
     * {@code true} to wrap text when needed; {@code false}, otherwise.
     */
    public void setWrapText(boolean wrapText) {
        this.wrapText = wrapText;

        revalidate();
    }
}
