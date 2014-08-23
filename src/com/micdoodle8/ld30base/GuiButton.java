package com.micdoodle8.ld30base;

import com.micdoodle8.ld30.Game;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;


public class GuiButton extends GuiElement
{
	public String text;
	public static final Texture buttonTexture = Texture.getTexture("button2.png");
	public boolean hover = false;
    private Font font = null;
    private final Texture texture;
    public boolean enabled = true;
    public int identifier = -1;
	
	public GuiButton(Font font, Vector2i position, Vector2i size, String text, Texture texture)
	{
        super(position, size);
        this.font = font;
        this.text = text;
        this.texture = texture;
    }

	public GuiButton(Font font, Vector2i position, Vector2i size, String text)
	{
        this(font, position, size, text, buttonTexture);
	}

	@Override
	public void update(int deltaTicks)
	{
		if (Mouse.getX() > this.position.x - this.size.x / 2 &&
				Mouse.getX() < this.position.x + this.size.x / 2 &&
				Mouse.getY() > this.position.y - this.size.y / 2 &&
				Mouse.getY() < this.position.y + this.size.y / 2)
		{
			hover = true;
		}
		else
		{
			hover = false;
		}
	}

	@Override
	public void init() 
	{
		
	}

	@Override
	public void draw() 
	{
		GL11.glPushMatrix();
		GL11.glTranslatef(this.position.x, this.position.y, 0);
		
		if (hover)
		{
			GL11.glColor3f(0.8F, 0.8F, 0.8F);
		}
		else
		{
			GL11.glColor3f(1, 1, 1);
		}

        if (!enabled)
        {
            GL11.glColor3f(0.6F, 0.6F, 0.6F);
        }
		
		this.texture.bind();
		GL11.glBegin(GL11.GL_QUADS);
		float sizeX = (float) (this.size.x);
		float sizeY = (float) (this.size.y);		
		GL11.glTexCoord2f(0, 0);
		GL11.glNormal3f(0, 0, 1);
		GL11.glVertex2f(-sizeX / 2.0F, sizeY / 2.0F);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(sizeX / 2.0F, sizeY / 2.0F);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(sizeX / 2.0F, -sizeY / 2.0F);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(-sizeX / 2.0F, -sizeY / 2.0F);
		GL11.glEnd();
		
		GL11.glPopMatrix();
		
		Vector2i pos2 = this.position.copy().add(new Vector2i(this.font.getWidth(text) / - 2, -13));
		Game.getInstance().drawText(Game.getInstance().fontSourceSansProSize24, pos2, text, Color.white);
	}

    @Override
    public void onMouseClick(int x, int y) {

    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
