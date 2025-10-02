package sheridan.gcaa.client.events;


import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;


import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.io.GltfLoader;
import sheridan.gcaa.client.model.modelPart.BufferedModelBone;
import sheridan.gcaa.client.model.modelPart.GCAAShaderInstance;

import java.io.IOException;


// Removed @EventBusSubscriber - manually registered on game bus in GCAA.java
// registerShader moved to GCAA.RegistryEvents (mod bus)
public class Test {

    public static ShaderInstance testShader;

    static boolean firstTick = true;
    // Disabled - empty test method
    // @SubscribeEvent
    // public static void init(LevelTickEvent.Post event) throws IOException {
    //
    // }

    public static ShaderInstance getTestShader() {
        return testShader;
    }


    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && player.getMainHandItem().getItem() == Items.APPLE) {
                BufferedModelBone.__test_render__(Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(player, event.getPartialTick().getGameTimeDeltaPartialTick(false)));
            }
        }
    }

    static boolean test = false;
    // Disabled - GltfLoader.test() uses JME3 classes not available at runtime
    // @SubscribeEvent
    // public static void test(LevelTickEvent.Post event) {
    //     if (!test) {
    //         System.out.println("Player logged in test load gltf");
    //         GltfLoader.test();
    //         test = true;
    //     }
    // }

    public static ShaderInstance SHADER_FOR_ENTITY_CUTOUT;
    // registerShader method moved to GCAA.RegistryEvents (mod bus events)
}
