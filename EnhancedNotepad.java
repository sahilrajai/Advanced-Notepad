
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import java.util.Stack;

public class EnhancedNotepad extends JFrame implements ActionListener {
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JMenuBar menuBar;
    private JMenu fileMenu, editMenu, viewMenu;
    private JMenuItem newFile, openFile, saveFile, exit, findReplace, toggleTheme, fontSettings, undo, redo;
    private boolean isDarkMode = false;
    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private Font currentFont = new Font("Monospaced", Font.PLAIN, 14);

    public EnhancedNotepad() {
        // Setup the main frame
        setTitle("Enhanced Notepad");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Text Area with Scroll Pane
        textArea = new JTextArea();
        textArea.setFont(currentFont);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane = new JScrollPane(textArea);
        add(scrollPane);

        // Menu Bar
        menuBar = new JMenuBar();

        // File Menu
        fileMenu = new JMenu("File");
        newFile = new JMenuItem("New");
        openFile = new JMenuItem("Open");
        saveFile = new JMenuItem("Save");
        exit = new JMenuItem("Exit");
        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.addSeparator();
        fileMenu.add(exit);
        menuBar.add(fileMenu);

        // Edit Menu
        editMenu = new JMenu("Edit");
        findReplace = new JMenuItem("Find & Replace");
        undo = new JMenuItem("Undo");
        redo = new JMenuItem("Redo");
        editMenu.add(findReplace);
        editMenu.add(undo);
        editMenu.add(redo);
        menuBar.add(editMenu);

        // View Menu
        viewMenu = new JMenu("View");
        toggleTheme = new JMenuItem("Toggle Dark/Light Theme");
        fontSettings = new JMenuItem("Font Settings");
        viewMenu.add(toggleTheme);
        viewMenu.add(fontSettings);
        menuBar.add(viewMenu);

        setJMenuBar(menuBar);

        // Action Listeners
        newFile.addActionListener(this);
        openFile.addActionListener(this);
        saveFile.addActionListener(this);
        exit.addActionListener(this);
        findReplace.addActionListener(this);
        toggleTheme.addActionListener(this);
        fontSettings.addActionListener(this);
        undo.addActionListener(this);
        redo.addActionListener(this);

        // Key Bindings for Undo/Redo
        textArea.getDocument().addUndoableEditListener(e -> {
            undoStack.push(textArea.getText());
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newFile) {
            textArea.setText("");
        } else if (e.getSource() == openFile) {
            openFile();
        } else if (e.getSource() == saveFile) {
            saveFile();
        } else if (e.getSource() == exit) {
            System.exit(0);
        } else if (e.getSource() == findReplace) {
            showFindReplaceDialog();
        } else if (e.getSource() == toggleTheme) {
            toggleTheme();
        } else if (e.getSource() == fontSettings) {
            changeFont();
        } else if (e.getSource() == undo) {
            undo();
        } else if (e.getSource() == redo) {
            redo();
        }
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                textArea.read(reader, null);
                reader.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error opening file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                textArea.write(writer);
                writer.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showFindReplaceDialog() {
        // Simple Find and Replace Dialog
        JDialog dialog = new JDialog(this, "Find & Replace", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new GridLayout(3, 2));

        JLabel findLabel = new JLabel("Find:");
        JTextField findField = new JTextField();
        JLabel replaceLabel = new JLabel("Replace:");
        JTextField replaceField = new JTextField();
        JButton findButton = new JButton("Find");
        JButton replaceButton = new JButton("Replace");

        findButton.addActionListener(e -> {
            String searchText = findField.getText();
            String content = textArea.getText();
            int index = content.indexOf(searchText);
            if (index >= 0) {
                textArea.select(index, index + searchText.length());
            } else {
                JOptionPane.showMessageDialog(this, "Text not found", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        replaceButton.addActionListener(e -> {
            String searchText = findField.getText();
            String replaceText = replaceField.getText();
            textArea.setText(textArea.getText().replace(searchText, replaceText));
        });

        dialog.add(findLabel);
        dialog.add(findField);
        dialog.add(replaceLabel);
        dialog.add(replaceField);
        dialog.add(findButton);
        dialog.add(replaceButton);

        dialog.setVisible(true);
    }

    private void toggleTheme() {
        if (isDarkMode) {
            textArea.setBackground(Color.WHITE);
            textArea.setForeground(Color.BLACK);
        } else {
            textArea.setBackground(Color.DARK_GRAY);
            textArea.setForeground(Color.WHITE);
        }
        isDarkMode = !isDarkMode;
    }

    private void changeFont() {
        JDialog fontDialog = new JDialog(this, "Font Settings", true);
        fontDialog.setSize(400, 150);
        fontDialog.setLayout(new GridLayout(2, 2));

        JLabel fontSizeLabel = new JLabel("Font Size:");
        JTextField fontSizeField = new JTextField(String.valueOf(currentFont.getSize()));
        JButton applyButton = new JButton("Apply");

        applyButton.addActionListener(e -> {
            try {
                int fontSize = Integer.parseInt(fontSizeField.getText());
                currentFont = new Font(currentFont.getName(), Font.PLAIN, fontSize);
                textArea.setFont(currentFont);
                fontDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid font size", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        fontDialog.add(fontSizeLabel);
        fontDialog.add(fontSizeField);
        fontDialog.add(new JLabel());
        fontDialog.add(applyButton);

        fontDialog.setVisible(true);
    }

    private void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(textArea.getText());
            textArea.setText(undoStack.pop());
        }
    }

    private void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(textArea.getText());
            textArea.setText(redoStack.pop());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EnhancedNotepad().setVisible(true));
    }
}
