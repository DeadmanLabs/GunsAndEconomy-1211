package sheridan.gcaa.data.industrial.bulletCrafting;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.data.IJsonSyncable;
import sheridan.gcaa.industrial.AmmunitionRecipe;
import sheridan.gcaa.industrial.RecipeRegister;
import sheridan.gcaa.items.ammunition.Ammunition;
import net.neoforged.neoforge.network.PacketDistributor;
import sheridan.gcaa.network.packets.s2c.UpdateBulletCraftingRecipePacket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = GCAA.MODID)
public class Handler extends SimplePreparableReloadListener<Map<String, JsonObject>> {
    private static String string = null;
    @Override
    protected @NotNull Map<String, JsonObject> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        Map<String, JsonObject> map = new HashMap<>();
        Map<Ammunition, AmmunitionRecipe> ammunitionRecipeMap = RecipeRegister.getAmmunitionRecipeMap();
        for (Map.Entry<Ammunition, AmmunitionRecipe> entry : ammunitionRecipeMap.entrySet()) {
            String fileName = entry.getKey().getDescriptionId().split("\\.")[2];
            String forgeKey = String.valueOf(BuiltInRegistries.ITEM.getKey(entry.getKey()));
            String name = "bullet_crafting_recipes/" + fileName + ".json";
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(GCAA.MODID, name);
            resourceManager.getResource(location).ifPresent(res -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(res.open(), StandardCharsets.UTF_8))) {
                    JsonObject jsonObject = GsonHelper.fromJson(IJsonSyncable.GSON, reader, JsonObject.class);
                    map.put(forgeKey, jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return map;
    }
    @Override
    protected void apply(@NotNull Map<String, JsonObject> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        List<Ammunition> ammunitionList = Ammunition.getAll();
        for (Ammunition ammunition : ammunitionList) {
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(ammunition);
            if (key == null) continue;
            JsonObject jsonObject = map.get(key.toString());
            AmmunitionRecipe recipe = RecipeRegister.getRecipe(ammunition);
            if (recipe != null && jsonObject != null) {
                recipe.loadData(jsonObject);
            }
        }
        Map<Ammunition, AmmunitionRecipe> ammunitionRecipeMap = RecipeRegister.getAmmunitionRecipeMap();
        JsonObject newJsonObject = new JsonObject();
        for (Map.Entry<Ammunition, AmmunitionRecipe> entry : ammunitionRecipeMap.entrySet()) {
            AmmunitionRecipe recipe = entry.getValue();
            JsonObject jsonObject = new JsonObject();
            recipe.writeData(jsonObject);
            ResourceLocation itemsKey = BuiltInRegistries.ITEM.getKey(entry.getKey());
            if (itemsKey == null) continue;
            newJsonObject.add(itemsKey.toString(), jsonObject);
        }
        string = newJsonObject.toString();
    }
    @SubscribeEvent
    public static void addReloadListenerEvent(AddReloadListenerEvent event) {
        Handler handler = new Handler();
        event.addListener(handler);
    }
    @SubscribeEvent
    public static void onDataPackSync(OnDatapackSyncEvent event) {
        if (string != null) {
            ServerPlayer player = event.getPlayer();
            if (player == null) {
                PacketDistributor.sendToAllPlayers(new UpdateBulletCraftingRecipePacket(string));
            } else {
                PacketDistributor.sendToPlayer(player, new UpdateBulletCraftingRecipePacket(string));
            }
        }
    }
}
