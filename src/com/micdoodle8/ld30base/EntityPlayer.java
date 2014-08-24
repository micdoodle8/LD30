package com.micdoodle8.ld30base;

import com.micdoodle8.ld30.ButtonEffect;
import com.micdoodle8.ld30.Game;
import com.micdoodle8.ld30.Light;
import com.micdoodle8.ld30.TeleportConnection;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;


public class EntityPlayer extends EntityWithLife
{
    private Texture[][] textures;
    private Texture[] standingTexture;
    private Texture[] jumpingTexture;
    private Direction facingDir = Direction.RIGHT;
    private int texture = -1;
    private int playerType = 1;
    private Vector2i buttonVec = null;
    private Light lightSource;
    private double soundEffectWalkCooldown = 0.0F;
    private double zapSoundCooldown;

	public EntityPlayer(int playerType, World world, Vector2d position)
	{
		super(world);
        this.playerType = playerType;
		this.size = new Vector2d(0.4, 1.8);
        this.position = position;
        this.lightSource = new Light(this.position.copy().add(new Vector2d(0, this.size.y)), 0.2F, new Vector3f(1, playerType == 1 ? 1 : 0.0F, 0.0F));
        world.lightList.add(this.lightSource);
        textures = new Texture[][] {
                {
                        Texture.getTexture("walk0.png"),
                        Texture.getTexture("walk1.png"),
                        Texture.getTexture("walk2.png"),
                        Texture.getTexture("walk3.png"),
                        Texture.getTexture("walk4.png"),
                        Texture.getTexture("walk5.png")
                },
//                {
//                        Texture.getTexture("robot0_yel.png"),
//                        Texture.getTexture("robot1_yel.png"),
//                        Texture.getTexture("robot2_yel.png"),
//                        Texture.getTexture("robot3_yel.png")
//                },
//                {
//                        Texture.getTexture("robot0_ora.png"),
//                        Texture.getTexture("robot1_ora.png"),
//                        Texture.getTexture("robot2_ora.png"),
//                        Texture.getTexture("robot3_ora.png")
//                }
        };
        this.standingTexture = new Texture[] { Texture.getTexture("robot4.png"), Texture.getTexture("robot4_yel.png"), Texture.getTexture("robot4_ora.png"), Texture.getTexture("outline_yel.png"), Texture.getTexture("outline_ora.png") };
        this.jumpingTexture = new Texture[] { Texture.getTexture("robot5.png"), Texture.getTexture("robot5_yel.png"), Texture.getTexture("robot5_ora.png") };
	}

    public void zap()
    {
        if (zapSoundCooldown <= 0)
        {
            zapSoundCooldown = 1.010F;
            Game.getInstance().soundEffectZap.playAsSoundEffect(1.0F, 0.7F, false, this.position.floatX(), this.position.floatY(), 0);
        }
        this.scheduleRemoval = true;
        if (Game.getInstance().startNextWorld(this.world.levelIndex))
        {
            Game.getInstance().gameWorld.update(0);
        }
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

        for (int i = 0; i < (this.isActivePlayer() ? 1 : 2); i++)
        {
            if (this.isActivePlayer())
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
                    this.textures[i][this.texture].bind();
                }
            }
            else
            {
                if (i == 1)
                {
                    this.standingTexture[0].bind();
                }
                else
                {
                    this.standingTexture[this.playerType + 2].bind();
                }
            }

            Game.getInstance().tessellator.start(GL11.GL_QUADS);
            int minX = facingDir == Direction.RIGHT ? 1 : 0;
            int maxX = facingDir == Direction.RIGHT ? 0 : 1;
            Tile.AIR_TILE.colorDynamic(this.position.copy().add(new Vector2d(0, this.size.y / 2)), new Vector2d(0, 0), i == 1 ? new Vector3f(0.6F, 0.6F, 0.6F) : new Vector3f(0.08F, 0.08F, 0.08F), 1.0F);

            if (!this.isActivePlayer())
            {
                switch (this.playerType)
                {
                    case 1:
                        GL11.glColor4f(1, 1, 0, 0.2F);
                        break;
                    case 2:
                        GL11.glColor4f(1, 0.0F, 0, 0.2F);
                        break;
                }
            }

            if (this.boundingBox != null)
            {
                Game.getInstance().tessellator.addVertexScaled(this.boundingBox.minVec.floatX() - 0.5F, this.boundingBox.minVec.floatY() - 0.4F, maxX, 1);
                Game.getInstance().tessellator.addVertexScaled(this.boundingBox.maxVec.floatX() + 0.5F, this.boundingBox.minVec.floatY() - 0.4F, minX, 1);
                Game.getInstance().tessellator.addVertexScaled(this.boundingBox.maxVec.floatX() + 0.5F, this.boundingBox.maxVec.floatY() + 0.6F, minX, 0);
                Game.getInstance().tessellator.addVertexScaled(this.boundingBox.minVec.floatX() - 0.5F, this.boundingBox.maxVec.floatY() + 0.6F, maxX, 0);
                Game.getInstance().tessellator.draw();
            }
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

    public boolean isActivePlayer()
    {
        return this == Game.getInstance().players[Game.getInstance().activePlayer];
    }

	@Override
	public void update(float deltaTime) 
	{
		super.update(deltaTime);

        if (this.zapSoundCooldown > 0)
        {
            this.zapSoundCooldown -= deltaTime;
        }

        if (this.onGround && this.isActivePlayer())
        {
            boolean jump = false;

            if (Game.getInstance().transitionState == 1)
            {
                jump = Game.getInstance().keyButtonS.isKeyDown() || Game.getInstance().keyButtonDown.isKeyDown();
            }
            else
            {
                jump = Game.getInstance().keyButtonW.isKeyDown() || Game.getInstance().keyButtonUp.isKeyDown();
            }

            if (jump)
            {
                this.motion.y = 5;
            }
        }

        this.motion.y -= 9.81 * deltaTime;

        boolean motionHandled = false;

        if (this.isActivePlayer() && Game.getInstance().transitionProgress == -1)
        {
            if (Game.getInstance().keyButtonRight.isKeyPressed() || Game.getInstance().keyButtonD.isKeyPressed())
            {
                this.facingDir = Game.getInstance().transitionState == 0 ? Direction.RIGHT : Direction.LEFT;
                this.motion.x = -2.5 * (Game.getInstance().transitionState * 2 - 1);
                motionHandled = true;
            }

            if (Game.getInstance().keyButtonLeft.isKeyPressed() || Game.getInstance().keyButtonA.isKeyPressed())
            {
                this.facingDir = Game.getInstance().transitionState == 0 ? Direction.LEFT : Direction.RIGHT;
                this.motion.x = 2.5 * (Game.getInstance().transitionState * 2 - 1);
                motionHandled = true;
            }
        }

        this.soundEffectWalkCooldown -= deltaTime;

        if (!motionHandled)
        {
            this.motion.x *= (0.9);
        }
        else
        {
            if (this.soundEffectWalkCooldown <= 0 && this.onGround && this.lastOnGround)
            {
                Game.getInstance().soundEffectWalk.playAsSoundEffect(4.0F, 0.3F, false, this.position.floatX(), this.position.floatY(), 0.0F);
                soundEffectWalkCooldown = 2370 / 4000.0F;
            }
        }

        if (this.isActivePlayer())
        {
            boolean b;

            if (Game.getInstance().transitionState == 0)
            {
                b = Game.getInstance().keyButtonS.isKeyDown() || Game.getInstance().keyButtonDown.isKeyDown();
            }
            else
            {
                b = Game.getInstance().keyButtonW.isKeyDown() || Game.getInstance().keyButtonUp.isKeyDown();
            }

            BoundingBox bounds = this.getBounds().copy();
            bounds = new BoundingBox(bounds.minVec.x, bounds.minVec.y - 0.05, bounds.maxVec.x, bounds.maxVec.y + 0.05);

            for (TeleportConnection connections : this.world.teleportConnectionList)
            {
                if (connections.playerType == this.playerType)
                {
                    for (int i = 0; i < connections.connections.size(); i++)
                    {
                        TeleportConnection.DirectionalPoint connection0 = connections.connections.get(i);

                        if (connection0.direction == Direction.DOWN || b)
                        {
                            if (bounds.intersects(new BoundingBox(connection0.point.toDoubleVec().sub(new Vector2d(0, 0.0)), connection0.point.toDoubleVec().add(new Vector2d(1, 1)))))
                            {
                                TeleportConnection.DirectionalPoint nextConnection;
                                if (i == connections.connections.size() - 1)
                                {
                                    nextConnection = connections.connections.get(0);
                                }
                                else
                                {
                                    nextConnection = connections.connections.get(i + 1);
                                }

                                switch (nextConnection.direction)
                                {
                                    case DOWN:
                                        this.position.x = nextConnection.point.x + 0.5;
                                        this.position.y = nextConnection.point.y - 2;
                                        break;
                                    case UP:
                                        this.position.x = nextConnection.point.x + 0.5;
                                        this.position.y = nextConnection.point.y + 1.1;
                                        break;
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
            double mY = box.calcYOffset(this.getBounds(), this.motion.y * deltaTime) / deltaTime;
            double mX = box.calcXOffset(this.getBounds(), this.motion.x * deltaTime) / deltaTime;
            this.motion.y = mY;
            this.motion.x = mX;
        }

//        boundsAround = this.world.getBoundsWithin(this.getBounds().copy());

        for (BoundingBox box : boundsAround)
        {
            Vector2i vec = box.minVec.toIntVec();

            if (world.getTile(vec.x, vec.y, 1) == Tile.NULL_TILE20)
            {
                this.zap();
            }
        }

        this.lightSource.position = this.position.copy().add(new Vector2d(0, this.size.y));

        this.onGround = Math.abs(oldMotionY - motion.y) > 0.0001D;

        if (!this.onGround && !this.lastOnGround)
        {
            this.texture = -2;
        }
        else
        {
            int count = (int)Math.floor(this.position.x * 3.5F) % this.textures[0].length;
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
