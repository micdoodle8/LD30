package com.micdoodle8.ld30base;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Window
{
	public Timer gameTimer;
	public Vector2i windowSize = this.getStartingWindowDimensions();
	private boolean isCloseRequested = false;
    public KeyButton keyButtonFullscreen = new KeyButton(Keyboard.KEY_F11);
    public List<DisplayMode> displayModeList = new ArrayList<DisplayMode>();
    public GuiScreen currentScreen;
    public Vector2d scale = new Vector2d(1, 1);
	
	public void start()
	{
		initOpenGL();
		init();
		this.gameTimer = new Timer();
		
		while (true)
		{
			while (Keyboard.next())
			{
				KeyButton.setKeyBindState(Keyboard.getEventKey(), Keyboard.getEventKeyState());
				
				if (Keyboard.getEventKeyState())
				{
					KeyButton.increaseHeldCount(Keyboard.getEventKey());
				}
			}
			
			runFrame();
			
			this.gameTimer.update();

            if (keyButtonFullscreen.isKeyDown())
            {
                this.setDisplayMode(getStartingWindowDimensions().x, getStartingWindowDimensions().y, true);
            }

			Display.update();
			Display.sync(60);
			
			if (Display.isCloseRequested() || isCloseRequested)
			{
				Display.destroy();
				AL.destroy();
				System.exit(0);
			}
		}
	}

    private Map<Integer, DisplayMode> displayModeMap = new HashMap<Integer, DisplayMode>();

    public void setDisplayMode(int width, int height, boolean fullscreen)
    {
        if (Display.getDisplayMode().getWidth() == width && Display.getDisplayMode().getHeight() == height && Display.isFullscreen() == fullscreen)
        {
            return;
        }

        displayModeList.clear();
        displayModeMap.clear();

        try
        {
            DisplayMode currentDisplayMode;

            DisplayMode[] modes = Display.getAvailableDisplayModes();

            for (DisplayMode mode : modes)
            {
                int key = mode.getWidth() ^ mode.getHeight();
                DisplayMode targetDisplayMode = displayModeMap.get(key);

                if (targetDisplayMode == null || mode.getFrequency() >= targetDisplayMode.getFrequency())
                {
                    if (targetDisplayMode == null || mode.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())
                    {
                        displayModeMap.put(key, mode);
                    }
                }

                if (mode.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel() && mode.getFrequency() == Display.getDesktopDisplayMode().getFrequency())
                {
                    displayModeMap.put(key, mode);
                }
            }

            if (fullscreen)
            {
                currentDisplayMode = displayModeMap.get(width ^ height);
            }
            else
            {
                currentDisplayMode = new DisplayMode(width, height);
            }

            if (currentDisplayMode == null)
            {
                System.err.println("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
                return;
            }

            Display.setDisplayMode(currentDisplayMode);
            Display.setFullscreen(fullscreen);
            windowSize.x = width;
            windowSize.y = height;
            scale.x = width / 1280.0D;
            scale.y = height / 720.0D;

            displayModeList.addAll(displayModeMap.values());

            int j;
            for (int i = 1; i < displayModeMap.size(); i++)
            {
                j = i;
                while (j > 0 && getDisplayModeKey(displayModeList.get(j - 1)) > getDisplayModeKey(displayModeList.get(j)))
                {
                    DisplayMode d0 = displayModeList.get(j);
                    displayModeList.set(j, displayModeList.get(j - 1));
                    displayModeList.set(j - 1, d0);
                    j--;
                }
            }

            if (currentScreen != null)
            {
                currentScreen.init();
            }
        }
        catch (LWJGLException e)
        {
            System.err.println("Unable to setup mode: " + width + "x" + height + " fs=" + fullscreen);
            e.printStackTrace();
        }
    }

    private int getDisplayModeKey(DisplayMode mode)
    {
        return mode.getWidth() * mode.getHeight();
    }
	
	private void initOpenGL()
	{
		try
		{
			this.setDisplayMode(getStartingWindowDimensions().x, getStartingWindowDimensions().y, false);
			Display.create();
			Display.setVSyncEnabled(true);
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
			System.exit(0);
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);        
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);                    
        
		GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);                
        GL11.glClearDepth(1);                                       
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        this.resize(this.windowSize.x, this.windowSize.y);
	}

    public void resize(int x, int y)
    {
        GL11.glViewport(0, 0, x, y);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, x, 0, y, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }
	
	private void runFrame()
	{
		update(this.gameTimer.getDelta());
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		render();
	}
	
	public void requestClose()
	{
		isCloseRequested = true;
	}
	
	protected abstract void init();
	
	protected abstract void update(int deltaTicks);
	
	protected abstract void render();
	
	protected abstract void shutDown();
	
	public abstract Vector2i getStartingWindowDimensions();
}
