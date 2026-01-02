package top.yuhh.incubators.mixin;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity;
import ludichat.cobbreeding.EggUtilities;
import ludichat.cobbreeding.PastureBreedingData;
import ludichat.cobbreeding.PastureInventory;
import ludichat.cobbreeding.components.CobbreedingComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.yuhh.incubators.component.ModComponents;

@Mixin(PokemonPastureBlockEntity.class)
public abstract class PastureBlockBlockEntityMixin implements PastureInventory {

    @Inject(method = "TICKER$lambda$0", at = @At("TAIL"))
    private static void tick(Level world, BlockPos blockPos, BlockState blockState, PokemonPastureBlockEntity pokemonPastureBlockEntity, CallbackInfo ci) {
        PastureBreedingData data = PastureBreedingData.registry.get(blockPos);
        if (data != null) {
            NonNullList<ItemStack> eggs = data.getEggs();
            for (ItemStack egg : eggs) {
                if (!egg.has(ModComponents.INITIAL_TIMER)) {
                    egg.set(ModComponents.INITIAL_TIMER, egg.get(CobbreedingComponents.TIMER));
                }
            }
        }
    }
}