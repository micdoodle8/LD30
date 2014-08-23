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
	public static Tile NULL_TILE0 = new Tile('^', "tileUp.png", "tileUp_col.png");
	public static Tile NULL_TILE1 = new Tile('V', "tileDown.png", "tileDown_col.png");
	public static Tile NULL_TILE2 = new Tile('<', "tileLeft.png", "tileLeft_col.png");
	public static Tile NULL_TILE3 = new Tile('>', "tileRight.png", "tileRight_col.png");
	public static Tile NULL_TILE4 = new Tile('0', "corner0.png", "corner0_col.png");
	public static Tile NULL_TILE5 = new Tile('1', "corner1.png", "corner1_col.png");
	public static Tile NULL_TILE6 = new Tile('2', "corner2.png", "corner2_col.png");
	public static Tile NULL_TILE7 = new Tile('3', "corner3.png", "corner3_col.png");
	public static Tile NULL_TILE8 = new Tile('4', "corner4.png", "corner4_col.png");
	public static Tile NULL_TILE9 = new Tile('5', "corner5.png", "corner5_col.png");
	public static Tile NULL_TILE10 = new Tile('6', "corner6.png", "corner6_col.png");
	public static Tile NULL_TILE11 = new Tile('7', "corner7.png", "corner7_col.png");
	public static Tile NULL_TILE12 = new Tile('S', "solid.png");
    public static Tile NULL_TILE13 = new Tile('|', "tileUp.png", "tileUp_col.png");
    public static Tile NULL_TILE14 = new Tile('~', "tileDown.png", "tileDown_col.png");
    public static Tile NULL_TILE15 = new Tile('/', "tileUp.png", "tileUp_col.png");
    public static Tile NULL_TILE16 = new Tile('?', "tileDown.png", "tileDown_col.png");

	public String textureName;
    public char identifier;
    private String colored;
	
	public Tile(char identifier, String texture)
	{
        this(identifier, texture, null);
    }

	public Tile(char identifier, String texture, String colored)
	{
		tileList.put(tileList.size(), this);
        charTileMap.put(identifier, this);
		textureName = texture;
        this.identifier = identifier;
        this.colored = colored;
	}
	
	public void draw()
	{
        Texture tex;
        GL11.glColor3f(1, 1, 1);
		tex = Texture.getTexture(textureName);
		tex.bind();
        Game.getInstance().tessellator.start(GL11.GL_QUADS);
        Game.getInstance().tessellator.addVertexScaled(0.0F, 0.0F, 0, 1);
        Game.getInstance().tessellator.addVertexScaled(1.0F, 0.0F, 1, 1);
        Game.getInstance().tessellator.addVertexScaled(1.0F, 1.0F, 1, 0);
        Game.getInstance().tessellator.addVertexScaled(0.0F, 1.0F, 0, 0);
        Game.getInstance().tessellator.draw();
		GL11.glEnd();
        if (colored != null && Game.getInstance().player != null)
        {
            tex = Texture.getTexture(colored);
            if (this == NULL_TILE13 || this == NULL_TILE14)
            {
                GL11.glColor3f((float)Math.sin(Game.getInstance().player.timeAlive * 3) * 0.25F + 0.75F, 0, 0);
            }
            else if (this == NULL_TILE15|| this == NULL_TILE16)
            {
                GL11.glColor3f((float)Math.sin(Game.getInstance().player.timeAlive * 3) * 0.25F + 0.75F, (float)Math.sin(Game.getInstance().player.timeAlive * 3) * 0.25F + 0.75F, 0);
            }
            else
            {
                GL11.glColor3f(0.5F, 0.5F, 0.5F);
            }
            tex.bind();
            Game.getInstance().tessellator.start(GL11.GL_QUADS);
            Game.getInstance().tessellator.addVertexScaled(0.0F, 0.0F, 0, 1);
            Game.getInstance().tessellator.addVertexScaled(1.0F, 0.0F, 1, 1);
            Game.getInstance().tessellator.addVertexScaled(1.0F, 1.0F, 1, 0);
            Game.getInstance().tessellator.addVertexScaled(0.0F, 1.0F, 0, 0);
            Game.getInstance().tessellator.draw();
            GL11.glEnd();
        }
	}
	
	public BoundingBox getBounds(int x, int y)
	{
		return new BoundingBox(new Vector2i(x, y), new Vector2i(x + 1, y + 1));
	}
}
