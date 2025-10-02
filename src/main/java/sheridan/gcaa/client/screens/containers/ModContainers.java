package sheridan.gcaa.client.screens.containers;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;
import sheridan.gcaa.GCAA;

import java.util.function.Supplier;

public class ModContainers {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(Registries.MENU, GCAA.MODID);

    public static final Supplier<MenuType<GunModifyMenu>> ATTACHMENTS = REGISTER.register("attachments", () -> new MenuType<>(GunModifyMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<AmmunitionModifyMenu>> AMMUNITION_MODIFY_MENU = REGISTER.register("ammunition_modify_menu", () -> new MenuType<>(AmmunitionModifyMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<VendingMachineMenu>> VENDING_MACHINE_MENU = REGISTER.register("vending_machine_menu", () -> new MenuType<>(VendingMachineMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<BulletCraftingMenu>> BULLET_CRAFTING_MENU = REGISTER.register("bullet_crafting_menu", () -> new MenuType<>(BulletCraftingMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
