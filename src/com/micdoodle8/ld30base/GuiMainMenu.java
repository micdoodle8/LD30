package com.micdoodle8.ld30base;

import com.micdoodle8.ld30.Game;
import com.micdoodle8.ld30.GuiChooseLevel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

public class GuiMainMenu extends GuiScreen
{
	private GuiButton buttonStart;
	private GuiButton buttonExit;
	private GuiDropdown dropdown;
    private float totalTicks;
    public static final Texture logoTexture = Texture.getTexture("logo.png");

	@Override
	public void init() 
	{
        super.init();

		this.addElement(buttonStart = new GuiButton(Game.getInstance().fontSourceSansProSize24, new Vector2i(Game.getInstance().windowSize.x / 2, Game.getInstance().windowSize.y / 5 + 60), new Vector2i(250, 40), "Start"));
		this.addElement(buttonExit = new GuiButton(Game.getInstance().fontSourceSansProSize24, new Vector2i(Game.getInstance().windowSize.x / 2, Game.getInstance().windowSize.y / 5), new Vector2i(250, 40), "Quit"));
        String[] displayModes = new String[Game.getInstance().displayModeList.size()];

        int selectedIndex = 0;

        for (int i = 0; i < Game.getInstance().displayModeList.size(); i++)
        {
            DisplayMode mode = Game.getInstance().displayModeList.get(i);
            displayModes[i] = mode.getWidth() + "x" + mode.getHeight();
            if (mode.getWidth() == Game.getInstance().windowSize.x && mode.getHeight() == Game.getInstance().windowSize.y)
            {
                selectedIndex = i;
            }
        }

		this.addElement(dropdown = new GuiDropdown(Game.getInstance().fontSourceSansProSize16, new Vector2i(Game.getInstance().windowSize.x - 150, Game.getInstance().windowSize.y - 50), new Vector2i(150, 22), selectedIndex, displayModes));
	}

	@Override
	public void update(int deltaTicks)
	{
		super.update(deltaTicks);
        totalTicks += deltaTicks;
	}

	@Override
	public void draw()
	{
		GL11.glPushMatrix();
        int center = 128;
        int width = 127;
        float phase = 0.0F;
        float freqMod = 1.0F;
        float frequency = (float) (Math.PI * 2 / freqMod);
        float red = (float)Math.sin(frequency * totalTicks / 3000.0D + 2 + phase) * width + center;
        float green = (float)Math.sin(frequency * totalTicks / 3000.0D + 0 + phase) * width + center;
        float blue = (float)Math.sin(frequency * totalTicks / 3000.0D + 4 + phase) * width + center;
        GL11.glColor3f(red / 2550.0F, green / 2550.0F, blue / 2550.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_QUADS);	
		GL11.glTexCoord2f(0, 0);
		GL11.glNormal3f(0, 0, 1);
		GL11.glVertex2f(0, Game.getInstance().windowSize.y);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(Game.getInstance().windowSize.x, Game.getInstance().windowSize.y);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(Game.getInstance().windowSize.x, 0);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(0, 0);
		GL11.glEnd();
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);

        logoTexture.bind();
		GL11.glPushMatrix();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glNormal3f(0, 0, 1);
		GL11.glVertex2f(Game.getInstance().windowSize.x / 2 - 256, Game.getInstance().windowSize.y - 180 + 128);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(Game.getInstance().windowSize.x / 2 + 256, Game.getInstance().windowSize.y - 180 + 128);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(Game.getInstance().windowSize.x / 2 + 256, Game.getInstance().windowSize.y - 180 - 128);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(Game.getInstance().windowSize.x / 2 - 256, Game.getInstance().windowSize.y - 180 - 128);
		GL11.glEnd();

        String title = "Created by:";
        Game.getInstance().drawText(Game.getInstance().fontSourceSansProSize24, new Vector2i(Game.getInstance().windowSize.x / 2 - Game.getInstance().fontSourceSansProSize24.getWidth(title) / 2, this.buttonExit.position.y - 70), title, Color.red);
        title = "@Micdoodle8";
        Game.getInstance().drawText(Game.getInstance().fontSourceSansProSize24, new Vector2i(Game.getInstance().windowSize.x / 2 - Game.getInstance().fontSourceSansProSize24.getWidth(title) / 2, this.buttonExit.position.y - 95), title, Color.red);
        title = (Display.isFullscreen() ? "Exit " : "") + "Fullscreen: " + Keyboard.getKeyName(Game.getInstance().keyButtonFullscreen.getKeyButtonCode());
        Game.getInstance().drawText(Game.getInstance().fontSourceSansProSize16, new Vector2i(this.dropdown.position.x - Game.getInstance().fontSourceSansProSize16.getWidth(title) / 2, Game.getInstance().windowSize.y - 35), title, Color.red);
		
		super.draw();
	}

	@Override
	protected void onElementClicked(GuiElement element) 
	{
		if (element == this.buttonStart)
		{
			Game.getInstance().setGuiScreen(new GuiChooseLevel());
//			Game.getInstance().init();
		}
		else if (element == this.buttonExit)
		{
			Game.getInstance().requestClose();
		}
	}
}
