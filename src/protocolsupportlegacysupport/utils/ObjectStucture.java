package protocolsupportlegacysupport.utils;

import com.comphenix.protocol.events.AbstractStructure;
import com.comphenix.protocol.reflect.StructureModifier;

public class ObjectStucture extends AbstractStructure {

	public ObjectStucture(Object object, StructureModifier<Object> objectModifier) {
		super(object, objectModifier.withTarget(object));
		if (!objectModifier.getTargetType().isInstance(object)) {
			throw new IllegalArgumentException("Invalid object structure modifier, expected " + objectModifier.getTargetType() + ", got " + object.getClass());
		}
	}

}
