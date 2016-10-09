package com.hexotic.aiv.viewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Pin extends JPanel{
	
	
	int width = 20;
	int height = 20; 
	
    int[] xPoints = {0, 0, width, width};
    int[] yPoints = {0, height, height, 0};
    
    
    int[] xPointsTwo = {0, 0, 0, 0};
    int[] yPointsTwo = {0, height, height, 0};
    
    private int modifyValue = 0;

    private boolean pinned = false;
    private boolean mouseOver = false;
    
    private Timer timer;
	public Pin(){
		this.setPreferredSize(new Dimension(width, height));
		
		this.setBackground(new Color(0,0,0,0));
		setupTimer();
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				toggle();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				mouseOver = true;
			}

			@Override
			public void mouseExited(MouseEvent e) {
				mouseOver = false;
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
	}
	
	public boolean isPinned(){
		if(timer.isRunning()){
			return true;
		}
		return pinned;
	}

	public void toggle(){
		if(timer!= null)
			timer.start();
	}
    
	public void setupTimer() {
		timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(pinned)
            		modifyValue--;
            	else
            		modifyValue++;
            	xPoints[0] = modifyValue;
            	xPoints[1] = modifyValue;
            	yPoints[2] = height-modifyValue/2;
            	yPoints[3] = modifyValue/2;
            	
            	xPointsTwo[2] = modifyValue;
            	xPointsTwo[3] = modifyValue;
            	yPointsTwo[0] = height/2-modifyValue/2;
            	yPointsTwo[1] = height/2+modifyValue/2;

            	if(modifyValue == width || modifyValue == 0){
            		pinned = !pinned;
            		timer.stop();
            	}
            }
        });
	}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int opacity = 35;
        if(mouseOver){
        	opacity = 75;
        }
        g2d.setColor(new Color(0,0,0, opacity));
        g2d.setStroke(new BasicStroke(2f));

        g2d.fillPolygon(xPoints, yPoints, 4);
        
        g2d.setColor(new Color(0,0,0,150+opacity));
        g2d.fillPolygon(xPointsTwo, yPointsTwo, 4);
    }

}
