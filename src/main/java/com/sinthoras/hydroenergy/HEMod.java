package com.sinthoras.hydroenergy;

import com.sinthoras.hydroenergy.hooks.HEHooksClient;
import com.sinthoras.hydroenergy.hooks.HEHooksShared;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;


@Mod(modid = HE.MODID, version = HE.VERSION, name = HE.NAME)
public class HEMod {

    @SidedProxy(clientSide=HE.COM_SINTHORAS_HYDROENERGY + ".hooks.HEHooksClient", serverSide=HE.COM_SINTHORAS_HYDROENERGY + ".hooks.HEHooksShared")
    public static HEHooksShared proxy;

    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items,
    // etc, and register them with the GameRegistry."
    public void fmlLifeCycleEvent(FMLPreInitializationEvent event) {
        HE.LOG.info("Registered sided proxy for: " + (proxy instanceof HEHooksClient ? "Client" : "Dedicated server"));
        HE.LOG.info("preInit()"+event.getModMetadata().name);
        proxy.fmlLifeCycleEvent(event);
    }

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes."
    public void fmlLifeCycleEvent(FMLInitializationEvent event) {
        HE.LOG.info("init()");
        proxy.fmlLifeCycleEvent(event);
    }

    @Mod.EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this."
    public void fmlLifeCycle(FMLPostInitializationEvent event) {
        HE.LOG.info("postInit()");
        proxy.fmlLifeCycleEvent(event);
    }

    @Mod.EventHandler
    public void fmlLifeCycle(FMLServerAboutToStartEvent event) {
        HE.LOG.info("Server about to start");
        proxy.fmlLifeCycleEvent(event);
    }

    @Mod.EventHandler
    // register server commands in this event handler
    public void fmlLifeCycle(FMLServerStartingEvent event) {
        HE.LOG.info("Server starting");
        proxy.fmlLifeCycleEvent(event);
    }

    @Mod.EventHandler
    public void fmlLifeCycle(FMLServerStartedEvent event) {
        HE.LOG.info("Server started");
        proxy.fmlLifeCycleEvent(event);
    }

    @Mod.EventHandler
    public void fmlLifeCycle(FMLServerStoppingEvent event) {
        HE.LOG.info("Server stopping");
        proxy.fmlLifeCycleEvent(event);
    }

    @Mod.EventHandler
    public void fmlLifeCycle(FMLServerStoppedEvent event) {
        HE.LOG.info("Server stopped");
        proxy.fmlLifeCycleEvent(event);
    }
}