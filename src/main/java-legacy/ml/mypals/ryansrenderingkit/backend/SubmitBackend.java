package ml.mypals.ryansrenderingkit.backend;

/**
 * Pre-26.1 counterpart of the submit backend. These versions have no
 * {@code LevelRenderEvents.COLLECT_SUBMITS} hook, so shapes keep going through the hand-rolled
 * Tesselator batcher and there is nothing to install here.
 * <p>
 * Exists so shared code can call {@link #init()} unconditionally, with no version comments.
 */
public final class SubmitBackend {

    private SubmitBackend() {
    }

    public static void init() {
    }
}
