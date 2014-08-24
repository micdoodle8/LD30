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
                    this.setTile(j, i, layer, investigateTile(j, i, layer, levelData.charArray));
                }
            }
        }

        this.setupWorld();
	}

    private Tile investigateTile(int x, int y, int layer, char[][][] data)
    {
        if (layer == 1)
        {
            if (getCharAt(x, y, layer, data) == 'S')
            {
                Character charLeft = getCharAt(x - 1, y, layer, data);
                Character charRight = getCharAt(x + 1, y, layer, data);
                Character charAbove = getCharAt(x, y + 1, layer, data);
                Character charBelow = getCharAt(x, y - 1, layer, data);
                Character charBelowRight = getCharAt(x + 1, y - 1, layer, data);
                Character charBelowLeft = getCharAt(x - 1, y - 1, layer, data);
                Character charAboveRight = getCharAt(x + 1, y + 1, layer, data);
                Character charAboveLeft = getCharAt(x - 1, y + 1, layer, data);

                if (charLeft == null) charLeft = 'S';
                if (charRight == null) charRight = 'S';
                if (charAbove == null) charAbove = 'S';
                if (charBelow == null) charBelow = 'S';
                if (charBelowRight == null) charBelowRight = 'S';
                if (charBelowLeft == null) charBelowLeft = 'S';
                if (charAboveRight == null) charAboveRight = 'S';
                if (charAboveLeft == null) charAboveLeft = 'S';

                if (charRight != 'S' && charLeft != 'S' && charAbove != 'S' && charBelow != 'S')
                {
                    this.addLightWithChance(x + 1, y);
                    return Tile.charTileMap.get('F');
                }
                else if (charRight == 'S' && charLeft != 'S' && charAbove != 'S' && charBelow != 'S')
                {
                    this.addLightWithChance(x + 1, y);
                    return Tile.charTileMap.get('G');
                }
                else if (charRight != 'S' && charLeft == 'S' && charAbove != 'S' && charBelow != 'S')
                {
                    this.addLightWithChance(x + 1, y);
                    return Tile.charTileMap.get('H');
                }
                else if (charRight != 'S' && charLeft != 'S' && charAbove == 'S' && charBelow != 'S')
                {
                    this.addLightWithChance(x + 1, y);
                    return Tile.charTileMap.get('J');
                }
                else if (charRight != 'S' && charLeft != 'S' && charAbove != 'S' && charBelow == 'S')
                {
                    this.addLightWithChance(x + 1, y);
                    return Tile.charTileMap.get('I');
                }
                else if (charRight == 'S' && charLeft == 'S' && charAbove != 'S' && charBelow != 'S')
                {
                    this.addLightWithChance(x + 1, y);
                    return Tile.charTileMap.get('K');
                }
                else if (charRight != 'S' && charLeft != 'S' && charAbove == 'S' && charBelow == 'S')
                {
                    this.addLightWithChance(x + 1, y);
                    return Tile.charTileMap.get('L');
                }
                else if (charRight == 'S' && charBelow == 'S' && (charBelowRight != 'S'))
                {
                    this.addLightWithChance(x + 1, y);
                    return Tile.charTileMap.get('0');
                }
                else if (charLeft == 'S' && charBelow == 'S' && (charBelowLeft != 'S'))
                {
                    this.addLightWithChance(x, y);
                    return Tile.charTileMap.get('1');
                }
                else if (charLeft == 'S' && charAbove == 'S' && (charAboveLeft != 'S'))
                {
                    this.addLightWithChance(x, y + 1);
                    return Tile.charTileMap.get('2');
                }
                else if (charRight == 'S' && charAbove == 'S' && (charAboveRight != 'S'))
                {
                    this.addLightWithChance(x + 1, y + 1);
                    return Tile.charTileMap.get('3');
                }
                else if ((charLeft == 'S') && (charRight == 'S') && (charBelow != 'S'))
                {
                    this.addLightWithChance(x + 0.5F, y);
                    return Tile.charTileMap.get('V');
                }
                else if ((charLeft == 'S') && (charRight == 'S') && (charAbove != 'S'))
                {
                    this.addLightWithChance(x + 0.5, y + 1);
                    return Tile.charTileMap.get('^');
                }
                else if ((charAbove == 'S') && (charBelow == null || charBelow == 'S') && (charLeft != 'S'))
                {
                    this.addLightWithChance(x, y + 0.5);
                    return Tile.charTileMap.get('<');
                }
                else if ((charAbove == 'S') && (charBelow == null || charBelow == 'S') && (charRight != 'S'))
                {
                    this.addLightWithChance(x + 1, y + 0.5);
                    return Tile.charTileMap.get('>');
                }
                else if (charLeft == 'S' && charAbove == 'S' && (charBelowRight != 'S'))
                {
                    this.addLightWithChance(x + 1, y);
                    return Tile.charTileMap.get('4');
                }
                else if (charRight == 'S' && charAbove == 'S' && (charBelowLeft != 'S'))
                {
                    this.addLightWithChance(x, y);
                    return Tile.charTileMap.get('5');
                }
                else if (charRight == 'S' && charBelow == 'S' && (charAboveLeft != 'S'))
                {
                    this.addLightWithChance(x, y + 1);
                    return Tile.charTileMap.get('6');
                }
                else if (charLeft == 'S' && charBelow == 'S' && (charAboveRight != 'S'))
                {
                    this.addLightWithChance(x + 1, y + 1);
                    return Tile.charTileMap.get('7');
                }
                else if ((charLeft == 'S') && (charRight == 'S') && (charAbove == 'S') && (charBelow == null || charBelow == 'S'))
                {
                    return Tile.charTileMap.get('S');
                }
            }
            else if (getCharAt(x, y, layer, data) == 'R')
            {
                Light light = new Light(new Vector2d(x + 0.5, y + 0.5), 0.5F, new Vector3f(0.1F, 0.1F, 0.8F), 0.9F);
                this.lightList.add(light);
                return Tile.charTileMap.get('R');
            }
        }

        return Tile.charTileMap.get(data[y][x][layer]);
    }

    private void addLightWithChance(double x, double y)
    {
        float distance = getClosestDynamicLight(new Vector2d(x, y));

        if (Math.random() < 0.5 * (distance / 8.0F))
        {
            for (Light li : lightList)
            {
                if (li.position.x == x && li.position.y == y)
                {
                    return;
                }
            }

            Light light = new Light(new Vector2d(x, y), 0.1F, Game.getInstance().transitionState == 0 ? new Vector3f(1, 0, 0.8F) : new Vector3f(0.0F, 0.8F, 1.0F), 0.5F);
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

    public float getClosestDynamicLight(Vector2d position)
    {
        float closest = Float.MAX_VALUE;

        for (Light light : this.dynamicLightList)
        {
            float distance = (float)light.position.copy().sub(position).getLengthUnSqrd();
            closest = Math.min(distance, closest);
        }

        return (float)Math.sqrt(closest);
    }

    private void setupWorld()
    {
        switch (levelIndex)
        {
            case 0:
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(18, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(6, 14), 1, Tile.AIR_TILE, new Light(new Vector2d(6, 14), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.CLOSE_BLOCK, new Object[] { new Vector2i(6, 14), 1, this.getTile(6, 14, 1) }));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(18, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(6, 13), 1, Tile.AIR_TILE, new Light(new Vector2d(6, 13), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.CLOSE_BLOCK, new Object[] { new Vector2i(6, 13), 1, this.getTile(6, 13, 1) }));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(20, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(14, 5), 1, Tile.AIR_TILE, new Light(new Vector2d(14, 5), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.CLOSE_BLOCK, new Object[] { new Vector2i(14, 5), 1, this.getTile(14, 5, 1) }));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(20, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(15, 5), 1, Tile.AIR_TILE, new Light(new Vector2d(15, 5), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.CLOSE_BLOCK, new Object[] { new Vector2i(15, 5), 1, this.getTile(15, 5, 1) }));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(20, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(14, 6), 1, Tile.AIR_TILE, new Light(new Vector2d(14, 6), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.CLOSE_BLOCK, new Object[] { new Vector2i(14, 6), 1, this.getTile(14, 6, 1) }));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(20, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(15, 6), 1, Tile.AIR_TILE, new Light(new Vector2d(15, 6), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.CLOSE_BLOCK, new Object[] { new Vector2i(15, 6), 1, this.getTile(15, 6, 1) }));
                buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(25, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(27, 0), 1, Tile.AIR_TILE, new Light(new Vector2d(27, 0), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] { }));

                Game.getInstance().players = new EntityPlayer[2];
                Game.getInstance().players[0] = new EntityPlayer(1, this, new Vector2d(6, 15));
                Game.getInstance().players[1] = new EntityPlayer(2, this, new Vector2d(2, 1));
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
            case 2:
                teleportConnectionList.add(new TeleportConnection(2, new TeleportConnection.DirectionalPoint(new Vector2i(30, 1), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(3, 0), Direction.UP)));
                teleportConnectionList.add(new TeleportConnection(1, new TeleportConnection.DirectionalPoint(new Vector2i(6, 8), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(6, 0), Direction.UP)));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(2, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(6, 1), 1, Tile.AIR_TILE, new Light(new Vector2d(6, 1), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.CLOSE_BLOCK, new Object[] { new Vector2i(6, 1), 1, this.getTile(6, 1, 1) }));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(2, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(29, 0), 1, Tile.AIR_TILE, new Light(new Vector2d(29, 0), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] { }));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(2, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(29, 1), 1, Tile.AIR_TILE, new Light(new Vector2d(29, 1), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] { }));

                Game.getInstance().players = new EntityPlayer[2];
                Game.getInstance().players[0] = new EntityPlayer(1, this, new Vector2d(4, 1));
                Game.getInstance().players[1] = new EntityPlayer(2, this, new Vector2d(1.6, 15));
                this.addEntityToWorld(Game.getInstance().players[0]);
                this.addEntityToWorld(Game.getInstance().players[1]);
                break;
            case 3:
                buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(7, 13), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(6, 13), 1, Tile.AIR_TILE, new Light(new Vector2d(6, 13), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] {  }));
                buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(7, 13), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(6, 14), 1, Tile.AIR_TILE, new Light(new Vector2d(6, 14), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] {  }));
                buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(7, 13), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(3, 0), 1, Tile.AIR_TILE, new Light(new Vector2d(3, 0), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] {  }));
                buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(7, 13), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(4, 0), 1, Tile.AIR_TILE, new Light(new Vector2d(4, 0), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] {  }));

                teleportConnectionList.add(new TeleportConnection(1, new TeleportConnection.DirectionalPoint(new Vector2i(29, 1), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(29, 17), Direction.DOWN)));

                EntityLaser laser = new EntityLaser(this, 0.15F);
                laser.facingDir = Direction.LEFT;
                laser.position = new Vector2d(27, 1);
                laser.calculateTarget();
                this.addEntityToWorld(laser);
                laser = new EntityLaser(this, 0.15F);
                laser.facingDir = Direction.LEFT;
                laser.position = new Vector2d(27, 6);
                laser.calculateTarget();
                this.addEntityToWorld(laser);

                Game.getInstance().players = new EntityPlayer[2];
                Game.getInstance().players[0] = new EntityPlayer(1, this, new Vector2d(2.2, 15));
                Game.getInstance().players[1] = new EntityPlayer(2, this, new Vector2d(1.5, 15));
                this.addEntityToWorld(Game.getInstance().players[0]);
                this.addEntityToWorld(Game.getInstance().players[1]);
                break;
            case 4:
                teleportConnectionList.add(new TeleportConnection(1, new TeleportConnection.DirectionalPoint(new Vector2i(5, 0), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(4, 2), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(3, 4), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(4, 6), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(3, 8), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(4, 10), Direction.UP)));
                teleportConnectionList.add(new TeleportConnection(1, new TeleportConnection.DirectionalPoint(new Vector2i(4, 14), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(6, 17), Direction.DOWN)));
                buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(1, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(1, 14), 1, Tile.AIR_TILE, new Light(new Vector2d(1, 14), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.CLOSE_BLOCK, new Object[] { new Vector2i(1, 14), 1, this.getTile(1, 14, 1) }));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(1, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(1, 0), 1, Tile.AIR_TILE, new Light(new Vector2d(1, 0), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] { }));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(1, 1), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(1, 1), 1, Tile.AIR_TILE, new Light(new Vector2d(1, 1), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] { }));

                for (int i = 1; i < this.worldSize.y - 1; i++)
                {
                    laser = new EntityLaser(this, 2.0F);
                    laser.position.x = this.worldSize.x - 2;
                    laser.position.y = i;
                    laser.calculateTarget();
                    laser.cooldownProcess = (float)(Math.sin((this.worldSize.y - 1 - i) / 15.0D) * 0.5 + 0.5) * laser.shootCooldown;
                    this.addEntityToWorld(laser);
                }

                Game.getInstance().players = new EntityPlayer[2];
                Game.getInstance().players[0] = new EntityPlayer(1, this, new Vector2d(2.5, 15));
                Game.getInstance().players[1] = new EntityPlayer(2, this, new Vector2d(1.6, 15));
                this.addEntityToWorld(Game.getInstance().players[0]);
                this.addEntityToWorld(Game.getInstance().players[1]);
                break;
            case 5:
                teleportConnectionList.add(new TeleportConnection(2, new TeleportConnection.DirectionalPoint(new Vector2i(4, 14), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(29, 17), Direction.DOWN)));
                teleportConnectionList.add(new TeleportConnection(2, new TeleportConnection.DirectionalPoint(new Vector2i(8, 10), Direction.DOWN), new TeleportConnection.DirectionalPoint(new Vector2i(7, 15), Direction.DOWN)));
                teleportConnectionList.add(new TeleportConnection(1, new TeleportConnection.DirectionalPoint(new Vector2i(3, 13), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(21, 17), Direction.DOWN)));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(22, 12), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(3, 14), 1, Tile.AIR_TILE, new Light(new Vector2d(3, 14), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.CLOSE_BLOCK, new Object[] { new Vector2i(3, 14), 1, this.getTile(3, 14, 1) }));

                buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(20, 12), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(7, 12), 1, Tile.AIR_TILE, new Light(new Vector2d(7, 12), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] { }));
                for (int i = 0; i < 5; i++)
                {
                    buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(20, 12), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(6, 14 - i), 1, Tile.AIR_TILE, new Light(new Vector2d(6, 14 - i), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] { }));
                    buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(20, 12), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(1 + i, 10), 1, Tile.AIR_TILE, new Light(new Vector2d(1 + i, 10), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] { }));
                }

                for (Direction dir : Direction.values())
                {
                    laser = new EntityLaser(this, 2.0F);
                    laser.position.x = 15 + dir.getOffset().x;
                    laser.position.y = 9 + dir.getOffset().y;
                    laser.facingDir = dir;
                    switch(dir)
                    {
                        case DOWN:
                            laser.shootCooldown = 2;
                            break;
                        case LEFT:
                            laser.shootCooldown = 4;
                            break;
                        case UP:
                            laser.shootCooldown = 2;
                            break;
                        case RIGHT:
                            laser.shootCooldown = 2;
                            break;
                    }
                    laser.calculateTarget();
                    this.addEntityToWorld(laser);
                }

                Game.getInstance().players = new EntityPlayer[2];
                Game.getInstance().players[0] = new EntityPlayer(1, this, new Vector2d(2.5, 15));
                Game.getInstance().players[1] = new EntityPlayer(2, this, new Vector2d(1.6, 15));
                this.addEntityToWorld(Game.getInstance().players[0]);
                this.addEntityToWorld(Game.getInstance().players[1]);
                break;
            case 6:
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(29, 7), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(30, 15), 1, Tile.AIR_TILE, new Light(new Vector2d(30, 15), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.CLOSE_BLOCK, new Object[] { new Vector2i(30, 15), 1, this.getTile(30, 15, 1) }));

                teleportConnectionList.add(new TeleportConnection(2, new TeleportConnection.DirectionalPoint(new Vector2i(30, 6), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(30, 4), Direction.DOWN)));
                teleportConnectionList.add(new TeleportConnection(1, new TeleportConnection.DirectionalPoint(new Vector2i(30, 14), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(29, 4), Direction.DOWN)));

                for (int i = 3; i < this.worldSize.x - 4; i += 3)
                {
                    buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(i, 15), Direction.UP), ButtonEffect.ButtonEffectType.TOGGLE_BUTTON, new Object[] { new Vector2i(i, 13) }, ButtonEffect.ButtonEffectType.TOGGLE_BUTTON, new Object[] { new Vector2i(i, 13) }));
                    laser = new EntityLaser(this, 0.5F);
                    laser.facingDir = Direction.DOWN;
                    laser.position.x = i;
                    laser.position.y = 13;
                    laser.calculateTarget();
                    laser.cooldownProcess = (float)(Math.sin((this.worldSize.y - 1 - i) / 15.0D) * 0.5 + 0.5) * laser.shootCooldown;
                    this.addEntityToWorld(laser);
                }

                Game.getInstance().players = new EntityPlayer[2];
                Game.getInstance().players[0] = new EntityPlayer(1, this, new Vector2d(1.6, 15));
                Game.getInstance().players[1] = new EntityPlayer(2, this, new Vector2d(1.5, 1));
                this.addEntityToWorld(Game.getInstance().players[0]);
                this.addEntityToWorld(Game.getInstance().players[1]);
                break;
            case 7:
//                buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(7, 13), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(6, 13), 1, Tile.AIR_TILE, new Light(new Vector2d(6, 13), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] {  }));
//                buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(7, 13), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(6, 14), 1, Tile.AIR_TILE, new Light(new Vector2d(6, 14), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] {  }));
//                buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(7, 13), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(3, 0), 1, Tile.AIR_TILE, new Light(new Vector2d(3, 0), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] {  }));
//                buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(7, 13), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(4, 0), 1, Tile.AIR_TILE, new Light(new Vector2d(4, 0), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] {  }));

                teleportConnectionList.add(new TeleportConnection(1, new TeleportConnection.DirectionalPoint(new Vector2i(14, 14), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(16, 4), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(12, 8), Direction.UP)));
                teleportConnectionList.add(new TeleportConnection(1, new TeleportConnection.DirectionalPoint(new Vector2i(4, 0), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(26, 0), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(23, 11), Direction.UP)));
                teleportConnectionList.add(new TeleportConnection(2, new TeleportConnection.DirectionalPoint(new Vector2i(17, 14), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(15, 4), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(20, 2), Direction.UP)));
                teleportConnectionList.add(new TeleportConnection(2, new TeleportConnection.DirectionalPoint(new Vector2i(5, 0), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(27, 0), Direction.UP), new TeleportConnection.DirectionalPoint(new Vector2i(8, 11), Direction.UP)));

                buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(7, 3), Direction.UP), ButtonEffect.ButtonEffectType.TOGGLE_BUTTON, new Object[] { new Vector2i(3, 1) }, ButtonEffect.ButtonEffectType.NONE, new Object[] {} ));
                buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(7, 3), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(8, 1), 1, Tile.NULL_TILE12, new Light(new Vector2d(8, 1), 0.00000000001F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] {} ));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(28, 5), Direction.UP), ButtonEffect.ButtonEffectType.TOGGLE_BUTTON, new Object[] { new Vector2i(28, 1) }, ButtonEffect.ButtonEffectType.NONE, new Object[] {} ));
                buttonEffectList.add(new ButtonEffect(2, new TeleportConnection.DirectionalPoint(new Vector2i(6, 12), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(6, 0), 1, Tile.AIR_TILE, new Light(new Vector2d(6, 0), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] {  }));
                buttonEffectList.add(new ButtonEffect(1, new TeleportConnection.DirectionalPoint(new Vector2i(25, 12), Direction.UP), ButtonEffect.ButtonEffectType.OPEN_BLOCK, new Object[] { new Vector2i(25, 0), 1, Tile.AIR_TILE, new Light(new Vector2d(25, 0), 1.0F, new Vector3f(0, 1, 0)) }, ButtonEffect.ButtonEffectType.NONE, new Object[] {  }));

                laser = new EntityLaser(this, 0.15F);
                laser.facingDir = Direction.LEFT;
                laser.position = new Vector2d(28, 1);
                laser.calculateTarget();
                this.addEntityToWorld(laser);
                laser = new EntityLaser(this, 0.15F);
                laser.facingDir = Direction.RIGHT;
                laser.position = new Vector2d(3, 1);
                laser.calculateTarget();
                this.addEntityToWorld(laser);

                Game.getInstance().players = new EntityPlayer[2];
                Game.getInstance().players[0] = new EntityPlayer(1, this, new Vector2d(15, 15));
                Game.getInstance().players[1] = new EntityPlayer(2, this, new Vector2d(17, 15));
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
		return this.getTile(x, y, 1);
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
        GL11.glRotatef(transitionProgress * transitionProgress * 360.0F, 1, 0, 0);
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
                    bounds = new BoundingBox(bounds.minVec.x, bounds.minVec.y - 0.05, bounds.maxVec.x, bounds.maxVec.y + 0.05);
                    if (bounds.intersects(new BoundingBox(effect.directionalPoint.point.toDoubleVec().sub(new Vector2d(0, 0.0)), effect.directionalPoint.point.toDoubleVec().add(new Vector2d(1, 0.3)))))
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
                        Vector2i setPos = (Vector2i) effect.additionalData[0];
                        this.setTile(setPos.x, setPos.y, (Integer) effect.additionalData[1], (Tile) effect.additionalData[2]);
                        if (!effect.lastPressed)
                        {
//                            Game.getInstance().soundEffectButton.playAsSoundEffect(1.0F, 0.5F, false, setPos.x, setPos.y, 1.0F);
                            this.lightList.add((Light) effect.additionalData[3]);
                        }
                        break;
                    case TOGGLE_BUTTON:
                        setPos = (Vector2i) effect.additionalData[0];
                        for (Entity entity : entityList)
                        {
                            if (entity instanceof EntityLaser)
                            {
                                if (entity.position.x == setPos.x && entity.position.y == setPos.y)
                                {
                                    if (!effect.lastPressed)
                                    {
                                        ((EntityLaser) entity).toggleActive();
//                                        Game.getInstance().soundEffectButton.playAsSoundEffect(1.0F, 0.5F, false, setPos.x, setPos.y, 1.0F);
//                                        if (((EntityLaser) entity).isActive) this.lightList.add((Light) effect.additionalData[1]);
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                }
            }
            else
            {
                switch (effect.effectTypeOff)
                {
                    case CLOSE_BLOCK:
                        Vector2i setPos = (Vector2i) effect.additionalDataOff[0];
                        this.setTile(setPos.x, setPos.y, (Integer) effect.additionalDataOff[1], (Tile) effect.additionalDataOff[2]);
                        if (effect.lastPressed)
                        {
                            this.lightList.remove((Light) effect.additionalData[3]);
                        }
                        break;
                    case TOGGLE_BUTTON:
                        setPos = (Vector2i) effect.additionalData[0];
                        for (Entity entity : entityList)
                        {
                            if (entity instanceof EntityLaser)
                            {
                                if (entity.position.x == setPos.x && entity.position.y == setPos.y)
                                {
                                    if (effect.lastPressed)
                                    {
                                        ((EntityLaser) entity).toggleActive();
//                                        Game.getInstance().soundEffectButton.playAsSoundEffect(1.0F, 0.5F, false, setPos.x, setPos.y, 1.0F);
//                                        if (!((EntityLaser) entity).isActive) this.lightList.remove((Light) effect.additionalData[1]);
                                    }
                                    break;
                                }
                            }
                        }
                        break;


                }
            }

            effect.lastPressed = pressed;
        }

        for (int x = 0; x < this.worldSize.x; x++)
        {
            for (int y = 0; y < this.worldSize.y; y++)
            {
//                if (x > screenMin.x * 2 && x < screenMax.x * 2 && y > screenMin.y * 2 && y < screenMax.y * 2)
                {
                    Tile tile = this.getTile(x, y);

                    if (tile != Tile.AIR_TILE && tile.isAdvanced())
                    {
                        tile.update(deltaTicks);
                    }
                }
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
