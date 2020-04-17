package me.Pride.korra.Blossom;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
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
	private long duration;
	private int radius;
	private long revertTime;
	private boolean reversible;
	private int growthSpeed;
	private boolean donework = false;

	Random rand = new Random();
	private final static List<Material> grassplants = Arrays.asList(Material.GRASS, Material.GRASS, Material.GRASS,  Material.POPPY, Material.DANDELION);

	public Blossom(Player player) {
		super(player);
		
		if (!bPlayer.canPlantbend()) {
			return;
		}
		
		cooldown = config.getLong(path + "Cooldown");
		duration = config.getLong(path + "Duration");
		radius = config.getInt(path + "Radius");
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

		if(this.getStartTime() + duration < System.currentTimeMillis()){
			if (donework) {
				bPlayer.addCooldown(this);
			}
			remove();
			return;
		}
		
		if (!player.isSneaking()) {
			if (donework) {
				bPlayer.addCooldown(this);
			}
			remove();
			return;
		}

		blossom();
	}
	
	private void blossom() {
		for (int i = 0; i < growthSpeed; i++) {
			Location loc = player.getLocation();
			loc.add((rand.nextBoolean() ? 1 : -1) * rand.nextInt(radius), 0, (rand.nextBoolean() ? 1 : -1) * rand.nextInt(radius));
			Block baseblock = GeneralMethods.getTopBlock(loc, 3);
			Block plantblock = baseblock.getRelative(BlockFace.UP);

			if (GeneralMethods.isRegionProtectedFromBuild(player, "Blossom", plantblock.getLocation())) {
				continue;
			}
			
			if (isAir(baseblock.getType()) || !isAir(plantblock.getType())){
				continue;
			}

			Material plant;
			if (baseblock.getType() == Material.GRASS_BLOCK) {
				plant = grassplants.get(rand.nextInt(grassplants.size()));
			} else if (isMushroomBase(baseblock.getType())) {
				plant = rand.nextBoolean() ? Material.BROWN_MUSHROOM : Material.RED_MUSHROOM;
			} else {
				continue;
			}

			if (reversible) {
				TempBlock tempBlock = new TempBlock(plantblock, plant);
				tempBlock.setRevertTime(revertTime);
			} else {
				plantblock.setType(plant, false);
			}
			
			for (Block block : GeneralMethods.getBlocksAroundPoint(player.getLocation(), radius)) {
				
				if (block.getBlockData() instanceof Ageable) {
					
					Ageable crop = (Ageable) block.getBlockData();
					
					crop.setAge(crop.getMaximumAge());
					
					block.setBlockData(crop);
					
				}
			}

			donework = true;

			ParticleEffect.VILLAGER_HAPPY.display(plantblock.getLocation(), 3, 0.2F, 0.2F, 0.2F, 0.2F);
		}
	}

	private boolean isMushroomBase(Material type){
		switch(type){
			case MYCELIUM:
			case SOUL_SAND:
			case PODZOL:
			case GRAVEL:
				return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return Element.WATER.getColor() + "Waterbenders are able to redirect energy paths within plants in order to initiate plant growth "
				+ "and cause deep roots of trees to grow flowers from underground! When used on mycelium and soul sand, these plants will "
				+ "blossom into mushrooms. When used near seed sources, they will grow immediately.";
	}
	
	@Override
	public String getInstructions() {
		return ChatColor.GOLD + "To use, hold sneak to cause plants and flowers to bloom.";
	}

	@Override
	public String getAuthor() {
		return Element.WATER.getColor() + "" + ChatColor.UNDERLINE + 
				"Prride, LiamRP, Shookified and PhanaticD";
	}

	@Override
	public String getVersion() {
		return Element.WATER.getColor() + "" + ChatColor.UNDERLINE + 
				"VERSION 2";
	}

	@Override
	public void load() {
		ProjectKorra.plugin.getServer().getPluginManager().registerEvents(new BlossomListener(), ProjectKorra.plugin);	
		ProjectKorra.log.info(getName() + " by " + getAuthor() + " " + getVersion() + " loaded!");
		
		ConfigManager.getConfig().addDefault(path + "Cooldown", 6500);
		ConfigManager.getConfig().addDefault(path + "Duration", 2000);
		ConfigManager.getConfig().addDefault(path + "Radius", 5);
		ConfigManager.getConfig().addDefault(path + "PlantsRevert", false);
		ConfigManager.getConfig().addDefault(path + "RevertTime", 20000);
		ConfigManager.getConfig().addDefault(path + "GrowthSpeed", 1);
		ConfigManager.defaultConfig.save();
	}

	@Override
	public void stop() {
		ProjectKorra.log.info(getName() + " by " + getAuthor() + " " + getVersion() + " stopped!");
		super.remove();
	}

}
