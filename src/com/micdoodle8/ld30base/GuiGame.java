package com.micdoodle8.ld30base;

import com.micdoodle8.ld30.Game;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class GuiGame extends GuiScreen
{
    private GuiButton buttonResume;
    private GuiButton buttonQuit;
    private GuiButton buttonRestart;

	@Override
	public void init() 
	{
		this.elements.clear();

        if (Game.getInstance().paused)
        {
            this.addElement((buttonResume = new GuiButton(Game.getInstance().fontSourceSansProSize32, new Vector2i(Game.getInstance().windowSize.x / 2, Game.getInstance().windowSize.y / 2 + 150), new Vector2i(200, 40), "Resume")));
            this.addElement((buttonRestart = new GuiButton(Game.getInstance().fontSourceSansProSize32, new Vector2i(Game.getInstance().windowSize.x / 2, Game.getInstance().windowSize.y / 2 + 75), new Vector2i(200, 40), "Restart Level")));
            this.addElement((buttonQuit = new GuiButton(Game.getInstance().fontSourceSansProSize32, new Vector2i(Game.getInstance().windowSize.x / 2, Game.getInstance().windowSize.y / 2), new Vector2i(200, 40), "Menu")));
        }
	}

	@Override
	public void update(int deltaTicks)
	{
		super.update(deltaTicks);
	}

	@Override
	public void draw()
	{
        // TODO DRAW HUD TEXT
		
		super.draw();

        if (Game.getInstance().paused)
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(0.5F, 0.5F, 0.5F, 0.5F);
            Game.getInstance().tessellator.start(GL11.GL_QUADS);
            Game.getInstance().tessellator.addVertexScaled(0, 0);
            Game.getInstance().tessellator.addVertexScaled(Game.getInstance().windowSize.x, 0);
            Game.getInstance().tessellator.addVertexScaled(Game.getInstance().windowSize.x, Game.getInstance().windowSize.y);
            Game.getInstance().tessellator.addVertexScaled(0, Game.getInstance().windowSize.y);
            Game.getInstance().tessellator.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
	}

	@Override
	protected void onElementClicked(GuiElement element) 
	{
        if (element == buttonResume)
        {
            Game.getInstance().paused = false;
            this.init();
        }
        else if (element == buttonRestart)
        {
            Game.getInstance().paused = false;
            this.init();
            Game.getInstance().startNextWorld(Game.getInstance().gameWorld.levelIndex);
        }
        else if (element == buttonQuit)
        {
            Game.getInstance().paused = false;
            this.init();
            Game.getInstance().setGuiScreen(new GuiMainMenu());
            for (Entity e : new ArrayList<Entity>(Game.getInstance().gameWorld.entityList))
            {
                Game.getInstance().gameWorld.entityList.remove(e);
            }
            Game.getInstance().gameWorld = null;
        }
	}
}
