package advancedfactorymanager.blocks;


import advancedfactorymanager.AdvancedFactoryManager;
import advancedfactorymanager.registry.ModBlocks;
import advancedfactorymanager.tileentities.TileEntityClusterElement;
import advancedfactorymanager.tileentities.TileEntityIntake;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

//This is indeed not a subclass to the cable, you can't relay signals through this block
public class BlockCableIntake extends BlockCableDirectionAdvanced
{

    public BlockCableIntake()
    {
        super(AdvancedFactoryManager.UNLOCALIZED_START + ModBlocks.CABLE_INTAKE_NAME_TAG);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityIntake();
    }


    @Override
    protected String getFrontTextureName(boolean isAdvanced)
    {
        return isAdvanced ? "cable_intake_out_instant" : "cable_intake_out";
    }

    @Override
    protected String getSideTextureName(boolean isAdvanced)
    {
        return isAdvanced ? "cable_intake_instant" : "cable_intake";
    }

    @Override
    protected Class<? extends TileEntityClusterElement> getTeClass()
    {
        return TileEntityIntake.class;
    }

}
