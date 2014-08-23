package com.micdoodle8.ld30base;

import java.util.HashMap;
import java.util.Map;

import com.micdoodle8.ld30.Game;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

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
    public static Tile NULL_TILE17 = new Tile('O', "solidBack.png");
    public static Tile NULL_TILE18 = new Tile('T', "button_up_yel.png");
    public static Tile NULL_TILE19 = new Tile('U', "button_up_ora.png");

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

    private void colorDynamic(Vector2d mousePos, Vector2d drawPos)
    {
        this.colorDynamic(mousePos, drawPos, new Vector3f(1, 1, 1), 1.0F);
    }

    private void colorDynamic(Vector2d mousePos, Vector2d drawPos, Vector3f baseColor, float darknessMod)
    {
        float distance = (float)drawPos.sub(mousePos).getLength();
        float col = 1 / ((distance + 0.1F) * (distance + 0.1F) * (distance + 0.1F));
        col *= 1 / (darknessMod / 10.0F);
        GL11.glColor3f(col * baseColor.x, col * baseColor.y, col * baseColor.z);
    }
	
	public void draw(Vector2d drawPos, Vector2d deltaPos)
	{
        boolean specialLight = false;
        Texture tex;
        if (colored != null && Game.getInstance().player != null)
        {
            tex = Texture.getTexture(colored);
            Vector3f color;
            specialLight = true;
            if (this == NULL_TILE13 || this == NULL_TILE14)
            {
                color = new Vector3f((float)Math.sin(Game.getInstance().player.timeAlive * 3) * 0.25F + 2F, 0, 0);
            }
            else if (this == NULL_TILE15|| this == NULL_TILE16)
            {
                color = new Vector3f((float)Math.sin(Game.getInstance().player.timeAlive * 3) * 0.25F + 2F, (float)Math.sin(Game.getInstance().player.timeAlive * 3) * 0.25F + 2F, 0);
            }
            else
            {
                specialLight = false;
                color = new Vector3f(0.5F, 0.5F, 0.5F);
            }
            tex.bind();
            Game.getInstance().tessellator.start(GL11.GL_QUADS);
            this.colorDynamic(deltaPos, new Vector2d(0.5, 0.5), color, specialLight ? 0.05F : 1.0F);
            Game.getInstance().tessellator.addVertexScaled(0.0F, 0.0F, 0, 1);
            this.colorDynamic(deltaPos, new Vector2d(-0.5, 0.5), color, specialLight ? 0.05F : 1.0F);
            Game.getInstance().tessellator.addVertexScaled(1.0F, 0.0F, 1, 1);
            this.colorDynamic(deltaPos, new Vector2d(-0.5, -0.5), color, specialLight ? 0.05F : 1.0F);
            Game.getInstance().tessellator.addVertexScaled(1.0F, 1.0F, 1, 0);
            this.colorDynamic(deltaPos, new Vector2d(0.5, -0.5), color, specialLight ? 0.05F : 1.0F);
            Game.getInstance().tessellator.addVertexScaled(0.0F, 1.0F, 0, 0);
            Game.getInstance().tessellator.draw();
            GL11.glEnd();
        }

        if (!specialLight)
        {
            tex = Texture.getTexture(textureName);

            if ((this == NULL_TILE18 || this == NULL_TILE19) && Game.getInstance().player != null && Game.getInstance().player.boundingBox.intersects(new BoundingBox(drawPos.copy(), drawPos.copy().add(new Vector2d(0.5, 0.6)))))
            {
                if (this == NULL_TILE18)
                {
                    tex = Texture.getTexture("button_down_yel.png");
                }
                else
                {
                    tex = Texture.getTexture("button_down_ora.png");
                }
            }

            tex.bind();
            Game.getInstance().tessellator.start(GL11.GL_QUADS);
            this.colorDynamic(deltaPos, new Vector2d(0.5, 0.5));
            Game.getInstance().tessellator.addVertexScaled(0.0F, 0.0F, 0, 1);
            this.colorDynamic(deltaPos, new Vector2d(-0.5, 0.5));
            Game.getInstance().tessellator.addVertexScaled(1.0F, 0.0F, 1, 1);
            this.colorDynamic(deltaPos, new Vector2d(-0.5, -0.5));
            Game.getInstance().tessellator.addVertexScaled(1.0F, 1.0F, 1, 0);
            this.colorDynamic(deltaPos, new Vector2d(0.5, -0.5));
            Game.getInstance().tessellator.addVertexScaled(0.0F, 1.0F, 0, 0);
            Game.getInstance().tessellator.draw();
            GL11.glEnd();
        }
	}
	
	public BoundingBox getBounds(int x, int y)
	{
        if (this == NULL_TILE18 || this == NULL_TILE19)
        {
            return new BoundingBox(new Vector2d(x, y), new Vector2d(x + 1, y));
        }
		return new BoundingBox(new Vector2i(x, y), new Vector2i(x + 1, y + 1));
	}
}
