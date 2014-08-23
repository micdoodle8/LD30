package com.micdoodle8.ld30;

import com.micdoodle8.ld30base.*;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GuiChooseLevel extends GuiScreen
{
    private int levelCount = 0;
    private int gridWidth;
    private int gridHeight;
    private Texture texture = Texture.getTexture("buttonSquare.png");

    public GuiChooseLevel()
    {
        File baseFile = new File("./res/");
        System.out.println(baseFile.getAbsolutePath());
        File directory = new File(baseFile, "levels");
        List<File> levelFiles = new ArrayList<File>();
        File[] fList = directory.listFiles();

        if (fList != null)
        {
            for (File file : fList)
            {
                if (file.isFile())
                {
                    levelFiles.add(file);
                }
            }
        }

        levelCount = levelFiles.size();
        this.gridWidth = 4;
        this.gridHeight = 4;
    }

    @Override
    protected void onElementClicked(GuiElement element)
    {
        if (element instanceof GuiButton && ((GuiButton) element).identifier < this.gridWidth * this.gridHeight)
        {
            Game.getInstance().setGuiScreen(new GuiGame());
            // TODO Start Game Here
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
