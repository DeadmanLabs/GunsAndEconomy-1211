package sheridan.gcaa.client.animation.frameAnimation;

import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class Mark {
    private static final Vector3f DEFAULT_SCALE = new Vector3f(1, 1, 1);
    public AnimationDefinition animationDefinition;
    public long timeStamp;
    public Vector3f scales = DEFAULT_SCALE;
    private int tick = 0;
    private int prevSoundIndex = -1;
    private boolean enableSound = false;
    public boolean soundOnServer = false;
    public int loopTimes = 0;
    public int looped = 0;

    public Mark(AnimationDefinition animationDefinition, long timeStamp, Vector3f scales) {
        this.animationDefinition = animationDefinition;
        this.timeStamp = timeStamp;
        this.scales = scales;
    }

    public Mark(AnimationDefinition animationDefinition, long timeStamp) {
        this.animationDefinition = animationDefinition;
        this.timeStamp = timeStamp;
    }

    public Mark(AnimationDefinition animationDefinition) {
        this.animationDefinition = animationDefinition;
        this.timeStamp = 0L;
    }

    public Mark enableSound(boolean enableSound) {
        this.enableSound = enableSound;
        return this;
    }

    public Mark setLoopTimes(int loopTimes) {
        this.loopTimes = loopTimes;
        return this;
    }

    public Mark soundOnServer(boolean soundOnServer) {
        this.soundOnServer = soundOnServer;
        return this;
    }

    public long length() {
        return (long) (animationDefinition.lengthInSeconds() * 1000);
    }

    public float lengthInSec() {
        return animationDefinition.lengthInSeconds();
    }

    public boolean loop() {
        return animationDefinition.looping() && loopTimes != 0;
    }

    public boolean shouldLoop() {
        return loop() && looped < loopTimes;
    }

    public void reset() {
        prevSoundIndex = -1;
        tick = 0;
    }

    public void onClientTick() {
        if (enableSound) {
            List<KeyframeAnimations.SoundPoint> soundPoints = animationDefinition.soundPoints();
            if (soundPoints == null || !(soundPoints.size() > 0)) {
                return;
            }
            int soundIndex = Math.max(0, Mth.binarySearch(
                    0, soundPoints.size(), (index) -> tick < soundPoints.get(index).tick) - 1);
            if (soundIndex != prevSoundIndex) {
                soundPoints.get(soundIndex).playSound(soundOnServer);
                prevSoundIndex = soundIndex;
            }
        }
        tick++;
    }
}
