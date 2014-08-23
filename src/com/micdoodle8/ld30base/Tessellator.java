package com.micdoodle8.ld30base;

import org.lwjgl.opengl.GL11;

public class Tessellator
{
    public void start(int type)
    {
        GL11.glBegin(type);
    }

    public void addVertexScaled(float x0, float y0)
    {
        GL11.glNormal3f(0, 0, 1);
        GL11.glVertex2f(x0, y0);
    }

    public void addVertexScaled(float x0, float y0, float u, float v)
    {
        GL11.glTexCoord2f(u, v);
        addVertexScaled(x0, y0);
    }

    public void draw()
    {
        GL11.glEnd();
    }
}
