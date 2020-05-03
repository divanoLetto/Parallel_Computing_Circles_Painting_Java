
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.imageio.ImageIO;

class MyDrawerThreadLayers extends Thread{    
    Vector<Circle> listOfCircles;
    int [] pixels;
    int [] sovrapp_matrices;
    int start_t;
    int end_t;
    int width;
    int height;
    int idLay;
    int numChannels;

    public MyDrawerThreadLayers(Vector<Circle> circleList,int [] pixels, int [] sovrapp_matrices, int start_t, int end_t, int width, int height, int num_thread, int numChannels){
        this.listOfCircles = circleList;
        this.pixels=pixels;
        this.sovrapp_matrices=sovrapp_matrices;
        this.start_t = start_t;
        this.end_t=end_t;
        this.width = width;
        this.height=height;
        this.idLay = num_thread;
        this.numChannels= numChannels;
    }
    private static double distance(int a1, int a2, int b1, int b2){
        return Math.sqrt(Math.pow(a1-b1,2)+Math.pow(a2-b2,2));   
    }
    
    @Override
    public void run(){
        float alpha = (float)128/255;
        for(int k = start_t; k<end_t; k++) {
            Circle c = listOfCircles.get(k);
            int r = c.getR();
            int cx = c.getX();
            int cy = c.getY();
            for (int i = cx; i <cx + 2*r; i++) {
                for (int j = cy; j < cy + 2*r; j++) {
                    if (distance(i,j,cx+r,cy+r)< r && i<width && j<height && i>=0 && j>=0){
                        int id = (i + j * width) * numChannels;
                        sovrapp_matrices[id]++;
                        sovrapp_matrices[id+1]++;
                        sovrapp_matrices[id+2]++;

                        if(pixels[id]==-1) {
                            pixels[id] = (int) ((float) c.getColR() * alpha );
                            pixels[id + 1] = (int) ((float) c.getColG() * alpha);
                            pixels[id + 2] = (int) ((float) c.getColB() * alpha);
                        }
                        else {
                            pixels[id] = (int) ((float) c.getColR() * alpha + (float) pixels[id] * (1 - alpha));
                            pixels[id + 1] = (int) ((float) c.getColG() * alpha + (float) pixels[id + 1] * (1 - alpha));
                            pixels[id + 2] = (int) ((float) c.getColB() * alpha + (float) pixels[id + 2] * (1 - alpha));
                        }
                    }
                }
            }
        }
    }
}

public class paintParallelLayers {
    public static void drawCircles(Vector<Circle> list,int width,int height, int numThread){
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB );
        int numChannels=4;
        float alpha = (float) 128 / 255;
        int [][] layers_pixels = new int[numThread][];
        int [][] layers_sovrapp_matrices = new int[numThread][];
        int [] image_pixels = new int[width*height*numChannels];
        int [] image_sovrapp_matrix  = new int[width*height*numChannels];
        for (int i = 0; i < numThread; ++i) {
            layers_pixels[i]=new int[width * height * numChannels];
            layers_sovrapp_matrices[i]=new int[width * height * numChannels];
            for (int j = 0; j < width * height * numChannels; j++) {
                layers_pixels[i][j] = -1;
                layers_sovrapp_matrices[i][j]=0;
                image_sovrapp_matrix[j]=0;
            }
        }
        List<MyDrawerThreadLayers> threads = new ArrayList<>();
        int start_t=0;
        int end_t;
        int num_circle_per_thread = (int)(list.size()/numThread);
        int rest = (int)list.size()%numThread;

        for (int k=0; k<numThread; k++){
            end_t = start_t + num_circle_per_thread ;
            if(k<rest){
                end_t++;
            }
            MyDrawerThreadLayers t = new MyDrawerThreadLayers(list, layers_pixels[k], layers_sovrapp_matrices[k], start_t, end_t, width, height, k, numChannels);
            threads.add(t);
            t.start();
            start_t = end_t;
        }
        for (MyDrawerThreadLayers tr: threads){
            try{tr.join();}catch(Exception e){System.out.println("error at joining threads");}        
        }
        for(int i=0; i<numThread; i++) {
            for (int id = 0; id < width * height * numChannels; id++) {
                image_sovrapp_matrix[id]+=layers_sovrapp_matrices[i][id];
            }
        }
        for(int i=0; i<numThread; i++){
            for(int id = 0; id< width * height * numChannels; id++){
                if(i==0){
                    image_pixels[id]= (int)(255 * Math.pow(1 - alpha, image_sovrapp_matrix[id]));
                }
                if(layers_pixels[i][id] != -1 && layers_pixels[i][id] != 255) {
                    image_sovrapp_matrix[id]-=layers_sovrapp_matrices[i][id];
                    image_pixels[id] += (int) ((float) layers_pixels[i][id] * Math.pow(1 - alpha, image_sovrapp_matrix[id]));
                }
            }
        }
        for ( int x = 0; x < width; x++ ) {
            for ( int y = 0; y < height; y++ ) {
                int id = (x + y * width) * numChannels;
                Color c = new Color(image_pixels[id],image_pixels[id+1], image_pixels[id+2], 255);
                img.setRGB(x, y, c.getRGB());
            }
        }
        File file = new File("java_parallel_layers_pixels.png");
        try{
            ImageIO.write(img, "png", file);
        }catch(Exception e){}
    }
}
