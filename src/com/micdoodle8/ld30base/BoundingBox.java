package com.micdoodle8.ld30base;

public class BoundingBox
{
	public Vector2d minVec;
	public Vector2d maxVec;
	
	public BoundingBox(Vector2d minVec, Vector2d maxVec)
	{
		this.minVec = minVec;
		this.maxVec = maxVec;
	}
	
	public BoundingBox(Vector2i minVec, Vector2i maxVec)
	{
		this(minVec.toDoubleVec(), maxVec.toDoubleVec());
	}
	
	public BoundingBox expand(double amount)
	{
		this.minVec.x -= amount;
		this.minVec.y -= amount;
		this.maxVec.x += amount;
		this.maxVec.y += amount;
		return this;
	}
	
	public BoundingBox add(Vector2d toAdd)
	{
		this.minVec.x += toAdd.x;
		this.minVec.y += toAdd.y;
		this.maxVec.x += toAdd.x;
		this.maxVec.y += toAdd.y;
		return this;
	}
	
	public double calcYOffset(BoundingBox otherBox, double motionY)
	{
		if (otherBox.maxVec.x > this.minVec.x && otherBox.minVec.x < this.maxVec.x)
		{
			double delta;
			
			if (motionY > 0.0 && otherBox.maxVec.y <= this.minVec.y)
			{
				delta = this.minVec.y - otherBox.maxVec.y;
				
				if (delta < motionY)
				{
					motionY = delta;
				}
			}
			
			if (motionY < 0.0 && otherBox.minVec.y >= this.maxVec.y)
			{
				delta = this.maxVec.y - otherBox.minVec.y;
				
				if (delta > motionY)
				{
					motionY = delta;
				}
			}
		}
		
		return motionY;
	}
	
	public double calcXOffset(BoundingBox otherBox, double motionX)
	{
		if (otherBox.maxVec.y > this.minVec.y && otherBox.minVec.y < this.maxVec.y)
		{
			double delta;
			
			if (motionX > 0.0 && otherBox.maxVec.x <= this.minVec.x)
			{
				delta = this.minVec.x - otherBox.maxVec.x;
				
				if (delta < motionX)
				{
					motionX = delta;
				}
			}
			
			if (motionX < 0.0 && otherBox.minVec.x >= this.maxVec.x)
			{
				delta = this.maxVec.x - otherBox.minVec.x;
				
				if (delta > motionX)
				{
					motionX = delta;
				}
			}
		}
		
		return motionX;
	}
	
	public BoundingBox copy()
	{
		return new BoundingBox(new Vector2d(this.minVec.x, this.minVec.y), new Vector2d(this.maxVec.x, this.maxVec.y));
	}
	
	public boolean intersects(BoundingBox otherBox)
	{
		return otherBox.maxVec.y > this.minVec.y && otherBox.minVec.y < this.maxVec.y &&
				otherBox.maxVec.x > this.minVec.x && otherBox.minVec.x < this.maxVec.x;
	}
	
	public boolean equals(Object other)
	{
		if (other instanceof BoundingBox)
		{
			return ((BoundingBox) other).minVec.equals(this.minVec) && ((BoundingBox) other).maxVec.equals(this.maxVec);
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return this.minVec.hashCode() ^ this.maxVec.hashCode();
	}

    @Override
    public String toString()
    {
        return "[" + minVec.x + ", " + minVec.y + "]-[" + maxVec.x + ", " + maxVec.y + "]";
    }
}
