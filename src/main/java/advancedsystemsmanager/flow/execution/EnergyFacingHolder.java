package advancedsystemsmanager.flow.execution;

import cofh.api.energy.IEnergyProvider;
import net.minecraftforge.common.util.ForgeDirection;

public class EnergyFacingHolder
{
    public IEnergyProvider energyHandler;
    public ForgeDirection accessibleFace;

    public EnergyFacingHolder(IEnergyProvider energyHandler, ForgeDirection direction)
    {
        this.energyHandler = energyHandler;
        this.accessibleFace = direction;
    }

    public IEnergyProvider getEnergyProvider()
    {
        return energyHandler;
    }

    public ForgeDirection getAccessibleFace()
    {
        return accessibleFace;
    }

    public int extract(int amount, boolean simulate)
    {
        return energyHandler.extractEnergy(accessibleFace, amount, simulate);
    }
}
