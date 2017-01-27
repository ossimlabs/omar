package omar.geoscript

import geoscript.render.Image
import geoscript.render.Map as GeoScriptMap

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.ColorModel
import java.awt.image.IndexColorModel
import java.awt.image.WritableRaster

class TransparentGif extends Image
{
    TransparentGif()
    {
        super('gif')
    }

   @Override
    public BufferedImage render(GeoScriptMap map) {
        convertRGBAToIndexed(super.render(map))
    }

    BufferedImage convertRGBAToIndexed(BufferedImage src) {
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
        Graphics g = dest.getGraphics();
        g.setColor(new Color(231,20,189));
        g.fillRect(0, 0, dest.getWidth(), dest.getHeight()); //fill with a hideous color and make it transparent
        dest = makeTransparent(dest,0,0);
        dest.createGraphics().drawImage(src,0,0, null);
        return dest;
    }

    BufferedImage makeTransparent(BufferedImage image, int x, int y)
    {
        ColorModel cm = image.getColorModel();
        if (!(cm instanceof IndexColorModel))
            return image; //sorry...
        IndexColorModel icm = (IndexColorModel) cm;
        WritableRaster raster = image.getRaster();
        int pixel = raster.getSample(x, y, 0); //pixel is offset in ICM's palette
        int size = icm.getMapSize();
        byte[] reds = new byte[size];
        byte[] greens = new byte[size];
        byte[] blues = new byte[size];
        icm.getReds(reds);
        icm.getGreens(greens);
        icm.getBlues(blues);
        IndexColorModel icm2 = new IndexColorModel(8, size, reds, greens, blues, pixel);
        return new BufferedImage(icm2, raster, image.isAlphaPremultiplied(), null);
    }
}
