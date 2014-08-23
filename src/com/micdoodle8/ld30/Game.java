package com.micdoodle8.ld30;

import com.micdoodle8.ld30base.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.openal.SoundStore;

import java.awt.*;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Game extends com.micdoodle8.ld30base.Window
{
	private World gameWorld;
	public Random random;
	private static Game INSTANCE;
	public EntityPlayer player;
	public KeyButton keyButtonW = new KeyButton(Keyboard.KEY_W);
	public KeyButton keyButtonA = new KeyButton(Keyboard.KEY_A);
	public KeyButton keyButtonS = new KeyButton(Keyboard.KEY_S);
	public KeyButton keyButtonD = new KeyButton(Keyboard.KEY_D);
	public KeyButton keyButtonUp = new KeyButton(Keyboard.KEY_UP);
	public KeyButton keyButtonDown = new KeyButton(Keyboard.KEY_DOWN);
	public KeyButton keyButtonLeft = new KeyButton(Keyboard.KEY_LEFT);
	public KeyButton keyButtonRight = new KeyButton(Keyboard.KEY_RIGHT);
    public KeyButton keyButtonSpace = new KeyButton(Keyboard.KEY_SPACE);
	public ParticleManager particleManager = new ParticleManager();
	public Map<TrueTypeFont, Integer> fontTextures = new HashMap<TrueTypeFont, Integer>();
	public boolean mouseClickedLast = false;
	private boolean initOnce = false;
    public TrueTypeFont fontSourceSansProSize24;
    public TrueTypeFont fontSourceSansProSize16;
    public Tessellator tessellator = new Tessellator();
    public int unlockedLevel = 0;

	public URL getResource(String name)
	{
        try
        {
            URL url = getClass().getClassLoader().getResource("res/" + name);

            if (url == null)
            {
                System.err.println("Failed to get resource: \"res/" + name + "\"");
            }

            return url;
        }
        catch (Exception e)
        {
            System.err.println("Failed to get resource: \"res/" + name + "\"");
            e.printStackTrace();
        }

        return null;
	}
	
	private Game()
	{
		INSTANCE = this;
	}
	
	@Override
	public void init()
	{
		if (!initOnce)
		{
			try
			{
                // TODO LOAD RESOURCES

                InputStream inputStream = getResource("SourceSansPro-Semibold.otf").openStream();
                Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
                awtFont = awtFont.deriveFont(24.0F);
                fontSourceSansProSize24 = new TrueTypeFont(awtFont, false);
                awtFont = awtFont.deriveFont(16.0F);
                fontSourceSansProSize16 = new TrueTypeFont(awtFont, false);

                Field f = fontSourceSansProSize24.getClass().getDeclaredField("fontTexture");
                f.setAccessible(true);
                fontTextures.put(fontSourceSansProSize24, ((org.newdawn.slick.opengl.Texture)f.get(fontSourceSansProSize24)).getTextureID());
                fontTextures.put(fontSourceSansProSize16, ((org.newdawn.slick.opengl.Texture)f.get(fontSourceSansProSize16)).getTextureID());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if (this.currentScreen == null)
		{
			this.setGuiScreen(new GuiMainMenu());
		}
		else
		{
            // TODO START NEW GAME

            gameWorld = new World(new Vector2i(250, 256));
//            gameWorld.worldScale = 8;
//            gameWorld.worldTranslate = new Vector2d(80, 44);

            gameWorld.addEntityToWorld(new EntityPlayer(gameWorld, new Vector2d(0, 0)));
		}
		
		initOnce = true;
	}

	@Override
	protected void update(int deltaTicks) 
	{
		if (deltaTicks > 200)
		{
			deltaTicks = 0;
		}

        if (keyButtonRight.isKeyPressed())
        {
            this.gameWorld.worldTranslate.x += 1;
        }

        if (keyButtonLeft.isKeyPressed())
        {
            this.gameWorld.worldTranslate.x -= 1;
        }
		
		if (!mouseClickedLast && Mouse.isButtonDown(0))
		{
			if (this.currentScreen != null)
			{
				this.currentScreen.onMouseClick(Mouse.getX(), Mouse.getY());
			}
		}
		
		mouseClickedLast = Mouse.isButtonDown(0);
		
		if (currentScreen instanceof GuiGame)
		{
            particleManager.update(deltaTicks);
            gameWorld.update(deltaTicks);
			
			if (this.player != null)
			{
				if (this.player.position.y <= 1)
				{
					this.startNextWorld();
				}
			}
		}
		
		SoundStore.get().poll(deltaTicks);
		
		if (this.currentScreen != null)
		{
			currentScreen.update(deltaTicks);
		}
	}
	
	public void startNextWorld()
	{
		double playerPosX = this.player.position.x;
		double playerMotionY = this.player.motion.y;
		this.player = null;
		this.gameWorld = new World(new Vector2i(342, 106));
		
//		this.player = new EntityPlayer(gameWorld);
//		this.player.position = new Vector2d(100, 100);
//		this.player.motion = new Vector2d(0, playerMotionY);
//		gameWorld.addEntityToWorld(this.player);
	}

	@Override
	protected void render() 
	{
		GL11.glPushMatrix();
		
		if (currentScreen instanceof GuiGame)
		{
			gameWorld.render();
		}
		
		if (this.currentScreen != null)
		{
			currentScreen.draw();
		}
		
		GL11.glPopMatrix();
	}
	
	public void setGuiScreen(GuiScreen screen)
	{
		if (screen != null)
		{
			screen.init();
		}
		
		this.currentScreen = screen;
	}

	@Override
	protected void shutDown() 
	{
		
	}

	@Override
	public Vector2i getStartingWindowDimensions() 
	{
		return new Vector2i(1280, 720);
	}
	
	public void drawText(TrueTypeFont font, Vector2i position, String text, Color color)
	{
		GL11.glPushMatrix();
		
		GL11.glScalef(1, -1, 1);
		GL11.glTranslatef(position.x, -position.y - font.getHeight(), 0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTextures.get(font));
        font.drawString(0, 0, text, color);
		
		GL11.glPopMatrix();
	}
	
	public static void main(String[] args)
	{
		Game game = new Game();
		game.start();
	}
	
	public static Game getInstance()
	{
		return INSTANCE;
	}
}
