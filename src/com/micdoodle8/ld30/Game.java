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
import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.List;

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
    public KeyButton keyButtonEsc = new KeyButton(Keyboard.KEY_ESCAPE);
	public ParticleManager particleManager = new ParticleManager();
	public Map<TrueTypeFont, Integer> fontTextures = new HashMap<TrueTypeFont, Integer>();
	public boolean mouseClickedLast = false;
	private boolean initOnce = false;
    public TrueTypeFont fontSourceSansProSize24;
    public TrueTypeFont fontSourceSansProSize16;
    public TrueTypeFont fontSourceSansProSize32;
    public Tessellator tessellator = new Tessellator();
    public int unlockedLevel = 0;
    public int activePlayer = 0;
    public Light mouseLight;
    public float totalGameTime;
    public static final float TOTAL_TRANSITION_TIME = 1100.0F;
    public float transitionProgress = -1;
    public int transitionState = 0;
    public Audio[] transitionSound = new Audio[2];
    public Audio soundEffectWalk;
//    public Audio soundEffectButton;
    public Audio soundEffectZap;
    public Audio musicMain;
    public Audio musicMenu;
    public java.util.List<LevelData> levelData = new ArrayList<LevelData>();
    public boolean paused = false;

    public URL getResource(String name)
    {
        return this.getResource("res/", name);
    }

    public URL getResource(String prefix, String name)
	{
        try
        {
            URL url = getClass().getClassLoader().getResource(prefix + name);

            if (url == null)
            {
                System.err.println("Failed to get resource: \"" + prefix + name + "\"");
            }

            return url;
        }
        catch (Exception e)
        {
            System.err.println("Failed to get resource: \"" + prefix + name + "\"");
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
            soundEffectWalk = AudioLoader.getAudio("OGG", getResource("walk.ogg").openStream());
//            soundEffectButton = AudioLoader.getAudio("WAV", getResource("button.wav").openStream());
            soundEffectZap = AudioLoader.getAudio("OGG", getResource("zap.ogg").openStream());
            musicMain = AudioLoader.getStreamingAudio("OGG", getResource("music.ogg").openConnection().getURL());
            musicMenu = AudioLoader.getStreamingAudio("OGG", getResource("neutral.ogg").openConnection().getURL());
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
                this.readLevelProgress();
                // TODO LOAD RESOURCES

                InputStream inputStream = getResource("SourceSansPro-Semibold.otf").openStream();
                Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
                awtFont = awtFont.deriveFont(24.0F);
                fontSourceSansProSize24 = new TrueTypeFont(awtFont, false);
                awtFont = awtFont.deriveFont(16.0F);
                fontSourceSansProSize16 = new TrueTypeFont(awtFont, false);
                awtFont = awtFont.deriveFont(32.0F);
                fontSourceSansProSize32 = new TrueTypeFont(awtFont, false);

                Field f = fontSourceSansProSize24.getClass().getDeclaredField("fontTexture");
                f.setAccessible(true);
                fontTextures.put(fontSourceSansProSize24, ((org.newdawn.slick.opengl.Texture) f.get(fontSourceSansProSize24)).getTextureID());
                fontTextures.put(fontSourceSansProSize16, ((org.newdawn.slick.opengl.Texture)f.get(fontSourceSansProSize16)).getTextureID());
                fontTextures.put(fontSourceSansProSize32, ((org.newdawn.slick.opengl.Texture)f.get(fontSourceSansProSize32)).getTextureID());
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

    public void setTransitionState(int state)
    {
        this.transitionState = state;
        this.activePlayer = state;
    }

    public void saveLevelProgress()
    {
        File file = new File(".");
        File saveFolder = new File(file, "save");
        if (!saveFolder.exists())
        {
            saveFolder.mkdir();
        }
        File saveFile = new File(saveFolder, "progress.fsav");
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile));
            bw.write(String.valueOf(unlockedLevel));
            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void readLevelProgress()
    {
        File file = new File(".");
        File saveFolder = new File(file, "save");
        if (saveFolder.exists())
        {
            File saveFile = new File(saveFolder, "progress.fsav");
            if (saveFile.exists())
            {
                try
                {
                    BufferedReader br = new BufferedReader(new FileReader(saveFile));
                    this.unlockedLevel = Integer.parseInt(br.readLine());
                    br.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

	@Override
	protected void update(int deltaTicks) 
	{
		if (deltaTicks > 200)
		{
			deltaTicks = 0;
		}

        this.totalGameTime += deltaTicks;

        if (!(currentScreen instanceof GuiGame))
        {
            this.paused = true;
        }

        if (Game.getInstance().keyButtonEsc.isKeyDown())
        {
            this.paused = !this.paused;

            if (this.currentScreen instanceof GuiGame)
            {
                this.currentScreen.init();
            }
        }

        if (transitionProgress >= 0)
        {
            this.transitionProgress += deltaTicks;

            if (this.transitionProgress < TOTAL_TRANSITION_TIME / 4.0F)
            {
                int transitionStateTemp = (this.transitionState == 0 ? 0 : 1);

                for (Light light : gameWorld.dynamicLightList)
                {
                    light.color = transitionStateTemp == 0 ? new Vector3f(0.0F, 0.8F, 1.0F) : new Vector3f(1, 0, 0.8F);
                }
            }

            if (this.transitionProgress >= TOTAL_TRANSITION_TIME)
            {
                this.setTransitionState(this.transitionState == 0 ? 1 : 0);
                this.transitionProgress = -1;
            }
        }

        if (gameWorld != null && !paused)
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

        if (!paused && keyButtonSpace.isKeyDown())
        {
            if (this.transitionProgress < 0)
            {
                this.setTransitionState(this.transitionState);
                this.transitionProgress = 0;
                transitionSound[this.transitionState].playAsSoundEffect(1.0F, 1.0F, false);
            }
        }
		
		if (!mouseClickedLast && Mouse.isButtonDown(0))
		{
			if (this.currentScreen != null)
			{
				this.currentScreen.onMouseClick(Mouse.getX(), Mouse.getY());
			}
		}
		
		mouseClickedLast = Mouse.isButtonDown(0);
		
		if (!paused && currentScreen instanceof GuiGame)
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
        this.unlockedLevel = Math.max(this.unlockedLevel, level);
        this.saveLevelProgress();
        if (level < levelData.size())
        {
            this.setTransitionState(0);
            this.gameWorld = new World(level, Game.getInstance().levelData.get(level));
            mouseLight = new Light(Game.getInstance().gameWorld.screenCoordsToWorld(Mouse.getX(), Mouse.getY()), 1.0F, Game.getInstance().transitionState == 0 ? new Vector3f(1, 0, 0.8F) : new Vector3f(0.0F, 0.8F, 1.0F));
            gameWorld.lightList.add(mouseLight);
            gameWorld.dynamicLightList.add(mouseLight);
            Game.getInstance().paused = false;
            this.setGuiScreen(new GuiGame());
            return true;
        }
        else
        {
            this.setGuiScreen(new GuiOutro());
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

    public int drawTextSplit(TrueTypeFont font, Vector2i position, String text, Color color, int splitWidth)
    {
        String[] split = text.split(" ");
        int i = 0;
        int j = 0;
        int width = 0;
        List<String> postSplit = new ArrayList<String>();

        while (i < split.length)
        {
            String str = split[i] + " ";
            int temp = font.getWidth(str);

            if (width + temp > splitWidth)
            {
                j++;
                width = 0;
            }
            else
            {
                width += temp;
                if (j == postSplit.size())
                {
                    postSplit.add(str);
                }
                else
                {
                    postSplit.set(j, postSplit.get(j).concat(str));
                }

                i++;
            }
        }

        for (String str : postSplit)
        {
            drawText(font, position.copy().add(new Vector2i(-font.getWidth(str) / 2, -postSplit.indexOf(str) * (font.getHeight() + 5))), str, color);
        }

        return postSplit.size();
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
