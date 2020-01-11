package protocolsupportlegacysupport.features;

public abstract class AbstractFeature<T> {

	private final boolean enabled = false;

	public void enable(T config) {
		if (enabled) {
			return;
		}
		enable0(config);
	}

	protected abstract void enable0(T config);

	public void disable() {
		if (!enabled) {
			return;
		}
		disable0();
	}

	protected abstract void disable0();

}
