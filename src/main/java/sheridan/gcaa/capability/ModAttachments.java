package sheridan.gcaa.capability;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import sheridan.gcaa.GCAA;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, GCAA.MODID);

    public static final Supplier<AttachmentType<PlayerStatus>> PLAYER_STATUS = ATTACHMENT_TYPES.register(
        "player_status",
        () -> AttachmentType.serializable(PlayerStatus::new).build()
    );
}
