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
package org.httprpc.sierra.previewer;

import org.httprpc.sierra.previewer.engine.RenderingEngine;
import org.httprpc.sierra.previewer.model.RenderError;
import org.httprpc.sierra.previewer.model.RenderResult;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

import org.httprpc.sierra.UILoader;
import org.httprpc.sierra.Outlet;

/**
 * The main application window for the Sierra UI Previewer. UI is defined in
 * MainFrame.xml and loaded by UILoader. This class contains the wiring and
 * business logic.
 */
public class MainFrame extends JFrame {

    // --- Subsystems ---
    private final RenderingEngine renderingEngine;
    private final Timer debounceTimer;
    private final RecentFilesManager recentFilesManager; // NEW: Manager instance

    // --- File Handling State ---
    private final JFileChooser fileChooser;
    private Path currentFilePath = null; // Stores the path of the currently loaded file

    // --- UI Components (Injected by Sierra) ---
    @Outlet
    private JScrollPane editorScrollPane; // The <scroll-pane> placeholder

    @Outlet
    private JPanel previewPanel; // The <column-panel>

    @Outlet
    private JLabel statusBar;

    @Outlet
    private JLabel filePathLabel; // The <label> for the file path

    // --- Manually Created Components ---
    private RSyntaxTextArea editorPane;
    private JMenuItem saveItem; // Reference to the manually created menu item
    private JMenu recentMenu; // New reference for the Recent Files menu

    public MainFrame() {
        super("Sierra UI Previewer");
        this.renderingEngine = new RenderingEngine();

        // 0. Initialize the Recent Files Manager
        this.recentFilesManager = new RecentFilesManager(MainFrame.class);

        // 1. Load the UI from the declarative .xml file
        setContentPane(UILoader.load(this, "MainFrame.xml"));

        // 2. Setup the File Chooser
        this.fileChooser = new JFileChooser();
        FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("XML Files (*.xml)", "xml");
        fileChooser.setFileFilter(xmlFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        // 3. Setup the menu bar
        setupMenuBar();

        // 4. Manually set up components not supported by the DTD
        setupCustomEditor();

        // 5. Set layout for previewPanel
        previewPanel.setLayout(new BorderLayout());

        // Create the JSplitPane with the editor on the left and the preview on the right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorScrollPane, previewPanel);

        // Set the initial divider location (e.g., 50% of the window width)
        splitPane.setDividerLocation(0.5);

        // Ensure space is split equally when the frame is resized
        splitPane.setResizeWeight(0.5);

        // Create a new main panel to hold the JSplitPane (Center) and the StatusBar (South)
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(splitPane, BorderLayout.CENTER);
        mainContentPanel.add(statusBar, BorderLayout.SOUTH);

        // Set the new panel as the frame's content pane
        setContentPane(mainContentPanel);

        // 6. Setup Control Subsystem (Debounce Timer)
        this.debounceTimer = setupDebounceTimer();

        // 7. Wire editor events
        editorPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                debounceTimer.restart();
                statusBar.setText("Typing...");
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                debounceTimer.restart();
                statusBar.setText("Typing...");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Style changes, not relevant for text
            }
        });
        
        // 7. load icon
         URL iconURL = getClass().getResource("/sierra.png");
        Image icon = new ImageIcon(iconURL).getImage();
        this.setIconImage(icon);

        // 8. Trigger an initial render
        triggerRender();
    }

    // --- Menu Setup ---
    /**
     * Creates and sets the application's menu bar.
     */
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // --- File Menu ---
        JMenu fileMenu = new JMenu("File");

        // Open
        JMenuItem openItem = new JMenuItem("Open...");
        openItem.addActionListener(e -> {
            int result = fileChooser.showOpenDialog(MainFrame.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                loadFile(selectedFile);
            }
        });
        fileMenu.add(openItem);

        // --- NEW: Recent Menu ---
        recentMenu = new JMenu("Recent");
        fileMenu.add(recentMenu);
        updateRecentMenu(); // Populate the menu initially

        fileMenu.addSeparator();

        // Save
        saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> saveFile());
        saveItem.setEnabled(false); // Disabled until a file is successfully loaded
        fileMenu.add(saveItem);

        // Separator
        fileMenu.addSeparator();

        // Exit
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> {
            System.exit(0); // Exit the application
        });
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);

        // --- About Menu ---
        JMenu aboutMenu = new JMenu("About");
        JMenuItem aboutItem = new JMenuItem("About Previewer");
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    """
                    Sierra DSL Previewer
                    License: Apache 2.0                    
                    """,
                    "About",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
        aboutMenu.add(aboutItem);
        menuBar.add(aboutMenu);

        this.setJMenuBar(menuBar);
    }

    /**
     * Clears and repopulates the Recent Files menu based on the
     * RecentFilesManager list.
     */
    private void updateRecentMenu() {
        recentMenu.removeAll();
        List<Path> recentFiles = recentFilesManager.getRecentFiles();

        if (recentFiles.isEmpty()) {
            recentMenu.setEnabled(false);
            JMenuItem emptyItem = new JMenuItem("No recent files");
            emptyItem.setEnabled(false);
            recentMenu.add(emptyItem);
        } else {
            recentMenu.setEnabled(true);
            for (int i = 0; i < recentFiles.size(); i++) {
                Path fullPath = recentFiles.get(i);

                // Display only the file name in the menu item, but use the full path for loading
                String label = (i + 1) + ". " + fullPath.getFileName().toString();
                JMenuItem item = new JMenuItem(label);

                // Use a local variable for the path in the lambda
                item.addActionListener(e -> loadFile(fullPath.toFile()));
                recentMenu.add(item);
            }
        }
    }

    /**
     * Creates and returns the completion provider for Sierra XML.
     */
    private CompletionProvider createCompletionProvider() {
        return new SierraXMLCompletionProvider();
    }

    // --- Editor Setup ---
    /**
     * Creates the custom RSyntaxTextArea and adds it to the
     * <scroll-pane> placeholder that Sierra injected.
     */
    private void setupCustomEditor() {
        editorPane = new RSyntaxTextArea(25, 80);
        editorPane.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        editorPane.setCodeFoldingEnabled(true);
        editorPane.setAntiAliasingEnabled(true);
        editorPane.setEditable(true); // Ensure it's editable

        CompletionProvider provider = createCompletionProvider();

        // Create the auto-completion manager
        AutoCompletion ac = new AutoCompletion(provider);
        ac.setAutoActivationEnabled(true);
        ac.setAutoActivationDelay(500); // Activate after 500ms of typing

        // Install the auto-completion on the editor pane
        if (provider != null) {
            ac.install(editorPane);
        }

        editorScrollPane.setViewportView(editorPane);
    }

    // --- Rendering/Control Logic ---
    /**
     * Implements the debounce mechanism.
     */
    private Timer setupDebounceTimer() {
        Timer timer = new Timer(1000, (e) -> triggerRender());
        timer.setRepeats(false);
        return timer;
    }

    /**
     * Kicks off the rendering process on a background thread.
     */
    private void triggerRender() {
        statusBar.setText("Rendering...");
        String xmlText = editorPane.getText();

        RenderWorker worker = new RenderWorker(xmlText, currentFilePath, renderingEngine, this::displayRenderResult);
        worker.execute();
    }

    /**
     * This is the callback that runs on the EDT when the SwingWorker is done.
     */
    private void displayRenderResult(RenderResult result) {
        switch (result) {
            case RenderResult.Success success -> {
                previewPanel.removeAll();
                JComponent component = success.component();
                previewPanel.add(component, BorderLayout.CENTER);
                previewPanel.revalidate();
                previewPanel.repaint();

                this.setTitle("Sierra UI Previewer");
                if (saveItem == null || saveItem.isEnabled()) {
                    statusBar.setText("Render successful.");
                }
            }
            case RenderResult.Error error -> {
                String errorMessage = error.details().toString();
                statusBar.setText("Error: " + errorMessage);
            }
            default -> {
            }
        }
    }

    // --- FILE LOAD/SAVE LOGIC ---
    /**
     * Kicks off a SwingWorker to load a file's content onto a background
     * thread.
     */
    private void loadFile(File file) {
        // Reset save state while loading
        saveItem.setEnabled(false);
        currentFilePath = null;

        filePathLabel.setText("Loading " + file.getName() + "...");
        FileLoaderWorker worker = new FileLoaderWorker(file.toPath(), this::displayFileContent);
        worker.execute();
    }

    /**
     * Callback that runs on the EDT after the file is loaded. Updates the UI
     * with the file content and path.
     */
    private void displayFileContent(FileLoadResult result) {
        switch (result) {
            case FileLoadResult.Success success -> {
                editorPane.setText(success.content());
                editorPane.setCaretPosition(0);

                // Set file state and enable Save
                currentFilePath = success.path();
                filePathLabel.setText(success.path().toAbsolutePath().toString());
                saveItem.setEnabled(true);

                // NEW: Update the Recent Files list via the manager
                recentFilesManager.addFile(currentFilePath);
                updateRecentMenu(); // Refresh the menu display

                // Re-render the preview with the new content
                triggerRender();
            }
            case FileLoadResult.Error error -> {
                filePathLabel.setText("Error loading file.");
                JOptionPane.showMessageDialog(
                        this,
                        "Could not read file:\n" + error.exception().getMessage(),
                        "File Load Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            default -> {
            }
        }
    }

    /**
     * Kicks off a SwingWorker to save the current content back to the loaded
     * file path.
     */
    private void saveFile() {
        if (currentFilePath == null) {
            JOptionPane.showMessageDialog(this, "No file is currently open.", "Save Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        saveItem.setEnabled(false); // Disable save during save operation
        statusBar.setText("Saving to " + currentFilePath.getFileName() + "...");

        String content = editorPane.getText();

        FileSaverWorker worker = new FileSaverWorker(currentFilePath, content, this::displaySaveResult);
        worker.execute();
    }

    /**
     * Callback that runs on the EDT after the file is saved.
     */
    private void displaySaveResult(FileSaveResult result) {
        saveItem.setEnabled(true); // Re-enable save

        if (result instanceof FileSaveResult.Success) {
            statusBar.setText("File saved successfully.");
        } else if (result instanceof FileSaveResult.Error error) {
            statusBar.setText("Save failed.");
            JOptionPane.showMessageDialog(
                    this,
                    "Could not save file:\n" + error.exception().getMessage(),
                    "File Save Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // --- INNER CLASSES FOR FILE LOAD/SAVE ---
    private sealed interface FileLoadResult {

        record Success(String content, Path path) implements FileLoadResult {

        }

        record Error(Exception exception) implements FileLoadResult {

        }
    }

    private static class FileLoaderWorker extends SwingWorker<FileLoadResult, Void> {

        private final Path filePath;
        private final Consumer<FileLoadResult> callback;

        FileLoaderWorker(Path filePath, Consumer<FileLoadResult> callback) {
            this.filePath = filePath;
            this.callback = callback;
        }

        @Override
        protected FileLoadResult doInBackground() {
            try {
                String content = Files.readString(filePath);
                return new FileLoadResult.Success(content, filePath);
            } catch (IOException e) {
                return new FileLoadResult.Error(e);
            }
        }

        @Override
        protected void done() {
            try {
                FileLoadResult result = get();
                callback.accept(result);
            } catch (InterruptedException | ExecutionException e) {
                callback.accept(new FileLoadResult.Error(e));
            }
        }
    }

    private sealed interface FileSaveResult {

        record Success() implements FileSaveResult {
        }

        record Error(Exception exception) implements FileSaveResult {

        }
    }

    private static class FileSaverWorker extends SwingWorker<FileSaveResult, Void> {

        private final Path filePath;
        private final String content;
        private final Consumer<FileSaveResult> callback;

        FileSaverWorker(Path filePath, String content, Consumer<FileSaveResult> callback) {
            this.filePath = filePath;
            this.content = content;
            this.callback = callback;
        }

        @Override
        protected FileSaveResult doInBackground() {
            try {
                // Write content back to the original file, replacing the old content
                Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                return new FileSaveResult.Success();
            } catch (IOException e) {
                return new FileSaveResult.Error(e);
            }
        }

        @Override
        protected void done() {
            try {
                FileSaveResult result = get();
                callback.accept(result);
            } catch (InterruptedException | ExecutionException e) {
                callback.accept(new FileSaveResult.Error(e));
            }
        }
    }

    // --- INNER CLASS FOR RENDERING ---
    private static class RenderWorker extends SwingWorker<RenderResult, Void> {

        private final String xmlText;
        private final Path targetPath; // ADDED: Field to hold the target path
        private final RenderingEngine engine;
        private final Consumer<RenderResult> callback;

        // MODIFIED: Constructor now accepts Path
        RenderWorker(String xmlText, Path targetPath, RenderingEngine engine, Consumer<RenderResult> callback) {
            this.xmlText = xmlText;
            this.targetPath = targetPath; // Store the path
            this.engine = engine;
            this.callback = callback;
        }

        @Override
        protected RenderResult doInBackground() throws Exception {
            // MODIFIED: Pass the path to the rendering engine
            return engine.render(xmlText, targetPath);
        }

        @Override
        protected void done() {
            try {
                RenderResult result = get();
                callback.accept(result);
            } catch (InterruptedException | ExecutionException e) {
                System.out.println(e);
                callback.accept(new RenderResult.Error(new RenderError(e.getMessage(), e)));
            }
        }
    }
}
