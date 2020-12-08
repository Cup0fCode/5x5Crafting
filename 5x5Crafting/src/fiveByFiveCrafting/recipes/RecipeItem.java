package fiveByFiveCrafting.recipes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.output.ByteArrayOutputStream;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.google.gson.annotations.Expose;

public class RecipeItem {
	@Expose
	private String item;
	@Expose
	private int count;
	@Expose
	private RecipeChoice recipeChoice; //used for crafting recipes with choices for blocks, RecipeItem will have no
										//item
	@Expose
	private String base64ItemStack;

	public RecipeItem(ItemStack item) {
		if (item == null) {
			this.item = "AIR";
			count = 0;
		} else {
			this.item = item.getType().name();
			base64ItemStack = itemTo64(item);
			count = item.getAmount();
		}
	}

	public RecipeItem(RecipeChoice value) {
		recipeChoice = value;
	}

	public Material getMaterial() {
		if (item == null) {
			return Material.AIR;
		}
		Material m = Material.getMaterial(item.substring(item.indexOf(":") + 1).toUpperCase());
		if (m == null) {
			return Material.AIR;
		}

		return Material.getMaterial(item.substring(item.indexOf(":") + 1).toUpperCase());
	}

	public boolean check(RecipeItem recipeItem) {
		if (recipeChoice != null) { // this is a recipeChoice item
			if (recipeChoice.test(recipeItem.getItemStack()))
				return true;
			return false;
		}

		if (recipeItem == null) {
			Bukkit.getLogger().info("Null RecipeItem");
			return false;
		}

		ItemStack item = getItemStack();
		ItemStack otherItem = recipeItem.getItemStack();
		
		if (item.getType().equals(Material.AIR) && otherItem.getType().equals(Material.AIR))
			return true;
		
		Map<String, Object> serializedItem = otherItem.serialize();
		
		//check if existing values are the same for both objects
		for (Entry<String, Object> entry : item.serialize().entrySet()) {
			if (entry.getValue() == null) 
				continue;
			if (serializedItem.get(entry.getKey()) == null)
				return false;
			if (!serializedItem.get(entry.getKey()).equals(entry.getValue()))
				return false;
		}
		return true;
	}

	public ItemStack getItemStack() {
		if (base64ItemStack != null) {
			try {
				ItemStack result = itemFrom64(base64ItemStack);
				result.setAmount(getCount());
				return result;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ItemStack(getMaterial(), getCount());
	}

	public int getCount() {
		if (count == 0)
			return 1;
		return count;
	}
	
	//Taken from: https://www.spigotmc.org/threads/deserializing-itemmeta-from-json.69760/
	private static String itemTo64(ItemStack stack) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(stack);

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to save item stack.", e);
        }
    }
   
	//Taken from: https://www.spigotmc.org/threads/deserializing-itemmeta-from-json.69760/
    private static ItemStack itemFrom64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            try {
                return (ItemStack) dataInput.readObject();
            } finally {
                dataInput.close();
            }
        }
        catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
