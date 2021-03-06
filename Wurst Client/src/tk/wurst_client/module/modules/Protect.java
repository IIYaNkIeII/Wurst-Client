/*
 * Copyright � 2014 - 2015 | Alexander01998 | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.module.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import tk.wurst_client.Client;
import tk.wurst_client.module.Category;
import tk.wurst_client.module.Module;
import tk.wurst_client.utils.EntityUtils;

public class Protect extends Module
{
	public Protect()
	{
		super(
			"Protect",
			"A bot that follows the closest entity and protects it.",
			Category.COMBAT);
	}
	
	private EntityLivingBase friend;
	private EntityLivingBase enemy;
	private float range = 6F;
	private double distanceF = 2D;
	private double distanceE = 3D;
	private float speed;
	
	@Override
	public String getRenderName()
	{
		if(friend != null)
			return "Protecting " + friend.getName();
		else
			return "Protect";
	}
	
	@Override
	public void onEnable()
	{
		friend = null;
		if(EntityUtils.getClosestEntity(false) != null)
		{
			EntityLivingBase en = EntityUtils.getClosestEntity(false);
			if(Minecraft.getMinecraft().thePlayer.getDistanceToEntity(en) <= range)
				friend = en;
		}
	}
	
	@Override
	public void onUpdate()
	{
		if(!getToggled())
			return;
		if(friend == null || friend.isDead || friend.getHealth() <= 0
			|| Minecraft.getMinecraft().thePlayer.getHealth() <= 0)
		{
			friend = null;
			enemy = null;
			setToggled(false);
			return;
		}
		if(enemy != null && (enemy.getHealth() <= 0 || enemy.isDead))
			enemy = null;
		double xDistF =
			Math.abs(Minecraft.getMinecraft().thePlayer.posX - friend.posX);
		double zDistF =
			Math.abs(Minecraft.getMinecraft().thePlayer.posZ - friend.posZ);
		double xDistE = distanceE;
		double zDistE = distanceE;
		if(enemy != null
			&& Minecraft.getMinecraft().thePlayer.getDistanceToEntity(enemy) <= range)
		{
			xDistE =
				Math.abs(Minecraft.getMinecraft().thePlayer.posX - enemy.posX);
			zDistE =
				Math.abs(Minecraft.getMinecraft().thePlayer.posZ - enemy.posZ);
		}else
			EntityUtils.faceEntityClient(friend);
		if((xDistF > distanceF || zDistF > distanceF)
			&& (enemy == null || Minecraft.getMinecraft().thePlayer
				.getDistanceToEntity(enemy) > range) || xDistE > distanceE
			|| zDistE > distanceE)
			Minecraft.getMinecraft().gameSettings.keyBindForward.pressed = true;
		else
			Minecraft.getMinecraft().gameSettings.keyBindForward.pressed =
				false;
		if(Minecraft.getMinecraft().thePlayer.isCollidedHorizontally
			&& Minecraft.getMinecraft().thePlayer.onGround)
			Minecraft.getMinecraft().thePlayer.jump();
		if(Minecraft.getMinecraft().thePlayer.isInWater()
			&& Minecraft.getMinecraft().thePlayer.posY < friend.posY)
			Minecraft.getMinecraft().thePlayer.motionY += 0.04;
		if(Client.wurst.moduleManager.getModuleFromClass(YesCheat.class)
			.getToggled())
			speed = Killaura.yesCheatSpeed;
		else
			speed = Killaura.normalSpeed;
		updateMS();
		if(hasTimePassedS(speed) && EntityUtils.getClosestEnemy(friend) != null)
		{
			enemy = EntityUtils.getClosestEnemy(friend);
			if(Minecraft.getMinecraft().thePlayer.getDistanceToEntity(enemy) <= range)
			{
				Criticals.doCritical();
				EntityUtils.faceEntityClient(enemy);
				Minecraft.getMinecraft().thePlayer.swingItem();
				Minecraft.getMinecraft().playerController.attackEntity(
					Minecraft.getMinecraft().thePlayer, enemy);
				updateLastMS();
			}
		}
	}
	
	@Override
	public void onDisable()
	{
		if(friend != null)
			Minecraft.getMinecraft().gameSettings.keyBindForward.pressed =
				false;
	}
}
