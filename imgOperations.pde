import java.util.Collections;
int[] picked = new int[6];

color[] colors = {#1A7DAA, #000069, #FF9403, #FF1926, #3BC6A7};
// Main operation that loads in actual images
void loadFilesToImages() {
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

PImage cropResizeImage300(PImage p) {
  // resize the largest side to 300px
  if (p.width > p.height) {
    p.resize(0, 300);
  } else {
    p.resize(300, 0);
  }
  return (p.get(p.width/2-150, 0, 300, 300));
}

PImage maskImg(PImage p, int leftright) {
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

PImage getImage(int k) {
  return loadImage(path + "images/" + fileLocations[k]);
}