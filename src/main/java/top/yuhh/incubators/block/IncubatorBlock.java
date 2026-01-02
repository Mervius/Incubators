package top.yuhh.incubators.block;

import com.mojang.serialization.MapCodec;
import ludichat.cobbreeding.CustomProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import top.yuhh.incubators.block.entity.IncubatorBlockEntity;
import top.yuhh.incubators.block.entity.ModBlockEntities;
import top.yuhh.incubators.mpl.VoxelHelper;

import javax.annotation.Nullable;

import static com.cobblemon.mod.common.util.VectorShapeExtensionsKt.rotateShape;
public class IncubatorBlock extends BaseEntityBlock {
    public static final  MapCodec<IncubatorBlock> CODEC = simpleCodec(IncubatorBlock::new);

    public static final BooleanProperty HAS_EGG = CustomProperties.HAS_EGG;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;


    private final VoxelShape EAST_SHAPE = VoxelHelper.buildCollider(Direction.EAST);


    public IncubatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HAS_EGG, FACING);
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(HAS_EGG, Boolean.FALSE).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return state.getValue(CustomProperties.HAS_EGG);
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof IncubatorBlockEntity incubatorBlockEntity) {
            return incubatorBlockEntity.getComparatorOutput();
        }
        return 0;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return rotateShape(Direction.EAST, state.getValue(FACING), EAST_SHAPE);
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new IncubatorBlockEntity(blockPos, blockState);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEnttity = level.getBlockEntity(pos);
            if (blockEnttity instanceof IncubatorBlockEntity incubatorBlockEntity) {
                incubatorBlockEntity.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {

        if(level.getBlockEntity(pos) instanceof IncubatorBlockEntity incubatorBlockEntity) {
            if(incubatorBlockEntity.getItem(0).isEmpty() && incubatorBlockEntity.inventory.isItemValid(0,stack)) {
                incubatorBlockEntity.setItem(0,stack.copy());
                stack.shrink(1);
                if (level.isClientSide) {
                    level.playLocalSound(player, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);
                }
            } else if (stack.isEmpty() && !incubatorBlockEntity.inventory.getStackInSlot(0).isEmpty()) {
                ItemStack eggInIncubator = incubatorBlockEntity.removeItem(0, 1);
                player.setItemInHand(InteractionHand.MAIN_HAND, eggInIncubator);
                incubatorBlockEntity.clearContents();
                if (level.isClientSide) {
                    level.playLocalSound(player, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
                }
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public @org.jetbrains.annotations.Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if(level.isClientSide()) {
            return  null;
        }
        
        return createTickerHelper(blockEntityType, ModBlockEntities.INCUBATOR_BE.get(), (leveltick, blockPos, blockState, blockEntity) -> blockEntity.tick(leveltick, blockPos, blockState));
    }
}
