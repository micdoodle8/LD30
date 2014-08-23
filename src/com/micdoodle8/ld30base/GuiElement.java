package com.micdoodle8.ld30base;

import com.micdoodle8.ld30base.Gui;
import com.micdoodle8.ld30base.Vector2i;

public abstract class GuiElement extends Gui
{
	public Vector2i position;
	public Vector2i size;
	
	public GuiElement(Vector2i pos, Vector2i size)
	{
		this.position = pos;
		this.size = size;
	}

    public abstract void onMouseClick(int x, int y);

    public abstract boolean isEnabled();
}
