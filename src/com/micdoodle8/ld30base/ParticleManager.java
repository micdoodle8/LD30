package com.micdoodle8.ld30base;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

public class ParticleManager 
{
	public List<Particle> particleList = new ArrayList<Particle>();
	
	public static class Particle
	{
		public Texture texture;
		public Vector2d position;
		public Vector2d scale;
		public Vector2d motion;
		public float rotation;
		public int lifespan;
		public int age;
		
		public Particle(String texture, Vector2d position, Vector2d scale, float rotation, int lifespan)
		{
			this.texture = Texture.getTexture(texture);
			this.position = position;
			this.scale = scale;
			this.rotation = rotation;
			this.lifespan = lifespan;
		}
		
		public void draw()
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(this.position.floatX(), this.position.floatY(), 0);
			GL11.glRotatef(this.rotation, 0, 0, 1);
			GL11.glScalef(this.scale.floatX(), this.scale.floatY(), 1.0F);
			this.texture.bind();
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(0.0F, 0.0F);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2f(1.0F, 0.0F);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2f(1.0F, 1.0F);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2f(0.0F, 1.0F);
			GL11.glEnd();
			GL11.glPopMatrix();
		}
		
		public void update(int deltaTicks)
		{
			this.age += deltaTicks;
			this.position.add(motion.copy().multiply(deltaTicks / 1000.0F));
		}
	}
	
	public void update(int deltaTicks)
	{
		for (Particle particle : new ArrayList<Particle>(particleList))
		{
			particle.update(deltaTicks);
			
			if (particle.age >= particle.lifespan)
			{
				particleList.remove(particle);
			}
		}
	}
	
	public void drawParticles()
	{
		for (Particle particle : particleList)
		{
			particle.draw();
		}
	}
	
	public void addParticle(Particle particle)
	{
		particleList.add(particle);
	}
}
