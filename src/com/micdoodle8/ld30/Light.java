package com.micdoodle8.ld30;

import com.micdoodle8.ld30base.Vector2d;
import org.lwjgl.util.vector.Vector3f;

public class Light
{
    public Vector2d position;
    public float brightness;
    public Vector3f color;

    public Light(Vector2d position, float brightness, Vector3f color)
    {
        this.position = position;
        this.brightness = brightness;
        this.color = color;
    }
}
