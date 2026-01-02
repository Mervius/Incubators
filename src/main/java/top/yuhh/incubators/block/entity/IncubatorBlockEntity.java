package top.yuhh.incubators.block.entity;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.util.DataKeys;
import ludichat.cobbreeding.Cobbreeding;
import ludichat.cobbreeding.CustomProperties;
import ludichat.cobbreeding.EggUtilities;
import ludichat.cobbreeding.PokemonEgg;
import ludichat.cobbreeding.compat.CobblemonSizeVariations;
import ludichat.cobbreeding.components.CobbreedingComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.yuhh.incubators.Incubators;
import top.yuhh.incubators.block.IncubatorBlock;
import top.yuhh.incubators.component.ModComponents;

import java.util.List;
import java.util.UUID;

public class IncubatorBlockEntity extends BlockEntity implements WorldlyContainer {

    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return 1;
        }
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getItem() instanceof PokemonEgg;
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (!level.isClientSide()) {
                BlockState state = getBlockState();
                boolean hasEgg = !getStackInSlot(slot).isEmpty();

                if (state.getValue(CustomProperties.HAS_EGG) != hasEgg) {
                    BlockState updated = state.setValue(CustomProperties.HAS_EGG, hasEgg);
                    level.setBlock(getBlockPos(), updated, 3);
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), updated, 3);
                }
            }

            setChanged();
        }
    };

    public IncubatorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.INCUBATOR_BE.get(), pos, blockState);
    }

    public void clearContents() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for(int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        ItemStack stack = inventory.getStackInSlot(0);

        if(inventory.isItemValid(0, stack)) {
            int second = stack.getOrDefault(CobbreedingComponents.SECOND, PokemonEgg.DEFAULT_SECOND);
            stack.set(CobbreedingComponents.SECOND, second + 1);
            if (second < 20) {
                return;
            }
            stack.set(CobbreedingComponents.SECOND, 0);

            int timer = stack.getOrDefault(CobbreedingComponents.TIMER, PokemonEgg.DEFAULT_TIMER);
            stack.set(CobbreedingComponents.TIMER, Mth.clamp(timer - 20, 0, Integer.MAX_VALUE));
            setChanged();

            if(!level.isClientSide()) {
                if(timer <= 0) {
                    PokemonProperties pokemonProperties = EggUtilities.extractProperties(stack);
                    String species = pokemonProperties.getSpecies();

                    if(species != null && (PokemonSpecies.getByName(species) != null || species.equals("random")))
                    {
                        if (species.equals("random"))
                        {
                            FormData randomPokemon = EggUtilities.randomEgg();
                            pokemonProperties.setSpecies(randomPokemon.species.showdownId());
                            pokemonProperties.setForm(randomPokemon.formOnlyShowdownId());
                        }

                        resolveForm(pokemonProperties);
                        PokemonEntity entity = hatchEgg(level, blockPos, pokemonProperties);
                        if(entity != null) {
                            BlockPos front = blockPos.relative(level.getBlockState(blockPos).getValue(IncubatorBlock.FACING));
                            if(!level.collidesWithSuffocatingBlock(entity, entity.getDimensions(Pose.STANDING).makeBoundingBox(front.getBottomCenter()))) {
                                BlockState state = level.getBlockState(front.below());
                                if(!state.isAir() && (state.entityCanStandOn(level, front.below(), entity)) || state.entityCanStandOnFace(level, front.below(), entity, Direction.DOWN)) {
                                    entity.setPos(blockPos.relative(level.getBlockState(blockPos).getValue(IncubatorBlock.FACING)).getCenter());
                                    level.addFreshEntity(entity);
                                    clearContents();
                                }
                            }
                        }
                    }
                    else
                    {
                        Incubators.LOGGER.error("Couldn't resolve pokemon species to hatch, deleting egg.");
                        clearContents();
                    }

                }
            }

        }
    }

    private void resolveForm(PokemonProperties pokemonProperties) {
        if (pokemonProperties.getForm() != null) {
            Species species = pokemonProperties.getSpecies() != null ? PokemonSpecies.getByName(pokemonProperties.getSpecies()) : null;
            if (species != null) {
                species.getForms().stream()
                        .filter(form -> form.formOnlyShowdownId().equals(pokemonProperties.getForm()))
                        .findFirst()
                        .ifPresent(form -> {
                            for (String aspect : form.getAspects()) {
                                // alternative form
                                pokemonProperties.getCustomProperties().add(new FlagSpeciesFeature(aspect, true));
                                // regional bias
                                pokemonProperties.getCustomProperties().add(
                                        new StringSpeciesFeature(
                                                "region_bias",
                                                aspect.substring(aspect.lastIndexOf("-") + 1)
                                        )
                                );
                            }
                        });
            }
        }
    }

        private PokemonEntity hatchEgg(Level level, BlockPos pos, PokemonProperties properties) {
            try {
//                CobblemonEvents.HATCH_EGG_PRE.post(new HatchEggEvent.Pre(properties, (ServerPlayer) entity));
                Pokemon pokemon = properties.create();
//                pokemon.setFriendship(120);

                // CobblemonSizeVariations compat
                if (CobblemonSizeVariations.getEnabled()) {
                    if (CobblemonSizeVariations.getEvent() != null) {
                        CobblemonSizeVariations.getEvent().invoke(pokemon);
                    }
                }

                // Baby sheep have to grow their wool
                if (pokemon.getSpecies() == PokemonSpecies.getByName("wooloo") || pokemon.getSpecies() == PokemonSpecies.getByName("mareep")) {
                    FlagSpeciesFeature feature = pokemon.getFeature(DataKeys.HAS_BEEN_SHEARED);
                    if (feature != null) {
                        feature.setEnabled(true);
                        pokemon.markFeatureDirty(feature);
                        pokemon.updateAspects();
                    }
                }
                return new PokemonEntity(level, pokemon, CobblemonEntities.POKEMON);

                // Adds the hatched pokémon to the party/pc and registers it in the Pokédex
//                Cobblemon.storage.getParty(entity.getUUID(), entity.registryAccess()).add(pokemon);
//                Cobblemon.playerDataManager.getPokedexData(entity.getUUID()).obtain(pokemon);

//                CobblemonEvents.HATCH_EGG_POST.post(new HatchEggEvent.Post(entity, pokemon));
            } catch (Exception e) {
                Incubators.LOGGER.error("Egg hatching failed: {}\n{}", e.getMessage(), e.getStackTrace());
            }
            return null;
        }

    @Override
    public int getContainerSize() {
        return inventory.getSlots();
    }

    @Override
    public boolean isEmpty() {
        return inventory.getStackInSlot(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return inventory.extractItem(slot, amount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return inventory.extractItem(slot, 1, false);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.setStackInSlot(slot, stack);
        setChanged();

    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        clearContents();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if(side != getBlockState().getValue(IncubatorBlock.FACING)) {
            return new int[]{0};
        }
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        return isEmpty() && inventory.isItemValid(index, itemStack) && (direction != getBlockState().getValue(IncubatorBlock.FACING));
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return direction == Direction.DOWN;
    }

    public int getComparatorOutput() {
        ItemStack stack = inventory.getStackInSlot(0);
        if (stack.isEmpty()) {
            return 0;
        }return Mth.clamp((int)Math.ceil(15.0 * stack.getOrDefault(CobbreedingComponents.TIMER, 0)/stack.getOrDefault(ModComponents.INITIAL_TIMER, 0)), 0, 15);
    }
}
