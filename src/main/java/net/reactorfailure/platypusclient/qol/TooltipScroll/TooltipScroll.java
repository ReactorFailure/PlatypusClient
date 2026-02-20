package net.reactorfailure.platypusclient.qol.TooltipScroll;


public class TooltipScroll {
    private static TooltipScroll INSTANCE;

    private int yOffset = 0;
    private static final int SCROLL_SPEED = 10; // Pixels to scroll per wheel notch

    private TooltipScroll() {}

    public static TooltipScroll getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TooltipScroll();
        }
        return INSTANCE;
    }

    public void scroll(double amount) {
        yOffset -= (int) (amount * SCROLL_SPEED);
    }


    public int getYOffset() {
        return yOffset;
    }


    public void reset() {
        if (yOffset != 0) {
            yOffset = 0;
        }
    }


    public void setYOffset(int offset) {
        this.yOffset = offset;
    }
}
