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

package org.httprpc.sierra.test;

import com.formdev.flatlaf.FlatLightLaf;
import org.httprpc.kilo.WebServiceProxy;
import org.httprpc.kilo.beans.BeanAdapter;
import org.httprpc.sierra.ActivityIndicator;
import org.httprpc.sierra.Outlet;
import org.httprpc.sierra.TaskExecutor;
import org.httprpc.sierra.UILoader;
import org.pushingpixels.radiance.theming.api.skin.RadianceBusinessLookAndFeel;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.net.URI;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

import static org.httprpc.kilo.util.Collections.*;
import static org.httprpc.kilo.util.Optionals.*;

public class TiingoTest extends JFrame implements Runnable {
    private static class HistoricalPricingTableModel implements TableModel {
        List<BeanAdapter> values;

        List<String> columns = listOf("date", "open", "high", "low", "close", "volume");

        HistoricalPricingTableModel(List<AssetPricing> rows) {
            values = rows.stream().map(BeanAdapter::new).toList();
        }

        @Override
        public int getRowCount() {
            return values.size();
        }

        @Override
        public int getColumnCount() {
            return columns.size();
        }

        @Override
        public String getColumnName(int columnIndex) {
            return resourceBundle.getString(columns.get(columnIndex));
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Object.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return values.get(rowIndex).get(columns.get(columnIndex));
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addTableModelListener(TableModelListener listener) {
            // No-op
        }

        @Override
        public void removeTableModelListener(TableModelListener listener) {
            // No-op
        }
    }

    private static class DateCellRenderer extends DefaultTableCellRenderer {
        static final DateTimeFormatter dateFormatter;
        static {
            dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withZone(ZoneId.of("America/New_York"));
        }

        @Override
        public void setValue(Object value) {
            setText(map((Instant)value, dateFormatter::format));
        }
    }

    private static class PriceCellRenderer extends DefaultTableCellRenderer {
        static final NumberFormat priceFormat;
        static {
            priceFormat = NumberFormat.getCurrencyInstance(Locale.US);
        }

        PriceCellRenderer() {
            setHorizontalAlignment(SwingConstants.TRAILING);
        }

        @Override
        public void setValue(Object value) {
            setText(map((Number)value, priceFormat::format));
        }
    }

    private static class VolumeCellRenderer extends DefaultTableCellRenderer {
        static final NumberFormat volumeFormat;
        static {
            volumeFormat = NumberFormat.getNumberInstance();
        }

        VolumeCellRenderer() {
            setHorizontalAlignment(SwingConstants.TRAILING);
        }

        @Override
        public void setValue(Object value) {
            setText(map((Number)value, volumeFormat::format));
        }
    }

    private boolean radiance;

    private @Outlet JTextField tickerTextField = null;
    private @Outlet JSpinner countSpinner = null;

    private @Outlet ActivityIndicator activityIndicator = null;

    private @Outlet JButton submitButton = null;

    private @Outlet JTextField nameTextField = null;
    private @Outlet JTextField exchangeCodeTextField = null;
    private @Outlet JTextField startDateTextField = null;
    private @Outlet JTextField endDateTextField = null;

    private @Outlet JTextArea descriptionTextArea = null;

    private @Outlet JTable historicalPricingTable = null;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(TiingoTest.class.getName());

    private static final TaskExecutor taskExecutor = new TaskExecutor(Executors.newCachedThreadPool(runnable -> {
        var thread = new Thread(runnable);

        thread.setDaemon(true);

        return thread;
    }));

    private static final URI baseURI = URI.create("https://api.tiingo.com/");

    private TiingoTest(boolean radiance) {
        super(resourceBundle.getString("title"));

        this.radiance = radiance;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void run() {
        if (radiance) {
            try {
                UIManager.setLookAndFeel(RadianceBusinessLookAndFeel.class.getName());
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        setContentPane(UILoader.load(this, "TiingoTest.xml", resourceBundle));

        countSpinner.setModel(new SpinnerNumberModel(30, 10, 150, 10));

        submitButton.addActionListener(event -> submit());

        rootPane.setDefaultButton(submitButton);

        setSize(960, 540);
        setVisible(true);
    }

    private void submit() {
        var token = System.getProperty("token");

        if (token == null) {
            showErrorMessage("apiTokenRequired", null);
            return;
        }

        var ticker = tickerTextField.getText();

        if (ticker.isEmpty()) {
            showErrorMessage("tickerRequired", tickerTextField);
            return;
        }

        var count = (int)countSpinner.getValue();

        var endDate = LocalDate.now();
        var startDate = endDate.minusDays(count);

        submitButton.setEnabled(false);

        activityIndicator.start();

        var tiingoServiceProxy = WebServiceProxy.of(TiingoServiceProxy.class, baseURI, mapOf(
            entry("Authorization", String.format("Token %s", token))
        ));

        taskExecutor.execute(() -> tiingoServiceProxy.getAsset(ticker), (result, exception) -> {
            if (exception == null) {
                updateAsset(result);
            } else {
                exception.printStackTrace(System.out);
            }
        });

        taskExecutor.execute(() -> tiingoServiceProxy.getHistoricalPricing(ticker, startDate, endDate), (result, exception) -> {
            if (exception == null) {
                updateHistoricalPricing(result);
            } else {
                exception.printStackTrace(System.out);
            }
        });

        taskExecutor.notify(() -> {
            submitButton.setEnabled(true);

            activityIndicator.stop();
        });
    }

    private void showErrorMessage(String messageKey, JComponent component) {
        JOptionPane.showMessageDialog(this,
            resourceBundle.getString(messageKey),
            resourceBundle.getString("error"),
            JOptionPane.ERROR_MESSAGE);

        if (component != null) {
            component.requestFocus();
        }
    }

    private void updateAsset(Asset asset) {
        nameTextField.setText(asset.getName());
        nameTextField.setCaretPosition(0);

        exchangeCodeTextField.setText(asset.getExchangeCode());

        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);

        startDateTextField.setText(dateFormatter.format(asset.getStartDate()));
        endDateTextField.setText(dateFormatter.format(asset.getEndDate()));

        descriptionTextArea.setText(asset.getDescription());
        descriptionTextArea.setCaretPosition(0);
    }

    private void updateHistoricalPricing(List<AssetPricing> historicalPricing) {
        historicalPricing.sort(Comparator.comparing(AssetPricing::getDate).reversed());

        historicalPricingTable.setModel(new HistoricalPricingTableModel(historicalPricing));

        var columnModel = historicalPricingTable.getColumnModel();

        columnModel.getColumn(0).setCellRenderer(new DateCellRenderer());

        var priceRenderer = new PriceCellRenderer();

        columnModel.getColumn(1).setCellRenderer(priceRenderer);
        columnModel.getColumn(2).setCellRenderer(priceRenderer);
        columnModel.getColumn(3).setCellRenderer(priceRenderer);
        columnModel.getColumn(4).setCellRenderer(priceRenderer);

        columnModel.getColumn(5).setCellRenderer(new VolumeCellRenderer());

        historicalPricingTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public static void main(String[] args) {
        var radiance = coalesce(map(System.getProperty("radiance"), Boolean::valueOf), () -> false);

        if (!radiance) {
            FlatLightLaf.setup();
        }

        SwingUtilities.invokeLater(new TiingoTest(radiance));
    }
}
