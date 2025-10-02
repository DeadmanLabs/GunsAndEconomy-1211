package sheridan.gcaa.client.animation.io;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import sheridan.gcaa.client.animation.frameAnimation.AnimationChannel;
import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;
import sheridan.gcaa.client.animation.frameAnimation.KeyframeAnimations;
import sheridan.gcaa.client.animation.frameAnimation.Keyframe;
import sheridan.gcaa.utils.RenderAndMathUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@OnlyIn(Dist.CLIENT)
public class AnimationLoader {
    private static final Gson GSON_INSTANCE = new Gson();

    public static Map<String, AnimationDefinition> loadAnimationCollection(ResourceLocation location) {
        return loadAnimationCollection(location, false);
    }

    /**
     * This method is not guaranteed to be thread-safe.
     * <p>
     * Reads bedrock format animation json file which exported by blockbench.
     * <p>
     * STEP interpretation is not supported and will be read as LINEAR instead.
     * */
    public static Map<String, AnimationDefinition> loadAnimationCollection(ResourceLocation location, boolean readSounds) {
        AtomicReference<Map<String, AnimationDefinition>> resultRef = new AtomicReference<>(new HashMap<>());
        try {
            ResourceManager manager = Minecraft.getInstance().getResourceManager();
            manager.getResource(location).ifPresent(res -> {
                try {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(res.open(), StandardCharsets.UTF_8))) {
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        reader.close();
                        String json = stringBuilder.toString();
                        JsonObject jsonObject = GSON_INSTANCE.fromJson(json, JsonObject.class);
                        JsonObject animations = jsonObject.getAsJsonObject("animations");
                        String modid = readSounds ? location.getNamespace() : "";
                        Map<String, AnimationDefinition> animationsMap = new HashMap<>();
                        for (String key : animations.keySet()) {
                            AnimationDefinition animation = readAnimation(animations.getAsJsonObject(key), readSounds ,modid);
                            animationsMap.put(key, animation);
                        }
                        resultRef.set(animationsMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultRef.get();
    }

    private static AnimationDefinition readAnimation(JsonObject jsonObject, boolean readSounds, String modid) {
        float length = jsonObject.get("animation_length").getAsFloat();
        boolean loop = jsonObject.has("loop") && jsonObject.get("loop").getAsBoolean();
        AnimationDefinition.Builder builder = AnimationDefinition.Builder.withLength(length).setLooping(loop);
        JsonObject animation = jsonObject.getAsJsonObject("bones");
        for (String bone : animation.keySet()) {
            JsonObject animationBone = animation.getAsJsonObject(bone);
            AnimationChannel rotate = readRotation(animationBone);
            AnimationChannel position = readPosition(animationBone);
            AnimationChannel scale = readScale(animationBone);
            if (rotate != null) {
                builder.addAnimation(bone, rotate);
            }
            if (position != null) {
                builder.addAnimation(bone, position);
            }
            if (scale != null) {
                builder.addAnimation(bone, scale);
            }
        }
        if (readSounds && jsonObject.has("sound_effects")) {
            JsonObject sounds = jsonObject.getAsJsonObject("sound_effects");
            for (String key : sounds.keySet()) {
                JsonObject soundPointObject = sounds.getAsJsonObject(key);
                ResourceLocation soundName = ResourceLocation.fromNamespaceAndPath(modid, soundPointObject.get("effect").getAsString());
                int tick = RenderAndMathUtils.secondsToTicks(Float.parseFloat(key));
                builder.addSoundPoint(new KeyframeAnimations.SoundPoint(tick, soundName));
            }
        }
        return builder.build();
    }

    private static AnimationChannel readRotation(JsonObject jsonObject) {
        return readChannel(jsonObject, "rotation", AnimationChannel.Targets.ROTATION);
    }

    private static AnimationChannel readPosition(JsonObject jsonObject) {
        return readChannel(jsonObject, "position", AnimationChannel.Targets.POSITION);
    }

    private static AnimationChannel readScale(JsonObject jsonObject) {
        return readChannel(jsonObject, "scale", AnimationChannel.Targets.SCALE);
    }

    private static AnimationChannel readChannel(JsonObject jsonObject, String type, AnimationChannel.Target target) {
        if (!jsonObject.has(type)) {
            return null;
        } else {
            List<Keyframe> keyframes = new ArrayList<>();
            JsonElement content = jsonObject.get(type);
            if (content.isJsonArray()) {
                Vector3f scale = getAsVector3f(content.getAsJsonArray());
                keyframes.add(new Keyframe(0, getVec(scale, type), AnimationChannel.Interpolations.LINEAR));
            } else if (content.isJsonObject()) {
                keyframes = readKeyframes(content.getAsJsonObject(), type);
            }
            return new AnimationChannel(target, keyframes.toArray(new Keyframe[0]));
        }
    }

    private static List<Keyframe> readKeyframes(JsonObject jsonObject, String type) {
        List<Keyframe> keyframes = new ArrayList<>();
        for (String timeStamp : jsonObject.keySet()) {
            float time = Float.parseFloat(timeStamp);
            if (jsonObject.get(timeStamp).isJsonObject()) {
                JsonObject frameObject = jsonObject.get(timeStamp).getAsJsonObject();
                JsonArray pos = frameObject.get("post").getAsJsonArray();
                Vector3f post = getAsVector3f(pos);
                if (frameObject.has("lerp_mode") && "catmullrom".equals(frameObject.get("lerp_mode").getAsString())) {
                    Keyframe keyframe = new Keyframe(time, getVec(post, type), AnimationChannel.Interpolations.CATMULLROM);
                    keyframes.add(keyframe);
                } else {
                    Keyframe keyframe = new Keyframe(time, getVec(post, type), AnimationChannel.Interpolations.LINEAR);
                    keyframes.add(keyframe);
                }
            } else if (jsonObject.get(timeStamp).isJsonArray()) {
                Vector3f pos = getAsVector3f(jsonObject.get(timeStamp).getAsJsonArray());
                Keyframe keyframe = new Keyframe(time, getVec(pos, type), AnimationChannel.Interpolations.LINEAR);
                keyframes.add(keyframe);
            }
        }
        return keyframes;
    }

    private static Vector3f getAsVector3f(JsonArray array) {
        return new Vector3f(
                array.get(0).getAsFloat(),
                array.get(1).getAsFloat(),
                array.get(2).getAsFloat()
        );
    }

    private static Vector3f getVec(Vector3f pos, String type) {
        if (type.equals("rotation")) {
            return KeyframeAnimations.degreeVec(pos.x, pos.y, pos.z);
        }
        if (type.equals("position")) {
            return KeyframeAnimations.posVec(pos.x, pos.y, pos.z);
        }
        if (type.equals("scale")) {
            return KeyframeAnimations.scaleVec(pos.x, pos.y, pos.z);
        }
        return null;
    }

}
