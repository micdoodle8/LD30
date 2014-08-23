package com.micdoodle8.ld30base;

import com.micdoodle8.ld30.Game;
import com.micdoodle8.ld30.LevelData;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class World 
{
	public List<Entity> entityList = new ArrayList<Entity>();
	private Tile[][][] tileMap;
	public Set<BoundingBox> tileBounds = new HashSet<BoundingBox>();
	public Vector2i worldSize;
//	public Vector2d screenTranslate = new Vector2d(0, 0);
	public float worldScale = 1;
	public Vector2d worldTranslate = new Vector2d(0, 0);
	
	public World(LevelData levelData)
	{

//		for (int x = 0; x < this.worldSize.x; x++)
//		{
//			for (int y = 0; y < this.worldSize.y; y++)
//			{
//				for (int layer = 0; layer < 2; layer++)
//				{
//					tileMap[x][y][layer] = Tile.AIR_TILE;
//				}
//			}
//		}

        for (int i = 0; i < levelData.charArray.length; i++)
        {
            for (int j = 0; j < levelData.charArray[i].length; j++)
            {
                if (this.worldSize == null)
                {
                    this.worldSize = new Vector2i(levelData.charArray[i].length, levelData.charArray.length / levelData.charArray[i][j].length);
                    tileMap = new Tile[worldSize.x][worldSize.y][levelData.charArray[i][j].length];
                }

                for (int layer = 0; layer < levelData.charArray[i][j].length; layer++)
                {
                    this.setTile(j, i, layer, Tile.charTileMap.get(levelData.charArray[i][j][layer]));
                }
            }
        }

	}
	
	public void addEntityToWorld(Entity entity)
	{
		entity.initEntity();
		this.entityList.add(entity);
	}
	
	public void setTile(int x, int y, Tile tile)
	{
		this.setTile(x, y, 0, tile);
	}
	
	public void setTile(int x, int y, int layer, Tile tile)
	{
		if (x >= 0 && x < worldSize.x && y >= 0 && y < worldSize.y && tile != null)
		{
			Tile tileBefore = tileMap[x][y][layer];
			
			if (layer == 1 && tileBefore != tile)
			{
				if (tileBefore != Tile.AIR_TILE && tileBefore != null)
				{
					tileBounds.remove(tileBefore.getBounds(x, y));
				}
				
				if (tile != Tile.AIR_TILE)
				{
//					if (x > 1 && x < worldSize.x - 1 && y > 0 && y < worldSize.y - 1)
					{
//						if (tileMap[x - 1][y][layer] == Tile.AIR_TILE ||
//								tileMap[x + 1][y][layer] == Tile.AIR_TILE ||
//								tileMap[x][y - 1][layer] == Tile.AIR_TILE ||
//								tileMap[x][y + 1][layer] == Tile.AIR_TILE)
						{
							tileBounds.add(tile.getBounds(x, y));
						}
					}
				}
			}
			
			tileMap[x][y][layer] = tile;
		}
	}
	
	public Tile getTile(int x, int y)
	{
		return this.getTile(x, y, 0);
	}
	
	public Tile getTile(int x, int y, int layer)
	{
		if (x >= 0 && x < worldSize.x && y >= 0 && y < worldSize.y)
		{
			return tileMap[x][y][layer];
		}
		
		return null;
	}
	
	public Vector2d screenCoordsToWorld(int screenX, int screenY)
	{
		Vector2d start = new Vector2d(screenX, screenY);
		
//		start.add(screenTranslate.copy().multiply(-1));
//		start.add(new Vector2d(-Game.getInstance().windowSize.x / 2, -Game.getInstance().windowSize.y / 2));
//
		start.x /= Game.getInstance().scale.floatX();
		start.y /= Game.getInstance().scale.floatY();

		start.x /= worldScale;
		start.y /= worldScale;

		start.x += worldTranslate.floatX();
		start.y += worldTranslate.floatY();
//        start.x /= 2.0;
//        start.y /= 2.0;
		
//		start.y += 2;
		return start;
	}
	
	public void render()
	{
		GL11.glPushMatrix();

//		GL11.glTranslatef(screenTranslate.floatX(), screenTranslate.floatY(), 0);
//		GL11.glTranslatef(Game.getInstance().windowSize.x / 2, Game.getInstance().windowSize.y / 2, 0);
		GL11.glScalef(worldScale, worldScale, worldScale);
        GL11.glScalef(Game.getInstance().scale.floatX(), Game.getInstance().scale.floatY(), 1.0F);
		GL11.glTranslatef(-worldTranslate.floatX(), -worldTranslate.floatY(), 0);

//		GL11.glTranslatef(0, -2, 0);
		
//		Game.getInstance().mouseCoordsWorld = screenCoordsToWorld(Mouse.getX(), Mouse.getY());
		
		Vector2d screenMin = screenCoordsToWorld(-20, -20);
		Vector2d screenMax = screenCoordsToWorld(Game.getInstance().windowSize.x + 10, Game.getInstance().windowSize.y + 10);

		Game.getInstance().particleManager.drawParticles();

		GL11.glColor3f(1.0F, 1.0F, 1.0F);

        Vector2d playerVec = this.screenCoordsToWorld(Mouse.getX(), Mouse.getY());

		for (int x = 0; x < this.worldSize.x; x++)
		{
			for (int y = 0; y < this.worldSize.y; y++)
			{
				for (int layer = 0; layer < this.tileMap[x][y].length; layer++)
				{
					if (x > screenMin.x * 2 && x < screenMax.x * 2 && y > screenMin.y * 2 && y < screenMax.y * 2)
					{
						Tile tile = this.tileMap[x][y][layer];
						
						if (tile != Tile.AIR_TILE)
						{
							GL11.glPushMatrix();
                            GL11.glTranslatef(x, y, 0);
                            Vector2d vec = new Vector2d(x + 0.5, y + 0.5);
							tile.draw(vec, vec.copy().sub(playerVec));
							GL11.glPopMatrix();
						}
					}
				}
			}
		}

        if (Game.getInstance().keyButtonB.isKeyPressed()) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glLineWidth(3);
            for (BoundingBox box : this.tileBounds) {
                Game.getInstance().tessellator.start(GL11.GL_LINE_LOOP);
                Game.getInstance().tessellator.addVertexScaled(box.minVec.floatX(), box.minVec.floatY(), 0, 1);
                Game.getInstance().tessellator.addVertexScaled(box.maxVec.floatX(), box.minVec.floatY(), 1, 1);
                Game.getInstance().tessellator.addVertexScaled(box.maxVec.floatX(), box.maxVec.floatY(), 1, 0);
                Game.getInstance().tessellator.addVertexScaled(box.minVec.floatX(), box.maxVec.floatY(), 0, 0);
                Game.getInstance().tessellator.draw();
            }
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

		GL11.glColor3f(1, 1, 1);
		
		BoundingBox screenBox = new BoundingBox(screenMin, screenMax);
		
		for (Entity entity : entityList)
		{
//			if (entity.getBounds() == null || entity.getBounds().intersects(screenBox))
			{
				entity.draw();
			}
		}
		
		GL11.glPopMatrix();
	}
	
	public void update(int deltaTicks)
	{
		Vector2d screenMin = screenCoordsToWorld(-2, -2);
		Vector2d screenMax = screenCoordsToWorld(Game.getInstance().windowSize.x + 2, Game.getInstance().windowSize.y + 2);
		BoundingBox screenBox = new BoundingBox(screenMin, screenMax);
		
		for (Entity entity : new ArrayList<Entity>(entityList))
		{
			if (entity.scheduleRemoval)
			{
				entityList.remove(entity);
			}
			else// if (entity.updateOffscreen || entity.getBounds() == null || entity.getBounds().intersects(screenBox))
			{
				entity.updateInternal(deltaTicks / 1000.0F);
			}
		}
	}
	
	public List<BoundingBox> getBoundsWithin(BoundingBox containingBox)
	{
		List<BoundingBox> boxesWithin = new ArrayList<BoundingBox>();
		
		BoundingBox screenBounds = new BoundingBox(screenCoordsToWorld(-2, -2), screenCoordsToWorld(Game.getInstance().windowSize.x + 2, Game.getInstance().windowSize.y + 2));
		
		for (BoundingBox box : tileBounds)
		{
			if (box.intersects(screenBounds) && box.intersects(containingBox))
			{
				boxesWithin.add(box);
			}
		}
		
		return boxesWithin;
	}

	public List<Entity> getEntitiesWithin(BoundingBox containingBox, Class<? extends Entity> entityClassMatch, Entity... ignored)
	{
		List<Entity> boxesWithin = new ArrayList<Entity>();
		
		BoundingBox screenBounds = new BoundingBox(screenCoordsToWorld(-2, -2), screenCoordsToWorld(Game.getInstance().windowSize.x + 2, Game.getInstance().windowSize.y + 2));

		for (Entity entity : entityList)
		{
			if ((entityClassMatch == null || entityClassMatch.isAssignableFrom(entity.getClass())) && entity.getBounds() != null && entity.getBounds().intersects(screenBounds) && entity.getBounds().intersects(containingBox))
			{
				if (!Arrays.asList(ignored).contains(entity))
				{
					boxesWithin.add(entity);
				}
			}
		}
		
		return boxesWithin;
	}
	
	public List<Entity> getEntitiesWithin(BoundingBox containingBox, Entity... ignored)
	{
		return getEntitiesWithin(containingBox, null, ignored);
	}
	
	public RayCastResult castRay(Vector2d position, Vector2d direction, int maxChecks, Class<? extends Entity> entityClassMatch, Entity... ignoredEntities)
	{
		position = position.copy();
		direction = direction.copy();
		float distance = 0;
		
		while (distance < maxChecks)
		{
			position.x += direction.x * 0.2F;
			position.y += direction.y * 0.2F;
			
			List<Entity> entitiesHit = this.getEntitiesWithin(new BoundingBox(new Vector2d(position.x - 0.1, position.y - 0.1), new Vector2d(position.x + 0.1, position.y + 0.1)), entityClassMatch, ignoredEntities);

			if (!entitiesHit.isEmpty())
			{
                return new RayCastResult(distance, entitiesHit, Tile.AIR_TILE, position);
			}

			Vector2i blockVec = position.toIntVec();
			Tile tileAt = this.getTile(blockVec.x, blockVec.y);
			if (tileAt != Tile.AIR_TILE && tileAt != null)
			{				
				BoundingBox hitBounds = tileAt.getBounds(blockVec.x, blockVec.y);
				position.x = Math.min(Math.max(position.x, hitBounds.minVec.x), hitBounds.maxVec.x);
				position.y = Math.min(Math.max(position.y, hitBounds.minVec.y), hitBounds.maxVec.y);
				return new RayCastResult(distance, entitiesHit, tileAt, position);
			}
			
			distance += 0.2F;
		}
		
		return new RayCastResult(distance, null, Tile.AIR_TILE, null);
	}
	
	class RayCastResult
	{
		public RayCastResult(double distance, List<Entity> entitiesHit, Tile tileHit, Vector2d hitPosition)
		{
			this.distance = distance;
			this.entitiesHit = entitiesHit;
			this.tileHit = tileHit;
			this.hitPosition = hitPosition;
		}
		
		public double distance;
		public List<Entity> entitiesHit;
		public Tile tileHit;
		public Vector2d hitPosition;
		
		public boolean hitEntity()
		{
			return entitiesHit != null && !entitiesHit.isEmpty() && hitPosition != null;
		}
		
		public boolean hitTile()
		{
			return tileHit != null && tileHit != Tile.AIR_TILE && hitPosition != null;
		}
	}
}
