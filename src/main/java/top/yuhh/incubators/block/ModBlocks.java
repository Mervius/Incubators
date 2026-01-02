package top.yuhh.incubators.block;

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

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Incubators.MODID);

    public static final DeferredBlock<Block> INCUBATOR_BLOCK = registerBlock("egg_incubator", () -> new IncubatorBlock(BlockBehaviour.Properties.of().noOcclusion().mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOD).pushReaction(PushReaction.BLOCK).isSuffocating((state, world, pos) -> false).isRedstoneConductor((state, world, pos) -> false)));


    private  static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}