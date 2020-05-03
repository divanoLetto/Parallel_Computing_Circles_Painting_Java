import java.awt.Color;

public class Circle {
    private int x;
    private int y;
    private int z;
    private int r;
    private int id=-1;
    private int colorR;
    private int colorG;
    private int colorB;

        
    public Circle(int id, int x, int y,int z, int r1, int colorR, int colorG,int colorB) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r1;
        this.colorR=colorR;
        this.colorG=colorG;
        this.colorB=colorB;
    }
    
    public Circle(Circle c) {
        this.id=c.getId();
        this.x=c.getX();
        this.y=c.getY();
        this.z = c.getZ();
        this.r=c.getR();
        this.colorR=c.getColR();
        this.colorG=c.getColG();
        this.colorB=c.getColB();
    }
    
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getZ(){
        return z;
    }
    public int getId(){
        return id;
    }
    public int getR(){
        return r;
    }
    public int getColR(){
        return colorR;
    }
    public int getColG(){
        return colorG;
    }
    public int getColB(){
        return colorB;
    }
}

