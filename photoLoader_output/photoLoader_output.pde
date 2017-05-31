PImage[] images;
String[] imageLocations;

void setup() {
  background(0);
  size(600, 300);
  
  loadImages();
}

void draw() {
  
}

void loadImages() {
  imageLocations = listFileNames("/Users/digitalmedialab/Documents/Processing/processing_photoLoader/data");
}

void pickImage() {
  
}

void animateImageIn() {
  
}

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