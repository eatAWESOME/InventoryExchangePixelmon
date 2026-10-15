package eatawesome.inventoryexchangepixelmon;

import com.pixelmonmod.pixelmon.Pixelmon;

import eatawesome.inventoryexchangepixelmon.events.PixelmonListener;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(InventoryExchangePixelmon.MOD_ID)
public class InventoryExchangePixelmon {

    public static final String MOD_ID = "inventoryexchangepixelmon";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    
    private static InventoryExchangePixelmon instance;
    
    public InventoryExchangePixelmon(IEventBus modEventBus) {
        instance = this;

        modEventBus.addListener(InventoryExchangePixelmon::onModLoad);
    }

    public static void onModLoad(FMLCommonSetupEvent event) {
        Pixelmon.EVENT_BUS.register(new PixelmonListener());
    }
    
    public static InventoryExchangePixelmon getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
