package com.micdoodle8.ld30;

import com.micdoodle8.ld30base.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiChooseLevel extends GuiScreen
{
    private int levelCount = 0;
    private int gridWidth;
    private int gridHeight;
    private Texture texture = Texture.getTexture("buttonSquare.png");
    private List<LevelData> levelData = new ArrayList<LevelData>();

    public GuiChooseLevel()
    {
        File baseFile = new File("./res/");
        System.out.println(baseFile.getAbsolutePath());
        File directory = new File(baseFile, "levels");
        File[] fList = directory.listFiles();

        if (fList != null)
        {
            for (File file : fList)
            {
                if (file.isFile())
                {
                    try
                    {
                        LevelData data = LevelData.read(file);
                        if (data != null)
                        {
                            levelData.add(data);
                        }
                    }
                    catch (IOException e)
                    {
                        System.err.println("Failed to read level: " + file.getAbsolutePath());
                        e.printStackTrace();
                    }
                }
            }
        }

        levelCount = levelData.size();
        this.gridWidth = 4;
        this.gridHeight = 4;
    }

    @Override
    protected void onElementClicked(GuiElement element)
    {
        if (element instanceof GuiButton && ((GuiButton) element).identifier < this.gridWidth * this.gridHeight)
        {
            Game.getInstance().gameWorld = new World(this.levelData.get(((GuiButton) element).identifier));
            Game.getInstance().gameWorld.worldScale = 40;
            Game.getInstance().setGuiScreen(new GuiGame());
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
                GuiButton button = new GuiButton(Game.getInstance().fontSourceSansProSize24, new Vector2i(Game.getInstance().windowSize.x / 2 - 200 + i * 100, Game.getInstance().windowSize.y / 2 + 200 - j * 100), new Vector2i(64, 64), String.valueOf(buttonNumber + 1), texture);
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
