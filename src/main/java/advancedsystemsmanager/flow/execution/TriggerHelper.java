package advancedsystemsmanager.flow.execution;

import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.api.tileentities.IRedstoneNode;
import advancedsystemsmanager.api.tileentities.ITriggerNode;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.commands.CommandBase;
import advancedsystemsmanager.flow.menus.MenuContainer;
import advancedsystemsmanager.flow.menus.MenuRedstoneSides;
import advancedsystemsmanager.flow.menus.MenuRedstoneSidesTrigger;
import advancedsystemsmanager.registry.ConnectionOption;
import advancedsystemsmanager.util.SystemCoord;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;
import java.util.List;

public abstract class TriggerHelper
{
    public static final int TRIGGER_INTERVAL_ID = 2;

    public boolean canUseMergedDetection;
    public int containerId;
    public int sidesId;
    public ISystemType blockType;

    public TriggerHelper(boolean canUseMergedDetection, int containerId, int sidesId, ISystemType blockType)
    {
        this.canUseMergedDetection = canUseMergedDetection;
        this.containerId = containerId;
        this.sidesId = sidesId;
        this.blockType = blockType;
    }

    public abstract void onTrigger(FlowComponent item, EnumSet<ConnectionOption> valid);

    public boolean hasRedStoneFlipped(FlowComponent component, int[] newPower, int[] oldPower, boolean high)
    {
        MenuRedstoneSides menuRedstone = (MenuRedstoneSides)component.getMenus().get(sidesId);

        for (int i = 0; i < oldPower.length; i++)
        {
            if (menuRedstone.isSideRequired(i))
            {
                boolean power = isBlockPowered(component, newPower[i]);
                boolean old = isBlockPowered(component, oldPower[i]);
                if (high == power && power != old)
                {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isPulseReceived(FlowComponent component, int[] newPower, int[] oldPower, boolean high)
    {
        return hasRedStoneFlipped(component, newPower, oldPower, high) && isTriggerPowered(component, newPower, high);
    }

    public boolean isPulseReceived(FlowComponent component, List<SystemCoord> containers, ITriggerNode trigger, boolean high)
    {
        boolean requiresAll = trigger != null;
        for (SystemCoord container : containers)
        {
            ITriggerNode input = (ITriggerNode)container.getTileEntity();

            boolean flag;
            if (input.equals(trigger) || !requiresAll)
            {
                flag = isPulseReceived(component, input.getData(), input.getOldData(), high);
            } else
            {
                flag = isTriggerPowered(component, input.getData(), high);
            }

            if (flag)
            {
                if (!requiresAll)
                {
                    return true;
                }
            } else if (requiresAll)
            {
                return false;
            }
        }

        return requiresAll;
    }

    public boolean isSpecialPulseReceived(FlowComponent component, boolean high)
    {
        List<SlotInventoryHolder> containers = CommandExecutor.getContainers(component.getManager(), component.getMenus().get(containerId), blockType);

        if (containers != null)
        {
            MenuContainer componentMenuContainer = (MenuContainer)component.getMenus().get(containerId);

            boolean requiresAll = componentMenuContainer.getOption() == 0;
            boolean foundPulse = false;

            for (SlotInventoryHolder container : containers)
            {
                ITriggerNode input = container.getTrigger();


                boolean flag;

                flag = isPulseReceived(component, input.getData(), input.getOldData(), high);
                if (flag)
                {
                    foundPulse = true;
                } else
                {
                    flag = isTriggerPowered(component, input.getData(), high);
                }


                if (foundPulse)
                {
                    if (!requiresAll)
                    {
                        return true;
                    }
                } else if (requiresAll && !flag)
                {
                    return false;
                }
            }

            return requiresAll && foundPulse;
        } else
        {
            return false;
        }
    }

    public boolean isTriggerPowered(FlowComponent item, boolean high)
    {
        List<SystemCoord> receivers = CommandBase.getContainers(item.getManager(), (MenuContainer)item.getMenus().get(containerId));

        return receivers != null && isTriggerPowered(receivers, item, high);
    }

    public boolean isTriggerPowered(List<SystemCoord> receivers, FlowComponent component, boolean high)
    {
        MenuContainer menuContainer = (MenuContainer)component.getMenus().get(containerId);
        if (canUseMergedDetection && menuContainer.getOption() == 0)
        {
            int[] currentPower = new int[ForgeDirection.VALID_DIRECTIONS.length];
            for (SystemCoord receiver : receivers)
            {
                int[] data;
                if (receiver.getTileEntity() instanceof ITriggerNode)
                {
                    data = ((ITriggerNode)receiver.getTileEntity()).getData();
                } else
                {
                    data = ((IRedstoneNode)receiver.getTileEntity()).getPower();
                }
                for (int i = 0; i < currentPower.length; i++)
                {
                    currentPower[i] = Math.min(15, currentPower[i] + data[i]);
                }
            }

            return isTriggerPowered(component, currentPower, high);
        } else
        {
            boolean requiresAll = menuContainer.getOption() == 0 || (menuContainer.getOption() == 1 && canUseMergedDetection);
            for (SystemCoord receiver : receivers)
            {
                int[] data;
                if (receiver.getTileEntity() instanceof ITriggerNode)
                {
                    data = ((ITriggerNode)receiver.getTileEntity()).getData();
                } else
                {
                    data = ((IRedstoneNode)receiver.getTileEntity()).getPower();
                }

                if (isTriggerPowered(component, data, high))
                {
                    if (!requiresAll)
                    {
                        return true;
                    }
                } else
                {
                    if (requiresAll)
                    {
                        return false;
                    }
                }
            }
            return requiresAll;
        }
    }

    public boolean isTriggerPowered(FlowComponent component, int[] currentPower, boolean high)
    {
        MenuRedstoneSidesTrigger menuSides = (MenuRedstoneSidesTrigger)component.getMenus().get(sidesId);
        for (int i = 0; i < currentPower.length; i++)
        {
            if (menuSides.isSideRequired(i))
            {
                if (isBlockPowered(component, currentPower[i]) == high)
                {
                    if (!menuSides.requireAll())
                    {
                        return true;
                    }
                } else if (menuSides.requireAll())
                {
                    return false;
                }
            }
        }

        return menuSides.requireAll();
    }

    public abstract boolean isBlockPowered(FlowComponent component, int power);

    public void activateTrigger(FlowComponent item, EnumSet<ConnectionOption> types)
    {
        item.getManager().activateTrigger(item, types);
    }
}
