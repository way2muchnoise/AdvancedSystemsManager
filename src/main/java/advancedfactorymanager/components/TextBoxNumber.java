package advancedfactorymanager.components;


public class TextBoxNumber
{
    public static final int TEXT_BOX_SIZE_W = 21;
    public static final int TEXT_BOX_SIZE_W_WIDE = 33;

    public int x;
    public int y;
    public int number;
    public int length;
    public boolean wide;

    public TextBoxNumber(int x, int y, int length, boolean wide)
    {
        this.x = x;
        this.y = y;
        number = 0;
        this.length = length;
        this.wide = wide;
    }

    public int getLength()
    {
        return length;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        int max = getMaxNumber();
        if (max != -1 && number > max)
        {
            number = max;
        }
        int min = getMinNumber();
        if (number < min)
        {
            number = min;
        }

        this.number = number;
    }

    public boolean isVisible()
    {
        return true;
    }

    public boolean isWide()
    {
        return wide;
    }

    public void onNumberChanged()
    {
    }

    public int getWidth()
    {
        return wide ? TEXT_BOX_SIZE_W_WIDE : TEXT_BOX_SIZE_W;
    }

    public int getMaxNumber()
    {
        return -1;
    }

    public int getMinNumber()
    {
        return 0;
    }

    public final boolean allowNegative()
    {
        return getMinNumber() < 0;
    }

    public float getTextSize()
    {
        return 1;
    }

    public int getTextY()
    {
        return 3;
    }
}
