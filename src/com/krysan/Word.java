//графічний редактор, виконала крисан Олена
package com.krysan;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class Word extends JFrame {
	
	private JTextPane field = new JTextPane(); //text field 
	private JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));//save\open window
	private String fileName = "Untitled";
	private boolean change = false;
	private int currFontSize = 24;
	private int beginCaret, endCaret = 0; //selected text
	private boolean boldpress,italianpress, underlinepress = false;  
	private UndoManager undo = new UndoManager();

	ActionMap m = field.getActionMap(); //default copy/cut/paste action
	Action Cut = m.get(DefaultEditorKit.cutAction);
	Action Copy = m.get(DefaultEditorKit.copyAction);
	Action Paste = m.get(DefaultEditorKit.pasteAction);
	
	
	Action Bold = new AbstractAction(){ //make text bold
		public void actionPerformed(ActionEvent e) {
			change = true;
			if(beginCaret>endCaret){
				int t = beginCaret;
				beginCaret = endCaret;
				endCaret = t;
			}
			SimpleAttributeSet attr = new SimpleAttributeSet();
			if(boldpress){
				StyleConstants.setBold(attr, false);
				boldpress = false;
			} else {
				StyleConstants.setBold(attr, true);
				boldpress = true;
			}
		 	field.getStyledDocument().setCharacterAttributes(beginCaret, Math.abs(beginCaret-endCaret), attr, false);
		}

};

Action Italic = new AbstractAction(){
	public void actionPerformed(ActionEvent e) {
		change = true;
		if(beginCaret>endCaret){
			int t = beginCaret;
			beginCaret = endCaret;
			endCaret = t;
		}
		SimpleAttributeSet attr = new SimpleAttributeSet();
		if(italianpress){
			StyleConstants.setItalic(attr, false);
			italianpress = false;
		} else {
			StyleConstants.setItalic(attr, true);
			italianpress = true;
		}
	 	field.getStyledDocument().setCharacterAttributes(beginCaret, Math.abs(beginCaret-endCaret), attr, false);
	}

};
Action Underline = new AbstractAction(){
	
	public void actionPerformed(ActionEvent e) {
		change = true;
		if(beginCaret>endCaret){
			int t = beginCaret;
			beginCaret = endCaret;
			endCaret = t;
		}
		SimpleAttributeSet attr = new SimpleAttributeSet();
		if(underlinepress){
			StyleConstants.setUnderline(attr, false);
			underlinepress = false;
		} else {
			StyleConstants.setUnderline(attr, true);
			underlinepress = true;
		}
	 	field.getStyledDocument().setCharacterAttributes(beginCaret, Math.abs(beginCaret-endCaret), attr, false);
	}

};
	
	Action Undo = new AbstractAction(){ //undo/redo action
		public void actionPerformed(ActionEvent e) {
			change = true;
			try {
		        undo.undo();
		    } catch (CannotUndoException ex) {
		    }
		}

};

	Action Redo = new AbstractAction(){
		public void actionPerformed(ActionEvent e) {
			change = true;
			try {
		        undo.redo();
		    } catch (CannotRedoException ex) {
		    }

		}

	};
	
	Action Up = new AbstractAction(){ //make size of text bigger/smaller by 2
			public void actionPerformed(ActionEvent e) {
				change = true;
				if(beginCaret>endCaret){
					int t = beginCaret;
					beginCaret = endCaret;
					endCaret = t;
				}
				if(currFontSize<=72){
				currFontSize = currFontSize +2;
				}
//			 	field.setFont(new Font("Times New Roman",Font.PLAIN,currFontSize));
			 	changeSize();
			}

	};
	Action Down = new AbstractAction(){
		public void actionPerformed(ActionEvent e) {
			change = true;
			if(beginCaret>endCaret){
				int t = beginCaret;
				beginCaret = endCaret;
				endCaret = t;
			}
			if(currFontSize>=8){
			currFontSize = currFontSize -2;
			}
//			field.setFont(new Font("Times New Roman",Font.PLAIN,currFontSize));
			changeSize();
		}

};

public void changeSize(){ //change size of the selected text
	SimpleAttributeSet attr = new SimpleAttributeSet();
 	StyleConstants.setFontSize(attr, currFontSize);
 	field.getStyledDocument().setCharacterAttributes(beginCaret,  Math.abs(beginCaret-endCaret), attr, false);
}
	
	public Word(){ //constructor of the text editor
		field.setFont(new Font("Times New Roman",Font.PLAIN,currFontSize));
		JScrollPane areaScrollPane = new JScrollPane(field,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(areaScrollPane,BorderLayout.CENTER);
		
	     field.getDocument().addUndoableEditListener(undo);
		   
	    Font font = new Font("Times New Roman", Font.PLAIN, 24);
	    field.setFont(font);
		JMenuBar menuBar = new JMenuBar(); //add list menu
		setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		fileMenu.setFont(font);

		JMenu newMenu = new JMenu("New");
		newMenu.setFont(font);
		fileMenu.add(newMenu);
		JMenuItem txtFileItem = new JMenuItem("Text file");
		txtFileItem.setFont(font);
		newMenu.add(txtFileItem);
		
		txtFileItem.addActionListener(new ActionListener() { //make new file
			public void actionPerformed(ActionEvent e) {
				saveOnCreating();
				field.setText(""); //delete text
				fileName = "Untitled";
				change = false;
				setTitle(fileName);
				 field.getDocument().addUndoableEditListener(undo);
			} 
		});

		JMenuItem openItem = new JMenuItem("Open");
		openItem.setFont(font);
		fileMenu.add(openItem);
		
		openItem.addActionListener(new ActionListener() { //open file
			public void actionPerformed(ActionEvent e) {
				saveOnCreating();
				
				if(dialog.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
					readInFile(dialog.getSelectedFile().getAbsolutePath());
				}
				 field.getDocument().addUndoableEditListener(undo);

			} 
		});

		JMenuItem saveItem = new JMenuItem("Save as");
		saveItem.setFont(font);
		fileMenu.add(saveItem);
		
		saveItem.addActionListener(new ActionListener() { // save file with selected name and place
			public void actionPerformed(ActionEvent e) {
			    saveFileAs();
			} 
		});
		
		JMenuItem saveAsItem = new JMenuItem("Save");
		saveAsItem.setFont(font);
		fileMenu.add(saveAsItem);
		
		saveAsItem.addActionListener(new ActionListener() { //save automatic
			public void actionPerformed(ActionEvent e) {
				if(fileName == "Untitled"){
			    saveFileAs();
				} else {
					saveFile(fileName);
				}
			} 
		});

		fileMenu.addSeparator();

		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.setFont(font);
		fileMenu.add(exitItem);
		
		exitItem.addActionListener(new ActionListener() { //close program
			public void actionPerformed(ActionEvent e) {
			 saveOnExit();
			} 
		});
		
		this.addWindowListener(new WindowListener() {
		public void windowClosing(WindowEvent event) { //close program by menu
			saveOnExit();
		}

		@Override
		public void windowActivated(WindowEvent arg0) {	
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
		}
		});
		
		menuBar.add(fileMenu);
		
		field.addCaretListener(new CaretListener(){//read selected text

			@Override
			public void caretUpdate(CaretEvent arg0) {
				beginCaret = arg0.getMark();
				endCaret = arg0.getDot();
				
			}
			
		});
		
		
		JToolBar tool = new JToolBar(); //add toolbar
		add(tool,BorderLayout.NORTH);
		
		  final JPopupMenu popup = new JPopupMenu(); //down-list for size
		  popup.add(new JMenuItem(new AbstractAction("8") {
	            public void actionPerformed(ActionEvent e) {
	            	change = true;
	                currFontSize = 8;
	                changeSize();
	            }
	        }));
		  popup.add(new JMenuItem(new AbstractAction("14") {
	            public void actionPerformed(ActionEvent e) {
	            	change = true;
	                currFontSize = 14;
	                changeSize();
	            }
	        }));
	        popup.add(new JMenuItem(new AbstractAction("24") {
	            public void actionPerformed(ActionEvent e) {
	            	change = true;
	                currFontSize = 24;
	                changeSize();
	            }
	        }));
	        popup.add(new JMenuItem(new AbstractAction("36") {
	            public void actionPerformed(ActionEvent e) {
	            	change = true;
	            	 currFontSize = 36;
	            	 changeSize();
	            }
	        }));
	        popup.add(new JMenuItem(new AbstractAction("48") {
	            public void actionPerformed(ActionEvent e) {
	            	change = true;
	            	 currFontSize = 48;
	            	 changeSize();
	            }
	        }));
	        popup.add(new JMenuItem(new AbstractAction("72") {
	            public void actionPerformed(ActionEvent e) {
	            	change = true;
	            	 currFontSize = 72;
	            	 changeSize();
	            }
	        }));

	        final JButton textSize = new JButton("Size");
	        textSize.addMouseListener(new MouseAdapter() {
	            public void mousePressed(MouseEvent e) {
	                popup.show(e.getComponent(), e.getX(), e.getY());
	            }
	        });
	      

		JButton cut = tool.add(Cut), cop = tool.add(Copy),pas = tool.add(Paste);
		tool.addSeparator();
		tool.addSeparator();
		tool.add(textSize);
		JButton aup = tool.add(Up), adown = tool.add(Down);
		tool.addSeparator();
		tool.addSeparator();
		JButton redo = tool.add(Redo), undo = tool.add(Undo);
		tool.addSeparator();
		tool.addSeparator();
		JButton bold = tool.add(Bold), italic = tool.add(Italic), underline = tool.add(Underline);
		
		textSize.setIcon(new ImageIcon("size1.png"));//add some pictures
		
		cut.setText("Cut");
		cut.setIcon(new ImageIcon("cut1.png"));
		cop.setText("Copy");
		cop.setIcon(new ImageIcon("copy1.png"));
		pas.setText("Paste");
		pas.setIcon(new ImageIcon("paste1.png"));
	  
	    aup.setText("SizeUp");
		aup.setIcon(new ImageIcon("sizeup1.png"));
		adown.setText("SizeDown");
		adown.setIcon(new ImageIcon("sizedown1.png"));
		

	    undo.setText("Undo");
		undo.setIcon(new ImageIcon("undo1.png"));
		redo.setText("Redo");
		redo.setIcon(new ImageIcon("redo1.png"));
		
		bold.setText("Bold");
		bold.setIcon(new ImageIcon("bold1.png"));
		italic.setText("Italic");
		italic.setIcon(new ImageIcon("italic1.png"));
		underline.setText("Underline");
		underline.setIcon(new ImageIcon("underline1.png"));
	  
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(1000, 1000));
		pack();
	    setLocationRelativeTo(null);
	    setVisible(true);
	    setTitle(fileName);
	    field.addKeyListener(k1);
		
		
	}
	
	private void saveFileAs() { //save as newfile
		if(dialog.showSaveDialog(null)==JFileChooser.APPROVE_OPTION)
			saveFile(dialog.getSelectedFile().getAbsolutePath());
	}
	
	private void saveFile(String fm) { //save file
		try {
			FileWriter w = new FileWriter(fm);
			field.write(w);
			w.close();
			fileName = fm;
			setTitle(fileName);
			change = false;
//			Save.setEnabled(false);
		}
		catch(IOException e) {
		}
	}
	
	private KeyListener k1 = new KeyAdapter() { //if pressed button
		public void keyPressed(KeyEvent e) {
			change = true;
		}
	};
	
	private void readInFile(String fm) { //read file on text field
		try {	
			FileReader r = new FileReader(fm);
			field.read(r,null);
			r.close();
			fileName = fm;
			setTitle(fileName);
			change = false;
		}
		catch(IOException e) {
	//		Toolkit.getDefaultToolkit().beep();
	// 		JOptionPane.showMessageDialog(this,"Editor can't find the file called "+fileName);
		}
	}
	
	private void saveOnExit(){ //try to save before exit
		JFrame f = new JFrame();
		if(change){
			Object[] options = { "Yes", "No", "Cancel" };
			int n = JOptionPane.showOptionDialog(f, "Save file before closing?",
			"", JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE, null, options,
			options[0]);
			if (n == 0) {
				if(fileName == "Untitled"){
				    saveFileAs();
					} else {
						saveFile(fileName);
					}
				f.setVisible(false);
				if(!change) System.exit(0);
			} else if(n == 1){
			f.setVisible(false);
			System.exit(0);
			} else {
			f.setVisible(false);
			}
		} else {
			System.exit(0);
		}
	}
	
	private void saveOnCreating(){//try to save before creating new
		JFrame f = new JFrame();
		f.setFont(new Font("Times New Roman",Font.PLAIN,34));
		if(change){
			Object[] options = { "Yes", "No" };
			int n = JOptionPane.showOptionDialog(f, "Do you want to save file?",
			"", JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE, null, options,
			options[0]);
			if (n == 0) {
				if(fileName == "Untitled"){
				    saveFileAs();
					} else {
						saveFile(fileName);
					}
				f.setVisible(false);
				if(!change) System.exit(0);
			} else {
			f.setVisible(false);
			}
		} 
	}
	
	
	public static void main(String[] args) {//main method
	new Word();
	}
}