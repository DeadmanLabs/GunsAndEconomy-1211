package sheridan.gcaa.items.gun;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import sheridan.gcaa.capability.ModAttachments;
import sheridan.gcaa.capability.PlayerStatus;

@OnlyIn(Dist.CLIENT)
public class ArmPoseHandler implements IClientItemExtensions {

    public static final ArmPoseHandler ARM_POSE_HANDLER = new ArmPoseHandler();

    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
        HumanoidModel.ArmPose pose = HumanoidModel.ArmPose.EMPTY;
        if (hand == InteractionHand.MAIN_HAND && entityLiving instanceof Player player) {
            PlayerStatus status = player.getData(ModAttachments.PLAYER_STATUS);
            if (status != null) {
                if (status.isReloading()) {
                    pose = HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                } else {
                    boolean modify = !(player.getMainHandItem().getItem() instanceof IGun gun) ||
                            !gun.canUseWithShield() || !(player.getOffhandItem().getItem() instanceof ShieldItem);
                    if (modify) {
                        pose = HumanoidModel.ArmPose.BOW_AND_ARROW;
                    }
                }
            }
        }
        return pose;
    }
}