package top.yuhh.incubators.item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.yuhh.incubators.Incubators;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Incubators.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}