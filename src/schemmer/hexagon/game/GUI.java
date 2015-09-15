package schemmer.hexagon.game;

import javax.swing.JFrame;

public class GUI extends JFrame{
	private int width, height;
	private Screen screen;
	
	public GUI(int width, int height, boolean fullscreen, Main main){
		
		if(fullscreen){
			this.setExtendedState(GUI.MAXIMIZED_BOTH);
			this.setUndecorated(true);
		}else{
			this.width = width;
			this.height = height;
			this.setSize(width, height);
		}
		
		this.setTitle("Hexagon");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		screen = new Screen(main.getEH(), main.getMH());
		screen.setSize(this.getSize());
		screen.setVisible(true);
		this.add(screen);
	}
	
	public Screen getScreen(){
		return screen;
	}
}
