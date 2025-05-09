package fr.maxlego08.menu.zcore.utils.discord;

@FunctionalInterface
public interface ReturnConsumer<T, G> {

	G accept(T t);
	
}