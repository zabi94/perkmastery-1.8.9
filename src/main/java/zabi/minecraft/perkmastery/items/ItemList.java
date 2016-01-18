package zabi.minecraft.perkmastery.items;

import net.minecraft.creativetab.CreativeTabs;
import zabi.minecraft.perkmastery.items.special.EvocationTome;
import zabi.minecraft.perkmastery.items.special.ExperienceTome;
import zabi.minecraft.perkmastery.items.special.GoldenBow;
import zabi.minecraft.perkmastery.items.special.GrapplingHook;
import zabi.minecraft.perkmastery.items.special.ReturnTome;
import zabi.minecraft.perkmastery.items.special.ScaffoldBuilder;
import zabi.minecraft.perkmastery.items.special.UndeadSoul;
import zabi.minecraft.perkmastery.misc.IJsonRenderizzabile;


public class ItemList {

	public static final ReturnTome		tomeReturn		= new ReturnTome("tomeReturn", CreativeTabs.tabMisc);
	public static final EvocationTome	tomeEvocation	= new EvocationTome("tomeEvocation", CreativeTabs.tabMisc);
	public static final ExperienceTome	tomeExperience	= new ExperienceTome("tomeExperience", CreativeTabs.tabMisc);
	public static final GoldenBow		goldenBow		= new GoldenBow("goldenBow", CreativeTabs.tabCombat);
	public static final ScaffoldBuilder	scaffoldBuilder	= new ScaffoldBuilder("scaffoldBuilder", CreativeTabs.tabMisc);
	public static final UndeadSoul		soul			= new UndeadSoul("soul", CreativeTabs.tabMisc);
	public static final GrapplingHook	hook			= new GrapplingHook("grapplingHook", CreativeTabs.tabTransport);

	public static final ItemBase		boneAmulet		= new ItemBase("boneAmulet", CreativeTabs.tabMisc);
	public static final ItemBase		chainmailCloth	= new ItemBase("chainmailCloth", CreativeTabs.tabMaterials);
	
	private static final IJsonRenderizzabile[] items=new IJsonRenderizzabile[] {
			tomeEvocation, tomeExperience, tomeReturn, goldenBow, scaffoldBuilder, soul, boneAmulet, chainmailCloth, hook
	};

	public static void register() {
		for (IJsonRenderizzabile i:items) i.register();
	}
	
	public static void registerModels() {
		for (IJsonRenderizzabile i:items) i.registerModel();
	}

}
