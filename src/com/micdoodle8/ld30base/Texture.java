package com.micdoodle8.ld30base;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.micdoodle8.ld30.Game;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;

public class Texture 
{
	private static Map<String, Texture> textureMap = new HashMap<String, Texture>();
	private int glID;
	private byte[] texData;
	
	private Texture(String textureLoc, boolean requestData)
	{
		int lastPeriod = textureLoc.lastIndexOf(".");
		String format = textureLoc.substring(lastPeriod + 1, textureLoc.length());
		
		try 
		{
			org.newdawn.slick.opengl.Texture texture = TextureLoader.getTexture(format.toUpperCase(), Game.getInstance().getResource(textureLoc).openStream(), GL11.GL_NEAREST);
			glID = texture.getTextureID();
			
			if (requestData)
			{
				texData = texture.getTextureData();
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void bind()
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, glID);
	}
	
	public byte[] getData()
	{
		return texData;
	}
	
	public static Texture getTexture(String textureName)
	{
		return getTexture(textureName, false);
	}
	
	public static Texture getTexture(String textureName, boolean requestData)
	{
		if (textureMap.containsKey(textureName))
		{
			return textureMap.get(textureName);
		}
		else
		{
			Texture tex = new Texture(textureName, requestData);
			textureMap.put(textureName, tex);
			return tex;
		}
	}
}
