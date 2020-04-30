package telegram.update.engine.interfaces.functional;

@FunctionalInterface
public interface ThiConsumer<T, U, K> {
	void accept(T t, U u, K k);
}
