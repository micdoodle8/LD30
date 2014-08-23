package com.micdoodle8.ld30base;

import java.util.HashMap;
import java.util.Map;

import com.micdoodle8.ld30.Game;
import org.lwjgl.opengl.GL11;

public class Tile 
{
	public static Map<Integer, Tile> tileList = new HashMap<Integer, Tile>();
	public static Map<Character, Tile> charTileMap = new HashMap<Character, Tile>();
	public static Tile AIR_TILE = new Tile('X', "air.png");
	public static Tile NULL_TILE0 = new Tile('^', "tileUp.png");
	public static Tile NULL_TILE1 = new Tile('V', "tileDown.png");
	public static Tile NULL_TILE2 = new Tile('<', "tileLeft.png");
	public static Tile NULL_TILE3 = new Tile('>', "tileRight.png");
	public static Tile NULL_TILE4 = new Tile('0', "corner0.png");
	public static Tile NULL_TILE5 = new Tile('1', "corner1.png");
	public static Tile NULL_TILE6 = new Tile('2', "corner2.png");
	public static Tile NULL_TILE7 = new Tile('3', "corner3.png");
	public static Tile NULL_TILE8 = new Tile('4', "corner4.png");
	public static Tile NULL_TILE9 = new Tile('5', "corner5.png");
	public static Tile NULL_TILE10 = new Tile('6', "corner6.png");
	public static Tile NULL_TILE11 = new Tile('7', "corner7.png");

	public String textureName;
    public char identifier;
	
	public Tile(char identifier, String texture)
	{
		tileList.put(tileList.size(), this);
        charTileMap.put(identifier, this);
		textureName = texture;
        this.identifier = identifier;
	}
	
	public void draw()
	{
		Texture tex = Texture.getTexture(textureName);
		tex.bind();
        Game.getInstance().tessellator.start(GL11.GL_QUADS);
        Game.getInstance().tessellator.addVertexScaled(0.0F, 0.0F, 0, 1);
        Game.getInstance().tessellator.addVertexScaled(1.0F, 0.0F, 1, 1);
        Game.getInstance().tessellator.addVertexScaled(1.0F, 1.0F, 1, 0);
        Game.getInstance().tessellator.addVertexScaled(0.0F, 1.0F, 0, 0);
        Game.getInstance().tessellator.draw();
		GL11.glEnd();
	}
	
	public BoundingBox getBounds(int x, int y)
	{
		return new BoundingBox(new Vector2i(x, y), new Vector2i(x + 1, y + 1));
	}
}
