package ml.mypals.ryansserverrenderingkit.display;

import com.mojang.math.Transformation;
import ml.mypals.ryansserverrenderingkit.mixin.BlockDisplayAccessor;
import ml.mypals.ryansserverrenderingkit.mixin.DisplayAccessor;
import ml.mypals.ryansserverrenderingkit.mixin.ItemDisplayAccessor;
import ml.mypals.ryansserverrenderingkit.mixin.SetPassengersPacketAccessor;
import ml.mypals.ryansserverrenderingkit.mixin.TextDisplayAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public final class VirtualDisplay {

    public static final byte FLAG_SHADOW = 1;
    public static final byte FLAG_SEE_THROUGH = 2;
    public static final byte FLAG_USE_DEFAULT_BACKGROUND = 4;
    public static final byte FLAG_ALIGN_LEFT = 8;
    public static final byte FLAG_ALIGN_RIGHT = 16;

    //? if >= 26.2 {
    private static final EntityType<Display.BlockDisplay> BLOCK_TYPE =
            net.minecraft.world.entity.EntityTypes.BLOCK_DISPLAY;
    private static final EntityType<Display.ItemDisplay> ITEM_TYPE =
            net.minecraft.world.entity.EntityTypes.ITEM_DISPLAY;
    private static final EntityType<Display.TextDisplay> TEXT_TYPE =
            net.minecraft.world.entity.EntityTypes.TEXT_DISPLAY;
    //?} else {
    /*private static final EntityType<Display.BlockDisplay> BLOCK_TYPE = EntityType.BLOCK_DISPLAY;
    private static final EntityType<Display.ItemDisplay> ITEM_TYPE = EntityType.ITEM_DISPLAY;
    private static final EntityType<Display.TextDisplay> TEXT_TYPE = EntityType.TEXT_DISPLAY;
    *///?}

    //? if >= 26.2 {
    public static final BlockState DEFAULT_BLOCK_STATE = Blocks.STAINED_GLASS.white().defaultBlockState();
    //?} else {
    /*public static final BlockState DEFAULT_BLOCK_STATE = Blocks.WHITE_STAINED_GLASS.defaultBlockState();
    *///?}
    private static final double VIEW_RANGE = 96.0D;
    private static final double VIEW_RANGE_SQR = VIEW_RANGE * VIEW_RANGE;
    private static final float VIEW_RANGE_DATA = 2.0F;

    private final ServerLevel level;
    private final Display entity;
    private final Set<UUID> viewers = new HashSet<>();

    @Nullable
    private Entity vehicle;
    @Nullable
    private Predicate<ServerPlayer> viewerFilter;

    private boolean dataDirty = true;
    private boolean seeThrough = false;

    private VirtualDisplay(ServerLevel level, Display entity) {
        this.level = level;
        this.entity = entity;
        this.entity.setNoGravity(true);
        //? if >= 26.3 {
        /*this.entity.setPermanentlyInvulnerable(true);
        *///?} else {
        this.entity.setInvulnerable(true);
        //?}
        DisplayAccessor accessor = (DisplayAccessor) this.entity;
        accessor.rrk$setViewRange(VIEW_RANGE_DATA);
        accessor.rrk$setInterpolationDuration(1);
        accessor.rrk$setInterpolationDelay(0);
    }

    public static VirtualDisplay block(ServerLevel level, double x, double y, double z, @Nullable BlockState state) {
        Display.BlockDisplay display = new Display.BlockDisplay(BLOCK_TYPE, level);
        display.setPos(x, y, z);
        ((BlockDisplayAccessor) display).rrk$setBlockState(state != null ? state : DEFAULT_BLOCK_STATE);
        return new VirtualDisplay(level, display);
    }

    public static VirtualDisplay block(ServerLevel level, double x, double y, double z) {
        return block(level, x, y, z, DEFAULT_BLOCK_STATE);
    }

    public static VirtualDisplay item(ServerLevel level, double x, double y, double z, ItemStack stack, ItemDisplayContext context) {
        Display.ItemDisplay display = new Display.ItemDisplay(ITEM_TYPE, level);
        display.setPos(x, y, z);
        ItemDisplayAccessor accessor = (ItemDisplayAccessor) display;
        accessor.rrk$setItemStack(stack);
        accessor.rrk$setItemTransform(context);
        return new VirtualDisplay(level, display);
    }

    public static VirtualDisplay item(ServerLevel level, double x, double y, double z, ItemStack stack) {
        return item(level, x, y, z, stack, ItemDisplayContext.NONE);
    }

    public static VirtualDisplay text(ServerLevel level, double x, double y, double z, Component text) {
        Display.TextDisplay display = new Display.TextDisplay(TEXT_TYPE, level);
        display.setPos(x, y, z);
        TextDisplayAccessor accessor = (TextDisplayAccessor) display;
        accessor.rrk$setText(text);
        return new VirtualDisplay(level, display);
    }

    public VirtualDisplay filter(@Nullable Predicate<ServerPlayer> filter) {
        this.viewerFilter = filter;
        return this;
    }

    public VirtualDisplay blockState(BlockState state) {
        if (this.entity instanceof Display.BlockDisplay display) {
            ((BlockDisplayAccessor) display).rrk$setBlockState(state);
            this.dataDirty = true;
        }
        return this;
    }

    public VirtualDisplay itemStack(ItemStack stack) {
        if (this.entity instanceof Display.ItemDisplay display) {
            ((ItemDisplayAccessor) display).rrk$setItemStack(stack);
            this.dataDirty = true;
        }
        return this;
    }

    public VirtualDisplay textComponent(Component text) {
        if (this.entity instanceof Display.TextDisplay display) {
            ((TextDisplayAccessor) display).rrk$setText(text);
            this.dataDirty = true;
        }
        return this;
    }

    public VirtualDisplay glow(int rgb) {
        this.entity.setGlowingTag(true);
        ((DisplayAccessor) this.entity).rrk$setGlowColorOverride(rgb);
        this.dataDirty = true;
        return this;
    }

    public VirtualDisplay bright() {
        ((DisplayAccessor) this.entity).rrk$setBrightnessOverride(Brightness.FULL_BRIGHT);
        this.dataDirty = true;
        return this;
    }

    public VirtualDisplay brightness(Brightness brightness) {
        ((DisplayAccessor) this.entity).rrk$setBrightnessOverride(brightness);
        this.dataDirty = true;
        return this;
    }

    public VirtualDisplay seeThrough(boolean seeThrough, int defaultGlowColor) {
        this.seeThrough = seeThrough;
        if (this.entity instanceof Display.TextDisplay display) {
            TextDisplayAccessor accessor = (TextDisplayAccessor) display;
            byte flags = accessor.rrk$getFlags();
            if (seeThrough) {
                flags |= FLAG_SEE_THROUGH;
            } else {
                flags &= (byte) ~FLAG_SEE_THROUGH;
            }
            accessor.rrk$setFlags(flags);
            this.dataDirty = true;
        } else {
            if (seeThrough) {
                glow(defaultGlowColor);
            } else {
                this.entity.setGlowingTag(false);
                this.dataDirty = true;
            }
        }
        return this;
    }

    public VirtualDisplay billboard(Display.BillboardConstraints constraints) {
        ((DisplayAccessor) this.entity).rrk$setBillboardConstraints(constraints);
        this.dataDirty = true;
        return this;
    }

    public VirtualDisplay transform(@Nullable Transformation transformation) {
        if (transformation != null) {
            ((DisplayAccessor) this.entity).rrk$setTransformation(transformation);
            this.dataDirty = true;
        }
        return this;
    }

    public VirtualDisplay pos(double x, double y, double z) {
        this.entity.setPos(x, y, z);
        this.dataDirty = true;
        return this;
    }

    public VirtualDisplay ride(Entity vehicle) {
        this.vehicle = vehicle;
        return this;
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    public Display getEntity() {
        return this.entity;
    }

    public int getId() {
        return this.entity.getId();
    }

    public void sync() {
        @Nullable List<SynchedEntityData.DataValue<?>> data =
                this.dataDirty ? this.entity.getEntityData().getNonDefaultValues() : null;

        if (data != null && data.isEmpty()) {
            data = null;
        }

        double x = this.entity.getX();
        double y = this.entity.getY();
        double z = this.entity.getZ();
        int id = this.entity.getId();

        for (ServerPlayer player : this.level.players()) {
            UUID viewer = player.getUUID();

            if (!this.visibleTo(player, x, y, z)) {
                if (this.viewers.remove(viewer)) {
                    player.connection.send(new ClientboundRemoveEntitiesPacket(id));
                }
                continue;
            }

            if (!this.viewers.add(viewer)) {
                if (data != null) {
                    player.connection.send(new ClientboundSetEntityDataPacket(id, data));
                }
                continue;
            }

            player.connection.send(new ClientboundAddEntityPacket(
                    id, this.entity.getUUID(), x, y, z,
                    0.0F, 0.0F, this.entity.getType(), 0, Vec3.ZERO, 0.0D));

            if (data != null) {
                player.connection.send(new ClientboundSetEntityDataPacket(id, data));
            }

            if (this.vehicle != null) {
                ClientboundSetPassengersPacket packet = this.passengersPacket();
                if (packet != null) {
                    player.connection.send(packet);
                }
            }
        }

        this.dataDirty = false;
    }

    public void remove() {
        if (this.viewers.isEmpty()) {
            return;
        }

        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(this.entity.getId());
        for (ServerPlayer player : this.level.players()) {
            if (this.viewers.remove(player.getUUID())) {
                player.connection.send(packet);
            }
        }
        this.viewers.clear();
    }

    private boolean visibleTo(ServerPlayer player, double x, double y, double z) {
        if (this.viewerFilter != null && !this.viewerFilter.test(player)) {
            return false;
        }

        if (player.distanceToSqr(x, y, z) > VIEW_RANGE_SQR) {
            return false;
        }

        if (this.vehicle == null) {
            return true;
        }

        //? if >= 1.20 {
        if (player.level() != this.level) {
        //?} else {
        /*if (player.getLevel() != this.level) {
        *///?}
            return false;
        }

        return this.level.getChunkSource().chunkMap
                .getPlayers(this.vehicle.chunkPosition(), false)
                .contains(player);
    }

    @Nullable
    private ClientboundSetPassengersPacket passengersPacket() {
        if (this.vehicle == null) return null;
        ClientboundSetPassengersPacket packet = new ClientboundSetPassengersPacket(this.vehicle);
        ((SetPassengersPacketAccessor) packet).rrk$setPassengers(new int[]{this.entity.getId()});
        return packet;
    }
}
