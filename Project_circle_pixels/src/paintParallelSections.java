import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

class MyDrawerThreadSections extends Thread{
        
    List<Circle> listOfCircles;
    int [] pixels;
    int idLay;
    int start_x;
    int end_x;
    int start_y;
    int end_y;
    int width;
    int numChannels;
    
    public MyDrawerThreadSections(List<Circle> circleList,int [] pixels, int start_x, int end_x, int start_y, int end_y,int width , int thread_id, int numChannels){
        this.listOfCircles = circleList;
        this.idLay = thread_id;
        this.start_x = start_x;
        this.end_x = end_x;
        this.start_y = start_y;
        this.end_y = end_y;
        this.width = width;
        this.pixels= pixels;
        this.numChannels=numChannels;
    }
    
    private static double distance(int a1, int a2, int b1, int b2){
        return Math.sqrt(Math.pow(a1-b1,2)+Math.pow(a2-b2,2));
    }
    
    @Override
    public void run(){
        int countC=0;
        int countCd=0;
        float alpha = (float)128/255;
        for(Circle c:listOfCircles) {
            countC++;
            int r = c.getR();
            int cx = c.getX();
            int cy = c.getY();
            if(!(cx>end_x || cx + 2*r < start_x || cy>end_y || cy + 2*r < start_y)) {
                for (int i = cx; i < cx + 2 * r; i++) {
                    for (int j = cy; j < cy + 2 * r; j++) {
                        if (i < end_x && j < end_y && i >= start_x && j >= start_y) {
                            if(distance(i, j, cx + r, cy + r) < (double) r ) {
                                countCd++;
                                int id = (i + j * width) * numChannels;
                                pixels[id] = (int)((float) c.getColR() * alpha + (float) pixels[id] * (1 - alpha));
                                pixels[id + 1] = (int)((float) c.getColG()* alpha + (float) pixels[id + 1] * (1 - alpha));
                                pixels[id + 2] = (int)((float) c.getColB() * alpha + (float) pixels[id + 2] * (1 - alpha));
                            }
                        }
                    }
                }
            }
        }
    }
}

public class paintParallelSections {
    public static void drawCircles(List<Circle> list,int width,int height, int numThread){
        
        int numChannels=4;
        int best_factors1=0;
        int best_factors2=0;
        List<Integer> factors = new ArrayList<>();
        int min_distance= Integer.MAX_VALUE;
        int [] pixels = new int[width*height*numChannels];
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB );
        for(int t=0; t<width*height*numChannels; t++){
            pixels[t]=255;
        }
        if(numThread==1){
            best_factors1=1;
            best_factors2=1;
        }
        else {
            for (int i = 1; i < numThread; i++) {
                if (numThread % i == 0) {
                    int distance = Math.abs(i - numThread / i);
                    if (distance < min_distance) {
                        min_distance = distance;
                        best_factors1 = i;
                        best_factors2 = numThread / i;
                    }
                }
            }
        }
        List<MyDrawerThreadSections> threads = new ArrayList<>();
        int start_x;
        int start_y;
        int end_x;
        int end_y;

        for(int i=0; i<numThread; i++){
            start_x = (i%best_factors1)*width/best_factors1;
            start_y = (int)Math.floor(i/best_factors1) * height / best_factors2;
            end_x = start_x + width/best_factors1;
            end_y = start_y + height/best_factors2;
            MyDrawerThreadSections t = new MyDrawerThreadSections(list, pixels, start_x, end_x,start_y, end_y, width, i, numChannels);
            threads.add(t);
            t.start();
        }
        for (MyDrawerThreadSections tr: threads){
            try{tr.join();}catch(Exception e){System.out.println("error at joining threads");}        
        }
        
        for ( int x = 0; x < width; x++ ) {
            for ( int y = 0; y < height; y++ ) {
                int id = (x + y * width) * numChannels;
                Color c = new Color(pixels[id],pixels[id+1], pixels[id+2], 255);
                img.setRGB(x, y, c.getRGB());
            }
        }
        File file = new File("java_parallel_sections_pixels.png");
        try{
            ImageIO.write(img, "png", file);
        }catch(Exception e){}
    }
}
