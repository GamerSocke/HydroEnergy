package com.sinthoras.hydroenergy.proxy;

import com.sinthoras.hydroenergy.HE;
import com.sinthoras.hydroenergy.HECommand;
import com.sinthoras.hydroenergy.HEEventHandlerFML;
import com.sinthoras.hydroenergy.controller.HEControllerBlock;
import com.sinthoras.hydroenergy.hewater.HEWater;
import com.sinthoras.hydroenergy.hewater.HEWaterRenderer;
import com.sinthoras.hydroenergy.network.HEWaterUpdate;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class HECommonProxy {
	
	public static HEWater water = new HEWater();
	public static HEControllerBlock controller = new HEControllerBlock();
	
	// load "Do your mod setup. Build whatever data structures you care about. Register recipes."
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event) {
		HE.LOG = event.getModLog();

    	HE.network = NetworkRegistry.INSTANCE.newSimpleChannel("hydroenergy");
    	HE.network.registerMessage(HEWaterUpdate.Handler.class, HEWaterUpdate.class, 0, Side.CLIENT);

    	GameRegistry.registerBlock(water, water.getUnlocalizedName());
		GameRegistry.registerBlock(controller, controller.getUnlocalizedName());

    	RenderingRegistry.registerBlockHandler(HEWaterRenderer.instance);
	}
	
	// load "Do your mod setup. Build whatever data structures you care about. Register recipes."
	public void fmlLifeCycleEvent(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new HEEventHandlerFML());
	}
	
	// postInit "Handle interaction with other mods, complete your setup based on this."
	public void fmlLifeCycleEvent(FMLPostInitializationEvent event) {
		
	}
	
	public void fmlLifeCycleEvent(FMLServerAboutToStartEvent event) {
		
	}

	public void fmlLifeCycleEvent(FMLServerStartingEvent event) {
		event.registerServerCommand(new HECommand());
	}
	
	public void fmlLifeCycleEvent(FMLServerStartedEvent event) {
		
	}
	
	public void fmlLifeCycleEvent(FMLServerStoppingEvent event) {
		
	}
	
	public void fmlLifeCycleEvent(FMLServerStoppedEvent event) {
		
	}
}