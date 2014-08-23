package com.micdoodle8.ld30base;

import com.micdoodle8.ld30.Game;
import org.lwjgl.opengl.GL11;

import java.util.List;


public class EntityPlayer extends EntityWithLife
{
    private Texture[] textures;
    private Texture standingTexture;
    private Texture jumpingTexture;
    private Direction facingDir = Direction.RIGHT;
    private int texture = -1;

	public EntityPlayer(World world, Vector2d position)
	{
		super(world);
		this.size = new Vector2d(0.9, 1.8);
        this.position = position;
        textures = new Texture[] { Texture.getTexture("robot0.png"), Texture.getTexture("robot1.png"), Texture.getTexture("robot2.png"), Texture.getTexture("robot3.png") };
        this.standingTexture = Texture.getTexture("robot4.png");
        this.jumpingTexture = Texture.getTexture("robot5.png");
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

        if (texture == -1)
        {
            this.standingTexture.bind();
        }
        else if (texture == -2)
        {
            this.jumpingTexture.bind();
        }
        else
        {
            this.textures[this.texture].bind();
        }

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

        if (this.onGround)
        {
            if (Game.getInstance().keyButtonSpace.isKeyDown())
            {
                this.motion.y = 5;
            }
        }
        else
        {
            this.motion.y -= 9.81 * deltaTime;
        }

        boolean motionHandled = false;

        if (Game.getInstance().keyButtonRight.isKeyPressed())
        {
            if (this.facingDir != Direction.RIGHT)
            {
                this.facingDir = Direction.RIGHT;
            }

            this.motion.x = 2.5;
            motionHandled = true;
        }

        if (Game.getInstance().keyButtonLeft.isKeyPressed())
        {
            if (this.facingDir != Direction.LEFT)
            {
                this.facingDir = Direction.LEFT;
            }

            this.motion.x = -2.5;
            motionHandled = true;
        }

        if (!motionHandled)
        {
            this.motion.x *= (0.9);
        }

        List<BoundingBox> boundsAround = this.world.getBoundsWithin(this.getBounds().copy().add(motion.copy().multiply(deltaTime)));

        double oldMotionY = this.motion.y;
        for (BoundingBox box : boundsAround)
        {
            double mY = box.calcYOffset(this.getBounds(), this.motion.y);
            double mX = box.calcXOffset(this.getBounds(), this.motion.x);
            this.motion.y = mY;
            this.motion.x = mX;
        }

        if (Math.abs(this.motion.y) < Math.abs(oldMotionY))
        {
            this.onGround = true;
        }
        else
        {
            this.onGround = false;
        }

        if (!this.onGround && !this.lastOnGround)
        {
            this.texture = -2;
        }
        else
        {
            int count = (int)Math.floor(this.timeAlive * 7.5F) % this.textures.length;
            if ((((count == 1 || count == 3) && this.texture != count) || this.texture == -1) && Math.abs(this.motion.x) < 0.5)
            {
                this.texture = -1;
            }
            else
            {
                this.texture = count;
            }
        }

        this.lastOnGround = this.onGround;
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
