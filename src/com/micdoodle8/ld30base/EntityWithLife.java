package com.micdoodle8.ld30base;

public abstract class EntityWithLife extends Entity
{
	public float health = this.getMaxHealth();
	
	public EntityWithLife(World world)
	{
		super(world);
	}
	
	public void update(float deltaTime)
	{
	}
	
	public void damage(float amount)
	{
		this.health -= amount;
		
		if (health <= 0)
		{
			this.onDeath();
		}
	}
	
	public void onDeath()
	{
		this.scheduleRemoval = true;
	}

	public abstract float getMaxHealth();
}
