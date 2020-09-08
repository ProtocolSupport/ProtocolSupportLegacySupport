package protocolsupportlegacysupport.utils;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import com.comphenix.protocol.utility.MinecraftReflection;

public interface EntityIdGenerator {

	public static final EntityIdGenerator INSTANCE = createEntityIdGenerator();

	static EntityIdGenerator createEntityIdGenerator() {
		try {
			return new ServerEntityCounterReflectionEntityIdGenerator();
		} catch (Throwable e) {
		}
		return new StartWithHighIdEntityIdGenerator();
	}

	public int nextId();

	static class ServerEntityCounterReflectionEntityIdGenerator implements EntityIdGenerator {

		private final AtomicInteger entityCounter;

		public ServerEntityCounterReflectionEntityIdGenerator() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
			Field field = MinecraftReflection.getEntityClass().getDeclaredField("entityCount");
			field.setAccessible(true);
			entityCounter = (AtomicInteger) field.get(null);
		}

		@Override
		public int nextId() {
			return entityCounter.incrementAndGet();
		}

	}

	static class StartWithHighIdEntityIdGenerator implements EntityIdGenerator {

		private final AtomicInteger id = new AtomicInteger(632813683);

		@Override
		public int nextId() {
			return id.incrementAndGet();
		}

	}

}
