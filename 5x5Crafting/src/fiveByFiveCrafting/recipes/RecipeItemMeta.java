package fiveByFiveCrafting.recipes;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Multimap;
import com.google.gson.annotations.Expose;

public class RecipeItemMeta {
	@Expose
	private Multimap<Attribute, AttributeModifier> AttributeModifiers;
	@Expose
	private int customModelData;
	@Expose
	private String displayName;
	@Expose
	private Map<Enchantment, Integer> enchants;
	@Expose
	private Set<ItemFlag> itemFlags;
	@Expose
	private String localizedName;
	@Expose
	private List<String> lore;
	// TODO: Add Subinterface values

	public RecipeItemMeta(ItemMeta meta) {
		if (meta.hasAttributeModifiers())
			AttributeModifiers = meta.getAttributeModifiers();
		if (meta.hasCustomModelData())
			customModelData = meta.getCustomModelData();
		if (meta.hasDisplayName())
			displayName = meta.getDisplayName();
		if (meta.hasEnchants())
			enchants = meta.getEnchants();
		itemFlags = meta.getItemFlags();
		if (meta.hasLocalizedName())
			localizedName = meta.getLocalizedName();
		if (meta.hasLore())
			lore = meta.getLore();
	}

	public ItemMeta getItemMeta(Material material) {
		ItemMeta itemMeta = new ItemStack(material, 1).getItemMeta();
		if (itemMeta == null)
			return itemMeta;

		itemMeta.setAttributeModifiers(AttributeModifiers);
		itemMeta.setCustomModelData(customModelData);
		itemMeta.setDisplayName(displayName);
		if (enchants != null) {
			for (Entry<Enchantment, Integer> entry : enchants.entrySet()) {
				itemMeta.addEnchant(entry.getKey(), entry.getValue(), true);
			}
		}
		if (itemFlags != null) {
			for (ItemFlag itemFlag : itemFlags) {
				itemMeta.addItemFlags(itemFlag);
			}
		}
		itemMeta.setLocalizedName(localizedName);
		itemMeta.setLore(lore);
		return itemMeta;
	}

	public boolean check(RecipeItemMeta otherMeta) {

		if (AttributeModifiers != null) {
			for (Attribute key : AttributeModifiers.keys()) {
				if (!otherMeta.getAttributeModifiers(key).equals(AttributeModifiers.get(key))) {
					return false;
				}
			}
		}

		if (customModelData != otherMeta.getCustomModelData())
			return false;

		if (displayName != null) {
			if (!displayName.equals(otherMeta.getDisplayName()))
				return false;
		}

		if (enchants != null) {
			for (Entry<Enchantment, Integer> entry : enchants.entrySet()) {
				if (!otherMeta.getEnchants().get(entry.getKey()).equals(entry.getValue()))
					return false;
			}
		}

		if (itemFlags != null) {
			for (ItemFlag itemFlag : itemFlags) {
				if (!otherMeta.hasItemFlag(itemFlag))
					return false;
			}
		}

		if (localizedName != null) {
			if (!localizedName.equals(otherMeta.getLocalizedName()))
				return false;
		}

		if (lore != null) {
			if (otherMeta.getLore() == null)
				return false;
			
			for (String line : lore) {
				boolean loreCheck = true;
				if (otherMeta.getLore().contains(line)) {
					loreCheck = false;
				}
				if (loreCheck)
					return false;
			}
			if (lore.size() != otherMeta.getLore().size())
				return false;
		}
		
		return true;
	}

	private List<String> getLore() {
		return lore;
	}

	private String getLocalizedName() {
		return localizedName;
	}

	private boolean hasItemFlag(ItemFlag itemFlag) {
		return itemFlags.contains(itemFlag);
	}

	private Map<Enchantment, Integer> getEnchants() {
		return enchants;
	}

	private Object getDisplayName() {
		return displayName;
	}

	private int getCustomModelData() {
		return customModelData;
	}

	private Collection<AttributeModifier> getAttributeModifiers(Attribute key) {
		if (AttributeModifiers == null)
			return null;
		return AttributeModifiers.get(key);
	}

}
