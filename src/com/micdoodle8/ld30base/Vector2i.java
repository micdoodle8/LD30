package com.micdoodle8.ld30base;

public class Vector2i 
{
	public int x;
	public int y;
	
	public Vector2i(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Vector2d toDoubleVec()
	{
		return new Vector2d(this.x, this.y);
	}
	
	@Override
	public int hashCode()
	{
		return ("x:" + x + " y:" + y).hashCode();
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Vector2d)
		{
			return ((Vector2d)other).x == this.x && ((Vector2d)other).y == this.y;
		}
		
		return false;
	}
	
	public Vector2i copy()
	{
		return new Vector2i(this.x, this.y);
	}
	
	public Vector2i add(Vector2i other)
	{
		this.x += other.x;
		this.y += other.y;
		return this;
	}

    @Override
    public String toString()
    {
        return "x: " + this.x + "  y: " + y;
    }
}
