// 6 locations for the images
PImage[] images = new PImage[6];
String[] fileLocations;

String path = "/Users/digitalmedialab/Documents/Processing/photoLoader_output/data/";

void setup() {
  background(0);
  size(1038, 1200);
  imageMode(CENTER);
  
  loadFiles();
  loadFilesToImages();
  
  frameRate(30);
  brushSetup();
  smooth();
}

void draw() {
  background(0);
  // check for updates on the files
  if (second() % 10 == 0) {
    loadFiles();
    loadFilesToImages();
  }
  
  drawImages();
  brushDraw();
}

void drawImages() {
  scale(2);
  imageMode(CORNER);
  pushMatrix();
  translate(width/2, height/2);
  rotate(radians(45));
  translate(0,0);
  for (int i = 0; i <= images.length-1; i++) {
    switch (i) {
      case 0:
        image(maskImg(images[i], 0), 0, 0);
        break;
      case 1:
        image(maskImg(images[i], 1), 219.618, 0);
        break;
      case 2:
        image(maskImg(images[i], 0), 259.808, 150);
        break; 
      case 3:
        image(maskImg(images[i], 1), 219.618, 300);
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
  popMatrix();
}