package top.yuhh.incubators.block.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.yuhh.incubators.Incubators;
import top.yuhh.incubators.block.ModBlocks;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Incubators.MODID);

    public static final Supplier<BlockEntityType<IncubatorBlockEntity>> INCUBATOR_BE = BLOCK_ENTITIES.register("egg_incubator_be", () -> BlockEntityType.Builder.of(IncubatorBlockEntity::new, ModBlocks.INCUBATOR_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
