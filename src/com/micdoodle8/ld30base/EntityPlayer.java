package com.micdoodle8.ld30base;

import com.micdoodle8.ld30.Game;
import org.lwjgl.opengl.GL11;

import java.util.List;


public class EntityPlayer extends EntityWithLife
{
    private Texture[] textures;
    private Direction facingDir = Direction.RIGHT;

	public EntityPlayer(World world, Vector2d position)
	{
		super(world);
		this.size = new Vector2d(0.9, 1.8);
        this.position = position;
        textures = new Texture[] { Texture.getTexture("robot0.png"), Texture.getTexture("robot1.png"), Texture.getTexture("robot2.png"), Texture.getTexture("robot3.png") };
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
//        GL11.glEnable(GL11.GL_TEXTURE_2D);
//        GL11.glTranslatef(this.position.floatX(), this.position.floatY(), 0.0F);

        this.textures[(int)Math.floor(this.timeAlive * 5.0F) % this.textures.length].bind();

        Game.getInstance().tessellator.start(GL11.GL_QUADS);
        int minX = facingDir == Direction.RIGHT ? 1 : 0;
        int maxX = facingDir == Direction.RIGHT ? 0 : 1;
        Game.getInstance().tessellator.addVertexScaled(this.boundingBox.minVec.floatX() - 0.2F, this.boundingBox.minVec.floatY() - 0.4F, maxX, 1);
        Game.getInstance().tessellator.addVertexScaled(this.boundingBox.maxVec.floatX() + 0.2F, this.boundingBox.minVec.floatY() - 0.4F, minX, 1);
        Game.getInstance().tessellator.addVertexScaled(this.boundingBox.maxVec.floatX() + 0.2F, this.boundingBox.maxVec.floatY() + 0.6F, minX, 0);
        Game.getInstance().tessellator.addVertexScaled(this.boundingBox.minVec.floatX() - 0.2F, this.boundingBox.maxVec.floatY() + 0.6F, maxX, 0);
        Game.getInstance().tessellator.draw();

        if (Game.getInstance().keyButtonB.isKeyPressed())
        {
            List<BoundingBox> boundsAround = this.world.getBoundsWithin(this.getBounds().copy().expand(0.1).add(motion.copy().multiply(1.0f)));
            GL11.glColor3f(1, 0, 0);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glLineWidth(3);
            for (BoundingBox box : boundsAround)
            {
                Game.getInstance().tessellator.start(GL11.GL_LINE_LOOP);
                Game.getInstance().tessellator.addVertexScaled(box.minVec.floatX(), box.minVec.floatY(), 0, 1);
                Game.getInstance().tessellator.addVertexScaled(box.maxVec.floatX(), box.minVec.floatY(), 1, 1);
                Game.getInstance().tessellator.addVertexScaled(box.maxVec.floatX(), box.maxVec.floatY(), 1, 0);
                Game.getInstance().tessellator.addVertexScaled(box.minVec.floatX(), box.maxVec.floatY(), 0, 0);
                Game.getInstance().tessellator.draw();
            }
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        GL11.glPopMatrix();
    }

	@Override
	public void update(float deltaTime) 
	{
		super.update(deltaTime);
        this.motion.x = this.facingDir == Direction.RIGHT ? 2.5 : -2.5;

        List<BoundingBox> boundsAround = this.world.getBoundsWithin(this.getBounds().copy().add(motion.copy().multiply(deltaTime)));

        double oldMotionY = this.motion.y;

        if (Game.getInstance().keyButtonRight.isKeyDown())
        {
            if (this.facingDir != Direction.RIGHT)
            {
                this.facingDir = Direction.RIGHT;
            }
        }

        if (Game.getInstance().keyButtonLeft.isKeyDown())
        {
            if (this.facingDir != Direction.LEFT)
            {
                this.facingDir = Direction.LEFT;
            }
        }

        for (BoundingBox box : boundsAround)
        {
            double newMotionX = box.calcYOffset(this.getBounds(), this.motion.y);
            double newMotionY = box.calcXOffset(this.getBounds(), this.motion.x);
            this.motion.y = newMotionX;
            this.motion.x = newMotionY;
        }
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
