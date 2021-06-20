package protocolsupportlegacysupport.features.hologram.legacyhologram;

import java.util.Collection;
import java.util.Optional;

import org.bukkit.util.Vector;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

//Some clients do not support custom entity display names at all, so don't even spawn hologram for them
public class NoopLegacyHologram implements LegacyHologram {

	@Override
	public void spawn(Collection<PacketContainer> player, Vector location, Optional<WrappedChatComponent> name) {
	}

	@Override
	public void updateLocation(Collection<PacketContainer> player, Vector location) {
	}

	@Override
	public void updateName(Collection<PacketContainer> player, Optional<WrappedChatComponent> name) {
	}

	@Override
	public void despawn(Collection<PacketContainer> packets) {
	}

}
