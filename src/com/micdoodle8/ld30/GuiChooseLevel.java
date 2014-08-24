package com.micdoodle8.ld30;

import com.micdoodle8.ld30base.*;

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
                GuiButton button = new GuiButton(Game.getInstance().fontSourceSansProSize24, new Vector2i(Game.getInstance().windowSize.x / 2 - 200 + i * 100, Game.getInstance().windowSize.y / 2 + 100 - j * 100), new Vector2i(64, 64), String.valueOf(buttonNumber + 1), texture);
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
    }

    @Override
    public void draw()
    {
        super.draw();
    }
}
