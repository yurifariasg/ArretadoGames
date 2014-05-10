package com.arretadogames.pilot.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.jbox2d.common.Vec2;

import android.util.Pair;

import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.entities.scenario.Grass;
import com.arretadogames.pilot.entities.scenario.Shrub;
import com.arretadogames.pilot.entities.scenario.Tree;
import com.arretadogames.pilot.items.BoxItem;
import com.arretadogames.pilot.levels.EntityDescriptor;
import com.arretadogames.pilot.levels.HoleDescriptor;
import com.arretadogames.pilot.levels.LevelDescriptor;
import com.arretadogames.pilot.levels.LianaDescriptor;
import com.arretadogames.pilot.levels.PlayerDescriptor;
import com.arretadogames.pilot.levels.WaterDescriptor;
import com.arretadogames.pilot.render.AnimationManager;
import com.arretadogames.pilot.render.AnimationSwitcher;

public class EntityBuilder {
	
	private boolean isBuilt;
	private LevelDescriptor levelDescriptor;
	private ArrayList<Entity> entities;
	private HashMap<PlayerNumber, Pair<Float, Float>> playerPositions;
	
	public EntityBuilder(LevelDescriptor levelDescriptor) {
		this.levelDescriptor = levelDescriptor;
	}
	
	public boolean isBuilt() {
		return isBuilt;
	}
	
	public void buildEntities() {
		entities = new ArrayList<Entity>();
		playerPositions = new HashMap<PlayerNumber, Pair<Float, Float>>();
		
		List<EntityDescriptor> levelEntities = levelDescriptor.getEntities();
		List<Water> waterEntities = new ArrayList<Water>();
		
		if (entities != null){
			
			for (EntityDescriptor entityDescriptor : levelEntities) {
				Entity entity = null;
				switch (entityDescriptor.getType()) {
				
				case BOX:
					entity = new Box(entityDescriptor.getX(), entityDescriptor.getY(),
							entityDescriptor.getSize());
					break;
					
				case SEED:
					entity = new Coin(entityDescriptor.getX(), entityDescriptor.getY(), 10);
					break;
					
				case HOLE:
					HoleDescriptor descriptor = ((HoleDescriptor) entityDescriptor);
					entity = new Hole(descriptor.getX(), descriptor.getX2());
					break;
					
				case PLAYER:
					playerPositions.put(((PlayerDescriptor)entityDescriptor).getPlayerNumber(),
						new Pair<Float,Float>(entityDescriptor.getX(), entityDescriptor.getY()));
					
					break;
				case ONEWAY_WALL:
					entity = new OneWayWall(entityDescriptor.getX(), entityDescriptor.getY());
					break;
					
				case SPIKE:
					entity = new Spike(entityDescriptor.getX(), entityDescriptor.getY());
					break;

				case BREAKABLE:
					entity = new Breakable(entityDescriptor.getX(),entityDescriptor.getY(),0.2f,1.5f,0,false);
					break;
				
				case LIANA:
					LianaDescriptor entityLiana = (LianaDescriptor) entityDescriptor; 
					entity = new Liana(entityLiana.getX(), entityLiana.getY(), entityLiana.getX1(), entityLiana.getY1());
					break;
					
				case FINALFLAG:
					entity = new FinalFlag(entityDescriptor.getX(), entityDescriptor.getY());
					break;
					
				case WATER:
					entity = new Water(entityDescriptor.getX(), entityDescriptor.getY(),((WaterDescriptor)entityDescriptor).getWidth(),((WaterDescriptor)entityDescriptor).getHeight(),((WaterDescriptor)entityDescriptor).getDensity());
					waterEntities.add((Water)entity);
					break;
					
				case TREE:
					entity = new Tree(entityDescriptor.getX(), entityDescriptor.getY());
					break;
					
				case SHRUB:
					entity = new Shrub(entityDescriptor.getX(), entityDescriptor.getY());
					break;
					
				case GRASS:
					entity = new Grass(entityDescriptor.getX(), entityDescriptor.getY());
					break;
					
				case BOX_ITEM:
				    entity = new BoxItem(entityDescriptor.getX(), entityDescriptor.getY());
				    break;
				    
				case TREELOG:
					entity = new TreeLog(entityDescriptor.getX(), entityDescriptor.getY(),
							entityDescriptor.getSize());
					break;
					
				default:
				    System.out.println("Entity Builder failed to assign item: " + entityDescriptor.getType().toString());
					break;
				}
				setEntitySprite(entity);
			}
		}

		// Add Ground
		Vec2[] groundPoints = createGroundLines(waterEntities, levelDescriptor.getLevelLength());
		int amountOfPoints = groundPoints.length;
		
		// Divide the Ground in chunks to avoid rendering it all
		Vec2[] vecs = new Vec2[amountOfPoints > GameSettings.GROUND_ENTITY_THRESHOLD ? GameSettings.GROUND_ENTITY_THRESHOLD : amountOfPoints];
		int internalPointer = 0;
		for (int i = 0 ; i < amountOfPoints ; i++) {
			
			vecs[internalPointer] = groundPoints[i];
			
			if (internalPointer == GameSettings.GROUND_ENTITY_THRESHOLD - 1) {
				entities.add(new Ground(vecs, vecs.length));
				vecs = new Vec2[GameSettings.GROUND_ENTITY_THRESHOLD];
				vecs[0] = groundPoints[i];
				internalPointer = 1;
			} else {
				internalPointer %= GameSettings.GROUND_ENTITY_THRESHOLD;
				internalPointer++;
			}
			
		}
		
		if (internalPointer != 0) {
			Vec2[] lastVec = new Vec2[internalPointer];
			for (int i = 0 ; i < internalPointer ; i++)
				lastVec[i] = vecs[i];
			entities.add(new Ground(lastVec, internalPointer));
		}
		
		isBuilt = true;
	}
	
	private void setEntitySprite(Entity entity) {
		
		if (entity != null) {
			
		    AnimationSwitcher sprite = null;
		    
		    if (entity.getType().equals(EntityType.PLAYER)) {
		        if (entity instanceof LoboGuara) {
		            sprite = AnimationManager.getInstance().getSprite("LoboGuara");
		        } else if (entity instanceof AraraAzul) {
		            sprite = AnimationManager.getInstance().getSprite("AraraAzul");
		        } else if (entity instanceof TatuBola) {
		            sprite = AnimationManager.getInstance().getSprite("TatuBola");
		        } else if (entity instanceof MacacoPrego) {
		            sprite = AnimationManager.getInstance().getSprite("MacacoPrego");
		        }
		    } else {
		    	sprite = AnimationManager.getInstance().getSprite(entity.getType().toString());
		    }
		    
			entity.setSprite(sprite);
			entities.add(entity);
		}
		
	}

	public List<Entity> getAllEntities() {
		return entities;
	}
	
	public List<Entity> getEntitiesOfType(EntityType type) {
		List<Entity> filteredEntities = new ArrayList<Entity>();
		
		for (Entity e : entities) {
			if (e.getType().equals(type)) {
				filteredEntities.add(e);
			}
		}
		
		return filteredEntities;
	}
	
	/**
	 * Create ground based on given entities
	 */
	public Vec2[] createGroundLines(List<Water> waterEntities, float lastX) {
		
		// Sort Based on X
		Collections.sort(waterEntities, new Comparator<Water>() {
			@Override
			public int compare(Water lhs, Water rhs) {
				return (int) (lhs.getPosX() - rhs.getPosX());
			}
		});
		
		Vec2[] groundLines = new Vec2[2 + waterEntities.size() * 4];
		int groundLineIndex = 0;
		
		// Initial Pos
		Vec2 pos = new Vec2(-10, 0);
		groundLines[groundLineIndex++] = pos;
		
		float waterWidth;
		float waterHeight;
		
		for (int i = 0 ; i < waterEntities.size() ; i++) {
			
			waterWidth = waterEntities.get(i).getWidth();
			waterHeight = waterEntities.get(i).getHeight();
			
			// Ground-Water Top Left
			pos = new Vec2(
					waterEntities.get(i).getPosX() - waterWidth / 2, 0);
			groundLines[groundLineIndex++] = pos;
			
			// Ground-Water Bottom Left
			pos = new Vec2(waterEntities.get(i).getPosX() - waterWidth / 2,
					waterEntities.get(i).getPosY() - waterHeight / 2);
			groundLines[groundLineIndex++] = pos;
			
			// Ground-Water Bottom Right
			pos = new Vec2(waterEntities.get(i).getPosX() + waterWidth / 2,
					waterEntities.get(i).getPosY() - waterHeight / 2);
			groundLines[groundLineIndex++] = pos;
			
			// Ground-Water Top Right
			pos = new Vec2(
					waterEntities.get(i).getPosX() + waterWidth / 2, 0);
			groundLines[groundLineIndex++] = pos;
			
		}
		
		pos = new Vec2(lastX + 10, 0);
		groundLines[groundLineIndex++] = pos;
		
		return groundLines;
	}
	
	public void addPlayer(PlayerNumber number, PlayableCharacter character) {
		if (!isBuilt()) {
			throw new IllegalStateException("The builder must have entities built before adding players");
		}
		
		float x, y;
		x = playerPositions.get(number).first;
		y = playerPositions.get(number).second;
		
		Player player = null;
		switch (character) {
		case LOBO_GUARA:
			player = new LoboGuara(x, y, number);
			break;
		case ARARA_AZUL:
			player = new AraraAzul(x, y, number);
			break;
		case MACACO_PREGO:
			player = new MacacoPrego(x, y, number);
			break;
		case TATU_BOLA:
			player = new TatuBola(x, y, number);
			break;
		default:
			break;
		}
		
		setEntitySprite(player);
	}

}
