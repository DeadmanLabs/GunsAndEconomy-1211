package sheridan.gcaa.addon;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLPaths;
import net.minecraft.server.packs.CompositePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.repository.KnownPack;

import java.util.Optional;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.ModTabs;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.gun.GunModelFactory;
import sheridan.gcaa.client.render.DisplayData;
import sheridan.gcaa.items.ModItems;
import sheridan.gcaa.items.gun.IGun;
import sheridan.gcaa.lib.ArsenalLib;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AddonHandler {
    static final Gson GSON = new Gson();
    private Pack pack;
    private final Map<String, Addon> addonMap = new HashMap<>();
    private final RepositorySource repositorySource = new RepositorySource() {
        @Override
        public void loadPacks(@NotNull Consumer<Pack> pOnLoad) {
            if (!addonMap.isEmpty() &&
                    pack != null) {
                pOnLoad.accept(pack);
            }
        }
    };
    public static final AddonHandler INSTANCE = new AddonHandler();

    public RepositorySource getRepositorySource() {
        return repositorySource;
    }

    public void readAddonPack(Dist dist) {
        Path path = FMLPaths.GAMEDIR.get().resolve("gcaa_addons");
        GCAA.LOGGER.info("Start addon scanning in dir: " + path);
        List<PackResources> addonPacks = new ArrayList<>();
        boolean isClient = dist.isClient();
        PackType packType = isClient ? PackType.CLIENT_RESOURCES : PackType.SERVER_DATA;
        scanAddon(isClient, packType, path);
        for (Map.Entry<String, Addon> entry : addonMap.entrySet()) {
            Addon addon = entry.getValue();
            String name = entry.getKey();
            PathPackResources pathPack = new PathPackResources(
                    new PackLocationInfo(
                            name,
                            Component.literal(name),
                            PackSource.DEFAULT,
                            Optional.empty()
                    ),
                    addon.rootPath
            );
            addonPacks.add(pathPack);
        }
        Pack.ResourcesSupplier resourcesSupplier = new Pack.ResourcesSupplier() {
            @Override
            public PackResources openPrimary(PackLocationInfo locationInfo) {
                if (addonPacks.isEmpty()) {
                    return new PathPackResources(locationInfo, path);
                } else if (addonPacks.size() == 1) {
                    return addonPacks.get(0);
                } else {
                    return new CompositePackResources(addonPacks.get(0), addonPacks.subList(1, addonPacks.size()));
                }
            }

            @Override
            public PackResources openFull(PackLocationInfo locationInfo, Pack.Metadata metadata) {
                return openPrimary(locationInfo);
            }
        };

        pack = Pack.readMetaAndCreate(
                new PackLocationInfo(
                        "gcaa_addons",
                        Component.literal("GCAA ADDON LOADER"),
                        PackSource.DEFAULT,
                        Optional.of(new KnownPack("gcaa", "gcaa_addons", "1.0.0"))
                ),
                resourcesSupplier,
                packType,
                new PackSelectionConfig(true, Pack.Position.BOTTOM, false)
        );
    }

    private void scanAddon(boolean isClient, PackType packType, Path path) {
        GCAA.LOGGER.info("Scanning directory: " + path + "<=====");
        String directoryPath = path.toString();
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            GCAA.LOGGER.info("not a directory, scanning stopped...");
            return;
        }
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            GCAA.LOGGER.info("empty directory, scanning stopped...");
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                GCAA.LOGGER.info("Directory: " + file.getName());
                try {
                    Addon addon = Addon.read(file.toPath());
                    if (addon != null && addon.completed) {
                        addonMap.put(addon.name, addon);
                    }
                } catch (Exception e) {
                    GCAA.LOGGER.info("Error reading: " + file.toPath() + " " + e.getMessage());
                }
            }
        }
    }

    private void scanZipFile(File zipFile) throws IOException {
        throw new NotImplementedException();
        //TODO nothing
    }

    public void handleRegister(Dist dist) {
        for (Map.Entry<String, Addon> entry : addonMap.entrySet()) {
            String addonName  = entry.getKey();
            Addon addon = entry.getValue();
            for (Runnable runnable : addon.soundRegistry) {
                runnable.run();
            }
            ModTabs.MOD_TABS.register(addonName, () -> CreativeModeTab.builder().title(Component.translatable("itemGroup." + addonName))
                    .icon(() -> addon.guns.isEmpty() ?
                            new ItemStack(ModItems.G19.get()) :
                            new ItemStack(addon.guns.get(0).get()))
                    .displayItems((parameters, tab) -> {
                        for (Supplier<Item> supplier : addon.guns) {
                            tab.accept(supplier.get());
                        }
                    }).build());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleRegisterClient() {
        for (Map.Entry<String, Addon> entry : addonMap.entrySet()) {
            String addonName  = entry.getKey();
            Addon addon = entry.getValue();
            handleRegisterClient(addonName, addon);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void handleRegisterClient(String addonName, Addon addon) {
        GCAA.LOGGER.info("Registering addon in client side: " + addonName);
        for (Supplier<Item> gun : addon.configMapping.keySet()) {
            JsonObject config = addon.configMapping.get(gun);
            if (config == null) {
                continue;
            }
            Path modelDefPath = addon.assetsPath.resolve(config.get("model_def").getAsString());
            if (Files.exists(modelDefPath)) {
                try (BufferedReader reader = Files.newBufferedReader(modelDefPath))  {
                    IGun gunInstance = (IGun) gun.get();
                    JsonObject modelDef = GsonHelper.fromJson(GSON, reader, JsonObject.class);
                    JsonObject displayDef = modelDef.get("display").getAsJsonObject();
                    DisplayData display = createDisplay(displayDef);
                    GunModel gunModel = createGunModel(modelDef, addon);
                    if (gunModel == null) {
                        System.out.println("Failed to create gun model for " + addonName + " gun: " + gun.get().getDescriptionId());
                        continue;
                    }
                    System.out.println("Registering gun model for " + addonName + " gun: " + gun.get().getDescriptionId());
                    ArsenalLib.registerGunModel(gunInstance, gunModel, display);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private DisplayData createDisplay(JsonObject displayDef) {
        DisplayData data = new DisplayData();
        data.loadData(displayDef);
        return data;
    }

    @OnlyIn(Dist.CLIENT)
    private GunModel createGunModel(JsonObject modelDef, Addon addon) {
        return GunModelFactory.createGunModel(modelDef, addon);
    }

}
