package com.micdoodle8.ld30;

import com.micdoodle8.ld30.Game;
import com.micdoodle8.ld30base.*;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

public class GuiIntro extends GuiScreen
{
    private float scroll = 0;
    private Texture keysTexture;

    @Override
    public void init()
    {
        this.elements.clear();
        keysTexture = Texture.getTexture("keys.png");
    }

    @Override
    public void update(int deltaTicks)
    {
        super.update(deltaTicks);
        if (this.scroll / 70.0F < Game.getInstance().windowSize.y / 1.5)
            this.scroll += deltaTicks * 4;
    }

    @Override
    public void draw()
    {
        super.draw();

        Game.getInstance().drawTextSplit(Game.getInstance().fontSourceSansProSize32, new Vector2i(Game.getInstance().windowSize.x / 2, (int)(30 + this.scroll / 70.0F)), "You are an AI system in the year 2149. With a recent breakthrough in AI technology, your mainframe has connected with a similar model in a parallel universe. Researchers were not expecting a massive power surge, which caused severe damage to some components of your system. Use your robot and a similar robot in the parallel universe to reach the control room of your system. Note: Colored robots can only use telepads/buttons of their color.", Color.cyan, Game.getInstance().windowSize.x - 50);

        this.keysTexture.bind();

        if (this.scroll / 70.0F >= Game.getInstance().windowSize.y / 1.5)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0, -160, 0);
            Game.getInstance().tessellator.start(GL11.GL_QUADS);
            Game.getInstance().tessellator.addVertexScaled(Game.getInstance().windowSize.x / 2 - 128, Game.getInstance().windowSize.y / 2 - 128, 0, 1);
            Game.getInstance().tessellator.addVertexScaled(Game.getInstance().windowSize.x / 2 + 128, Game.getInstance().windowSize.y / 2 - 128, 1, 1);
            Game.getInstance().tessellator.addVertexScaled(Game.getInstance().windowSize.x / 2 + 128, Game.getInstance().windowSize.y / 2 + 128, 1, 0);
            Game.getInstance().tessellator.addVertexScaled(Game.getInstance().windowSize.x / 2 - 128, Game.getInstance().windowSize.y / 2 + 128, 0, 0);
            Game.getInstance().tessellator.draw();
            GL11.glPopMatrix();

            Game.getInstance().drawText(Game.getInstance().fontSourceSansProSize24, new Vector2i(Game.getInstance().windowSize.x / 2 + 108, Game.getInstance().windowSize.y / 2 - 85), "Jump", Color.cyan);
            Game.getInstance().drawText(Game.getInstance().fontSourceSansProSize24, new Vector2i(Game.getInstance().windowSize.x / 2 + 135, Game.getInstance().windowSize.y / 2 - 135), "Move Right", Color.cyan);
            Game.getInstance().drawText(Game.getInstance().fontSourceSansProSize24, new Vector2i(Game.getInstance().windowSize.x / 2 - 235, Game.getInstance().windowSize.y / 2 - 115), "Move Left", Color.cyan);
            Game.getInstance().drawText(Game.getInstance().fontSourceSansProSize24, new Vector2i(Game.getInstance().windowSize.x / 2 + 20, Game.getInstance().windowSize.y / 2 - 192), "Use Telepad", Color.cyan);
            Game.getInstance().drawText(Game.getInstance().fontSourceSansProSize24, new Vector2i(Game.getInstance().windowSize.x / 2 + 20, Game.getInstance().windowSize.y / 2 - 290), "Switch to Parallel Universe", Color.cyan);
        }
    }

    @Override
    public void onMouseClick(int x, int y)
    {
        if (this.scroll / 70.0F >= Game.getInstance().windowSize.y / 1.5)
        {
            Game.getInstance().startNextWorld(0);
        }
        else
        {
            this.scroll = (float)(Game.getInstance().windowSize.y / 1.5) * 75.0F;
        }
    }

    @Override
    protected void onElementClicked(GuiElement element)
    {
    }
}
