package com.hexotic.aiv.utils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

public class Resources {

	private static Resources instance;
	private Map<String, Image> images;
	private ClassLoader cldr = this.getClass().getClassLoader();
	
	private Resources(){
		images = new HashMap<String, Image>();
	}
	
	public Image getImage(String resource){
		resource = resource.toLowerCase();
		Image image = null;
		if(images.containsKey(resource)){ /* To avoid reading from disk over and over, we'll get it from RAM if we can */
			image = images.get(resource);
		} 
		java.net.URL iconPth   = cldr.getResource("images/"+resource);
		image = new ImageIcon(iconPth).getImage();
		return image;
	}
	
	public Font getFont(String resource) throws FontFormatException, IOException{
		InputStream istream = cldr.getResourceAsStream("fonts/"+resource);
		Font myFont = Font.createFont(Font.TRUETYPE_FONT, istream);
		return myFont;
	}
	
	public static Resources getInstance(){
		if (instance == null){
			instance = new Resources();
		}
		return instance;		
	}
	
}
