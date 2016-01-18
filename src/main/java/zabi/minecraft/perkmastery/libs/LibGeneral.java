package zabi.minecraft.perkmastery.libs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import zabi.minecraft.perkmastery.items.ItemList;

public class LibGeneral {

	public static final String	MOD_ID			= "perkmastery";
	public static final String	MOD_NAME		= "Perk Mastery";
	public static final String	MOD_VERSION		= "@VERSION@-build@BUILD@";
	public static final int		MOD_BUILD		= getBuildNumber();

	public static final String	PROXY_SERVER	= "zabi.minecraft.perkmastery.proxy.ServerProxy";
	public static final String	PROXY_CLIENT	= "zabi.minecraft.perkmastery.proxy.ClientProxy";
	public static final String	UPDATE_MIRROR	= "http://zabi.altervista.org/perkmastery_updates/latest/%channel%";
	
	public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MOD_ID) {
		
		@Override
		public Item getTabIconItem() {
			return ItemList.soul;
		}
	};
	
	private static int getBuildNumber() {
		try {
			return Integer.parseInt("@BUILD@");
		} catch (Exception e) {
			return 0;
		}
	}
}
