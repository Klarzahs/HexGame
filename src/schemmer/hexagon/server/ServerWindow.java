package schemmer.hexagon.server;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ServerWindow extends JFrame{
	private static final long serialVersionUID = 100L;
	JTextArea textArea = new JTextArea("Server Output:\n");
	JScrollPane scrollPane = new JScrollPane(textArea);
	JPanel panel = new JPanel(new BorderLayout());


	public ServerWindow(){
		super("Server");
		panel.add(scrollPane);
		this.getContentPane().add(panel);
	}
	
	public void log(String s){
		textArea.append(s);
	}
	
	public void newLine(){
		textArea.append("\n");
	}
}
