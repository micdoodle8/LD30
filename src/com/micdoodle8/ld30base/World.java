package com.micdoodle8.ld30base;

import com.micdoodle8.ld30.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import java.util.*;

public class World 
{
	public List<Entity> entityList = new ArrayList<Entity>();
	private Tile[][][] tileMap;
	public Set<BoundingBox> tileBounds = new HashSet<BoundingBox>();
	public Vector2i worldSize;
	public float worldScale = 1;
	public Vector2d worldTranslate = new Vector2d(0, 0);
    public List<TeleportConnection> teleportConnectionList = new ArrayList<TeleportConnection>();
    public List<ButtonEffect> buttonEffectList = new ArrayList<ButtonEffect>();
    public final int levelIndex;
    public List<Light> lightList = new ArrayList<Light>();
    public List<Light> dynamicLightList = new ArrayList<Light>();

	public World(int levelIndex, LevelData levelData)
	{
        this.levelIndex = levelIndex;
        this.worldScale = 40;

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
                    this.setTile(j, i, layer, investigateSolid(j, i, layer, levelData.charArray));
                }
            }
        }

        this.setupWorld();
	}

    private Tile investigateSolid(int x, int y, int layer, char[][][] data)
    {
        if (layer == 1 && getCharAt(x, y, layer, data) == 'S')
        {
            Character charLeft = getCharAt(x - 1, y, layer, data);
            Character charRight = getCharAt(x + 1, y, layer, data);
            Character charAbove = getCharAt(x, y + 1, layer, data);
            Character charBelow = getCharAt(x, y - 1, layer, data);
            Character charBelowRight = getCharAt(x + 1, y - 1, layer, data);
            Character charBelowLeft = getCharAt(x - 1, y - 1, layer, data);
            Character charAboveRight = getCharAt(x + 1, y + 1, layer, data);
            Character charAboveLeft = getCharAt(x - 1, y + 1, layer, data);
            if (charRight != null && charBelow != null && charRight == 'S' && charBelow == 'S' && (charBelowRight != null && charBelowRight != 'S'))
            {
                this.addLightWithChance(x + 1, y);
                return Tile.charTileMap.get('0');
            }
            else if (charLeft != null && charBelow != null && charLeft == 'S' && charBelow == 'S' && (charBelowLeft != null && charBelowLeft != 'S'))
            {
                this.addLightWithChance(x, y);
                return Tile.charTileMap.get('1');
            }
            else if (charLeft != null && charAbove != null && charLeft == 'S' && charAbove == 'S' && (charAboveLeft != null && charAboveLeft != 'S'))
            {
                this.addLightWithChance(x, y + 1);
                return Tile.charTileMap.get('2');
            }
            else if (charRight != null && charAbove != null && charRight == 'S' && charAbove == 'S' && (charAboveRight != null && charAboveRight != 'S'))
            {
                this.addLightWithChance(x + 1, y + 1);
                return Tile.charTileMap.get('3');
            }
            else if ((charLeft == null || charLeft == 'S') && (charRight == null || charRight == 'S') && (charBelow != null && charBelow != 'S'))
            {
                this.addLightWithChance(x + 0.5F, y);
                return Tile.charTileMap.get('V');
            }
            else if ((charLeft == null || charLeft == 'S') && (charRight == null || charRight == 'S') && (charAbove != null && charAbove != 'S'))
            {
                this.addLightWithChance(x + 0.5, y + 1);
                return Tile.charTileMap.get('^');
            }
            else if ((charAbove == null || charAbove == 'S') && (charBelow == null || charBelow == 'S') && (charLeft != null && charLeft != 'S'))
            {
                this.addLightWithChance(x, y + 0.5);
                return Tile.charTileMap.get('<');
            }
            else if ((charAbove == null || charAbove == 'S') && (charBelow == null || charBelow == 'S') && (charRight != null && charRight != 'S'))
            {
                this.addLightWithChance(x + 1, y + 0.5);
                return Tile.charTileMap.get('>');
            }
            else if (charLeft != null && charAbove != null && charLeft == 'S' && charAbove == 'S' && (charBelowRight != null && charBelowRight != 'S'))
            {
                this.addLightWithChance(x + 1, y);
                return Tile.charTileMap.get('4');
            }
            else if (charRight != null && charAbove != null && charRight == 'S' && charAbove == 'S' && (charBelowLeft != null && charBelowLeft != 'S'))
            {
                this.addLightWithChance(x, y);
                return Tile.charTileMap.get('5');
            }
            else if (charRight != null && charBelow != null && charRight == 'S' && charBelow == 'S' && (charAboveLeft != null && charAboveLeft != 'S'))
            {
                this.addLightWithChance(x, y + 1);
                return Tile.charTileMap.get('6');
            }
            else if (charLeft != null && charBelow != null && charLeft == 'S' && charBelow == 'S' && (charAboveRight != null && charAboveRight != 'S'))
            {
                this.addLightWithChance(x + 1, y + 1);
                return Tile.charTileMap.get('7');
            }
            else if ((charLeft == null || charLeft == 'S') && (charRight == null || charRight == 'S') && (charAbove == null || charAbove == 'S') && (charBelow == null || charBelow == 'S'))
            {
                return Tile.charTileMap.get('S');
            }
        }

        return Tile.charTileMap.get(data[y][x][layer]);
    }

    private void addLightWithChance(double x, double y)
    {
        if (Math.random() < 0.075)
        {
            for (Light li : lightList)
            {
                if (li.position.x == x && li.position.y == y)
                {
                    return;
                }
            }

            Light light = new Light(new Vector2d(x, y), 0.1F, new Vector3f(1, 0, 0.8F));
            this.dynamicLightList.add(light);
            this.lightList.add(light);
        }
    }

    private Character getCharAt(int x, int y, int layer, char[][][] data)
    {
        if (y < 0 || y >= data.length || x < 0 || x >= data[y].length || layer < 0 || layer >= data[y][x].length)
        {
            // Out of bounds
            return null;
        }

        return data[y][x][layer];
    }

    private void setupWorld()
    {
        switch (levelIndex)
        {
            case 0:
                teleportConnectionList.add(new TeleportConnection(2, new TeleportConnection.DirectionalPoint(new Vector2i(6, 0), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(18, 8), Direction.DOWN)));
                teleportConnectionList.add(new TeleportConnection(1, new TeleportConnection.DirectionalPoint(new Vector2i(28, 0), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(14, 9), Direction.UP)));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(18, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(6, 14), 1, Tile.AIR_TILE, new Light(new Vector2d(6, 14), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.CLOSE_BLOCK, new Object[] { new Vector2i(6, 14), 1, this.getTile(6, 14, 1) }));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(18, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(6, 13), 1, Tile.AIR_TILE, new Light(new Vector2d(6, 13), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.CLOSE_BLOCK, new Object[] { new Vector2i(6, 13), 1, this.getTile(6, 13, 1) }));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(20, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(14, 5), 1, Tile.AIR_TILE, new Light(new Vector2d(14, 5), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.CLOSE_BLOCK, new Object[] { new Vector2i(14, 5), 1, this.getTile(14, 5, 1) }));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(20, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(15, 5), 1, Tile.AIR_TILE, new Light(new Vector2d(15, 5), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.CLOSE_BLOCK, new Object[] { new Vector2i(15, 5), 1, this.getTile(15, 5, 1) }));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(20, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(14, 6), 1, Tile.AIR_TILE, new Light(new Vector2d(14, 6), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.CLOSE_BLOCK, new Object[] { new Vector2i(14, 6), 1, this.getTile(14, 6, 1) }));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(20, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(15, 6), 1, Tile.AIR_TILE, new Light(new Vector2d(15, 6), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.CLOSE_BLOCK, new Object[] { new Vector2i(15, 6), 1, this.getTile(15, 6, 1) }));
                buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(25, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(27, 0), 1, Tile.AIR_TILE, new Light(new Vector2d(27, 0), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] { }));

                Game.getInstance().players = new EntityPlayer[2];
                Game.getInstance().players[0] = new EntityPlayer(1, this, new Vector2d(6, 15));
                Game.getInstance().players[1] = new EntityPlayer(2, this, new Vector2d(2, 3));
                this.addEntityToWorld(Game.getInstance().players[0]);
                this.addEntityToWorld(Game.getInstance().players[1]);
                break;
            case 1:
                teleportConnectionList.add(new TeleportConnection(1, new TeleportConnection.DirectionalPoint(new Vector2i(21, 4), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(8, 17), Direction.DOWN), new TeleportConnection.DirectionalPoint(new Vector2i(7, 7), Direction.UP)));
                buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(8, 8), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(24, 13), 1, Tile.AIR_TILE, new Light(new Vector2d(24, 13), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.CLOSE_BLOCK, new Object[] { new Vector2i(24, 13), 1, this.getTile(24, 13, 1) }));
                buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(8, 8), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(24, 12), 1, Tile.AIR_TILE, new Light(new Vector2d(24, 12), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.CLOSE_BLOCK, new Object[] { new Vector2i(24, 12), 1, this.getTile(24, 12, 1) }));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(24, 5), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(29, 1), 1, Tile.AIR_TILE, new Light(new Vector2d(29, 1), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] { }));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(24, 5), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(29, 0), 1, Tile.AIR_TILE, new Light(new Vector2d(29, 0), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] { }));

                Game.getInstance().players = new EntityPlayer[2];
                Game.getInstance().players[0] = new EntityPlayer(1, this, new Vector2d(2, 14));
                Game.getInstance().players[1] = new EntityPlayer(2, this, new Vector2d(14, 15));
                this.addEntityToWorld(Game.getInstance().players[0]);
                this.addEntityToWorld(Game.getInstance().players[1]);
                break;
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

		start.x /= Game.getInstance().scale.floatX();
		start.y /= Game.getInstance().scale.floatY();

		start.x /= worldScale;
		start.y /= worldScale;

		start.x += worldTranslate.floatX();
		start.y += worldTranslate.floatY();

		return start;
	}

	public Vector2d worldCoordsToScreen(int worldX, int worldZ)
	{
		Vector2d start = new Vector2d(worldX, worldZ);

		start.x *= Game.getInstance().scale.floatX();
		start.y *= Game.getInstance().scale.floatY();

		start.x *= worldScale;
		start.y *= worldScale;

		start.x -= worldTranslate.floatX();
		start.y -= worldTranslate.floatY();

		return start;
	}
	
	public void render()
	{
		GL11.glPushMatrix();

//		GL11.glTranslatef(screenTranslate.floatX(), screenTranslate.floatY(), 0);
		GL11.glTranslatef(Game.getInstance().windowSize.x / 2, Game.getInstance().windowSize.y / 2, 0);
        float transitionProgress;

        if (Game.getInstance().transitionProgress >= 0)
        {
            transitionProgress = (Game.getInstance().transitionProgress / (float)Game.TOTAL_TRANSITION_TIME) + (Game.getInstance().transitionState);
        }
        else
        {
            transitionProgress = (Game.getInstance().transitionState);
        }

        GL11.glRotatef(transitionProgress * transitionProgress * 180.0F, 0, 0, 1);
        GL11.glScalef(transitionProgress == 0 ? 1 : (transitionProgress > 1 ? transitionProgress - 1 : transitionProgress), transitionProgress == 0 ? 1 : (transitionProgress > 1 ? transitionProgress - 1 : transitionProgress), 1);
		GL11.glTranslatef(-Game.getInstance().windowSize.x / 2, -Game.getInstance().windowSize.y / 2, 0);
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
							tile.draw(vec, layer);
							GL11.glPopMatrix();
						}
					}
				}
			}
		}

        if (Game.getInstance().keyButtonB.isKeyPressed()) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glLineWidth(3);
            GL11.glColor3f(1, 1, 1);
            for (BoundingBox box : this.tileBounds) {
                Game.getInstance().tessellator.start(GL11.GL_LINE_LOOP);
                Game.getInstance().tessellator.addVertexScaled(box.minVec.floatX(), box.minVec.floatY(), 0, 1);
                Game.getInstance().tessellator.addVertexScaled(box.maxVec.floatX(), box.minVec.floatY(), 1, 1);
                Game.getInstance().tessellator.addVertexScaled(box.maxVec.floatX(), box.maxVec.floatY(), 1, 0);
                Game.getInstance().tessellator.addVertexScaled(box.minVec.floatX(), box.maxVec.floatY(), 0, 0);
                Game.getInstance().tessellator.draw();
            }
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            BoundingBox box = this.tileBounds.iterator().next();
            float width = box.minVec.floatX() - box.maxVec.floatX();
            float height = box.minVec.floatY() - box.maxVec.floatY();
            for (int i = 0; i < this.worldSize.x; i++)
            {
                GL11.glPushMatrix();
                GL11.glScalef(0.05F, 0.05F, 0.05F);
                Game.getInstance().drawText(Game.getInstance().fontSourceSansProSize16, new Vector2i((int)Math.floor(i * 20 + 4), (int)Math.floor(height / 2)), String.valueOf(i), Color.white);
                GL11.glPopMatrix();
            }
            for (int i = 1; i < this.worldSize.y; i++)
            {
                GL11.glPushMatrix();
                GL11.glScalef(0.05F, 0.05F, 0.05F);
                Game.getInstance().drawText(Game.getInstance().fontSourceSansProSize16, new Vector2i((int)Math.floor(height / 2), (int)Math.floor(i * 20 + 4)), String.valueOf(i), Color.white);
                GL11.glPopMatrix();
            }
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

        int completionCount = 0;
        for (int i = 0; i < Game.getInstance().players.length; i++)
        {
            if (Game.getInstance().players[i].position.y < -2)
            {
                completionCount++;
            }
        }

        if (completionCount == Game.getInstance().players.length)
        {
            if (Game.getInstance().startNextWorld(this.levelIndex + 1))
            {
                for (Entity entity : new ArrayList<Entity>(entityList))
                {
                    entityList.remove(entity);
                    entity.world = null;
                }
                Game.getInstance().gameWorld.update(deltaTicks);
                return;
            }
        }

        for (int i = 0; i < this.buttonEffectList.size(); i++)
        {
            boolean pressed = false;
            ButtonEffect effect = this.buttonEffectList.get(i);

            for (int j = 0; j < Game.getInstance().players.length; j++)
            {
                if (j + 1 == effect.playerType)
                {
                    BoundingBox bounds = Game.getInstance().players[j].getBounds().copy();
                    bounds = new BoundingBox(bounds.minVec.x, bounds.minVec.y - 0.55, bounds.maxVec.x, bounds.maxVec.y + 0.55);
                    if (bounds.intersects(effect.directionalPoint.point.toDoubleVec().add(new Vector2d(0.5, -0.5))))
                    {
                        pressed |= true;
                    }
                    else
                    {
                        pressed |= false;
                    }
                }
            }

            if (pressed)
            {
                switch (effect.effectType)
                {
                    case OPEN_BLOCK:
                        Vector2i setPot = (Vector2i) effect.additionalData[0];
                        this.setTile(setPot.x, setPot.y, (Integer) effect.additionalData[1], (Tile) effect.additionalData[2]);
                        if (!effect.lastPressed)
                            this.lightList.add((Light) effect.additionalData[3]);
                        break;
                }
            }
            else
            {
                switch (effect.effectTypeOff)
                {
                    case CLOSE_BLOCK:
                        Vector2i setPot = (Vector2i) effect.additionalDataOff[0];
                        this.setTile(setPot.x, setPot.y, (Integer) effect.additionalDataOff[1], (Tile) effect.additionalDataOff[2]);
                        if (effect.lastPressed)
                            this.lightList.remove((Light) effect.additionalData[3]);
                        break;
                }
            }

            effect.lastPressed = pressed;
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
