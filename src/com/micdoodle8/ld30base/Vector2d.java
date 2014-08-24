package com.micdoodle8.ld30base;

import org.lwjgl.util.vector.Vector2f;

public class Vector2d 
{
	public double x;
	public double y;
	
	public Vector2d()
	{
		this(0, 0);
	}
	
	public Vector2d(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Vector2d multiply(double toMul)
	{
		this.x *= toMul;
		this.y *= toMul;
		return this;
	}
	
	public Vector2d add(Vector2d other)
	{
		this.x += other.x;
		this.y += other.y;
		return this;
	}
	
	public Vector2d sub(Vector2d other)
	{
		this.x -= other.x;
		this.y -= other.y;
		return this;
	}
	
	public Vector2i toIntVec()
	{
		return new Vector2i((int)Math.floor(this.x), (int)Math.floor(this.y));
	}
	
	public Vector2f toFloatVec()
	{
		return new Vector2f((float)this.x, (float)this.y);
	}
	
	public double dot(Vector2d other)
	{
		return this.x * other.x + this.y * other.y;
	}
	
	public Vector2d normalize()
	{
		double length = this.getLength();
		this.x /= length;
		this.y /= length;
		return this;
	}
	
	public double getLength()
	{
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}

	public double getLengthUnSqrd()
	{
		return this.x * this.x + this.y * this.y;
	}
	
	public float floatX()
	{
		return (float)x;
	}
	
	public float floatY()
	{
		return (float)y;
	}
	
	public Vector2d copy()
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
	
	@Override
	public String toString()
	{
		return "x: " + this.x + "  y: " + y;
	}
}
