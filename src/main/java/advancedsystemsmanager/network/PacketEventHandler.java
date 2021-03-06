package advancedsystemsmanager.network;

import advancedsystemsmanager.api.network.IPacketBlock;
import advancedsystemsmanager.commands.ParentCommand;
import advancedsystemsmanager.containers.ContainerBase;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;

public class PacketEventHandler
{

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event)
    {
        handlePacket(event.packet.payload(), FMLClientHandler.instance().getClient().thePlayer, Side.CLIENT);
    }

    public void handlePacket(ByteBuf buffer, EntityPlayer player, Side side)
    {
        ASMPacket packet = new ASMPacket(buffer, side);
        if (player instanceof EntityPlayerMP) packet.setPlayer((EntityPlayerMP)player);
        int action = packet.readUnsignedByte();

        switch (action)
        {
            case PacketHandler.CONTAINER:
                int containerId = packet.readByte();
                Container container = player.openContainer;

                if (container != null && container.windowId == containerId && container instanceof ContainerBase)
                {
                    if (player instanceof EntityPlayerMP)
                    {
                        ((ContainerBase)container).updateServer(packet, (EntityPlayerMP)player);
                    } else
                    {
                        ((ContainerBase)container).updateClient(packet, player);
                    }

                }
                break;
            case PacketHandler.BLOCK:
                int x = packet.readInt();
                int y = packet.readUnsignedByte();
                int z = packet.readInt();

                TileEntity te = player.worldObj.getTileEntity(x, y, z);
                if (te != null && te instanceof IPacketBlock)
                {
                    int id = packet.readByte();
                    ((IPacketBlock)te).readData(packet, id);
                }
                break;
            case PacketHandler.COMMAND:
                ParentCommand.handlePacket(packet);
                break;
        }
    }

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event)
    {
        handlePacket(event.packet.payload(), ((NetHandlerPlayServer)event.handler).playerEntity, Side.SERVER);
    }
}
