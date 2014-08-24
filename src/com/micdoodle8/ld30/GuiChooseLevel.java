package com.micdoodle8.ld30;

import com.micdoodle8.ld30base.*;
import org.lwjgl.opengl.GL11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GuiChooseLevel extends GuiScreen
{
    private int gridWidth;
    private int gridHeight;
    private Texture texture = Texture.getTexture("buttonSquare.png");
    private float totalTicks;

    public GuiChooseLevel()
    {
        for (int i = 0; i < 8; i++)
        {
            URL url = Game.getInstance().getResource("levels/", "level" + i + ".txt");

            try
            {
                LevelData data = LevelData.read(url);
                if (data != null)
                {
                    Game.getInstance().levelData.add(data);
                }
            }
            catch (IOException e)
            {
                System.err.println("Failed to read level: " + (i + 1));
                e.printStackTrace();
            }
        }

        this.gridWidth = 4;
        this.gridHeight = 2;
    }

    @Override
    protected void onElementClicked(GuiElement element)
    {
        if (element instanceof GuiButton)
        {
            int id = ((GuiButton) element).identifier;

            if (id < this.gridWidth * this.gridHeight)
            {
                if (id == 0)
                {
                    Game.getInstance().setGuiScreen(new GuiIntro());
                }
                else
                {
                    Game.getInstance().startNextWorld(id);
                }
            }
        }
    }

    @Override
    public void init()
    {
        super.init();
        for (int i = 0; i < this.gridWidth; i++)
        {
            for (int j = 0; j < this.gridHeight; j++)
            {
                int buttonNumber = j * gridWidth + i;
                GuiButton button = new GuiButton(Game.getInstance().fontSourceSansProSize24, new Vector2i(Game.getInstance().windowSize.x / 2 - 82 * gridWidth / 2 + i * 82, Game.getInstance().windowSize.y / 2 + 82 * gridHeight / 2 - j * 82), new Vector2i(82, 82), String.valueOf(buttonNumber + 1), texture);
                this.addElement(button);
                button.identifier = buttonNumber;
                if (buttonNumber > Game.getInstance().unlockedLevel)
                {
                    button.enabled = false;
                }
            }
        }
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
        int center = 128;
        int width = 127;
        float phase = 0.0F;
        float freqMod = 1.0F;
        float frequency = (float) (Math.PI * 2 / freqMod);
        float red = (float)Math.sin(frequency * totalTicks / 3000.0D + 2 + phase) * width + center;
        float green = (float)Math.sin(frequency * totalTicks / 3000.0D + 0 + phase) * width + center;
        float blue = (float)Math.sin(frequency * totalTicks / 3000.0D + 4 + phase) * width + center;
        GL11.glColor3f(red / 1550.0F, green / 1550.0F, blue / 1550.0F);
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
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        super.draw();
    }
}
