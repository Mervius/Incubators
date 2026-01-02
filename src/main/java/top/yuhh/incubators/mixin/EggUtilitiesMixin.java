package top.yuhh.incubators.mixin;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import ludichat.cobbreeding.EggUtilities;
import ludichat.cobbreeding.components.CobbreedingComponents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.yuhh.incubators.component.ModComponents;

@Mixin(EggUtilities.class)
public class EggUtilitiesMixin {

    @Inject(method = "getEggFromPokemonProperties$default", at = @At("RETURN"))
    private static void addInitialTimer(PokemonProperties par1, Integer par2, int par3, Object par4, CallbackInfoReturnable<ItemStack> cir) {
        cir.getReturnValue().set(ModComponents.INITIAL_TIMER, cir.getReturnValue().get(CobbreedingComponents.TIMER));
    }

}
