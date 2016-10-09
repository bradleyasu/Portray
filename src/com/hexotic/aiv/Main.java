package com.hexotic.aiv;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.hexotic.aiv.viewer.Viewer;

public class Main {

	public static void main(String[] args){

		if(args.length == 1){
			Point p = MouseInfo.getPointerInfo().getLocation();
			new Viewer(new File(args[0]), p.x, p.y);
		} else if(args.length == 3 && isNumeric(args[1]) && isNumeric(args[2])){
			new Viewer(new File(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		} else if(args.length > 1) {
			int x = 0;
			int y = 0;
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			int maxWidth = dim.width/ ((int)Math.sqrt(args.length) > 1? (int)Math.sqrt(args.length)*2 :args.length);
			int maxHeight = dim.width/ ((int)Math.sqrt(args.length) > 1? (int)Math.sqrt(args.length)*2 :args.length);
			for(int i = 0; i < args.length; i++){
				Viewer viewer = new Viewer(new File(args[i]), x, y);
				viewer.togglePin();
				viewer.setSize(new Dimension(maxWidth, maxHeight));
				x += maxWidth;
				if(x+maxWidth > dim.width){
					x = 0;
					y += maxHeight;
				}
			}
		}
		
	}
	
	public static boolean isNumeric(String str) {
	    for (char c : str.toCharArray())
	    {
	        if (!Character.isDigit(c)) return false;
	    }
	    return true;
	}
}
