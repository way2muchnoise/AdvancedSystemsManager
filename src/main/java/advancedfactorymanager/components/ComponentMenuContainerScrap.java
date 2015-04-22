package advancedfactorymanager.components;

import advancedfactorymanager.helpers.Localization;
import advancedfactorymanager.interfaces.GuiManager;
import advancedfactorymanager.util.ConnectionBlockType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;


public class ComponentMenuContainerScrap extends ComponentMenuContainer
{
    public ComponentMenuContainerScrap(FlowComponent parent)
    {
        super(parent, ConnectionBlockType.INVENTORY);
    }

    @Override
    public String getName()
    {
        return Localization.OVERFLOW_MENU.toString();
    }

    public static final int MENU_WIDTH = 120;
    public static final int TEXT_MARGIN_X = 5;
    public static final int TEXT_Y = 25;

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        super.draw(gui, mX, mY);

        if (scrollController.getResult().isEmpty())
        {
            gui.drawSplitString(Localization.OVERFLOW_INFO.toString(), TEXT_MARGIN_X, TEXT_Y, MENU_WIDTH - TEXT_MARGIN_X * 2 - 20, 0.7F, 0x404040);
        }
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (selectedInventories.isEmpty())
        {
            errors.add(Localization.NO_OVERFLOW_ERROR.toString());
        }
    }

    @Override
    protected void initRadioButtons()
    {
        //no radio buttons
    }

    /*@Override
    protected String getDefaultSearch() {
        return "";
    }*/
}
