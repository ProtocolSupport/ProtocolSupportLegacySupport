package protocolsupportlegacysupport.utils;

import org.bukkit.entity.EntityType;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;

import protocolsupport.api.MaterialAPI;

public class Constants {

	public static final int SLIME_TYPE_ID = MaterialAPI.getEntityLivingTypeNetworkId(EntityType.SLIME);
	public static final int HORSE_TYPE_ID = MaterialAPI.getEntityLivingTypeNetworkId(EntityType.HORSE);
	public static final int WITHER_TYPE_ID = MaterialAPI.getEntityLivingTypeNetworkId(EntityType.WITHER);
	public static final int ARMORSTAND_LIVING_TYPE_ID = MaterialAPI.getEntityLivingTypeNetworkId(EntityType.ARMOR_STAND);
	public static final int ARMORSTAND_OBJECT_TYPE_ID = MaterialAPI.getEntityObjectTypeNetworkId(EntityType.ARMOR_STAND);

	public static final Serializer DW_FLOAT_SERIALIZER = Registry.get(Float.class, false);
	public static final Serializer DW_BYTE_SERIALIZER = Registry.get(Byte.class, false);
	public static final Serializer DW_OPTIONAL_CHAT_SERIALIZER = Registry.getChatComponentSerializer(true);
	public static final Serializer DW_BOOLEAN_SERIALIZER = Registry.get(Boolean.class, false);
	public static final Serializer DW_INTEGER_SERIALIZER = Registry.get(Integer.class, false);
	public static final int DW_BASE_FLAGS_INDEX = 0;
	public static final int DW_BASE_FLAGS_INVISIBLE_OFFSET = 0x20;
	public static final int DW_BASE_NAME_INDEX = 2;
	public static final int DW_BASE_NAME_VISIBLE_INDEX = 3;
	public static final int DW_LIVING_HEALTH_INDEX = 8;
	public static final int DW_ARMORSTANDDATA_INDEX = 14;
	public static final int DW_ARMORSTANDDATA_MARKER_OFFSET = 0x10;
	public static final int DW_WITHER_INVULNERABLE_TIME_INDEX = 18;
	public static final int DW_SLINE_SIZE_INDEX = 15;

}
