package org.httprpc.sierra;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;

import static org.httprpc.sierra.SwingUIBuilder.borderPanel;
import static org.httprpc.sierra.SwingUIBuilder.cell;

public class BorderLayoutTest extends JFrame implements Runnable {
    private BorderLayoutTest() {
        super("Border Layout Test");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(borderPanel(new BorderLayout(),
            cell(new JButton("Button 1 (PAGE_START)"), BorderLayout.PAGE_START),
            cell(new JButton("Button 2 (CENTER)"), button -> button.setPreferredSize(new Dimension(200, 100)), BorderLayout.CENTER),
            cell(new JButton("Button 3 (LINE_START)"), BorderLayout.LINE_START),
            cell(new JButton("Long-Named Button 4 (PAGE_END)"), BorderLayout.PAGE_END),
            cell(new JButton("5 (LINE_END)"), BorderLayout.LINE_END)
        ));

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new BorderLayoutTest());
    }
}