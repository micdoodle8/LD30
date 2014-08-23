package com.micdoodle8.ld30base;

import com.micdoodle8.ld30.Game;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.List;


public class EntityPlayer extends EntityWithLife
{
    private Texture[][] textures;
    private Texture[] standingTexture;
    private Texture[] jumpingTexture;
    private Direction facingDir = Direction.RIGHT;
    private int texture = -1;
    private int playerType = 1;

	public EntityPlayer(World world, Vector2d position)
	{
		super(world);
		this.size = new Vector2d(0.9, 1.8);
        this.position = position;
        textures = new Texture[][] {
                {
                        Texture.getTexture("robot0.png"),
                        Texture.getTexture("robot1.png"),
                        Texture.getTexture("robot2.png"),
                        Texture.getTexture("robot3.png")
                },
                {
                        Texture.getTexture("robot0_yel.png"),
                        Texture.getTexture("robot1_yel.png"),
                        Texture.getTexture("robot2_yel.png"),
                        Texture.getTexture("robot3_yel.png")
                },
                {
                        Texture.getTexture("robot0_ora.png"),
                        Texture.getTexture("robot1_ora.png"),
                        Texture.getTexture("robot2_ora.png"),
                        Texture.getTexture("robot3_ora.png")
                }
        };
        this.standingTexture = new Texture[] { Texture.getTexture("robot4.png"), Texture.getTexture("robot4_yel.png"), Texture.getTexture("robot4_ora.png") };
        this.jumpingTexture = new Texture[] { Texture.getTexture("robot5.png"), Texture.getTexture("robot5_yel.png"), Texture.getTexture("robot5_ora.png") };
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

        for (int i = 0; i < 2; i++)
        {
            if (texture == -1)
            {
                this.standingTexture[i == 0 ? 0 : playerType].bind();
            }
            else if (texture == -2)
            {
                this.jumpingTexture[i == 0 ? 0 : playerType].bind();
            }
            else
            {
                this.textures[i == 0 ? 0 : playerType][this.texture].bind();
            }

            Game.getInstance().tessellator.start(GL11.GL_QUADS);
            int minX = facingDir == Direction.RIGHT ? 1 : 0;
            int maxX = facingDir == Direction.RIGHT ? 0 : 1;
            float distance = (float)new Vector2d(this.position.x + 0.5, this.position.y + 0.5).sub(this.world.screenCoordsToWorld(Mouse.getX(), Mouse.getY())).getLength();
            float denom = distance + 0.1F;
            float col = (i + 1) / (denom * denom);
            col *= 1 / ((i == 0 ? 1 : 0.05F) / 10.0F);
            GL11.glColor3f(col, col, col);
            Game.getInstance().tessellator.addVertexScaled(this.boundingBox.minVec.floatX() - 0.2F, this.boundingBox.minVec.floatY() - 0.4F, maxX, 1);
            Game.getInstance().tessellator.addVertexScaled(this.boundingBox.maxVec.floatX() + 0.2F, this.boundingBox.minVec.floatY() - 0.4F, minX, 1);
            Game.getInstance().tessellator.addVertexScaled(this.boundingBox.maxVec.floatX() + 0.2F, this.boundingBox.maxVec.floatY() + 0.6F, minX, 0);
            Game.getInstance().tessellator.addVertexScaled(this.boundingBox.minVec.floatX() - 0.2F, this.boundingBox.maxVec.floatY() + 0.6F, maxX, 0);
            Game.getInstance().tessellator.draw();
        }

        if (Game.getInstance().keyButtonB.isKeyPressed())
        {
            List<BoundingBox> boundsAround = this.world.getBoundsWithin(this.getBounds().copy().expand(0.1));
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

        this.motion.y -= 9.81 * deltaTime;

        boolean motionHandled = false;

        if (Game.getInstance().keyButtonRight.isKeyPressed() || Game.getInstance().keyButtonD.isKeyPressed())
        {
            if (this.facingDir != Direction.RIGHT)
            {
                this.facingDir = Direction.RIGHT;
            }

            this.motion.x = 2.5;
            motionHandled = true;
        }

        if (Game.getInstance().keyButtonLeft.isKeyPressed() || Game.getInstance().keyButtonA.isKeyPressed())
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

        if (Game.getInstance().keyButtonS.isKeyDown() || Game.getInstance().keyButtonDown.isKeyDown())
        {
            List<BoundingBox> boundsAround = this.world.getBoundsWithin(this.getBounds().copy().add(new Vector2d(0, -0.01)));

            for (BoundingBox box : boundsAround)
            {
                Tile tile = world.getTile(box.minVec.toIntVec().x, box.minVec.toIntVec().y, 2);
                if (tile == Tile.NULL_TILE13 || tile == Tile.NULL_TILE15)
                {
                    for (int i = 0; i < world.worldSize.x; i++)
                    {
                        for (int j = 0; j < world.worldSize.y; j++)
                        {
                            Tile tile0 = world.getTile(i, j, 2);

                            if ((tile == tile0 || (tile == Tile.NULL_TILE13 && tile0 == Tile.NULL_TILE14) || (tile == Tile.NULL_TILE15 && tile0 == Tile.NULL_TILE16)) && i != box.minVec.toIntVec().x && j != box.minVec.toIntVec().y)
                            {
                                if (tile == tile0)
                                {
                                    this.position.x = i + 0.5;
                                    this.position.y = j + 1.1;
                                }
                                else
                                {
                                    this.position.x = i + 0.5;
                                    this.position.y = j - 2;
                                }
                            }
                        }
                    }
                }
            }
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

        this.onGround = Math.abs(oldMotionY - motion.y) > 0.0001D;

        if (!this.onGround && !this.lastOnGround)
        {
            this.texture = -2;
        }
        else
        {
            int count = (int)Math.floor(this.position.x * 3.5F) % this.textures.length;
            if (Math.abs(this.motion.x) < 0.5)
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
