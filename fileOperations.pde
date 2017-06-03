import java.io.File;
import java.io.FilenameFilter;

void loadFiles() {
  println("loading files");
  fileLocations = listFileNames(path + "images/");
}

String[] listFileNames(String dir) {
  File file = new File(dir);
  if (file.isDirectory()) {

    // adding a section to avoid adding .DS_Store
    FilenameFilter jpgFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".jpg")) {
					return true;
				} else {
					return false;
				}
			}
		};

    String names[] = file.list(jpgFilter);
    println("found ", names.length, " files");
    return names;
  } else {
    // If it's not a directory
    return null;
  }
}
