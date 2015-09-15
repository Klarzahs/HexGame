package schemmer.hexagon.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import schemmer.hexagon.handler.EntityHandler;
import schemmer.hexagon.handler.MapHandler;

public class Screen extends JPanel{
	
	private final GraphicsConfiguration gfxConf = GraphicsEnvironment
						.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	
	public final static int WIDTH = 1920;
	public final static int HEIGHT = 1080;

	private BufferedImage offImg;
	private EntityHandler eh;
	private MapHandler mh;
	private String debug;
	
	public Screen(EntityHandler meh, MapHandler mmh){
		eh = meh;
		mh = mmh;
		debug = "";
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		if ( offImg == null || offImg.getWidth() != getWidth() || offImg.getHeight() != getHeight() ){
			offImg = gfxConf.createCompatibleImage( getWidth(), getHeight() );
			paint( offImg.createGraphics() );
		}
	
		g.drawImage( offImg, 0, 0, this );
		g2d.setColor( Color.WHITE ); g.fillRect( 0, 0, getWidth(), getHeight() );
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		mh.draw(g2d);
		eh.draw(g2d);
		
		g2d.drawString(debug, 20, HEIGHT-100);
	}
	
	public void setDebug(String d){
		debug = d;
	}
	
	public void appenDebug(String d){
		debug = debug + "   " + d;
	}
	
}
