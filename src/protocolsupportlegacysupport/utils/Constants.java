package protocolsupportlegacysupport.utils;

import org.bukkit.entity.EntityType;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;

public class Constants {

	public static final int WITHER_SKULL_TYPE_ID = 66;
	@SuppressWarnings("deprecation")
	public static final int SLIME_TYPE_ID = EntityType.SLIME.getTypeId();
	@SuppressWarnings("deprecation")
	public static final int HORSE_TYPE_ID = EntityType.HORSE.getTypeId();
	@SuppressWarnings("deprecation")
	public static final int WITHER_TYPE_ID = EntityType.WITHER.getTypeId();
	@SuppressWarnings("deprecation")
	public static final int ARMORSTAND_LIVING_TYPE_ID = EntityType.ARMOR_STAND.getTypeId();
	public static final int ARMORSTAND_OBJECT_TYPE_ID = 78;

	public static final Serializer DW_FLOAT_SERIALIZER = Registry.get(Float.class, false);
	public static final Serializer DW_BYTE_SERIALIZER = Registry.get(Byte.class, false);
	public static final Serializer DW_STRING_SERIALIZER = Registry.get(String.class, false);
	public static final Serializer DW_BOOLEAN_SERIALIZER = Registry.get(Boolean.class, false);
	public static final Serializer DW_INTEGER_SERIALIZER = Registry.get(Integer.class, false);
	public static final int DW_NAME_VISIBLE_INDEX = 3;
	public static final int DW_NAME_INDEX = 2;
	public static final int DW_BASICDATA_INDEX = 0;
	public static final int DW_BASICADATA_INVISIBLE_OFFSET = 0x20;
	public static final int DW_ARMORSTANDDATA_INDEX = 11;
	public static final int DW_ARMORSTANDDATA_MARKER_OFFSET = 0x10;

}
