package com.micdoodle8.ld30base;

import java.util.Random;

public class SimpleNoise 
{
	private Random random;
	private float[] values;
	private boolean smoothstep;
	
	public SimpleNoise(long seed, int maxVertices, boolean smoothstep)
	{
		random = new Random(seed);
		values = new float[maxVertices];
		this.smoothstep = smoothstep;
		
		for (int i = 0; i <maxVertices; i++)
		{
			values[i] = random.nextFloat();
		}
	}
	
	public float evaluate(float position)
	{
		int position0 = (int)Math.floor(position);
		float t = position - position0;
		int xMin = position0 & (values.length - 1);
		int xMax = (xMin + 1) & (values.length - 1);
		return smoothStep(values[xMin], values[xMax], t);
	}
	
	private float smoothStep(float a, float b, float t)
	{
	    float tRemapSmoothstep = this.smoothstep ? (t * t * (3 - 2 * t)) : t;
	    return mix(a, b, tRemapSmoothstep);
	}
	
	private float mix(float a, float b, float t)
	{
	    return a * ( 1 - t ) + b * t;
	}
}
