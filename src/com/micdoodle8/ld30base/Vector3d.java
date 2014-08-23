package com.micdoodle8.ld30base;

public class Vector3d 
{
	public double x;
	public double y;
	public double z;
	
	public Vector3d(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3i toIntVec()
	{
		return new Vector3i((int)Math.floor(this.x), (int)Math.floor(this.y), (int)Math.floor(this.z));
	}
	
	public float floatX()
	{
		return (float)x;
	}
	
	public float floatY()
	{
		return (float)y;
	}
	
	public float floatZ()
	{
		return (float)z;
	}
}
