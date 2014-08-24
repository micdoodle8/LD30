package com.micdoodle8.ld30;

import com.micdoodle8.ld30base.Vector2d;
import org.lwjgl.util.vector.Vector3f;

public class Light
{
    public Vector2d position;
    public float brightness;
    public Vector3f color;
    public float flicker;
    public double flickerPhase;
    public double flickerPhase0;

    public Light(Vector2d position, float brightness, Vector3f color)
    {
        this(position, brightness, color, 0.0F);
    }

    public Light(Vector2d position, float brightness, Vector3f color, float flicker)
    {
        this.position = position;
        this.brightness = brightness;
        this.color = color;
        this.flicker = flicker;
        this.flickerPhase = Math.random() * 2 * Math.PI;
        this.flickerPhase0 = Math.random() * 2 * Math.PI;
    }
}
