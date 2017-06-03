import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Collections; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class photoLoader_output extends PApplet {

// 6 locations for the images
PImage[] images = new PImage[6];
String[] fileLocations;

String path = "/Users/hughsato/Documents/Processing/photoLoader_output/data/";

public void setup() {
  background(0);
  
  imageMode(CENTER);

  loadFiles();
  loadFilesToImages();

  frameRate(30);
  brushSetup();
  
}

public void draw() {
  background(0);
  // check for updates on the files
  if (second() % 10 == 0) {
    loadFiles();
    loadFilesToImages();
  }

  drawImages();
  brushDraw();
}

public void drawImages() {
  scale(2);
  imageMode(CORNER);
  for (int i = 0; i <= images.length-1; i++) {
    switch (i) {
      case 0:
        image(maskImg(images[i], 0), 0, 0);
        break;
      case 1:
        image(maskImg(images[i], 1), 219.618f, 0);
        break;
      case 2:
        image(maskImg(images[i], 0), 259.808f, 150);
        break;
      case 3:
        image(maskImg(images[i], 1), 219.618f, 300);
        break;
      case 4:
        image(maskImg(images[i], 0), 0, 300);
        break;
      case 5:
        image(maskImg(images[i], 1), -40, 150);
        break;
      default:
        break;
    }
  }
}
ArrayList <Mover> bouncers;

int bewegungsModus = 5;

boolean transparentBG = true;


public void brushSetup ()
{
  bouncers = new ArrayList();

  for (int i = 0; i < 10; i++)
  {
    Mover m = new Mover();
    bouncers.add (m);
  }
}

public void brushDraw ()
{
  if (transparentBG)
  {
    fill (0xff57385c, 40);
    noStroke();
    rect (0, 0, width, height);
  } 
  else background (0xff57385c);


  int i = 0;
  while (i < bouncers.size () )
  {
    Mover m = bouncers.get(i);
    if (bewegungsModus != 5) m.update (bewegungsModus);
    else
    {
      m.flock (bouncers);
      m.move();
      m.checkEdges();
      m.display();
    }

    i = i + 1;
  }
}

class Mover
{
  PVector direction;
  PVector location;

  float speed;
  float SPEED;

  float noiseScale;
  float noiseStrength;
  float forceStrength;

  float ellipseSize;
  
  int c;


  Mover () // Konstruktor = setup der Mover Klasse
  {
    setRandomValues();
  }

  Mover (float x, float y) // Konstruktor = setup der Mover Klasse
  {
    setRandomValues ();
  }

  // SET ---------------------------

  public void setRandomValues ()
  {
    location = new PVector (random (width), random (height));
    ellipseSize = random (4, 15);

    float angle = random (TWO_PI);
    direction = new PVector (cos (angle), sin (angle));

    speed = random (4, 7);
    SPEED = speed;
    noiseScale = 80;
    noiseStrength = 1;
    forceStrength = random (0.1f, 0.2f);
    
    setRandomColor();
  }

  public void setRandomColor ()
  {
    int colorDice = (int) random (4);
    //{#1A7DAA, #000069, #FF9403, #FF1926, #3BC6A7};
    if (colorDice == 0) c = 0xff1A7DAA;
    else if (colorDice == 1) c = 0xff000069;
    else if (colorDice == 2) c = 0xffFF9403;
    else c = 0xff3BC6A7;
  }

  // GENEREL ------------------------------

  public void update ()
  {
    update (0);
  }

  public void update (int mode)
  {
    if (mode == 0) // bouncing ball
    {
      speed = SPEED * 0.7f;
      move();
      checkEdgesAndBounce();
    }
    else if (mode == 1) // noise
    {
      speed = SPEED * 0.7f;
      addNoise ();
      move();
      checkEdgesAndRelocate ();
    }
    else if (mode == 2) // steer
    {
      steer (mouseX, mouseY);
      move();
    }
    else if (mode == 3) // seek
    {
      speed = SPEED * 0.7f;
      seek (mouseX, mouseY);
      move();
    }
    else // radial
    {
      speed = SPEED * 0.7f;
      addRadial ();
      move();
      checkEdges();
    }

    display();
  }

  // FLOCK ------------------------------

  public void flock (ArrayList <Mover> boids)
  {

    PVector other;
    float otherSize ;

    PVector cohesionSum = new PVector (0, 0);
    float cohesionCount = 0;

    PVector seperationSum = new PVector (0, 0);
    float seperationCount = 0;

    PVector alignSum = new PVector (0, 0);
    float speedSum = 0;
    float alignCount = 0;

    for (int i = 0; i < boids.size(); i++)
    {
      other = boids.get(i).location;
      otherSize = boids.get(i).ellipseSize;

      float distance = PVector.dist (other, location);


      if (distance > 0 && distance <70) //align + cohesion
      {
        cohesionSum.add (other);
        cohesionCount++;

        alignSum.add (boids.get(i).direction);
        speedSum += boids.get(i).speed;
        alignCount++;
      }

      if (distance > 0 && distance < (ellipseSize+otherSize)*1.2f) // seperate bei collision
      {
        float angle = atan2 (location.y-other.y, location.x-other.x);

        seperationSum.add (cos (angle), sin (angle), 0);
        seperationCount++;
      }

      if (alignCount > 8 && seperationCount > 12) break;
    }

    // cohesion: bewege dich in die Mitte deiner Nachbarn
    // seperation: renne nicht in andere hinein
    // align: bewege dich in die Richtung deiner Nachbarn

    if (cohesionCount > 0)
    {
      cohesionSum.div (cohesionCount);
      cohesion (cohesionSum, 1);
    }

    if (alignCount > 0)
    {
      speedSum /= alignCount;
      alignSum.div (alignCount);
      align (alignSum, speedSum, 1.3f);
    }

    if (seperationCount > 0)
    {
      seperationSum.div (seperationCount);
      seperation (seperationSum, 2);
    }
  }

  public void cohesion (PVector force, float strength)
  {
    steer (force.x, force.y, strength);
  }

  public void seperation (PVector force, float strength)
  {
    force.limit (strength*forceStrength);

    direction.add (force);
    direction.normalize();

    speed *= 1.1f;
    speed = constrain (speed, 0, SPEED * 1.5f);
  }

  public void align (PVector force, float forceSpeed, float strength)
  {
    speed = lerp (speed, forceSpeed, strength*forceStrength);

    force.normalize();
    force.mult (strength*forceStrength);

    direction.add (force);
    direction.normalize();
  }

  // HOW TO MOVE ----------------------------

  public void steer (float x, float y)
  {
    steer (x, y, 1);
  }

  public void steer (float x, float y, float strength)
  {

    float angle = atan2 (y-location.y, x -location.x);

    PVector force = new PVector (cos (angle), sin (angle));
    force.mult (forceStrength * strength);

    direction.add (force);
    direction.normalize();

    float currentDistance = dist (x, y, location.x, location.y);

    if (currentDistance < 70)
    {
      speed = map (currentDistance, 0, 70, 0, SPEED);
    }
    else speed = SPEED;
  }

  public void seek (float x, float y)
  {
    seek (x, y, 1);
  }

  public void seek (float x, float y, float strength)
  {

    float angle = atan2 (y-location.y, x -location.x);

    PVector force = new PVector (cos (angle), sin (angle));
    force.mult (forceStrength * strength);

    direction.add (force);
    direction.normalize();
  }

  public void addRadial ()
  {

    float m = noise (frameCount / (2*noiseScale));
    m = map (m, 0, 1, - 1.2f, 1.2f);

    float maxDistance = m * dist (0, 0, width/2, height/2);
    float distance = dist (location.x, location.y, width/2, height/2);

    float angle = map (distance, 0, maxDistance, 0, TWO_PI);

    PVector force = new PVector (cos (angle), sin (angle));
    force.mult (forceStrength);

    direction.add (force);
    direction.normalize();
  }

  public void addNoise ()
  {

    float noiseValue = noise (location.x /noiseScale, location.y / noiseScale, frameCount / noiseScale);
    noiseValue*= TWO_PI * noiseStrength;

    PVector force = new PVector (cos (noiseValue), sin (noiseValue));
    //Processing 2.0:
    //PVector force = PVector.fromAngle (noiseValue);
    force.mult (forceStrength);
    direction.add (force);
    direction.normalize();
  }

  // MOVE -----------------------------------------

  public void move ()
  {

    PVector velocity = direction.get();
    velocity.mult (speed);
    location.add (velocity);
  }

  // CHECK --------------------------------------------------------

  public void checkEdgesAndRelocate ()
  {
    float diameter = ellipseSize;

    if (location.x < -diameter/2)
    {
      location.x = random (-diameter/2, width+diameter/2);
      location.y = random (-diameter/2, height+diameter/2);
    }
    else if (location.x > width+diameter/2)
    {
      location.x = random (-diameter/2, width+diameter/2);
      location.y = random (-diameter/2, height+diameter/2);
    }

    if (location.y < -diameter/2)
    {
      location.x = random (-diameter/2, width+diameter/2);
      location.y = random (-diameter/2, height+diameter/2);
    }
    else if (location.y > height + diameter/2)
    {
      location.x = random (-diameter/2, width+diameter/2);
      location.y = random (-diameter/2, height+diameter/2);
    }
  }


  public void checkEdges ()
  {
    float diameter = ellipseSize;

    if (location.x < -diameter / 2)
    {
      location.x = width+diameter /2;
    }
    else if (location.x > width+diameter /2)
    {
      location.x = -diameter /2;
    }

    if (location.y < -diameter /2)
    {
      location.y = height+diameter /2;
    }
    else if (location.y > height+diameter /2)
    {
      location.y = -diameter /2;
    }
  }

  public void checkEdgesAndBounce ()
  {
    float radius = ellipseSize / 2;

    if (location.x < radius )
    {
      location.x = radius ;
      direction.x = direction.x * -1;
    }
    else if (location.x > width-radius )
    {
      location.x = width-radius ;
      direction.x *= -1;
    }

    if (location.y < radius )
    {
      location.y = radius ;
      direction.y *= -1;
    }
    else if (location.y > height-radius )
    {
      location.y = height-radius ;
      direction.y *= -1;
    }
  }

  // DISPLAY ---------------------------------------------------------------

  public void display ()
  {
  noStroke();
    fill (c);
    ellipse (location.x, location.y, ellipseSize, ellipseSize);
  }
}

public void keyPressed ()
{
  if (key == ' ') transparentBG = !transparentBG;
  if (key == 'n')
  {
    float noiseScale = random (5, 400);
    float noiseStrength = random (0.5f, 6);
    float forceStrength = random (0.5f, 4);

    for (int i = 0; i < bouncers.size(); i++)
    {
      Mover currentMover = bouncers.get(i);
      currentMover.noiseScale = noiseScale;
      currentMover.noiseStrength = noiseStrength;
      currentMover.forceStrength = forceStrength;
    }
  }
}

public void mousePressed ()
{
  if (mouseButton == LEFT)
  {
    bewegungsModus++;
    if (bewegungsModus > 5)
    {
      bewegungsModus = 0;
    }
  }
}
public void loadFiles() {
  println("loading files");
  fileLocations = listFileNames(path + "images/");
}

public String[] listFileNames(String dir) {
  File file = new File(dir);
  if (file.isDirectory()) {
    String names[] = file.list();
    
    println("found ", names.length, " files");
    return names;
  } else {
    // If it's not a directory
    return null;
  }
}

int[] picked = new int[6];

int[] colors = {0xff1A7DAA, 0xff000069, 0xffFF9403, 0xffFF1926, 0xff3BC6A7};
// Main operation that loads in actual images
public void loadFilesToImages() {
  // reset picked images
  picked = new int[6];
  
  // shuffler
  IntList nums = new IntList();
  for (int j = 0; j < fileLocations.length; j++) {
    nums.append(j);
  }
  nums.shuffle();
  
  // getter
  for (int i = 0; i < images.length; i++) {
    images[i] = getImage(nums.get(i));
  }
}

public PImage cropResizeImage300(PImage p) {
  // resize the largest side to 300px
  if (p.width > p.height) {
    p.resize(0, 300);
  } else {
    p.resize(300, 0);
  }
  return (p.get(p.width/2-150, 0, 300, 300));
}

public PImage maskImg(PImage p, int leftright) {
  // leftright 0 is left, 1 is right
  PImage outImg;
  if (p != null) {
     outImg = p;
  } else {
    println("no image");
    return null;
  }
  PImage mask;
  if (leftright == 0) {
    mask = loadImage(path + "masks/ml.jpg");
  } else {
    mask = loadImage(path + "masks/mr.jpg");
  }
  mask.resize(300, 300);
  outImg.resize(300, 300);
  outImg.mask(mask);
  return outImg;
}

public PImage getImage(int k) {
  return loadImage(path + "images/" + fileLocations[k]);
}
  public void settings() {  size(1038, 1200);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "photoLoader_output" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
