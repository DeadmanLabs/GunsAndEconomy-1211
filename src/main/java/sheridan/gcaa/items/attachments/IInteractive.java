package sheridan.gcaa.items.attachments;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import sheridan.gcaa.items.gun.IGun;

public interface IInteractive {
    @OnlyIn(Dist.CLIENT)
    void onMouseButton(int btn, int action, ItemStack stack, IGun gun, Player player);
    @OnlyIn(Dist.CLIENT)
    void onKeyPress(int key, int action, ItemStack stack, IGun gun, Player player);
}
