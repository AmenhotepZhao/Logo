package interpreter;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import parser.Parser;
import tokenizer.Token;
import tree.Tree;

/**
 * This is starter code for CIT 594 Assignment 9. You can do anything you want
 * with it (including ignore it entirely). If you keep more than a few lines,
 * you should also keep my name as one of the authors.
 * <p>
 * All comments should be brought up to date to correspond to your changes in
 * the code.
 * 
 * @author David Matuszek
 * @author Xiaolu Xu
 * @version April 1, 2014
 */
public class Logo extends JFrame implements Runnable {
    private static final long serialVersionUID = 1L;
    private Logo logoGui;
    private Interpreter interpreter;
    private Tree<Token> abstractSyntaxTree;
    private String currentFileName = null;

    private DrawingArea canvas;
    private JPanel titledCanvas;
    private JTextArea programTextArea;
    private JScrollPane scrollableProgramTextArea;
    private JPanel controlPanel;
    private JTextField statusField;
    private JSlider speedControlSlider;
    private JPanel inputs;

    private JMenuItem loadMenuItem;
    private JMenuItem saveMenuItem;
    private JMenuItem saveAsMenuItem;
    
    private JButton parseButton;
    private JButton startButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JButton clearButton;
    private int speed;

    private static final int INITIAL_CANVAS_WIDTH = 600;
    private static final int INITIAL_CANVAS_HEIGHT = 600;

    /**
     * Constructor for a Logo interpreter.
     */
    public Logo() {
        setTitle("Logo");
        logoGui = this;
    }

    /**
     * Starts the Logo interpreter application.
     * 
     * @param args Ignored.
     */
    public static void main(String[] args) {
        new Logo().setup();
    }

    /**
     * Creates and displays the GUI, then executes the interpreter.
     */
    public void setup() {
        createGui();
        pack();
        setVisible(true);
        interpreter = new Interpreter(canvas);
    }

    /**
     * Interprets the program (as displayed in the programTextArea).
     */
    @Override
    public void run() {
//    	canvas.clear();
    	interpreter = new Interpreter(canvas);
        interpreter.initialize();
        changeSpeed(speedControlSlider.getValue());
        programTextArea.setEditable(false);
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        clearButton.setEnabled(false);
        try {
            interpreter.interpret(abstractSyntaxTree);
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            stopButton.setEnabled(false);
            clearButton.setEnabled(true);
            programTextArea.setEditable(true);
        }
        catch (Throwable e) {
            displayStatus(e.getMessage());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(bytes));
            JOptionPane.showMessageDialog(canvas, e.getMessage() + "\n\n" + bytes.toString() + "\n");
            return;
        }
    }

    /**
     * Lays out the GUI for the Logo program.
     */
    private void createGui() {
        createComponents();
        arrangeComponents();
        attachListeners();
        repaint();
    }

    /**
     * Creates all Components used by the GUI.
     */
    private void createComponents() {
        loadMenuItem = new JMenuItem("Load");
        saveMenuItem = new JMenuItem("Save");
        saveAsMenuItem = new JMenuItem("Save As...");

        parseButton = new JButton("Parse");
        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        stopButton = new JButton("Stop");
        clearButton = new JButton("Clear");
        statusField = new JTextField();
        
        startButton.setEnabled(false);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        clearButton.setEnabled(false);

        setTitle("Logo");
        canvas = new DrawingArea();
        canvas.setBackground(Color.WHITE);
        titledCanvas = new JPanel();
        titledCanvas.setPreferredSize(new Dimension(INITIAL_CANVAS_WIDTH, INITIAL_CANVAS_HEIGHT));
        titledCanvas.setLayout(new BorderLayout());
        titledCanvas.add(canvas, BorderLayout.CENTER);
        addTitledBorder(titledCanvas, "Drawing area");
        
        programTextArea = new JTextArea(30, 30);
        scrollableProgramTextArea =
            makeScrollable(programTextArea, "Logo program");

        controlPanel = new JPanel();
        
        createSpeedSlider();
    }

    /**
     * Creates the speed control.
     */
    private void createSpeedSlider() {
        speedControlSlider =
            new JSlider(SwingConstants.HORIZONTAL, 0, 100, speed);
        speedControlSlider.setMajorTickSpacing(20);
        speedControlSlider.setMinorTickSpacing(5);
        speedControlSlider.setPaintTicks(true);
        speedControlSlider.setPaintLabels(true);
    }

    /**
     * Arrange the various components of the GUI in place.
     */
    private void arrangeComponents() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        this.setJMenuBar(menuBar);
        
        saveMenuItem.setEnabled(false);
        
        setLayout(new BorderLayout());

        add(titledCanvas, BorderLayout.CENTER);
        add(scrollableProgramTextArea, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);

        layoutControlPanel();
        
        setBackground(Color.WHITE);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Adds buttons to the GUI as appropriate.
     */
    private void layoutControlPanel() {
        inputs = new JPanel();
        JPanel sliderPanel = new JPanel();

        sliderPanel.add(new JLabel("          Speed:"));
        sliderPanel.add(speedControlSlider);

        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(inputs, BorderLayout.CENTER);
        controlPanel.add(statusField, BorderLayout.SOUTH);

        inputs.add(parseButton);
        inputs.add(new Label("      "));
        inputs.add(sliderPanel);
        inputs.add(new Label("      "));
        inputs.add(startButton);
        inputs.add(pauseButton);
        inputs.add(stopButton);
        inputs.add(clearButton);
    }

    /**
     * Attach listeners to all GUI components that need them.
     */
    private void attachListeners() {
        // Load
        loadMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                loadFile();
            }
        });
        // Save
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                saveFile();
            }
        });
        // Save As...
        saveAsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                saveFileAs();
            }
        });
        // Parse
        parseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Tree<Token> ast = parse();
                JTextArea textArea = new JTextArea();
//        		textArea.setFont(new Font("Sans-Serif", Font.PLAIN, 10));
        		textArea.setEditable(false);
        		textArea.setText(ast.toString());
        		
        		// stuff it in a scrollpane with a controlled size.
        		JScrollPane scrollPane = new JScrollPane(textArea);		
        		scrollPane.setPreferredSize(new Dimension(400, 600));
        		
        		// pass the scrollpane to the joptionpane.				
        		JOptionPane.showMessageDialog(logoGui, scrollPane, "AST Tree", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        // Start
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                abstractSyntaxTree = parse();
                if (abstractSyntaxTree == null) 
                    return;
                Thread thread = new Thread(logoGui);
                thread.start();
            }
        });
        // Pause
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if ("Pause".equals(pauseButton.getText())) {
                    pauseButton.setText("Resume");
                    clearButton.setEnabled(true);
                    interpreter.pauseTurtle();
                    displayStatus("Turtle is paused.");
                }
                else {
                    pauseButton.setText("Pause");
                    clearButton.setEnabled(false);
                    interpreter.resumeTurtle();
                    displayStatus("");
                }
            }
        });
        // Clear
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                canvas.clear();
            }
        });
        // Stop
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) { 
            	interpreter.stopDrawing();
            	startButton.setEnabled(true);
                pauseButton.setEnabled(false);
                stopButton.setEnabled(false);
                clearButton.setEnabled(true);
                programTextArea.setEditable(true);
            	return;
            }
        });
        // Set speed
        speedControlSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                changeSpeed(speedControlSlider.getValue());
            }
        });
        //input
        programTextArea.getDocument().addDocumentListener(new DocumentListener() {
			public void changed() {
			   if (programTextArea.getText().equals("")){
			     startButton.setEnabled(false);
			   }
			   else {
			  	 startButton.setEnabled(true);
			  	 stopButton.setEnabled(false);
			  }
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				changed();
			}
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				changed();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				changed();
			}
		});
        
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                // TODO Make this work!
                logoGui.invalidate();
                logoGui.validate();
                logoGui.repaint();
                canvas.repaint();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }
        });
    }

    /**
     * Parses the text in the <code>programTextArea</code> and returns it as a
     * binary tree, or <code>null</code> if the text fails to parse. Either way,
     * a message is put in the <code>errorField</code>.
     * 
     * @return The resultant abstract syntax tree.
     */
    Tree<Token> parse() {
        Parser parser = new Parser(programTextArea.getText() + '\n');
        Tree<Token> ast = null;

        displayStatus("");
        try {
            if (parser.isProgram()) {
                displayStatus("Program appears to be OK.");
                ast = parser.stack.peek();
                startButton.setEnabled(true);
            }
            displayStatus("It's not a correct program");
        }
        catch (Throwable e) {
            displayStatus(e.getMessage());
        }
        return ast;
    }

    /**
     * Embeds a GUI component inside a titled JScrollPane.
     * 
     * @param component The Component to be titled and made scrollable.
     * @param title The title for the component.
     * @return A scrollable, titled pane containing the given Component.
     */
    private static JScrollPane makeScrollable(Component component,
                                       String title) {        
        // Make a panel containing the component
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(component, BorderLayout.CENTER);

        // Make a titled scroll pane containing the panel
        JScrollPane scrollPane = new JScrollPane(panel);
        addTitledBorder(scrollPane, title);
        return scrollPane;
    }

    /**
     * Adds a titled border to the given Component. According to the Swing documentation,
     * the <code>setBorder(Border border)</code> does not work well with all types
     * of JComponents.
     * 
     * @param component The component to which a titled forder is to be added.
     * @param title The text used for the title.
     */
    private static void addTitledBorder(JComponent component, String title) {
        Border lineBorder = BorderFactory.createLineBorder(Color.black);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(lineBorder, title);
        component.setBorder(titledBorder);
    }
    
    /**
     * Asks the user for a file, and loads the file.
     */
    void loadFile() {
        BufferedReader reader = null;
        FileDialog dialog = new FileDialog(new JFrame(),
                                           "Load in a Logo program",
                                           FileDialog.LOAD);

        // Get the full path name of a file to load
        dialog.setVisible(true);
        String directory = dialog.getDirectory();
        String file = dialog.getFile();
        if (directory == null || file == null)
            return;
        String fileName = directory + file;

        // Read and display the file
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(fileName);
            reader = new BufferedReader(fileReader);

            programTextArea.setText("");
            String line = reader.readLine();
            while (line != null) {
                programTextArea.append(line + "\n");
                line = reader.readLine();
            }
            currentFileName = fileName;
            saveMenuItem.setEnabled(true);
            startButton.setEnabled(true);
        }
        catch (Exception e) {
            displayStatus("Problem reading input file: " + fileName);
        }
        finally {
            try {
                if (reader != null) reader.close();
            }
            catch (Exception e) {
                displayStatus("Can't close: " + fileName);
            }
        }
    }

    /**
     * Saves the program text on a file of the user's choice.
     */
    void saveFileAs() {
        FileDialog dialog = new FileDialog(new JFrame(), "Save a Logo program",
                                           FileDialog.SAVE);
        dialog.setVisible(true);
        String directory = dialog.getDirectory();
        String file = dialog.getFile();
        if (directory == null || file == null)
            return;
        String fileName = directory + file;

        saveFile(fileName);
    }
    
    /**
     * Saves the current program file.
     */
    void saveFile() {
        saveFile(currentFileName);
    }

    /**
     * Saves the program on the named file.
     * 
     * @param fileName The name of the file to save the program on.
     */
    private void saveFile(String fileName) {
        BufferedWriter writer = null;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName);
            writer = new BufferedWriter(fileWriter);
            assert writer != null;

            writer.write(programTextArea.getText());
            writer.flush();
        }
        catch (Exception e) {
            displayStatus("Problem writing Logo program: " + fileName);
        }
        finally {
            try {
                if (writer != null) writer.close();
            }
            catch (Exception e) {
                displayStatus("Can't close: " + fileName);
            }
        }
    }
    
    /**
     * Changes the speed at which the turtle draws, to correspond to
     * the speed set by the speed control slider. A speed of zero
     * means no movement, while the maximum speed available on the
     * slider means that there is no delay between turtle actions.
     * @param speed speed at which the turtle moves
     */
    private void changeSpeed(int speed) {
        interpreter.setTurtleSpeed(speed);
    }

    /**
     * Indicates the status of the interpreter process.
     * 
     * @param status Message to be reported
     */
    void displayStatus(String status) {
        statusField.setText(status);
    }
}