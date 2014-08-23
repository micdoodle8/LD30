package com.micdoodle8.ld30base;

import com.micdoodle8.ld30.Game;
import org.lwjgl.opengl.GL11;


public class EntityPlayer extends EntityWithLife
{
	public EntityPlayer(World world, Vector2d position)
	{
		super(world);
		this.size = new Vector2d(10, 10);
        this.position = position;
	}

	@Override
	public void initEntity() 
	{
	}

	@Override
	public void onDeath()
	{
		super.onDeath();
		if (Game.getInstance().currentScreen != null)
		{
			Game.getInstance().currentScreen.init();
		}
	}

	@Override
	public void draw() 
	{
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glTranslatef(this.position.floatX(), this.position.floatY(), 0.0F);

        Game.getInstance().tessellator.start(GL11.GL_QUADS);
        Game.getInstance().tessellator.addVertexScaled(this.boundingBox.minVec.floatX(), this.boundingBox.minVec.floatY(), 0, 1);
        Game.getInstance().tessellator.addVertexScaled(this.boundingBox.maxVec.floatX(), this.boundingBox.minVec.floatY(), 1, 1);
        Game.getInstance().tessellator.addVertexScaled(this.boundingBox.maxVec.floatX(), this.boundingBox.maxVec.floatY(), 1, 0);
        Game.getInstance().tessellator.addVertexScaled(this.boundingBox.minVec.floatX(), this.boundingBox.maxVec.floatY(), 0, 0);
        Game.getInstance().tessellator.draw();

        GL11.glPopMatrix();
    }

	@Override
	public void update(float deltaTime) 
	{
		super.update(deltaTime);
        this.motion.x = 2.5;
	}

	@Override
	public BoundingBox getBounds() 
	{
		double width = this.size.x;
		double height = this.size.y;
		return new BoundingBox(new Vector2d(this.position.x - width / 2, this.position.y), new Vector2d(this.position.x + width / 2, this.position.y + height));
	}

	@Override
	public float getMaxHealth() 
	{
		return 50.0F;
	}
}
