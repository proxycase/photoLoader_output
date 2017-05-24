import codeanticode.syphon.*;

String[] filenames;
//String path = sketchPath();
String path = "/Users/digitalmedialab/Documents/Processing/processing_photoLoader/data";
PImage curImg, nextImg;

// used to keep track of which image we're on
int curIndex = 0;

void setup() {
  background(0);
  size(700, 400);
  filenames = listFileNames(path);
}

void draw() {
  if(filenames != null && curImg == null) {
    printArray(filenames);
    curImg = loadImage(filenames[curIndex]);
  } else if (curImg != null) {
    image(curImg, 0, 0);
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