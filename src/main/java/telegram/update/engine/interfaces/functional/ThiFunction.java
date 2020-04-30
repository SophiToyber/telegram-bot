package telegram.update.engine.interfaces.functional;

@FunctionalInterface
public interface ThiFunction<T, U, K, R> {
	R apply(T t, U u, K k);
}
