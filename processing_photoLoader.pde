import codeanticode.syphon.*;

String[] filenames;
int[] fileSort;
int HEX_NUM = 5;
//String path = sketchPath();
String path = "/Users/digitalmedialab/Documents/Processing/processing_photoLoader/data";
PImage curImg, nextImg;

// ------------------------ image index variables
int curIndex = 0;
// filenames.length

// ------------------------ timer variables
int time, timeLimit;
int timeAmount = 3000;

void setup() {
  background(0);
  size(400, 400);
  imageMode(CENTER);
  
  //String[] args = {"SecondSketch"};
  //App2 sa = new App2();
  //PApplet.runSketch(args, sa);
  
  // ------------------------ load up the image filenames
  filenames = listFileNames(path);
}

void draw() {
  time = millis();
  
  if(filenames != null && curImg == null) {
    printArray(filenames);
    curImg = loadImage(filenames[curIndex]);
    timeLimit = millis() + timeAmount;
  } else if (curImg != null) {
    curImg.resize(0, height);
    image(curImg, width/2, height/2);
    
    // time check
    if (time > timeLimit) {
      curIndex++;
      curIndex = curIndex > filenames.length -1 ? 0 : curIndex;
      // grab the image
      curImg = loadImage(filenames[curIndex]);
      timeLimit = millis() + timeAmount;
    }
  }
}

// This function returns all the files in a directory as an array of Strings  
String[] listFileNames(String dir) {
  File file = new File(dir);
  if (file.isDirectory()) {
    String names[] = file.list();
    return names;
  } else {
    // If it's not a directory
    return null;
  }
}

// use this to load and sort the images into the hexagon locations
// currently will sort them uniquely into different spaces
void loaderSorter() {
  fileSort = new int[filenames.length];
  int count = 0;
  for (int i = 0; i < filenames.length -1; i++ ) {
    fileSort[i] = count++;
    if (count > HEX_NUM) {
      count = 0;
    }
  }
}