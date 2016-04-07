package coop.map;

import java.awt.image.*;

public class Texture {
	private String name;
	private BufferedImage image;	

	public Texture(String name, BufferedImage image) {
		this.name = name;		
		this.image = image;
	}

	public String getName() {
		return name;
	}	

	public BufferedImage getImage() {
		return image;
	}
}