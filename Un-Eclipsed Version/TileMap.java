import java.io.*;
import java.util.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import javax.imageio.*;

public class TileMap{
    private int tileSize, mapWidth, mapHeight;
    private int x, y;
    private int[][] map;
    private Tile[][] tiles;
    private BufferedImage[] sprites;
    private BufferedImage spriteSheet;
    private Scanner sc;
    private File file;
    private Player player; 
    private int px, py;
    private int[][] pixelated;
    private PrintWriter out;

    //reads file of ints to determine which sprites to use
    public TileMap(Player p, String path, String sheetPath, int tileSize){
	this.player = p;
	this.tileSize = tileSize;
	try{
	    file = new File(path);
	    sc = new Scanner(file);
	}catch(Exception e){
	    e.printStackTrace();
	}
	
	//width and height provided in amount of tiles not pixels
	mapWidth = Integer.parseInt(sc.nextLine());
	mapHeight = Integer.parseInt(sc.nextLine());
	map = new int[mapHeight][mapWidth];
	tiles = new Tile[mapHeight][mapWidth];
	sprites = new BufferedImage[mapHeight * mapWidth];
	pixelated = new int[mapHeight *32][mapWidth * 32];
	//	System.out.println(Arrays.deepToString(pixelate));

	String toSplit = "\\s+";
	for(int i = 0; i < mapHeight; i++){
	    String line = sc.nextLine();
	    String[] elements = line.split(toSplit);
	    for(int j = 0; j < mapWidth; j++){
		map[i][j] = Integer.parseInt(elements[j]);
	    }
	}

	try{
	    out = new PrintWriter(new BufferedWriter(new FileWriter("out.txt")));
	}catch(Exception e){
	    e.printStackTrace();
	}

	Pixelator work = new Pixelator(tileSize, map);
	pixelated = work.work();
	out.print(Arrays.deepToString(pixelated));
	out.close();

	try{
	    spriteSheet = ImageIO.read(new File(sheetPath));
	}catch(Exception e){
	    e.printStackTrace();
	}
	divideSheet(spriteSheet);
    }

    public void collider(){
        //bot left corner
	double botleftx = player.getX();
	double botlefty = player.getY() + 32;
	System.out.println(pixelated[(int)botlefty][(int)botleftx]);
	System.out.println(botleftx + " " + botlefty);
	if(pixelated[(int)botlefty][(int)botleftx] == 1){
	    player.setMoveDown(false);
	    System.out.println("Collision: " + botleftx + " " + 
			       botlefty);
	}
    }

    //splits up sheet into smaller images to be used for tiles/entities
    public void divideSheet(BufferedImage sheet){
	int count = 0;
	int w = sheet.getWidth();
	int h = sheet.getHeight();
	for(int m = 0; m < (w - tileSize); m += tileSize + 1){
	    for(int n = 0; n < (h - tileSize); n += tileSize + 1){
		//System.out.println(m + ", " + n);
		sprites[count] = sheet.getSubimage(m, n, tileSize, tileSize);
		count++;
	    }
	}
    }

    //actually draws the tiles on the screen depending on the int[][]
    //made in the contructor
    public void drawTiles(Graphics2D g){
	x = 0;
	y = 0;
	int count = 0;
	int sprite = 0;
	boolean solid = false;
	for(int i = 0; i < mapHeight; i++){
	    x = 0;
	    for(int j = 0; j < mapWidth; j++){
		int type = map[i][j];
		if(type == 0){
		    g.drawImage(sprites[3], x, y, null);
		    sprite = 3;
		    solid = false;
		}
		else if(type == 1){
		    g.drawImage(sprites[0], x, y, null);
		    sprite = 0;
		    solid = true;
		}
		else if(type == 2){
		    g.drawImage(sprites[6], x, y, null);
		    sprite = 6; 
		    solid = true;
		}
		else if(type == 3){
		    g.drawImage(sprites[1], x, y, null);
		    sprite = 1;
		    solid = false;
		}
		else if(type == 4){
		    g.drawImage(sprites[4], x, y, null);
		    sprite = 4;
		    solid = false;
		}
		else if(type == 5){
		    g.drawImage(sprites[7], x, y, null);
		    sprite = 7;
		    solid = false;
		}
		tiles[i][j] = new Tile(x, y, tileSize, sprites[sprite],solid);
		x += tileSize; //move column
	    }
	    y += tileSize; //moves over row when 1 is finished (now it should start at 0,0)
	}
    }

    public int[][] getMap(){
	return map;
    }
    public static void main(String[] args) {

    }
}
