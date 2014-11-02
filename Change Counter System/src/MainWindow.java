import java.awt.EventQueue;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;


public class MainWindow {

	private JFrame frmChangeCounterSystem;
	String content = null;
	String content2 = null;
	ChangeHeader header = new ChangeHeader();
	Counter counter = new Counter(Counter.PL.JAVA);
	DataBase db = new DataBase();
	String in = "", in2 = "";
	DefaultListModel<String> listModel = new DefaultListModel<String>();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmChangeCounterSystem.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("serial")
	private void initialize() {
		frmChangeCounterSystem = new JFrame();
		frmChangeCounterSystem.setBackground(Color.WHITE);
		frmChangeCounterSystem.setTitle("Change Counter System");
		frmChangeCounterSystem.setBounds(100, 100, 496, 424);
		frmChangeCounterSystem.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmChangeCounterSystem.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem(new AbstractAction("Compare") {
		    public void actionPerformed(ActionEvent e) {
		        // Button pressed logic goes here

				JFileChooser fc = new JFileChooser();
				//FileNameExtensionFilter filter = new FileNameExtensionFilter("Java source code file", "java", "java");
				//fc.setFileFilter(filter);
				int returnVal = fc.showOpenDialog(fc); 
		
				File file = null;
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					msg("Invalid file choice! Exiting.", "Error");
					return;
				}else{
					file = fc.getSelectedFile();
				}
				String oldver = null;
				try {
					db = new DataBase();
					oldver = db.getOldVersion(file);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					content = new Scanner(new File(file.getAbsolutePath())).useDelimiter("\\Z").next();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(oldver.isEmpty()){
					in = ask("Selected source code does not exist in database, copy to database?\nIf yes, enter the program name. \nIf no, enter nothing and press OK.");
					if(!in.isEmpty()){
						in2 = ask("Enter version of source code:");
						if(in2.isEmpty() || !isNumeric(in2))
							in2 = "1";
						header = new ChangeHeader();
						header.setCurrentVer(Double.parseDouble(in2) - 1);
						header.setProgramName(in);
						header.setTotalChanges(-1);
						content = header.createHeader(counter.CountLOC(content)) + content;
						try {
							db.copyToDB(file.getName(), content);
							db.writeTo(file.getPath(), content);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						msg("Copied file successfully to database. You can now compare different versions of the file '" + file.getName() + "'.", "Copied to DB");
					}
					refreshList();
					return;
				}
				
				in = ask("Found older version of file in database, compare? \nIf yes, enter the author name.\nIf no, enter nothing and press OK.");
				if(!in.isEmpty()){
					in2 = ask("Enter reason for any changes:");
					try {
						content2 = new Scanner(new File(db.getDataBasePath() + "\\" + file.getName())).useDelimiter("\\Z").next();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					header = new ChangeHeader();
					header.parseHeader(content2);
					Differ dmp = new Differ();
					
					LinkedList<Differ.Diff> dl = dmp.diff_main(header.removeHeader(content2),  header.removeHeader(content), false);
					dmp.diff_cleanupSemantic(dl);
					Compare cmp = new Compare(dl, header, in, in2);
					content = header.createHeader((header.getOldTotalChanges() == header.getTotalChanges() ? -1 : cmp.getTotalLOC())) + header.removeHeader(constructSourceCode(dl));
					System.out.println(content);
					if(header.getOldTotalChanges() == header.getTotalChanges()){
						msg("No changes were detected!", "Comparing files..");
					}else{
						try {
							db.copyToDB(file.getName(), content);
							db.writeTo(file.getPath(), content);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					}
					try {
						content2 = new Scanner(new File(db.getDataBasePath() + "\\" + file.getName())).useDelimiter("\\Z").next();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					header = new ChangeHeader();
					header.parseHeader(content2);
					msg(header.createHeader(-1).replace("*", "").replace("/", ""), "Change Report");
				}else{
					return;
				}
		    }
		});
		mnNewMenu.add(mntmNewMenuItem);
		final JList<String> list = new JList<String>(listModel);
		JMenuItem mntmPrintSourceCode = new JMenuItem(new AbstractAction("Print Source Code") {
		    public void actionPerformed(ActionEvent e) {
		        // Button pressed logic goes here
		    	try {
					msg(readFile(db.getDataBasePath() + "\\" + list.getSelectedValue().toString()), "Source Code");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    }
		});
		mnNewMenu.add(mntmPrintSourceCode);
		
		//JMenuItem mntmRefreshList = new JMenuItem("Refresh List");
		JMenuItem mntmRefreshList = new JMenuItem(new AbstractAction("Refresh List") {
		    public void actionPerformed(ActionEvent e) {
		        // Button pressed logic goes here
		    	refreshList();
		    }
		});
		mnNewMenu.add(mntmRefreshList);
		
		JMenuItem menuItem = new JMenuItem("-");
		mnNewMenu.add(menuItem);

		JMenuItem mntmExit = new JMenuItem(new AbstractAction("Exit") {
		    public void actionPerformed(ActionEvent e) {
		        // Button pressed logic goes here
		    	System.exit(0);
		    }
		});
		mnNewMenu.add(mntmExit);


		
		//final JList<String> list = new JList<String>(listModel);
		frmChangeCounterSystem.getContentPane().add(list, BorderLayout.CENTER);
		
		JButton btnPrintChangeReport = new JButton("Print Change Report");
		frmChangeCounterSystem.getContentPane().add(btnPrintChangeReport, BorderLayout.SOUTH);
		
		btnPrintChangeReport.addActionListener(new ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			  	// display/center the jdialog when the button is pressed
			  	try {
			  		header = new ChangeHeader();
					content2 = new Scanner(new File(db.getDataBasePath() + "\\" + list.getSelectedValue().toString())).useDelimiter("\\Z").next();
					header.parseHeader(content2);
					msg(header.createHeader(-1).replace("*", "").replace("/", "") + "\nTotal Lines of Code:" + counter.CountLOC(content2), "Change Report");
			  	} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
		  }
		});
		
		
		JLabel lblNewLabel = new JLabel("Program Listing:");
		lblNewLabel.setBackground(Color.WHITE);
		frmChangeCounterSystem.getContentPane().add(lblNewLabel, BorderLayout.NORTH);
		
		refreshList();
	}
	
	private void refreshList(){
		listModel.clear();
		File file = new File(db.getDataBasePath());  
	    File[] files = file.listFiles();  
	    for (int fileInList = 0; fileInList < files.length; fileInList++)  
	    {  
	    	listModel.addElement(files[fileInList].getName());
	    }
	}
	
	
	private static String constructSourceCode(LinkedList<Differ.Diff> d){
		String ret = "";
		for (Differ.Diff myDiff : d) {
			ret = ret + myDiff.text;	
		}
		return ret;
	}
	
	private static String ask(String s){
		return JOptionPane.showInputDialog(s);
	}
	
	public static void msg(String m, String t)
    {
        JOptionPane.showMessageDialog(null, m, t, JOptionPane.INFORMATION_MESSAGE);
    }
	
	String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    @SuppressWarnings("unused")
		double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
}
