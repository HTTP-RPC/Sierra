package org.httprpc.sierra;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;

import static org.httprpc.sierra.SwingUIBuilder.borderPanel;
import static org.httprpc.sierra.SwingUIBuilder.cardPanel;
import static org.httprpc.sierra.SwingUIBuilder.cell;
import static org.httprpc.sierra.SwingUIBuilder.flowPanel;

public class CardLayoutTest extends JFrame implements Runnable {
    private CardLayout cardLayout = new CardLayout();

    private JPanel cardPanel;

    private static final String BUTTONPANEL = "Card with JButtons";
    private static final String TEXTPANEL = "Card with JTextField";

    private CardLayoutTest() {
        super("Card Layout Test");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        setContentPane(borderPanel(new BorderLayout(),
            cell(flowPanel(new FlowLayout(),
                cell(new JComboBox<String>(), comboBox -> {
                    var model = new DefaultComboBoxModel<String>();

                    model.addElement("Card with JButtons");
                    model.addElement("Card with JTextField");

                    comboBox.setModel(model);
                    comboBox.setEditable(false);
                    comboBox.addItemListener(event -> {
                        cardLayout.show(cardPanel, (String)event.getItem());
                    });
                })
            ), BorderLayout.PAGE_START),

            cell(cardPanel(cardLayout,
                cell(flowPanel(new FlowLayout(),
                    cell(new JButton("Button 1")),
                    cell(new JButton("Button 2")),
                    cell(new JButton("Button 3"))
                ), BUTTONPANEL),

                cell(flowPanel(new FlowLayout(),
                    cell(new JTextField("TextField", 20))
                ), TEXTPANEL)
            ), cardPanel -> this.cardPanel = cardPanel, BorderLayout.CENTER)
        ));

        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new CardLayoutTest());
    }
}