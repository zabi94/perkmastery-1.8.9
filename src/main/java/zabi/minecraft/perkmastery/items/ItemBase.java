package zabi.minecraft.perkmastery.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import zabi.minecraft.perkmastery.Config;
import zabi.minecraft.perkmastery.PerkMastery;
import zabi.minecraft.perkmastery.libs.LibGeneral;
import zabi.minecraft.perkmastery.misc.IJsonRenderizzabile;


public class ItemBase extends Item implements IJsonRenderizzabile {

	public ItemBase(String modName, CreativeTabs tab) {
		super();
		this.setUnlocalizedName(modName);
		this.setCreativeTab(Config.useSeparateCreativeTab?LibGeneral.CREATIVE_TAB:tab);
	}

	public void register() {
		GameRegistry.registerItem(this, getUnlocalizedName().substring(5));
	}


	@Override
	public void registerModel() {
		PerkMastery.proxy.registerModel(this);
	}

}
