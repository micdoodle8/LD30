package com.micdoodle8.ld30base;

import java.util.HashMap;
import java.util.Map;

import com.micdoodle8.ld30.ButtonEffect;
import com.micdoodle8.ld30.Game;
import com.micdoodle8.ld30.Light;
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
    public static Tile NULL_TILE18 = new Tile('T', "button_up_yel.png", "button_up_yel.png");
    public static Tile NULL_TILE19 = new Tile('U', "button_up_ora.png", "button_up_ora.png");
    public static Tile NULL_TILE20 = new Tile('R', "coil.png");
    public static Tile NULL_TILE21 = new Tile('F', "full.png");
    public static Tile NULL_TILE22 = new Tile('G', "pointLeft.png");
    public static Tile NULL_TILE23 = new Tile('H', "pointRight.png");
    public static Tile NULL_TILE24 = new Tile('I', "pointUp.png");
    public static Tile NULL_TILE25 = new Tile('J', "pointDown.png");
    public static Tile NULL_TILE26 = new Tile('K', "tubeHoriz.png");
    public static Tile NULL_TILE27 = new Tile('L', "tubeVert.png");

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

    public void colorDynamic(Vector2d drawPos, Vector2d vecPos)
    {
        this.colorDynamic(drawPos, vecPos, new Vector3f(0.01F, 0.01F, 0.01F), 1.0F);
    }

    public void colorDynamic(Vector2d drawPos, Vector2d vecPos, Vector3f baseColor, float darknessMod)
    {
        Vector3f color = new Vector3f(baseColor.x, baseColor.y, baseColor.z);
        for (Light light : Game.getInstance().gameWorld.lightList)
        {
            if (Math.abs(light.position.x - drawPos.x) < 8 && Math.abs(light.position.y - drawPos.y) < 8)
            {
                float distance = (float)vecPos.copy().add(light.position).sub(drawPos).getLength() / 5.0F;
                float col = 1 / ((distance + 0.1F) * (distance + 0.1F) * (distance + 0.1F));
                col *= 1 / (darknessMod / 10.0F);
                col *= light.brightness;
                col += Math.sin(Game.getInstance().totalGameTime / 1000.0F + light.flickerPhase) * Math.cos(Game.getInstance().totalGameTime / 500.0F + light.flickerPhase0) * light.flicker;
                color.x += col * light.color.x * baseColor.x;
                color.y += col * light.color.y * baseColor.y;
                color.z += col * light.color.z * baseColor.z;
            }
        }
        GL11.glColor3f(color.x, color.y, color.z);
    }

    public void update(float deltaTicks) {}

    public boolean isAdvanced() { return false; }
	
	public void draw(Vector2d drawPos, int layer)
	{
        boolean drawFullLight = Game.getInstance().keyButtonB.isKeyPressed();
        boolean specialLight = false;
        if (drawFullLight)
        {
            GL11.glColor3f(1, 1, 1);
        }
        Texture tex;
        if (colored != null && Game.getInstance().players != null)
        {
            tex = Texture.getTexture(colored);
            Vector3f color;
            specialLight = true;
            if (this == NULL_TILE13 || this == NULL_TILE14)
            {
                color = new Vector3f((float)Math.sin(Game.getInstance().players[0].timeAlive * 3) * 0.25F + 2F, 0, 0);
            }
            else if (this == NULL_TILE15|| this == NULL_TILE16)
            {
                color = new Vector3f((float)Math.sin(Game.getInstance().players[0].timeAlive * 3) * 0.25F + 2F, (float)Math.sin(Game.getInstance().players[0].timeAlive * 3) * 0.25F + 2F, 0);
            }
            else if (this == NULL_TILE18)
            {
                color = new Vector3f((float)Math.sin(Game.getInstance().players[0].timeAlive * 3) * 0.25F + 2F, (float)Math.sin(Game.getInstance().players[0].timeAlive * 3) * 0.25F + 2F, 0);
            }
            else if (this == NULL_TILE19)
            {
                color = new Vector3f((float)Math.sin(Game.getInstance().players[0].timeAlive * 3) * 0.25F + 2F, 0, 0);
            }
            else
            {
                specialLight = false;
                color = new Vector3f(0.5F, 0.5F, 0.5F);
            }
            tex.bind();
            Game.getInstance().tessellator.start(GL11.GL_QUADS);
            if (!drawFullLight) this.colorDynamic(drawPos, new Vector2d(0.5, 0.5), color, specialLight ? 0.05F : 1.0F);
            Game.getInstance().tessellator.addVertexScaled(0.0F, 0.0F, 0, 1);
            if (!drawFullLight) this.colorDynamic(drawPos, new Vector2d(-0.5, 0.5), color, specialLight ? 0.05F : 1.0F);
            Game.getInstance().tessellator.addVertexScaled(1.0F, 0.0F, 1, 1);
            if (!drawFullLight) this.colorDynamic(drawPos, new Vector2d(-0.5, -0.5), color, specialLight ? 0.05F : 1.0F);
            Game.getInstance().tessellator.addVertexScaled(1.0F, 1.0F, 1, 0);
            if (!drawFullLight) this.colorDynamic(drawPos, new Vector2d(0.5, -0.5), color, specialLight ? 0.05F : 1.0F);
            Game.getInstance().tessellator.addVertexScaled(0.0F, 1.0F, 0, 0);
            Game.getInstance().tessellator.draw();
            GL11.glEnd();
        }

        if (!specialLight)
        {
            tex = Texture.getTexture(textureName);

            for (ButtonEffect effect : Game.getInstance().gameWorld.buttonEffectList)
            {
                if (layer == effect.directionalPoint.layer)
                {
                    if (effect.directionalPoint.point.x == drawPos.x - 0.5 && effect.directionalPoint.point.y == drawPos.y - 0.5)
                    {
                        if (effect.lastPressed)
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
                    }
                }
            }

//            if ((this == NULL_TILE18 || this == NULL_TILE19) && Game.getInstance().players[0] != null && Game.getInstance().player.boundingBox.intersects(new BoundingBox(drawPos.copy(), drawPos.copy().add(new Vector2d(0.5, 0.6)))))
//            {
//                if (this == NULL_TILE18)
//                {
//                    tex = Texture.getTexture("button_down_yel.png");
//                }
//                else
//                {
//                    tex = Texture.getTexture("button_down_ora.png");
//                }
//            }

            tex.bind();
            Game.getInstance().tessellator.start(GL11.GL_QUADS);
            if (!drawFullLight) this.colorDynamic(drawPos, new Vector2d(0.5, 0.5));
            Game.getInstance().tessellator.addVertexScaled(0.0F, 0.0F, 0, 1);
            if (!drawFullLight) this.colorDynamic(drawPos, new Vector2d(-0.5, 0.5));
            Game.getInstance().tessellator.addVertexScaled(1.0F, 0.0F, 1, 1);
            if (!drawFullLight) this.colorDynamic(drawPos, new Vector2d(-0.5, -0.5));
            Game.getInstance().tessellator.addVertexScaled(1.0F, 1.0F, 1, 0);
            if (!drawFullLight) this.colorDynamic(drawPos, new Vector2d(0.5, -0.5));
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
        if (this == NULL_TILE20)
        {
            return new BoundingBox(new Vector2d(x, y), new Vector2d(x + 1, y + 0.1));
        }
		return new BoundingBox(new Vector2i(x, y), new Vector2i(x + 1, y + 1));
	}
}
