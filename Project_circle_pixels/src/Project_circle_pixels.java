
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedDeque;


public class Project_circle_pixels {


    private static List<Circle> readCirclesFromFile(BufferedReader br, int numCicles, int currentExp) throws IOException{
        List<Circle> list = new ArrayList<>();
        int j =0;
        
        String line = br.readLine();
        while (j != numCicles) {
            j++;
            if(line!=null){
                String[] splited = line.split("\\s+");
                int id = Integer.parseInt(splited[0]);            
                int randomR = Integer.parseInt(splited[1]);            
                int randomX = Integer.parseInt(splited[2]); 
                int randomY = Integer.parseInt(splited[3]);  
                int randomZ = Integer.parseInt(splited[4]); 
                int randomColoR = Integer.parseInt(splited[5]); 
                int randomColoG = Integer.parseInt(splited[6]); 
                int randomColoB = Integer.parseInt(splited[7]); 
                Circle c = new Circle(id,randomX, randomY, randomZ, randomR,randomColoR, randomColoG, randomColoB);
                list.add(new Circle(c));
                line = br.readLine();
            }  
        }
        Collections.sort(list, new Comparator<Circle>() {
            public int compare(Circle p1, Circle p2) {return Integer.valueOf(p1.getZ()).compareTo(p2.getZ());}
        });
        
        return list;
    }
    
    public static void main(String [ ] args) throws FileNotFoundException, IOException
    {           
        int width = 1000;
        int height = 1000;
        int deep_z = 1000;
        List<Circle> listCircles= new ArrayList<>();
        
        int numExp = 10;
        int numCircles = 25000;
        int numThreads = 8;
        List<Integer> choices = new ArrayList<>();   
        choices.add(10000);

        for(int choice: choices){
            
            numCircles = choice;
            System.out.println("start main function with num circles= "+ numCircles+" and num threads= "+ numThreads+ " on image: "+width+"x"+height);
            String path = "/home/lorenzo/CLionProjects/Project_circle_pixels/test/test_"+ choice;
            BufferedReader br = new BufferedReader(new FileReader(path));
            List<Long> timesSeq = new ArrayList<>();
            List<Long> timesParaSections = new ArrayList<>();
            List<Long> timesParaLayers = new ArrayList<>();

            try {
                for(int e=0; e<numExp; e++){
                    //listCircles = generateCircles(numCircles, rangeSizeX, minimumX, rangeSizeY,minimumY, deep_z, rangeRadius, minimumRadius, rangeColor, minimumColor );
                    listCircles = readCirclesFromFile(br, numCircles, e);
                    Vector<Circle> vectorOfCircles = new Vector<Circle>(listCircles);  

                    long start_time_seq = System.currentTimeMillis();
                    paintSequential.drawCircles(listCircles, width, height);
                    long elapsed_time_seq = System.currentTimeMillis() - start_time_seq;
                    timesSeq.add(elapsed_time_seq);
                    
                    long start_time_sections = System.currentTimeMillis();
                    paintParallelSections.drawCircles(listCircles, width, height, numThreads);
                    long elapsed_time_sections = System.currentTimeMillis() - start_time_sections;
                    timesParaSections.add(elapsed_time_sections);
                    
                    long start_time_parallel_layers = System.currentTimeMillis();
                    paintParallelLayers.drawCircles(vectorOfCircles, width, height, numThreads);
                    long elapsed_time_parallel_layers = System.currentTimeMillis() - start_time_parallel_layers;
                    timesParaLayers.add(elapsed_time_parallel_layers);
                } 
            } finally {
                br.close();
            }
            
            long medSeq=0;
            long medPara1=0;
            long medPara2=0;
            for(int j=0;j<numExp;j++){
                medSeq += timesSeq.get(j);
                medPara1 += timesParaSections.get(j);
                medPara2 +=  timesParaLayers.get(j);
            }
            medSeq = medSeq/numExp;
            medPara1 = medPara1/numExp;
            medPara2 = medPara2/numExp;
            System.out.println("Time sequential version:               "+ medSeq);
            System.out.println("Time parallel sections pixels version: "+ medPara1);
            System.out.println("Time parallel layers pixel version:    "+ medPara2);
            float speedup1 = ((float)medSeq/(float)medPara1);
            float speedup2 = ((float)medSeq/(float)medPara2);
            System.out.println("Speedup medio sections:  "+ speedup1);
            System.out.println("Speedup medio layers:    "+ speedup2);
        }
    } 
}
