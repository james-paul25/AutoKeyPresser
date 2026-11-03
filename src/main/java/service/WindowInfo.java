package service;

public record WindowInfo(String id, String title) {
    /** Sentinel used for "no target: type into whatever window is focused". */
    public static final WindowInfo FOCUSED = new WindowInfo(null, "Focused window (global keys)");

    public boolean isGlobal() {
        return id == null;
    }

    @Override
    public String toString() {
        return id == null ? title : title + "  [" + id + "]";
    }
}