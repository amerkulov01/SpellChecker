package spellchecker;

//imports
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUI implements ActionListener {	
	private String ostype = System.getProperty("os.name", "generic").toLowerCase();
	
	private JFrame frame = new JFrame();
	
	private JPanel redpanel = new JPanel();
	private JPanel greenpanel = new JPanel();
	private JPanel bluepanel = new JPanel(new BorderLayout());
	
	
	private JLabel glitchlabel = new JLabel();
	
	private JButton uploadbutton = new JButton();
	private JButton savebutton = new JButton();
	private JButton saveasbutton = new JButton();
	
	private ImageIcon uploadicon = new ImageIcon("upload.png");
	private ImageIcon saveicon = new ImageIcon("dowlnoad3.png");
	private ImageIcon saveasicon = new ImageIcon("saveas.png");
	
	private final JFileChooser openFileChooser;
	private String pathToOpenFile;
	private String curDirectory;
	

	private Ourdocument thedoc;
	private CustomTextPane textpane = new CustomTextPane(null);
	
	private static JLabel charCountLabel = new JLabel("Characters: 0");
    private static JLabel wordCountLabel = new JLabel("Words: 0");
    private static JLabel lineCountLabel = new JLabel("Lines: 0");
    
    private static JLabel fileNameLabel;
	
	
	public GUI() {
		
		this.openFileChooser = new JFileChooser();
		
		//frame customizations
		frame.setSize(1000, 1000);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Spellchecker");
		frame.setVisible(true);
		frame.setLayout(new BorderLayout());
		
		//Adding panels, layounts, and building the app
		redpanel.setBackground(new Color(211, 211, 211));
		redpanel.setPreferredSize(new Dimension(1000, 80));
		redpanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		greenpanel.setBackground(Color.gray);
		greenpanel.setPreferredSize(new Dimension(1000, 900));
		
		bluepanel.setBackground(Color.gray);
		bluepanel.setPreferredSize(new Dimension(800, 850));
		bluepanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
		bluepanel.add(textpane, BorderLayout.CENTER);
				
		uploadbutton.setPreferredSize(new Dimension(135, 70));
		uploadbutton.setText("Upload Document");
		uploadbutton.setFocusable(false);
		uploadbutton.setHorizontalTextPosition(JButton.CENTER);
		uploadbutton.setVerticalTextPosition(JButton.BOTTOM);
		uploadbutton.setIcon(uploadicon);
		uploadbutton.setBackground(Color.white);
		
		savebutton.setPreferredSize(new Dimension(135, 70));
		savebutton.setText("Save Document");
		savebutton.setFocusable(false);	
		savebutton.setHorizontalTextPosition(JButton.CENTER);
		savebutton.setVerticalTextPosition(JButton.BOTTOM);
		savebutton.setIcon(saveicon);
		savebutton.setBackground(Color.white);
		
		saveasbutton.setPreferredSize(new Dimension(135, 70));
		saveasbutton.setText("Save As...");
		saveasbutton.setFocusable(false);	
		saveasbutton.setHorizontalTextPosition(JButton.CENTER);
		saveasbutton.setVerticalTextPosition(JButton.BOTTOM);
		saveasbutton.setIcon(saveasicon);
		saveasbutton.setBackground(Color.white);
		
		uploadbutton.addActionListener(this);
		savebutton.addActionListener(this);
		saveasbutton.addActionListener(this);
		
		frame.add(redpanel, BorderLayout.NORTH);
		frame.add(greenpanel, BorderLayout.SOUTH);
		
		greenpanel.add(bluepanel, BorderLayout.CENTER);
		
		redpanel.add(uploadbutton);
		redpanel.add(savebutton);
		redpanel.add(saveasbutton);

		frame.add(glitchlabel);
		
		
		//CODE FOR COUNT LABELS
		// Create labels to display counts
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        countPanel.setPreferredSize(new Dimension(800, 30));
        countPanel.add(charCountLabel);
        countPanel.add(wordCountLabel);
        countPanel.add(lineCountLabel);
        
        // Create panel for storing current filename label
        JPanel fileNamePanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        fileNamePanel.setPreferredSize(new Dimension(200, 30));

        // Create label for file name
        fileNameLabel = new JLabel("<No file currently open>");
        fileNamePanel.add(fileNameLabel);

        // Add file name label to the right of blue panel
        bluepanel.add(textpane, BorderLayout.CENTER);
        bluepanel.add(countPanel, BorderLayout.PAGE_START);
        bluepanel.add(fileNamePanel, BorderLayout.PAGE_END);
	}
	
	//Run the app
	public static void main(String[] args) {
		new GUI();
	}
	
	/**
	 * Sets the text to either no file selected or the actual filename
	 */
	public void updateFileNameLabel() {
		fileNameLabel.setText(
				pathToOpenFile == null ? 
				"<No file currently open>" :
				"Open file" + pathToOpenFile
		);
	}
	
	/**
	 * Updates the count labels, called by the pane when counts change
	 * @param counts
	 */
	public static void updateCountsLabel(int[] counts) {
        charCountLabel.setText("Characters: " + counts[0]);
        wordCountLabel.setText("Words: " + counts[1]);
        lineCountLabel.setText("Lines: " + counts[2]);
    }

	/**
	 * Button press action handlers
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == uploadbutton) {
			//Load a file, update label
			upload_file(null);
			updateFileNameLabel();			
		}
		
		//Save button pressed
		if (e.getSource() == savebutton) {
			//Check if a file was opened
			if (pathToOpenFile == null) {
				String errorMessage = "Cannot save because no file is currently open. Open a file using Upload or start typing and use Save As...";
		        JOptionPane.showMessageDialog(frame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			//Write to file
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.pathToOpenFile))) {
	            writer.write(textpane.getText());
	            //textpane.updateText(); //update the text
	            System.out.println("Document saved successfully to: " + this.pathToOpenFile);
	        } catch (IOException ex) {
	            System.out.println("Error saving document: " + ex.getMessage());
	        }
			
			if (pathToOpenFile != null && !pathToOpenFile.isEmpty()) {
				upload_file(this.pathToOpenFile);			
			}
		}
		
		//Save as button pressed
		if (e.getSource() == saveasbutton) {
			//setup file choosed
			JFileChooser saveAsFileChooser = new JFileChooser();
			File fileObj = null;
			
		    //set the directory if its not null
			if (curDirectory == null || !curDirectory.isEmpty())
				saveAsFileChooser.setCurrentDirectory(new File("."));
			else saveAsFileChooser.setCurrentDirectory(new File(curDirectory));

		    // Show the file chooser dialog
		    int retVal = saveAsFileChooser.showSaveDialog(frame);

		    if (retVal == JFileChooser.APPROVE_OPTION) {
		        fileObj = saveAsFileChooser.getSelectedFile();

		        //Try writing to file
		        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileObj))) {
		            //save the content of textpane to the selected file
		            writer.write(textpane.getText());
		            //textpane.updateText(); //update the text
		            System.out.println("Document saved successfully to: " + fileObj.getPath());
		            this.pathToOpenFile = fileObj.getPath();
		        } catch (IOException ex) {
		            System.out.println("Error saving document: " + ex.getMessage());
		        }
		    } else {
		        System.out.println("No file chosen");
		    }
		    
		    //If was successfully saved, upload the file to scan for new errors
		    if (fileObj != null)
				upload_file(fileObj.getPath());
		}
		
		
		
	}

	/**
	 * Loads the file from the path specified
	 * @param path Where is the file. If null, opens a file picker
	 */
	private void upload_file(String path) {
		if (ostype.contains("win")) {	
			//set the directory
			if (curDirectory == null || !curDirectory.isEmpty())
				openFileChooser.setCurrentDirectory(new File("."));
			else openFileChooser.setCurrentDirectory(new File(curDirectory));
			
			//init return val for later
			int returnval;
			if (path == null) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
				openFileChooser.setFileFilter(filter);
				returnval = openFileChooser.showOpenDialog(frame);
			}
			else
				returnval = JFileChooser.APPROVE_OPTION;
			
			if (returnval == JFileChooser.APPROVE_OPTION) {
				
				File file;
				if (path == null)
					file = openFileChooser.getSelectedFile();
				else
					file = new File(path);
				
				this.pathToOpenFile = file.getPath();
				this.curDirectory = file.getAbsolutePath();
				
				System.out.println(pathToOpenFile);
				thedoc = new Ourdocument(pathToOpenFile);
								
				textpane.setDoc(thedoc.getDocument()); //set the new dopcument
			}
			else {
				System.out.println("Error: no file was returned");
			}
		}
		
		else if (ostype.contains("mac")) {
			String errorMessage = "Unsupported operating system";
	        JOptionPane.showMessageDialog(frame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
		}
		updateFileNameLabel(); //update the labels finally
	}

}

