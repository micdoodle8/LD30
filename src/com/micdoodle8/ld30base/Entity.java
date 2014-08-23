package com.micdoodle8.ld30base;

public abstract class Entity
{
	private static int lastEntityID = 0;
	protected World world;
	protected int entityID;
	public Vector2d position = new Vector2d();
	public Vector2d motion = new Vector2d();
	public Vector2d size = new Vector2d();
	public boolean scheduleRemoval;
    public float timeAlive;
    public BoundingBox boundingBox;
    public boolean onGround;
    public boolean lastOnGround;
	
	public Entity(World world)
	{
		this.world = world;
		this.entityID = lastEntityID++;
	}
	
	public int getEntityID()
	{
		return entityID;
	}

    public final void updateInternal(float deltaTime)
    {
        timeAlive += deltaTime;
        this.update(deltaTime);
        this.position = this.position.add(this.motion.copy().multiply(deltaTime));
        this.boundingBox = this.getBounds();
    }
	
	public abstract void update(float deltaTime);
	
	public abstract void initEntity();
	
	public abstract void draw();
	
	public abstract BoundingBox getBounds();
}
