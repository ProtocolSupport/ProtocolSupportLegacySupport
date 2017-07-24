package protocolsupportlegacysupport.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {

	private static final AtomicInteger id = new AtomicInteger(632813683);

	public static int generateId() {
		return id.getAndIncrement();
	}

}
