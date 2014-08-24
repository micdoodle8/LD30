package com.micdoodle8.ld30;

import com.micdoodle8.ld30base.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class EntityLaser extends Entity
{
    private static final Texture texture = Texture.getTexture("laser.png");
    public float shootCooldown;
    public float cooldownProcess;
    public float firingCount;
    public static final float FIRING_TIME = 0.1F;
    public Direction facingDir = Direction.LEFT;
    public Vector2d target;
    public boolean isActive = true;

    public EntityLaser(World world, float shootCooldown)
    {
        super(world);
        this.shootCooldown = shootCooldown;
        this.size = new Vector2d(1, 1);
    }

    public void toggleActive()
    {
        this.isActive = !this.isActive;
        if (this.isActive)
        {
            this.calculateTarget();
        }
    }


    @Override
    public void update(float deltaTime)
    {
        if (this.cooldownProcess > 0)
        {
            this.cooldownProcess -= deltaTime;
        }

        if (this.cooldownProcess <= 0)
        {
            this.cooldownProcess = shootCooldown;
            this.shoot();
        }

        if (this.firingCount > 0)
        {
            this.firingCount -= deltaTime;
        }

        if (!this.isActive)
        {
            this.firingCount = 0;
        }

        if (this.firingCount > 0)
        {
            Vector2d delta = this.target.copy().sub(this.position.copy().add(new Vector2d(0.5, 0.5)));
            float length = (float)delta.getLength();

            for (float mul = 0.5F; mul < length; mul += 0.1F)
            {
                Vector2d toCheck = this.position.copy().add(new Vector2d(0.5, 0.5)).add(delta.copy().multiply(mul / length));
                if (Game.getInstance().players[0].getBounds().intersects(toCheck))
                {
                    Game.getInstance().players[0].zap();
                    break;
                }
                if (Game.getInstance().players[1].getBounds().intersects(toCheck))
                {
                    Game.getInstance().players[1].zap();
                    break;
                }
            }
        }
    }

    private void shoot()
    {
        this.firingCount = FIRING_TIME;
    }

    @Override
    public void initEntity()
    {

    }

    @Override
    public void draw()
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor3f(1, 0, 0);
        if (this.target != null && this.firingCount > 0)
        {
            Game.getInstance().tessellator.start(GL11.GL_LINES);
            Vector2d vec = this.position.copy().add(this.size.copy().multiply(0.5));
            Game.getInstance().tessellator.addVertexScaled(vec.floatX(), vec.floatY());
            Game.getInstance().tessellator.addVertexScaled(target.floatX(), target.floatY());
            Game.getInstance().tessellator.draw();
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        int minX = facingDir == Direction.LEFT ? 1 : 0;
        int maxX = facingDir == Direction.LEFT ? 0 : 1;
        int minY = facingDir == Direction.UP ? 1 : 0;
        int maxY = facingDir == Direction.UP ? 0 : 1;
        texture.bind();

        if (this.boundingBox != null)
        {
            GL11.glPushMatrix();
            if (this.facingDir.getOffset().y != 0)
            {
                GL11.glTranslatef(position.floatX() + 0.5F, position.floatY() + 0.5F, 0.0F);
                GL11.glRotatef(this.facingDir == Direction.UP ? 90 : -90, 0, 0, 1);
                GL11.glTranslatef(-position.floatX() - 0.5F, -position.floatY() - 0.5F, 0.0F);
            }
            Game.getInstance().tessellator.start(GL11.GL_QUADS);
            Tile.AIR_TILE.colorDynamic(this.position.copy().add(new Vector2d(this.size.x / 2, this.size.y / 2)), new Vector2d(0.5, 0.5), new Vector3f(0.1F, 0.1F, 0.1F), 1.0F);
            Game.getInstance().tessellator.addVertexScaled(this.boundingBox.minVec.floatX(), this.boundingBox.minVec.floatY(), maxX, maxY);
            Tile.AIR_TILE.colorDynamic(this.position.copy().add(new Vector2d(this.size.x / 2, this.size.y / 2)), new Vector2d(-0.5, 0.5), new Vector3f(0.1F, 0.1F, 0.1F), 1.0F);
            Game.getInstance().tessellator.addVertexScaled(this.boundingBox.maxVec.floatX(), this.boundingBox.minVec.floatY(), minX, maxY);
            Tile.AIR_TILE.colorDynamic(this.position.copy().add(new Vector2d(this.size.x / 2, this.size.y / 2)), new Vector2d(-0.5, -0.5), new Vector3f(0.1F, 0.1F, 0.1F), 1.0F);
            Game.getInstance().tessellator.addVertexScaled(this.boundingBox.maxVec.floatX(), this.boundingBox.maxVec.floatY(), minX, minY);
            Tile.AIR_TILE.colorDynamic(this.position.copy().add(new Vector2d(this.size.x / 2, this.size.y / 2)), new Vector2d(0.5, 0.5), new Vector3f(0.1F, 0.1F, 0.1F), 1.0F);
            Game.getInstance().tessellator.addVertexScaled(this.boundingBox.minVec.floatX(), this.boundingBox.maxVec.floatY(), maxX, minY);
            Game.getInstance().tessellator.draw();
            GL11.glPopMatrix();
        }
    }

    @Override
    public BoundingBox getBounds()
    {
        return new BoundingBox(this.position.copy(), this.position.copy().add(new Vector2d(1.0, 1.0)));
    }

    public void calculateTarget()
    {
        Vector2d base = this.position.copy().add(new Vector2d(0.5, 0.5));
        while (world.getTile(base.toIntVec().x, base.toIntVec().y) != null && world.getTile(base.toIntVec().x, base.toIntVec().y) == Tile.AIR_TILE)
        {
            base.add(this.facingDir.getOffset().toDoubleVec());
        }
        this.target = base.sub(this.facingDir.getOffset().toDoubleVec().multiply(0.5));
    }
}
