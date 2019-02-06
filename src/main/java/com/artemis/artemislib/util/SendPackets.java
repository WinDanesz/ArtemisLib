package com.artemis.artemislib.util;

import com.artemis.artemislib.network.NetworkHandler;
import com.artemis.artemislib.network.packets.CapDataMessage;
import com.artemis.artemislib.util.capability.CapPro;
import com.artemis.artemislib.util.capability.ICap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class SendPackets
{
	@SubscribeEvent
	public static void onTracking(StartTracking event)
	{
		if(event.getEntityPlayer() != null)
		{
			EntityPlayer player = event.getEntityPlayer();
			boolean client = player.world.isRemote;
			
			if((event.getTarget() != null) && (event.getTarget() instanceof EntityLivingBase) && event.getTarget().hasCapability(CapPro.sizeCapability, null))
			{
				EntityLivingBase entity = (EntityLivingBase) event.getTarget();
				ICap cap = entity.getCapability(CapPro.sizeCapability, null);
				
				if(entity instanceof EntityPlayer)
				{
					if(!client)
					{
						NetworkHandler.INSTANCE.sendTo(new CapDataMessage(cap.getSize(), cap.getTrans(), cap.getTarget(), cap.getWidth(), cap.getHeight(), cap.getDefaultWidth(), cap.getDefaultHeight(), entity.getEyeHeight(), entity.getEntityId()), (EntityPlayerMP)player);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void playerClone(PlayerEvent.Clone event)
	{
		if(event.getEntityPlayer().hasCapability(CapPro.sizeCapability, null))
		{
			final ICap oldCap = event.getOriginal().getCapability(CapPro.sizeCapability, null);
			final ICap newCap = event.getEntityPlayer().getCapability(CapPro.sizeCapability, null);
			
			if(event.isWasDeath())
			{
				if((event.getOriginal().world.getGameRules().getBoolean("keepInventory") == true))
				{
					if((newCap != null) && (oldCap != null))
					{
						newCap.setTrans(oldCap.getTrans());
						newCap.setTarget(oldCap.getTarget());
						newCap.setSize(oldCap.getSize());
						newCap.setWidth(oldCap.getWidth());
						newCap.setHeight(oldCap.getHeight());
						newCap.setDefaultWidth(oldCap.getDefaultWidth());
						newCap.setDefaultHeight(oldCap.getDefaultHeight());
						event.getEntityPlayer().eyeHeight = event.getOriginal().eyeHeight;
					}
				}
			}
		}
	}
}
