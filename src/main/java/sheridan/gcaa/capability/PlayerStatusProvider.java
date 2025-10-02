package sheridan.gcaa.capability;

import net.minecraft.world.entity.player.Player;

public class PlayerStatusProvider {

    public static PlayerStatus getStatus(Player player) {
        PlayerStatus status = player.getData(ModAttachments.PLAYER_STATUS);
        return status != null ? status : PlayerStatus.EMPTY;
    }

    public static void setLastShoot(Player player, long lastShootLeft)  {
        if (player != null) {
            PlayerStatus status = player.getData(ModAttachments.PLAYER_STATUS);
            if (status != null) {
                status.setLastShoot(lastShootLeft);
            }
        }
    }

    public static void setLastChamberAction(Player player, long lastChamberAction)  {
        if (player != null) {
            PlayerStatus status = player.getData(ModAttachments.PLAYER_STATUS);
            if (status != null) {
                status.setLastChamberAction(lastChamberAction);
            }
        }
    }

    public static void updateLocalTimeOffset(Player player)  {
        if (player != null) {
            PlayerStatus status = player.getData(ModAttachments.PLAYER_STATUS);
            if (status != null) {
                status.setLocalTimeOffset(System.currentTimeMillis() - player.level().getGameTime() * 50);
            }
        }
    }

    public static void setReloading(Player player, boolean reloading)  {
        if (player != null) {
            PlayerStatus status = player.getData(ModAttachments.PLAYER_STATUS);
            if (status != null) {
                status.setReloading(reloading);
            }
        }
    }
}
