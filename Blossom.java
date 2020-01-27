package me.Pride.korra.Blossom;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.PlantAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;

import net.md_5.bungee.api.ChatColor;

public class Blossom extends PlantAbility implements AddonAbility {
	
	private static String path = "ExtraAbilities.Prride.Blossom.";
	FileConfiguration config = ConfigManager.getConfig();
	
	private long cooldown;
	private double radius;
	private long revertTime;
	private boolean reversible;
	private int growthSpeed;
	
	private TempBlock tempBlock;
	Random rand = new Random();
	
	private Material[] nongrass = new Material[] { Material.MYCELIUM, Material.SOUL_SAND, Material.PODZOL };

	public Blossom(Player player) {
		super(player);
		
		if (!bPlayer.canPlantbend()) {
			return;
		}
		
		cooldown = config.getLong(path + "Cooldown");
		radius = config.getDouble(path + "Radius");
		revertTime = config.getLong(path + "RevertTime");
		reversible = config.getBoolean(path + "PlantsRevert");
		growthSpeed = config.getInt(path + "GrowthSpeed");
		
		start();
	}

	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public String getName() {
		return "Blossom";
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		return true;
	}

	@Override
	public void progress() {
		if (player.isDead() || !player.isOnline()) {
			remove();
			return;
		}
		
		if (!bPlayer.canBendIgnoreBindsCooldowns(this)) {
			bPlayer.addCooldown(this);
			remove();
			return;
		}
		
		if (player.isSneaking()) {
			blossom();
		} else {
			bPlayer.addCooldown(this);
			remove();
			return;
		}
	}
	
	private void blossom() {
		for (int i = 1; i < growthSpeed; i++) {
			Location loc = player.getLocation().clone();
			loc.add((rand.nextBoolean() ? 1 : -1) * rand.nextInt((int) radius),
					(rand.nextBoolean() ? 1 : -1) * rand.nextInt((int) radius),
					(rand.nextBoolean() ? 1 : -1) * rand.nextInt((int) radius));
			
			Block block = loc.getBlock().getRelative(BlockFace.UP);
			Block bloc = loc.add(0, 1, 0).getBlock().getRelative(BlockFace.UP);
			
			if (GeneralMethods.isRegionProtectedFromBuild(player, "Blossom", block.getLocation())) {
				return;
			}
			
			if (block.getType() != Material.AIR) {
				
				if (block.getType() == Material.GRASS_BLOCK) {
					if (reversible) {
						if (rand.nextInt(2) == 0) {
							
							tempBlock = new TempBlock(bloc, Material.GRASS);
							
						} else if (rand.nextInt(2) == 1) {
							
							tempBlock = new TempBlock(bloc, Material.DANDELION);
							
						} else if (rand.nextInt(2) == 0) {
							
							tempBlock = new TempBlock(bloc, Material.POPPY);
						}
					
						tempBlock.setRevertTime(revertTime);
						ParticleEffect.VILLAGER_HAPPY.display(bloc.getLocation(), 3, 0.2F, 0.2F, 0.2F, 0.2F);
						
					} else {
						if (rand.nextInt(2) == 0) {
							
							new TempBlock(bloc, Material.GRASS);
							
						} else if (rand.nextInt(2) == 1) {
							
							new TempBlock(bloc, Material.DANDELION);
							
						} else if (rand.nextInt(2) == 0) {
							
							new TempBlock(bloc, Material.POPPY);
						}
						
						ParticleEffect.VILLAGER_HAPPY.display(bloc.getLocation(), 3, 0.2F, 0.2F, 0.2F, 0.2F);
					}
				}
				
				for (Material nongrass : this.nongrass) {
					if (block.getType() == nongrass) {
						if (reversible) {
							if (rand.nextInt(2) == 0) {
								
								tempBlock = new TempBlock(bloc, Material.RED_MUSHROOM);
								
							} else if (rand.nextInt(2) == 1) {
								
								tempBlock = new TempBlock(bloc, Material.BROWN_MUSHROOM);
								
							}
							
							tempBlock.setRevertTime(revertTime);
							ParticleEffect.SPELL_INSTANT.display(block.getLocation(), 3, 0.2F, 0.2F, 0.2F, 0.2F);
							
						} else {
							if (rand.nextInt(2) == 0) {
								
								new TempBlock(bloc, Material.RED_MUSHROOM);
								
							} else if (rand.nextInt(2) == 1) {
								
								new TempBlock(bloc, Material.BROWN_MUSHROOM);
								
							}
							
							ParticleEffect.SPELL_INSTANT.display(block.getLocation(), 3, 0.2F, 0.2F, 0.2F, 0.2F);
						}
					}
				}
			}
		}
	}

	@Override
	public String getDescription() {
		return Element.WATER.getColor() + "Waterbenders are able to redirect energy paths within plants in order to initiate plant growth "
				+ "and cause deep roots of trees to grow flowers from underground! When used on mycelium and soul sand, these plants will "
				+ "blossom into mushrooms.";
	}
	
	@Override
	public String getInstructions() {
		return ChatColor.GOLD + "To use, hold sneak to cause plants and flowers to bloom.";
	}

	@Override
	public String getAuthor() {
		return Element.WATER.getColor() + "" + ChatColor.UNDERLINE + 
				"Prride, LiamRP and Shookified";
	}

	@Override
	public String getVersion() {
		return Element.WATER.getColor() + "" + ChatColor.UNDERLINE + 
				"VERSION 1";
	}

	@Override
	public void load() {
		ProjectKorra.plugin.getServer().getPluginManager().registerEvents(new BlossomListener(), ProjectKorra.plugin);	
		ProjectKorra.log.info(getName() + " by " + getAuthor() + " " + getVersion() + " loaded!");
		
		ConfigManager.getConfig().addDefault(path + "Cooldown", 6500);
		ConfigManager.getConfig().addDefault(path + "Radius", 5);
		ConfigManager.getConfig().addDefault(path + "PlantsRevert", false);
		ConfigManager.getConfig().addDefault(path + "RevertTime", 20000);
		ConfigManager.getConfig().addDefault(path + "GrowthSpeed", 5);
		ConfigManager.defaultConfig.save();
	}

	@Override
	public void stop() {
		ProjectKorra.log.info(getName() + " by " + getAuthor() + " " + getVersion() + " stopped!");
		super.remove();
	}

}
