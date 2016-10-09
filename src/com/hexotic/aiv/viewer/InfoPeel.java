package com.hexotic.aiv.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.Timer;
import javaxt.io.Image;

public class InfoPeel extends JPanel{

	private int width = 30;
	private int height = 60;
	private int peelSize = 30;
	private Image image;
	private Map<Integer, Object> exif;
    int offset = 0;
    private Timer timer;
    private boolean reverse = true;
    
	public InfoPeel(Image image){
		width = image.getWidth();
		height = image.getHeight()/2;
		this.image = image;
		this.exif = image.getExifTags();
		peelSize=height/2;
		this.setPreferredSize(new Dimension(width, height));
		
		this.setBackground(new Color(0,0,0,0));
		setupTimer();
	}
	
	public void peel(){
		timer.start();
		reverse = !reverse;
	}
	
	public boolean isPeeled(){
		return reverse;
	}
	
	public void setupTimer() {
		timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if((offset >= peelSize && !reverse) || (offset <= 0 && reverse)){
            		timer.stop();
            	} else{
            		if(reverse){
            			offset-=10;
            		} else {
	            		offset+=10;
            		}
            	}
            }
        });
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(new Color(0x212121));
		g2.fillRect(0, 0, width, offset);
		
		if(offset >= peelSize){
			int imgSize = height/3; 
			g2.drawImage(image.getImage(), 10,10, imgSize, imgSize, null);
			g2.setColor(new Color(0xe9e9e9));
			g2.setFont(new Font("Arial", Font.BOLD, 20));
			g2.drawString(exif.get(0xa002)+ "x" +exif.get(0xa003), imgSize+20, 40);
			
		}
		
	    Point2D start = new Point2D.Float(offset, offset);
	    Point2D end = new Point2D.Float(offset, height);
	    float[] dist = {0.0f, 0.1f, 0.3f, 0.9f};
	    Color[] colors = new Color[]{new Color(0x121212), new Color(0xababab), new Color(0xe9e9e9), new Color(0x333333)};
	    g2.setPaint(new LinearGradientPaint(start, end, dist, colors)); 
	    g2.fillRect(0, offset, width, offset/2);	    
	}
}
