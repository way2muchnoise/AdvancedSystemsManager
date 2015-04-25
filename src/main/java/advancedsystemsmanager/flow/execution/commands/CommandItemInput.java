package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.*;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.buffers.buffers.Buffer;
import advancedsystemsmanager.flow.execution.buffers.buffers.items.ItemBufferSubElement;
import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.flow.setting.ItemSetting;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.util.ConnectionBlock;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

public class CommandItemInput extends CommandInput<ItemStack>
{
    public CommandItemInput()
    {
        super(ITEM_INPUT, Localization.INPUT_SHORT.toString(), Localization.INPUT_LONG.toString(), IBuffer.ITEM);
    }

    @Override
    public List<Menu> getMenus(FlowComponent component)
    {
        List<Menu> menus = component.getMenus();
        menus.add(new MenuInventory(component));
        menus.add(new MenuTargetInventory(component));
        menus.add(new MenuItem(component));
        return menus;
    }

    @Override
    protected IBuffer getNewBuffer()
    {
        return new Buffer<ItemStack>()
        {
            @Override
            public Key<ItemStack> getKey(ItemStack key)
            {
                return new Key.ItemKey(key);
            }
        };
    }

    @Override
    protected List<IBufferSubElement<ItemStack>> getBufferSubElements(int id, List<ConnectionBlock> blocks, List<Menu> menus)
    {
        MenuTargetInventory target = (MenuTargetInventory)menus.get(1);
        MenuItem settings = (MenuItem)menus.get(2);
        List<IBufferSubElement<ItemStack>> subElements = new ArrayList<IBufferSubElement<ItemStack>>();
        List<Integer> checkedSlots = new ArrayList<Integer>();
        for (ConnectionBlock block : blocks)
        {
            TileEntity entity = block.getTileEntity();
            if (entity instanceof IInternalInventory)
            {
                List<IBufferSubElement<ItemStack>> newSubElements = ((IInternalInventory)entity).getSubElements(id, settings);
                subElements.addAll(newSubElements);
            }else
            {
                IInventory inventory = (IInventory)entity;
                int maxSize = inventory.getSizeInventory();
                for (int i = 0; i < 6; i++)
                {
                    if (target.activatedDirections[i])
                    {
                        int start = target.useRangeForDirections[i] ? target.getStart(i) : 0;
                        int end = target.useRangeForDirections[i] ? target.getEnd(i) : maxSize;

                        int[] slots;
                        if (inventory instanceof ISidedInventory)
                        {
                            slots = ((ISidedInventory)inventory).getAccessibleSlotsFromSide(i);
                        } else
                        {
                            slots = new int[maxSize];
                            for (int j = 0; j < maxSize; slots[j] = j++) ;
                        }
                        scanSlots(id, inventory, checkedSlots, slots, settings, start, end, subElements);
                    }
                }
            }
        }
        return subElements;
    }

    private void scanSlots(int id, IInventory inventory, List<Integer> checked, int[] toScan, MenuItem settings, int start, int end, List<IBufferSubElement<ItemStack>> subElements)
    {
        for (int slot : toScan)
        {
            if (slot < start || checked.contains(slot)) continue;
            if (slot > end) return;
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) continue;
            if (isItemValid(settings.settings, stack, settings.isFirstRadioButtonSelected()))
                subElements.add(new ItemBufferSubElement(id, inventory, slot));
            checked.add(slot);
        }
    }

    private boolean isItemValid(List<Setting> settings, ItemStack stack, boolean whitelist)
    {
        for (Setting setting : settings) if (setting.isValid() && ((ItemSetting)setting).isEqualForCommandExecutor(stack)) return whitelist;
        return !whitelist;
    }
}
