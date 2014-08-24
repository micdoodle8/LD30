package com.micdoodle8.ld30;

import com.micdoodle8.ld30base.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;

import java.awt.*;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

public class Game extends com.micdoodle8.ld30base.Window
{
	public World gameWorld;
	public Random random;
	private static Game INSTANCE;
	public EntityPlayer[] players;
	public KeyButton keyButtonW = new KeyButton(Keyboard.KEY_W);
	public KeyButton keyButtonA = new KeyButton(Keyboard.KEY_A);
	public KeyButton keyButtonS = new KeyButton(Keyboard.KEY_S);
	public KeyButton keyButtonD = new KeyButton(Keyboard.KEY_D);
	public KeyButton keyButtonB = new KeyButton(Keyboard.KEY_B);
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
    public int activePlayer = 0;
    public Light mouseLight;
    public float totalGameTime;
    public static final float TOTAL_TRANSITION_TIME = 1000.0F;
    public float transitionProgress = -1;
    public int transitionState = 0;
    public Audio[] transitionSound = new Audio[2];
    public java.util.List<LevelData> levelData = new ArrayList<LevelData>();

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
        try
        {
            transitionSound[0] = AudioLoader.getAudio("OGG", getResource("tele0.ogg").openStream());
            transitionSound[1] = AudioLoader.getAudio("OGG", getResource("tele1.ogg").openStream());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
            //

//            gameWorld = new World(new Vector2i(250, 256));
//            gameWorld.worldScale = 8;
//            gameWorld.worldTranslate = new Vector2d(80, 44);

            gameWorld.addEntityToWorld(new EntityPlayer(1, gameWorld, new Vector2d(0, 0)));
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

        this.totalGameTime += deltaTicks;

        if (transitionProgress >= 0)
        {
            this.transitionProgress += deltaTicks;

            if (this.transitionProgress >= TOTAL_TRANSITION_TIME)
            {
                this.transitionState = (this.transitionState == 0 ? 1 : 0);
                this.transitionProgress = -1;
            }
        }

        if (gameWorld != null)
        {
            if (mouseLight != null)
            {
                if (this.transitionState == 0)
                {
                    mouseLight.position = Game.getInstance().gameWorld.screenCoordsToWorld(Mouse.getX(), Mouse.getY());
                }
                else
                {
                    mouseLight.position = Game.getInstance().gameWorld.screenCoordsToWorld(windowSize.x - Mouse.getX(), windowSize.y - Mouse.getY() - 1);
                }
            }
        }

        if (keyButtonSpace.isKeyDown())
        {
            this.activePlayer = this.activePlayer == 0 ? 1 : 0;
            this.transitionProgress = 0;
            transitionSound[this.transitionState].playAsSoundEffect(1.0F, 1.0F, false);
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
			
//			if (this.players != null)
//			{
//				if (this.player.position.y <= 1)
//				{
//					this.startNextWorld();
//				}
//			}
		}
		
		SoundStore.get().poll(deltaTicks);
		
		if (this.currentScreen != null)
		{
			currentScreen.update(deltaTicks);
		}
	}
	
	public boolean startNextWorld(int level)
	{
        if (levelData.size() > level)
        {
            this.gameWorld = new World(level, Game.getInstance().levelData.get(level));
            mouseLight = new Light(Game.getInstance().gameWorld.screenCoordsToWorld(Mouse.getX(), Mouse.getY()), 1.0F, new Vector3f(1.0F, 1.0F, 1.0F));
            gameWorld.lightList.add(mouseLight);
            return true;
        }
        return false;
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
