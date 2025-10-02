package sheridan.gcaa.entities.industrial;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
// TODO: Migrate to data attachments
// import net.neoforged.neoforge.common.capabilities.Capability;
// import net.neoforged.neoforge.common.capabilities.ForgeCapabilities;
// import net.neoforged.neoforge.common.util.LazyOptional;
// import net.minecraftforge.items.IItemHandler;
// import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;
import sheridan.gcaa.client.screens.containers.BulletCraftingMenu;
import sheridan.gcaa.entities.ModEntities;
import sheridan.gcaa.industrial.AmmunitionRecipe;
import sheridan.gcaa.industrial.RecipeRegister;
import sheridan.gcaa.items.ammunition.Ammunition;

import javax.annotation.Nullable;
import java.util.*;

public class BulletCraftingBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible {
    private final BlockPos pos;
    protected NonNullList<ItemStack> items;
    protected final ContainerData dataAccess;
    private static final int[] SLOTS_FOR_UP = new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
    private static final int[] SLOTS_FOR_DOWN = new int[]{16};
    public static final int IS_CRAFTING = 0;
    private int isCrafting = 0;
    public static final int POS_X = 1;
    public static final int POS_Y = 2;
    public static final int POS_Z = 3;
    private Ammunition currentAmmunition = null;
    public static final int CRAFTING_BULLET_ID = 4;
    public static final int PREV_TICK = 5;
    public static final int TOTAL_TICK = 6;
    public int prevTick;
    public int totalTick;
    // TODO: Migrate to data attachments
    // LazyOptional<? extends IItemHandler>[] handlers;

    public BulletCraftingBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModEntities.BULLET_CRAFTING.get(), pPos, pBlockState);
        this.pos = pPos;
        this.items = NonNullList.withSize(17, ItemStack.EMPTY);
        this.dataAccess = new ContainerData() {
            public int get(int index) {
                switch (index) {
                    case IS_CRAFTING -> {
                        return isCrafting;
                    }
                    case POS_X -> {
                        return pos.getX();
                    }
                    case POS_Y -> {
                        return pos.getY();
                    }
                    case POS_Z -> {
                        return pos.getZ();
                    }
                    case CRAFTING_BULLET_ID -> {
                        return getCraftingBulletId();
                    }
                    case PREV_TICK -> {
                        return prevTick;
                    }
                    case TOTAL_TICK -> {
                        return totalTick;
                    }
                    default -> {
                        return -1;
                    }
                }
            }

            public void set(int index, int value) {}

            public int getCount() {
                return 16;
            }
        };
        // TODO: Migrate to data attachments - handlers initialization commented out
        // this.handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
    }
    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BulletCraftingBlockEntity bulletCraftingBlockEntity) {
        if (bulletCraftingBlockEntity.isCrafting == 1) {
            bulletCraftingBlockEntity.prevTick++;
            if (bulletCraftingBlockEntity.prevTick >= bulletCraftingBlockEntity.totalTick) {
                // 完成制造, 生成物品
                bulletCraftingBlockEntity.onFinished();
            }
        }
    }

    public void setCraftingBullet(Ammunition ammunition) {
        currentAmmunition = ammunition;
        // 检查能否开始工作
        if (canStartCrafting(ammunition)) {
            // 开始工作
            startBulletCrafting();
        }
    }
    private int getCraftingBulletId() {
        if (currentAmmunition == null) return -123456789;
        return Item.getId(currentAmmunition);
    }
    /** 能否开始制造工作 */
    private boolean canStartCrafting(Ammunition ammunition) {
        // 判断弹药是否为空
        if (ammunition == null) return false;
        // 如果生成栏已经有了也不能制造
        if (!this.items.get(16).isEmpty()) return false;
        // 获取弹药对应的配方
        AmmunitionRecipe recipe = RecipeRegister.getRecipe(ammunition);
        // 判断配方是否为空
        if (recipe == null || recipe.ingredients.isEmpty()) return false;
        // 获取配方所需的材料
        Map<Item, Integer> ingredients = recipe.getIngredients();
        // 获取材料数量
        int count = ingredients.size();
        // 创建一个Map，用于存储材料及其数量
        Map<Item, Integer> map = new HashMap<>();
        // 遍历物品栏中的物品
        for (ItemStack itemStack : this.items) {
            // 如果所需材料数量为0，则返回true
            if (count == 0) return true;
            // 获取物品
            Item item = itemStack.getItem();
            // 判断物品是否在配方所需材料中
            if (ingredients.containsKey(item)) {
                // 获取所需材料数量
                int needCount;
                // 判断材料是否已经在map中
                if (map.containsKey(item)) {
                    // 如果在，则获取map中的数量
                    needCount = map.get(item);
                }else {
                    // 如果不在，则获取配方中的数量
                    needCount = ingredients.get(item);
                }
                if (needCount == 0) continue;
                // 获取物品的数量
                int haveCount = itemStack.getCount();
                // 判断物品数量是否大于等于所需数量
                if (haveCount >= needCount) {
                    // 如果大于等于，则所需材料数量减1
                    count--;
                    // 将map中的数量置为0
                    map.put(item, 0);
                } else {
                    // 如果小于，则将所需数量减去物品数量
                    map.put(item, needCount - haveCount);
                }
            }
        }
        return count == 0;
    }
    /** 开始制造 */
    private void startBulletCrafting() {
        isCrafting = 1;
        AmmunitionRecipe recipe = RecipeRegister.getRecipe(currentAmmunition);
        totalTick = recipe.craftingTicks;
        // 扣除材料
        consumeMaterials(recipe);
    }
    /** 停止制造 */
    public void stopBulletCrafting() {
        if (isCrafting == 0) return;
        isCrafting = 0;
        returnMaterials();
        currentAmmunition = null;
        totalTick = 0;
        prevTick = 0;
    }
    /** 消费材料 */
    private void consumeMaterials(AmmunitionRecipe recipe) {
        Map<Item, ItemStack> materials = new HashMap<>();
        for (ItemStack itemStack : this.items) {
            Item item = itemStack.getItem();
            if (recipe.ingredients.containsKey(item)) {
                int haveCount = 0;
                if (materials.containsKey(item)) {
                    haveCount = materials.get(item).getCount();
                }
                if (haveCount == 0) {
                    int prevCount = itemStack.getCount();
                    int needCount = recipe.ingredients.get(item);
                    if (prevCount >= needCount) {
                        materials.put(item, new ItemStack(item, needCount));
                        itemStack.setCount(prevCount - needCount);
                    } else {
                        materials.put(item, new ItemStack(item, prevCount));
                        itemStack.setCount(0);
                    }
                } else {
                    int prevCount = itemStack.getCount();
                    int needCount = recipe.ingredients.get(item) - haveCount;
                    ItemStack found = materials.get(item);
                    if (prevCount >= needCount) {
                        materials.put(item, new ItemStack(item, needCount));
                        itemStack.setCount(prevCount - needCount);
                    } else {
                        materials.put(item, new ItemStack(item, found.getCount() + prevCount));
                        itemStack.setCount(0);
                    }
                }
            }
        }
    }
    /** 返还材料 */
    private void returnMaterials() {
        AmmunitionRecipe recipe = RecipeRegister.getRecipe(currentAmmunition);
        if (recipe != null) {
            Map<Item, Integer> ingredients = recipe.getIngredients();
            for (Map.Entry<Item, Integer> entry : ingredients.entrySet()) {
                Item key = entry.getKey();
                int value = entry.getValue();
                ItemStack itemStack = new ItemStack(key, value);
                boolean add = addItemStack(itemStack);
                if (this.level != null && !add && !this.level.isClientSide) {
                    ItemEntity itemEntity = new ItemEntity(this.level,
                            this.getBlockPos().getX(),
                            this.getBlockPos().getY(),
                            this.getBlockPos().getZ(),
                            itemStack);
                    this.level.addFreshEntity(itemEntity);
                }
            }
        }
    }
    /** 生成物品 */
    public void onFinished() {
        AmmunitionRecipe recipe = RecipeRegister.getRecipe(currentAmmunition);
        if (recipe != null) {
            ItemStack itemStack = recipe.getResult();
            this.items.set(16, itemStack);
        }
        isCrafting = 0;
        totalTick = 0;
        prevTick = 0;
    }
    /** 添加物品 */
    private boolean addItemStack(ItemStack itemStack) {
        for (int i = 0; i < items.size() - 1; i++) {
            ItemStack itemStack1 = items.get(i);
            if (itemStack1.isEmpty()) {
                items.set(i, itemStack);
                return true;
            } else if (itemStack1.getItem() == itemStack.getItem() && itemStack1.getCount() < itemStack1.getMaxStackSize()) {
                int i1 = Math.min(itemStack.getCount(), itemStack1.getMaxStackSize() - itemStack1.getCount());
                itemStack1.grow(i1);
                itemStack.shrink(i1);
                if (itemStack.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }
    /** 打掉方块-正制造需要返还配方材料 */
    public void onRemove(Level level, BlockPos pos) {
        if (isCrafting == 1) {
            AmmunitionRecipe recipe = RecipeRegister.getRecipe(currentAmmunition);
            if (recipe != null) {
                Map<Item, Integer> ingredients = recipe.getIngredients();
                for (Map.Entry<Item, Integer> entry : ingredients.entrySet()) {
                    Item key = entry.getKey();
                    int value = entry.getValue();
                    ItemStack itemStack = new ItemStack(key, value);
                    if (level != null && !level.isClientSide) {
                        ItemEntity itemEntity = new ItemEntity(level,
                                pos.getX(),
                                pos.getY(),
                                pos.getZ(),
                                itemStack);
                        level.addFreshEntity(itemEntity);
                    }
                }
            }
        }
    }
    @Override
    protected @NotNull Component getDefaultName() {
        return Component.literal("Bullet_Crafting");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pInventory) {
        return new BulletCraftingMenu(pContainerId, pInventory, this, this.dataAccess).setBlockPos(this.pos);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag pTag, @NotNull HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, this.items, pRegistries);
        this.isCrafting = pTag.getInt("isCrafting");
        String str = pTag.getString("currentAmmunition");
        if (!str.equals("key is null")) {
            Item value = BuiltInRegistries.ITEM.get(ResourceLocation.parse(str));
            if (value instanceof Ammunition ammunition) {
                this.currentAmmunition = ammunition;
            }
        }
        this.prevTick = pTag.getInt("prevTick");
        this.totalTick = pTag.getInt("totalTick");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag, @NotNull HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        ContainerHelper.saveAllItems(pTag, this.items, pRegistries);
        pTag.putInt("isCrafting", this.isCrafting);
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(currentAmmunition);
        pTag.putString("currentAmmunition", key == null ? "key is null" : key.toString());
        pTag.putInt("prevTick", this.prevTick);
        pTag.putInt("totalTick", this.totalTick);
    }
    @Override
    public int getContainerSize() {
        return items.size();
    }

    public int @NotNull [] getSlotsForFace(@NotNull Direction pSide) {
        if (pSide == Direction.UP) {
            return SLOTS_FOR_UP;
        } else if (pSide == Direction.DOWN){
            return SLOTS_FOR_DOWN;
        } else {
            return SLOTS_FOR_UP;
        }
    }
    @Override
    public boolean canPlaceItem(int pIndex, @NotNull ItemStack pStack) {
        if (pIndex == 16) return false;
        if (currentAmmunition == null) return false;
        AmmunitionRecipe recipe = RecipeRegister.getRecipe(currentAmmunition);
        if (recipe != null) {
            return recipe.getIngredients().containsKey(pStack.getItem());
        } else {
            return false;
        }
    }
    @Override
    public boolean canTakeItem(@NotNull Container pTarget, int pIndex, @NotNull ItemStack pStack) {
        return super.canTakeItem(pTarget, pIndex, pStack);
    }

    public boolean canPlaceItemThroughFace(int pIndex, @NotNull ItemStack pItemStack, @Nullable Direction pDirection) {
        return this.canPlaceItem(pIndex, pItemStack);
    }

    public boolean canTakeItemThroughFace(int pIndex, @NotNull ItemStack pStack, @NotNull Direction pDirection) {
        return true;
    }

    public void fillStackedContents(@NotNull StackedContents pHelper) {
        for (ItemStack itemstack : this.items) {
            pHelper.accountStack(itemstack);
        }
    }

    // TODO: Migrate to data attachments
    /*
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
        if (!this.remove && facing != null && capability == ForgeCapabilities.ITEM_HANDLER) {
            if (facing == Direction.UP) {
                return this.handlers[0].cast();
            } else {
                return facing == Direction.DOWN ? this.handlers[1].cast() : this.handlers[2].cast();
            }
        } else {
            return super.getCapability(capability, facing);
        }
    }
    */

    // TODO: Migrate to data attachments
    /*
    public void invalidateCaps() {
        super.invalidateCaps();
        for (LazyOptional<? extends IItemHandler> handler : this.handlers) {
            handler.invalidate();
        }
    }

    public void reviveCaps() {
        super.reviveCaps();
        this.handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
    }
    */
    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    public @NotNull ItemStack getItem(int pIndex) {
        return this.items.get(pIndex);
    }

    public @NotNull ItemStack removeItem(int pIndex, int pCount) {
        ItemStack itemStack1 = ContainerHelper.removeItem(this.items, pIndex, pCount);
        if (pIndex == 16) {
            ItemStack itemStack = this.items.get(16);
            if (itemStack.isEmpty() && canStartCrafting(currentAmmunition)) {
                // 自动化继续制造
                startBulletCrafting();
            }
        }
        return itemStack1;
    }

    public @NotNull ItemStack removeItemNoUpdate(int pIndex) {
        return ContainerHelper.takeItem(this.items, pIndex);
    }

    @Override
    public void setItem(int pIndex, @NotNull ItemStack pStack) {
        this.items.set(pIndex, pStack);
        if (!pStack.isEmpty() && pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }
        // 每放一次物品的时候检测是否能自动化开始工作
        if (pIndex < 16 && currentAmmunition != null && isCrafting == 0) {
            if (canStartCrafting(currentAmmunition)) {
                startBulletCrafting();
            }
        }
        this.setChanged();
    }


    public boolean stillValid(@NotNull Player pPlayer) {
        return Container.stillValidBlockEntity(this, pPlayer);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> pItems) {
        this.items = pItems;
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.items;
    }
}
