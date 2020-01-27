package me.Pride.korra.Blossom;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;

public class BlossomListener implements Listener {
	
	@EventHandler
	public void onSneak(PlayerToggleSneakEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (!event.isSneaking()) {
			return;
		}
		
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getPlayer());
		if (bPlayer != null && bPlayer.canBend(CoreAbility.getAbility("Blossom")) && CoreAbility.getAbility(event.getPlayer(), Blossom.class) == null) {
			new Blossom(event.getPlayer());
		}
	}

}
