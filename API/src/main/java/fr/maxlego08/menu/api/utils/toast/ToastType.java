package fr.maxlego08.menu.api.utils.toast;

public enum ToastType {
    TASK,
    GOAL,
    CHALLENGE;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}