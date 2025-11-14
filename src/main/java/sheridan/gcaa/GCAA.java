package sheridan.gcaa;

import com.mojang.logging.LogUtils;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;
import sheridan.gcaa.addon.AddonHandler;
import sheridan.gcaa.blocks.ModBlocks;
import sheridan.gcaa.capability.ModAttachments;
import sheridan.gcaa.capability.PlayerStatusEvents;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.client.KeyBinds;
import sheridan.gcaa.client.MuzzleFlashLightHandler;
import sheridan.gcaa.client.UnloadTask;
import sheridan.gcaa.client.animation.recoilAnimation.RecoilCameraHandler;
import sheridan.gcaa.client.config.ClientConfig;
import sheridan.gcaa.client.events.*;
import sheridan.gcaa.client.render.entity.GrenadeRenderer;
import sheridan.gcaa.client.render.fx.muzzleSmoke.MuzzleSmokeRenderer;
import sheridan.gcaa.client.screens.AmmunitionModifyScreen;
import sheridan.gcaa.client.screens.BulletCraftingScreen;
import sheridan.gcaa.client.screens.GunModifyScreen;
import sheridan.gcaa.client.screens.VendingMachineScreen;
import sheridan.gcaa.client.screens.containers.ModContainers;
import sheridan.gcaa.data.gun.GunPropertiesHandler;
import sheridan.gcaa.data.gun.GunPropertiesProvider;
import sheridan.gcaa.data.industrial.bulletCrafting.Handler;
import sheridan.gcaa.data.industrial.bulletCrafting.Provider;
import sheridan.gcaa.data.vendingMachineProducts.VendingMachineProductsHandler;
import sheridan.gcaa.data.vendingMachineProducts.VendingMachineProductsProvider;
import sheridan.gcaa.entities.ModEntities;
import sheridan.gcaa.common.events.CommonEvents;
import sheridan.gcaa.common.events.TestEvents;
import sheridan.gcaa.items.ModDataComponents;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.network.PacketRegistry;
import sheridan.gcaa.common.config.CommonConfig;
import sheridan.gcaa.common.server.projetile.PlayerPosCacheHandler;
import sheridan.gcaa.common.server.projetile.ProjectileHandler;
import sheridan.gcaa.sounds.ModSounds;

import java.util.concurrent.CompletableFuture;

@Mod(GCAA.MODID)
public class GCAA {

    public static final String MODID = "gcaa";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static boolean ALLOW_DEBUG = false;

    public GCAA(IEventBus modEventBus) {
        ALLOW_DEBUG = !FMLLoader.isProduction();
        // Client setup listener - NeoForge handles distribution automatically
        if (FMLLoader.getDist() == Dist.CLIENT) {
            modEventBus.addListener(this::onClientSetup);
        }
        modEventBus.addListener(this::commonSetup);
        // Removed custom AddonHandler for standard recipe loading
        // boolean notRunData = isNotRunData();
        // if (notRunData) {
        //     AddonHandler.INSTANCE.readAddonPack(FMLLoader.getDist());
        //     AddonHandler.INSTANCE.handleRegister(FMLLoader.getDist());
        // }
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        // Register addon items if needed (commented out custom addon system)
        // if (notRunData) {
        //     for (DeferredRegister<Item> items : ModItems.ADDON_ITEMS.values()) {
        //         items.register(modEventBus);
        //     }
        // }
        ModBlocks.BLOCKS.register(modEventBus);
        ModTabs.MOD_TABS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModEntities.BLOCK_ENTITIES.register(modEventBus);
        ModContainers.REGISTER.register(modEventBus);
        ModSounds.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        NeoForge.EVENT_BUS.register(GunPropertiesHandler.class);
        NeoForge.EVENT_BUS.register(VendingMachineProductsHandler.class);
        NeoForge.EVENT_BUS.register(Handler.class);
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        modEventBus.addListener(this::gatherDataEvent);
        modEventBus.addListener(PacketRegistry::register);
        // Test without any custom datapack registration - NeoForge should auto-load
    }

    private void gatherDataEvent(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), new GunPropertiesProvider(output, lookupProvider));
        generator.addProvider(event.includeServer(), new VendingMachineProductsProvider(output, lookupProvider));
        generator.addProvider(event.includeServer(), new Provider(output, lookupProvider));
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientSetup(final FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(Test.class);
        NeoForge.EVENT_BUS.register(RenderEvents.class);
        NeoForge.EVENT_BUS.register(ControllerEvents.class);
        NeoForge.EVENT_BUS.register(ClientPlayerEvents.class);
        NeoForge.EVENT_BUS.register(ClientEvents.class);
        NeoForge.EVENT_BUS.register(MuzzleSmokeRenderer.class);
        NeoForge.EVENT_BUS.register(MuzzleFlashLightHandler.class);
        // NeoForge.EVENT_BUS.register(RecoilCameraHandler.class); - No @SubscribeEvent methods, uses handle() method instead
        Clients.onSetUp(event);
        UnloadTask.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.register(PlayerStatusEvents.class);
        NeoForge.EVENT_BUS.register(CommonEvents.class);
        // NeoForge.EVENT_BUS.register(TestEvents.class); - TestEvents has no active event handlers
        NeoForge.EVENT_BUS.register(ProjectileHandler.class);
        NeoForge.EVENT_BUS.register(PlayerPosCacheHandler.class);
        // TODO: Replace with NeoForge Attachment system - Capabilities removed in NeoForge
        // NeoForge.EVENT_BUS.addGenericListener(Entity.class, this::attachCapabilityEvent);
        // PacketHandler is handled by PacketRegistry now
        Commons.onCommonSetUp(event);
    }

    // TODO: Capability system removed - convert to NeoForge Attachments
    // public void attachCapabilityEvent(AttachCapabilitiesEvent<Entity> event) {
    //     if (event.getObject() instanceof Player player) {
    //         if (!player.getCapability(PlayerStatusProvider.CAPABILITY).isPresent()) {
    //             event.addCapability(ResourceLocation.fromNamespaceAndPath(MODID, "player_status"), new PlayerStatusProvider());
    //         }
    //     }
    // }

    // Removed complex datapack registration - testing if NeoForge auto-loads

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class RegistryEvents {

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.GRENADE.get(), GrenadeRenderer::new);
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void registerKeyMapping(RegisterKeyMappingsEvent event) {
            KeyBinds.register(event);
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void onRegisterParticleFactories(RegisterParticleProvidersEvent event) {}

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModContainers.ATTACHMENTS.get(), GunModifyScreen::new);
            event.register(ModContainers.AMMUNITION_MODIFY_MENU.get(), AmmunitionModifyScreen::new);
            event.register(ModContainers.VENDING_MACHINE_MENU.get(), VendingMachineScreen::new);
            event.register(ModContainers.BULLET_CRAFTING_MENU.get(), BulletCraftingScreen::new);
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void registerShader(net.neoforged.neoforge.client.event.RegisterShadersEvent event) {
            try {
                event.registerShader(new sheridan.gcaa.client.model.modelPart.GCAAShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "rendertype_entity_cutout.json"),
                    com.mojang.blaze3d.vertex.DefaultVertexFormat.NEW_ENTITY),
                    (shader) -> {
                        sheridan.gcaa.client.events.Test.SHADER_FOR_ENTITY_CUTOUT = shader;
                    });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
