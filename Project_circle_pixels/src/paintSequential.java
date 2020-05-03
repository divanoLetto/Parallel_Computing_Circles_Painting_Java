import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;

public class paintSequential {
    
    private static double distance(int a1, int a2, int b1, int b2){
         return Math.sqrt(Math.pow(a1-b1,2)+Math.pow(a2-b2,2));
    }
    
    public static void drawCircles(List<Circle> list,int width,int height){
        
        long start_time_init = System.currentTimeMillis();
        int countC=0;
        int countCd=0;
        int countP=0;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB );
        float alpha=(float)128/255;
        int numChannels = 4;
        int [] pixels = new int[width*height*numChannels];
        for(int t=0; t<width*height*numChannels; t++){
            pixels[t]=255;
        }
        
        for(Circle c: list){
            countC++;
            int r = c.getR();
            int cx = c.getX();
            int cy = c.getY();
            for (int i = cx; i <cx + 2*r; i++) {
                for (int j = cy; j < cy+2*r; j++) {
                    if(i<width && j<height && i>=0 && j>=0){
                        if (distance(i,j,cx+r,cy+r)< (double)r ){
                            countCd++;
                            int id = (i + j * width) * numChannels;
                            pixels[id] = (int)((float)c.getColR()*alpha + (float)pixels[id]*(1-alpha));
                            pixels[id+1] = (int)((float)c.getColG()*alpha + (float)pixels[id+1]*(1-alpha));
                            pixels[id+2] = (int)((float)c.getColB()*alpha + (float)pixels[id+2]*(1-alpha));
                        }
                    }
                }
            }
        }
        for ( int x = 0; x < width; x++ ) {
            for ( int y = 0; y < height; y++ ) {
                int id = (x + y * width) * numChannels;
                Color c = new Color(pixels[id],pixels[id+1], pixels[id+2], 255);
                img.setRGB(x, y, c.getRGB());
            }
        }

        File file = new File("java_sequential_pixels.png");
        try{
            ImageIO.write(img, "png", file);
        }catch(Exception e){}
        
    }
}
