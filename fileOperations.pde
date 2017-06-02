void loadFiles() {
  println("loading files");
  fileLocations = listFileNames(path + "images/");
}

String[] listFileNames(String dir) {
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