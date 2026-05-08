package fr.maxlego08.menu.api.utils.itemstack;

public class ZToolRule<T> {
    private final T data;
    private final float speed;
    private final boolean correctForDrop;

    public ZToolRule(T data, float speed,boolean correctForDrop) {
        this.data = data;
        this.speed = speed;
        this.correctForDrop = correctForDrop;
    }

    public T getData() {
        return this.data;
    }

    public float getSpeed() {
        return this.speed;
    }

    public boolean isCorrectForDrop() {
        return this.correctForDrop;
    }
}
