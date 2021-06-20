package protocolsupportlegacysupport.features.hologram.legacyhologram;

import java.util.Collection;
import java.util.Optional;

import org.bukkit.util.Vector;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import protocolsupport.api.ProtocolVersion;

public interface LegacyHologram {

	public static LegacyHologram create(ProtocolVersion version, int entityId) {
		if (version.isBetween(ProtocolVersion.MINECRAFT_1_6_1, ProtocolVersion.MINECRAFT_1_7_10)) {
			return new HorseLegacyHologram(entityId);
		} else if (version.isBetween(ProtocolVersion.MINECRAFT_1_5_1, ProtocolVersion.MINECRAFT_1_5_2)) {
			return new SlimeLegacyHologram(entityId);
		} else {
			return new NoopLegacyHologram();
		}
	}

	public void spawn(Collection<PacketContainer> packets, Vector location, Optional<WrappedChatComponent> name);

	public void updateLocation(Collection<PacketContainer> packets, Vector location);

	public void updateName(Collection<PacketContainer> packets, Optional<WrappedChatComponent> name);

	public void despawn(Collection<PacketContainer> packets);

}
