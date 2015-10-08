package schemmer.hexagon.map;

import java.awt.Color;

public enum HexTypeColor {
	TYPE_FIELD(154,205,50),
	TYPE_HILL(233,150,122),
	TYPE_MOUNTAIN(176,224,230),
	TYPE_WATER(82, 63, 222),
	TYPE_DEEPWATER(0,0,128);

    private final int r;
    private final int g;
    private final int b;
    private final String rgb;

    private HexTypeColor(final int r,final int g,final int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.rgb = r + ", " + g + ", " + b;
    }

    public String getRGB() {
        return rgb;
    }

    public int getRed(){
        return r;
    }

    public int getGreen(){
        return g;
    }

    public int getBlue(){
        return r;
    }

    public Color getColor(){
        return new Color(r,g,b);
    }

    public int getARGB(){
        return 0xFF000000 | ((r << 16) & 0x00FF0000) | ((g << 8) & 0x0000FF00) | b;
    }
}
