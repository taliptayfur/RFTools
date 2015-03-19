package com.mcjty.rftools.blocks.spawner;

import com.mcjty.container.GenericContainerBlock;
import com.mcjty.container.WrenchUsage;
import com.mcjty.rftools.RFTools;
import com.mcjty.rftools.blocks.Infusable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class SpawnerBlock extends GenericContainerBlock implements Infusable {

    private IIcon iconTop;

    public SpawnerBlock() {
        super(Material.iron, SpawnerTileEntity.class);
        setBlockName("spawnerBlock");
        setCreativeTab(RFTools.tabRfTools);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
//        NBTTagCompound tagCompound = itemStack.getTagCompound();
//        if (tagCompound != null) {
//            String name = tagCompound.getString("tpName");
//            int id = tagCompound.getInteger("destinationId");
//            list.add(EnumChatFormatting.GREEN + "Name: " + name + (id == -1 ? "" : (", Id: " + id)));
//        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(EnumChatFormatting.WHITE + "This block can spawn creatures. It needs a syringe");
            list.add(EnumChatFormatting.WHITE + "of the appropriate type, RF power and also it");
            list.add(EnumChatFormatting.WHITE + "needs beams of energized matter.");
            list.add(EnumChatFormatting.YELLOW + "Infusing bonus: reduced power usage.");
        } else {
            list.add(EnumChatFormatting.WHITE + RFTools.SHIFT_MESSAGE);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        TileEntity te = accessor.getTileEntity();
        if (te instanceof SpawnerTileEntity) {
            SpawnerTileEntity spawnerTileEntity = (SpawnerTileEntity) te;
            int matter = spawnerTileEntity.getMatter();
            currenttip.add(EnumChatFormatting.GREEN + "Matter: " + matter);
        }
        return currenttip;
    }

    @Override
    public int getGuiID() {
        return RFTools.GUI_SPAWNER;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer createClientGui(EntityPlayer entityPlayer, TileEntity tileEntity) {
        SpawnerTileEntity spawnerTileEntity = (SpawnerTileEntity) tileEntity;
        SpawnerContainer spawnerContainer = new SpawnerContainer(entityPlayer, spawnerTileEntity);
        return new GuiSpawner(spawnerTileEntity, spawnerContainer);
    }

    @Override
    public Container createServerContainer(EntityPlayer entityPlayer, TileEntity tileEntity) {
        return new SpawnerContainer(entityPlayer, (SpawnerTileEntity) tileEntity);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float sx, float sy, float sz) {
        WrenchUsage wrenchUsed = testWrenchUsage(x, y, z, player);
        if (wrenchUsed == WrenchUsage.NORMAL) {
            if (world.isRemote) {
                SpawnerTileEntity spawnerTileEntity = (SpawnerTileEntity) world.getTileEntity(x, y, z);
                world.playSound(x, y, z, "note.pling", 1.0f, 1.0f, false);
                spawnerTileEntity.useWrench(player);
            }
            return true;
        } else if (wrenchUsed == WrenchUsage.SNEAKING) {
            breakAndRemember(world, x, y, z);
            return true;
        } else {
            return openGui(world, x, y, z, player);
        }
    }


    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        iconTop = iconRegister.registerIcon(RFTools.MODID + ":" + "machineSpawner");
        iconSide = iconRegister.registerIcon(RFTools.MODID + ":" + "machineSide");
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        if (side == ForgeDirection.UP.ordinal()) {
            return iconTop;
        } else {
            return iconSide;
        }
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == ForgeDirection.UP.ordinal()) {
            return iconTop;
        } else {
            return iconSide;
        }
    }

}