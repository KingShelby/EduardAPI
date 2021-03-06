package net.eduard.api.lib.storage.bukkit_storables;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.eduard.api.lib.storage.Storable;

public class PotionEffectStorable implements Storable<PotionEffect> {

	public PotionEffect newInstance() {
		return new PotionEffect(PotionEffectType.ABSORPTION, 20, 0);
	}
}
