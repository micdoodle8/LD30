package com.micdoodle8.ld30base;

import com.micdoodle8.ld30base.Vector2i;

public enum Direction 
{
	UP(0, 1),
	DOWN(0, -1),
	LEFT(-1, 0),
	RIGHT(1, 0);
	
	private int offsetX;
	private int offsetY;
	
	private Direction(int offsetX, int offsetY)
	{
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}
	
	public Vector2i getOffset()
	{
		return new Vector2i(this.offsetX, this.offsetY);
	}
}
