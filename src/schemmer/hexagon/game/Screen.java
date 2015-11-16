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
import schemmer.hexagon.handler.UIHandler;
import schemmer.hexagon.map.Hexagon;

public class Screen extends JPanel{
	
	private final GraphicsConfiguration gfxConf = GraphicsEnvironment
						.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	
	public final static int WIDTH = 1920;
	public final static int HEIGHT = 1080;

	private BufferedImage offImg;
	private EntityHandler eh;
	private MapHandler mh;
	private UIHandler uih;
	private String debug;
	
	private int offX = 0, offY = 0;
	private int maxOffX, maxOffY;
	
	public Screen(EntityHandler meh, MapHandler mmh){
		eh = meh;
		mh = mmh;
		debug = "";
		offX = 0;
		offY = 0;
		calculateOffsets();
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		if ( offImg == null || offImg.getWidth() != getWidth() || offImg.getHeight() != getHeight() ){
			offImg = gfxConf.createCompatibleImage( getWidth(), getHeight() );
			paint( offImg.createGraphics() );
		}
	
		g.drawImage( offImg, 0, 0, this );
		g2d.setColor( Color.LIGHT_GRAY ); g.fillRect( 0, 0, getWidth(), getHeight() );
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		mh.draw(g2d, offX, offY);
		eh.draw(g2d, offX, offY);
		uih.draw(g2d);
		
		g2d.drawString(debug+ "\n "+offX+"|"+offY , 20, HEIGHT-100);
	}
	
	public void setDebug(String d){
		debug = d;
	}
	
	public void appendDebug(String d){
		debug = debug + "   " + d;
	}
	
	public void recreate(int newRadius){
		mh.recreate(newRadius);
	}
	
	public void moveDown(){
		if(offY <= maxOffY) offY += 10;
	}
	
	public void moveUp(){
		if(offY >= -maxOffY) offY -= 10;
	}
	public void moveLeft(){
		if(offX >= -maxOffX) offX -= 10;
	}
	public void moveRight(){
		if(offX <= maxOffX) offX += 10;
	}
	
	public int getOffX(){
		return offX;
	}
	
	public int getOffY(){
		return offY;
	}
	
	public void calculateOffsets(){
		double diffX = WIDTH / 2 - ((mh.RADIUS*2 + 1) * Math.sqrt(3)/2 * Hexagon.getSize());
		double diffY = HEIGHT / 2 - ((mh.RADIUS*2 + 1) * Hexagon.getSize());
		maxOffX = (int) Math.abs((diffX ) * 1.2d);
		maxOffY = (int) Math.abs((diffY ) * 1.2d);
	}
	
	public void setUIH(UIHandler myUIH){
		uih = myUIH;
	}
}
