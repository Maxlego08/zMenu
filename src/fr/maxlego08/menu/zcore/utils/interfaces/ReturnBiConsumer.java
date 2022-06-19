package fr.maxlego08.menu.zcore.utils.interfaces;

@FunctionalInterface
public interface ReturnBiConsumer<T, G, C> {

	C accept(T t, G g);
	
}
