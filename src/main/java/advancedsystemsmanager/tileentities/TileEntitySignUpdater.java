package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.flow.menus.MenuSignText;
import advancedsystemsmanager.util.ClusterMethodRegistration;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;


public class TileEntitySignUpdater extends TileEntityClusterElement
{
    @Override
    public EnumSet<ClusterMethodRegistration> getRegistrations()
    {
        return EnumSet.of(ClusterMethodRegistration.ON_BLOCK_PLACED_BY);
    }

    public void updateSign(MenuSignText menu)
    {
        ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[getMetadata() % ForgeDirection.VALID_DIRECTIONS.length];
        TileEntity te = worldObj.getTileEntity(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
        if (te != null && te instanceof TileEntitySign)
        {
            TileEntitySign sign = (TileEntitySign)te;
            //if (sign.func_142009_b() == null) {
            sign.func_145912_a(null);
            boolean updated = false;
            for (int i = 0; i < 4; i++)
            {
                if (menu.shouldUpdate(i))
                {
                    String oldText = sign.signText[i];
                    String newText = menu.getText(i);
                    if (!newText.equals(oldText))
                    {
                        sign.signText[i] = newText;
                        updated = true;
                    }
                }
            }
            if (updated)
            {
                sign.markDirty();
                worldObj.markBlockForUpdate(sign.xCoord, sign.yCoord, sign.zCoord);
            }

            //}
        }
    }
}
