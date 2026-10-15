package eatawesome.inventoryexchangepixelmon.events;

import eatawesome.inventoryexchange.utils.PlayerHelper;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;

import java.math.BigInteger;
import java.util.List;
import com.pixelmonmod.pixelmon.api.events.ApricornEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent.SuccessfulCapture;
//import com.pixelmonmod.pixelmon.api.events.CaptureEvent.SuccessfulRaidCapture;
import com.pixelmonmod.pixelmon.api.events.DropEvent;
//import com.pixelmonmod.pixelmon.api.events.ExperienceGainEvent;
import com.pixelmonmod.pixelmon.api.events.PickupEvent;
import com.pixelmonmod.pixelmon.api.events.PixelmonKnockoutEvent;
import com.pixelmonmod.pixelmon.api.events.PokeLootEvent;
import com.pixelmonmod.pixelmon.api.events.PokeStopEvent;
import com.pixelmonmod.pixelmon.api.events.ShopEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DroppedItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;

public class PixelmonListener {

    public PixelmonListener() {
    }
    
    /*
    //Fails when lvl:100
    //Doesn't include Exp from catching
    @SubscribeEvent
    public void onExperienceGain(ExperienceGainEvent event) {
        if (event.isFromBattle()) {
            ServerPlayer serverPlayer = event.pokemon.getOwnerPlayer();
            if (serverPlayer != null) {
            	Pokemon pokemon = event.getFaintedPokemon();
            	int cp = calculateCP(pokemon);
                IKnowledgeProvider knowledge = serverPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
                BigInteger currentEmc = knowledge.getEmc();
                BigInteger newEmc = currentEmc.add(BigInteger.valueOf(cp));
                knowledge.setEmc(newEmc);
                knowledge.syncEmc(serverPlayer);
                Component message = Component.literal("Earned " + String.valueOf(cp) + " EMC for defeating a level " + String.valueOf(pokemon.getPokemonLevel()) + " (" + String.valueOf(cp) + " CP) " + pokemon.getSpecies().getTranslatedName().getString());
                serverPlayer.sendSystemMessage(message);
            }
        }
    }*/
    
    @SubscribeEvent
    public void onKnockout(PixelmonKnockoutEvent.Post event) {
    	ServerPlayer serverPlayer = event.getSource().getOwnerPlayer();
    	Pokemon pokemon = event.getPokemon();
    	int cp = calculateCP(pokemon);
        IKnowledgeProvider knowledge = serverPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
        BigInteger currentEmc = knowledge.getEmc();
        BigInteger newEmc = currentEmc.add(BigInteger.valueOf(cp));
        knowledge.setEmc(newEmc);
        knowledge.syncEmc(serverPlayer);
        Component message = Component.literal("Earned " + String.valueOf(cp) + " EMC for defeating a level " + String.valueOf(pokemon.getPokemonLevel()) + " (" + String.valueOf(cp) + " CP) " + pokemon.getSpecies().getTranslatedName().getString());
        serverPlayer.sendSystemMessage(message);
    }
    
    @SubscribeEvent
    public void onCapture(SuccessfulCapture event) {
        ServerPlayer serverPlayer = event.getPlayer();
        Pokemon pokemon = event.getPokemon();
        int cp = calculateCP(pokemon);
        IKnowledgeProvider knowledge = serverPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
        BigInteger currentEmc = knowledge.getEmc();
        BigInteger newEmc = currentEmc.add(BigInteger.valueOf(cp * 5));
        knowledge.setEmc(newEmc);
        knowledge.syncEmc(serverPlayer);
        Component message = Component.literal("Earned " + String.valueOf(cp * 5) + " EMC for catching a level " + String.valueOf(pokemon.getPokemonLevel()) + " (" + String.valueOf(cp) + " CP) " + pokemon.getSpecies().getTranslatedName().getString());
        serverPlayer.sendSystemMessage(message);
    }
    
    /*
    //Duplicated with onCapture
    @SubscribeEvent
    public void onRaidCapture(SuccessfulRaidCapture event) {
        ServerPlayer serverPlayer = event.getPlayer();
        Pokemon pokemon = event.getRaidPokemon();
        int cp = calculateCP(pokemon);
        IKnowledgeProvider knowledge = serverPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
        BigInteger currentEmc = knowledge.getEmc();
        BigInteger newEmc = currentEmc.add(BigInteger.valueOf(cp * 10));
        knowledge.setEmc(newEmc);
        knowledge.syncEmc(serverPlayer);
        Component message = Component.literal("Earned " + String.valueOf(cp * 10) + " EMC for catching a level " + String.valueOf(pokemon.getPokemonLevel()) + " (" + String.valueOf(cp) + " CP) " + pokemon.getSpecies().getTranslatedName().getString());
        serverPlayer.sendSystemMessage(message);
    }*/
    
    @SubscribeEvent
    public void onApricornPick(ApricornEvent.Pick event) {
        ServerPlayer serverPlayer = event.getPlayer();
        ItemStack itemStack = event.getPickedStack();
        PlayerHelper.knowledgeCheck(serverPlayer, itemStack);
    }
    
    @SubscribeEvent
    public void onDrop(DropEvent event) {
        ServerPlayer serverPlayer = event.player;
        for (DroppedItem droppedItem : event.getDrops()) {
            ItemStack itemStack = droppedItem.item;
            PlayerHelper.knowledgeCheck(serverPlayer, itemStack);
        }
    }
    
    @SubscribeEvent
    public void onPickup(PickupEvent event) {
        ServerPlayer serverPlayer = event.player.player;
        ItemStack itemStack = event.stack;
        PlayerHelper.knowledgeCheck(serverPlayer, itemStack);
    }
    
    @SubscribeEvent
    public void onPokeLoot(PokeLootEvent.GetDrops event) {
        ServerPlayer serverPlayer = event.player;
        ItemStack[] itemStacks = event.getDrops();
        for (ItemStack itemStack : itemStacks) {
            PlayerHelper.knowledgeCheck(serverPlayer, itemStack);
        }
    }
    
    @SubscribeEvent
    public void onPokeStop(PokeStopEvent.Drops.Post event) {
        Player player = event.getPlayer();
        if (player instanceof ServerPlayer serverPlayer) {
            List<ItemStack> itemStacks = event.getDrops();
            for (ItemStack itemStack : itemStacks) {
                PlayerHelper.knowledgeCheck(serverPlayer, itemStack);
            }
        }
    }
    
    @SubscribeEvent
    public void onShopPurchase(ShopEvent.Purchase event) {
        ServerPlayer serverPlayer = event.getPlayer();
        ItemStack itemStack = event.getItem();
        PlayerHelper.knowledgeCheck(serverPlayer, itemStack);
    }
    
    private int calculateCP(Pokemon pokemon) {
        double hp = pokemon.getStats().getHP();
        double attack = pokemon.getStats().getAttack();
        double defense = pokemon.getStats().getDefense();
        double specialAttack = pokemon.getStats().getSpecialAttack();
        double specialDefense = pokemon.getStats().getSpecialDefense();
        double speed = pokemon.getStats().getSpeed();
        double level = pokemon.getPokemonLevel();

        double cp = ((hp + attack + defense + specialAttack + specialDefense + speed) * level * 6) / 100;

        return (int) Math.floor(cp);
    }
}