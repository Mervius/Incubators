package top.yuhh.incubators.mixin;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.yuhh.incubators.component.ModComponents;

@Mixin(ludichat.cobbreeding.PokemonEgg.class)
public class PokemonEggMixin{
    @Unique
    private DataComponentType<Integer> incubators$initialTimer = ModComponents.INITIAL_TIMER.get();
}
