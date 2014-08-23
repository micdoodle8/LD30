package com.micdoodle8.ld30base;

import com.micdoodle8.ld30.Game;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;


public class GuiDropdown extends GuiElement
{
    public String[] text;
    public static final Texture dropdownTexture = Texture.getTexture("dropdown.png");
    public static final Texture buttonTexture = Texture.getTexture("button3.png");
    public boolean hover = false;
    private Font font = null;
    private boolean inFocus = false;
    private int selectedIndex;
    private int hoverIndex;

    public GuiDropdown(Font font, Vector2i position, Vector2i size, int selectedIndex, String... text)
    {
        super(position, size);
        this.font = font;
        this.text = text;
        this.selectedIndex = selectedIndex;
    }

    @Override
    public void update(int deltaTicks)
    {
        int mouseX = Mouse.getX();
        int mouseY = Mouse.getY();

        if (mouseX > this.position.x - this.size.x / 2 &&
                mouseX < this.position.x + this.size.x / 2 &&
                mouseY > this.position.y - this.size.y / 2 &&
                mouseY < this.position.y + this.size.y / 2)
        {
            hover = true;
        }
        else
        {
            hover = false;
        }

        if (this.inFocus)
        {
            if (mouseX > this.position.x - this.size.x / 2 &&
                    mouseX < this.position.x + this.size.x / 2 &&
                    mouseY > this.position.y - this.size.y / 2 - this.text.length * this.size.y &&
                    mouseY < this.position.y + this.size.y / 2 - this.size.y)
            {
                int offsetY = (mouseY - this.position.y + this.size.y / 2) * -1;
                this.hoverIndex = (int)Math.floor(offsetY / this.size.y) + 1;
            }
            else
            {
                this.hoverIndex = -1;
            }
        }
        else
        {
            this.hoverIndex = -1;
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

        if (hover || inFocus)
        {
            GL11.glColor3f(0.8F, 0.8F, 0.8F);
        }
        else
        {
            GL11.glColor3f(1, 1, 1);
        }

        float sizeX = (float) (this.size.x);
        float sizeY = (float) (this.size.y);
        int amount = 1;

        if (this.inFocus)
        {
            amount = this.text.length + 1;
        }

        for (int i = 0; i < amount; i++)
        {
            if (i == 0)
            {
                dropdownTexture.bind();
            }
            else if (i == 1)
            {
                buttonTexture.bind();
            }

            if (i != 0)
            {
                if (i == this.selectedIndex + 1)
                {
                    GL11.glColor3f(0.72F, 0.72F, 0.72F);
                }
                else if (this.hoverIndex != -1 && i == this.hoverIndex)
                {
                    GL11.glColor3f(0.72F, 0.72F, 0.72F);
                }
                else
                {
                    GL11.glColor3f(1, 1, 1);
                }
            }

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0, 0);
            GL11.glNormal3f(0, 0, 1);
            GL11.glVertex2f(-sizeX / 2.0F, sizeY / 2.0F - sizeY * i);
            GL11.glTexCoord2f(150 / 256.0F, 0);
            GL11.glVertex2f(sizeX / 2.0F, sizeY / 2.0F - sizeY * i);
            GL11.glTexCoord2f(150 / 256.0F, 22 / 32.0F);
            GL11.glVertex2f(sizeX / 2.0F, -sizeY / 2.0F - sizeY * i);
            GL11.glTexCoord2f(0, 22 / 32.0F);
            GL11.glVertex2f(-sizeX / 2.0F, -sizeY / 2.0F - sizeY * i);
            GL11.glEnd();
        }

        GL11.glPopMatrix();

        if (this.selectedIndex < this.text.length)
        {
            String text = this.text[this.selectedIndex];
            Vector2i pos2 = this.position.copy().add(new Vector2i(this.font.getWidth(text) / - 2, -10));
            Game.getInstance().drawText(Game.getInstance().fontSourceSansProSize16, pos2, text, Color.white);

            if (this.inFocus)
            {
                for (int i = 0; i < this.text.length; i++)
                {
                    text = this.text[i];
                    pos2 = this.position.copy().add(new Vector2i(this.font.getWidth(text) / - 2, -10 - this.size.y * (i + 1)));
                    Game.getInstance().drawText(Game.getInstance().fontSourceSansProSize16, pos2, text, Color.white);
                }
            }
        }
    }

    @Override
    public void onMouseClick(int x, int y)
    {
        if (this.hoverIndex >= 0)
        {
            if (this.selectedIndex != this.hoverIndex - 1)
            {
                this.selectedIndex = this.hoverIndex - 1;
                this.inFocus = false;
                DisplayMode newMode = Game.getInstance().displayModeList.get(this.selectedIndex);
                Game.getInstance().setDisplayMode(newMode.getWidth(), newMode.getHeight(), Display.isFullscreen());
                Game.getInstance().resize(newMode.getWidth(), newMode.getHeight());
            }
        }
        else
        {
            if (hover)
            {
                this.inFocus = !this.inFocus;
            }
            else
            {
                this.inFocus = false;
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
