package com.micdoodle8.ld30base;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

public class Tile 
{
	public static Map<Integer, Tile> tileList = new HashMap<Integer, Tile>();
	public static Tile AIR_TILE = new Tile("air.png");

	public String textureName;
	
	public Tile(String texture)
	{
		tileList.put(tileList.size(), this);
		textureName = texture;
	}
	
	public void draw()
	{
		Texture tex = Texture.getTexture(textureName);
		tex.bind();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(0.0F, 0.0F);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(1.0F, 0.0F);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(1.0F, 1.0F);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(0.0F, 1.0F);
		GL11.glEnd();
	}
	
	public BoundingBox getBounds(int x, int y)
	{
		return new BoundingBox(new Vector2i(x, y), new Vector2i(x + 1, y + 1));
	}
}
