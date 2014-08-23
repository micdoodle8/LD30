package com.micdoodle8.ld30base;

import java.util.HashMap;
import java.util.Map;

public class KeyButton 
{
    private static final Map<Integer, KeyButton> buttonMap = new HashMap<Integer, KeyButton>();
    private boolean isPressed;
    private int keyButtonCode;
    private int pressTickCount;
    
    public KeyButton(int keyButtonCode)
    {
    	this.isPressed = false;
    	this.keyButtonCode = keyButtonCode;
    	this.pressTickCount = 0;
    	buttonMap.put(keyButtonCode, this);
    }

    public static void setKeyBindState(int keyButtonCode, boolean keyDown)
    {
        if (keyButtonCode != 0)
        {
        	KeyButton keybinding = (KeyButton)buttonMap.get(keyButtonCode);

            if (keybinding != null)
            {
                keybinding.isPressed = keyDown;
            }
        }
    }
    
    public static void increaseHeldCount(int keyButtonCode)
    {
    	if (keyButtonCode != 0)
    	{
        	KeyButton keybinding = (KeyButton)buttonMap.get(keyButtonCode);
        	
        	if (keybinding != null)
        	{
        		++keybinding.pressTickCount;
        	}
    	}
    }
    
    public boolean isKeyPressed()
    {
    	return isPressed;
    }
    
    public boolean isKeyDown()
    {
        if (this.pressTickCount == 0)
        {
            return false;
        }
        else
        {
            --this.pressTickCount;
            return true;
        }
    }
    
    public int getHeldTime()
    {
    	return pressTickCount;
    }
    
    public int getKeyButtonCode()
    {
    	return keyButtonCode;
    }
}
