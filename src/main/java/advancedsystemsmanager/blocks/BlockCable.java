package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.ICable;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import advancedsystemsmanager.util.WorldCoordinate;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class BlockCable extends BlockBase implements ICable
{
    public BlockCable()
    {
        super(Names.CABLE, 0.4F);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);

        updateInventories(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        super.onNeighborBlockChange(world, x, y, z, block);

        updateInventories(world, x, y, z);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        super.breakBlock(world, x, y, z, block, meta);

        updateInventories(world, x, y, z);
    }

    public void updateInventories(World world, int blockX, int blockY, int blockZ)
    {
        List<WorldCoordinate> visited = new ArrayList<WorldCoordinate>();

        Queue<WorldCoordinate> queue = new PriorityQueue<WorldCoordinate>();
        WorldCoordinate start = new WorldCoordinate(blockX, blockY, blockZ, 0);
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty())
        {
            WorldCoordinate element = queue.poll();

            for (int x = -1; x <= 1; x++)
            {
                for (int y = -1; y <= 1; y++)
                {
                    for (int z = -1; z <= 1; z++)
                    {
                        if (Math.abs(x) + Math.abs(y) + Math.abs(z) == 1)
                        {
                            WorldCoordinate target = new WorldCoordinate(element.getX() + x, element.getY() + y, element.getZ() + z, element.getDepth() + 1);

                            if (!visited.contains(target))
                            {
                                visited.add(target);
                                //if (element.getDepth() < TileEntityManager.MAX_CABLE_LENGTH){
                                Block block = world.getBlock(target.getX(), target.getY(), target.getZ());
                                int meta = world.getBlockMetadata(target.getX(), target.getY(), target.getZ());
                                if (block == BlockRegistry.blockManager)
                                {
                                    TileEntity tileEntity = world.getTileEntity(target.getX(), target.getY(), target.getZ());
                                    if (tileEntity != null && tileEntity instanceof TileEntityManager)
                                    {
                                        ((TileEntityManager)tileEntity).updateInventories();
                                    }
                                } else if (isCable(block, meta) /*&& target.getDepth() < TileEntityManager.MAX_CABLE_LENGTH*/)
                                {
                                    queue.add(target);
                                }
                                //}
                            }
                        }
                    }
                }
            }

        }
    }

    public boolean isCable(Block block, int meta)
    {
        return block instanceof ICable && ((ICable)block).isCable(meta);
    }

    @Override
    public boolean isCable(int meta)
    {
        return true;
    }
}
