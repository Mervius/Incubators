package top.yuhh.incubators.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.yuhh.incubators.Incubators;
import top.yuhh.incubators.item.ModItems;

import java.util.function.Supplier;

public class ModComponents {
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Incubators.MODID);

    public static final Supplier<DataComponentType<Integer>> INITIAL_TIMER = COMPONENTS.registerComponentType(
            "initial_timer",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(Codec.INT)
                    // The codec to read/write the data across the network
                    .networkSynchronized(ByteBufCodecs.INT)
    );

}