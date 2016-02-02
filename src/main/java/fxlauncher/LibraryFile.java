package fxlauncher;

import javax.xml.bind.annotation.XmlAttribute;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.Adler32;

public class LibraryFile {
    @XmlAttribute
    String file;
    @XmlAttribute
    Long checksum;
    @XmlAttribute
    Long size;

    public boolean needsUpdate() {
        Path path = Paths.get(file);
        try {
            return !Files.exists(path) || Files.size(path) != size || checksum(path) != checksum;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LibraryFile() {
    }

    public LibraryFile(Path basepath, Path file) throws IOException {
        this.file = basepath.relativize(file).toString();
        this.size = Files.size(file);
        this.checksum = checksum(file);
    }

    public URL toURL() {
        try {
            return Paths.get(file).toFile().toURI().toURL();
        } catch (MalformedURLException whaat) {
            throw new RuntimeException(whaat);
        }
    }

    public static long checksum(Path path) throws IOException {
        try (InputStream input = Files.newInputStream(path)) {
            Adler32 checksum = new Adler32();
            byte[] buf = new byte[16384];

            int read;
            while ((read = input.read(buf)) > -1)
                checksum.update(buf, 0, read);

            return checksum.getValue();
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LibraryFile that = (LibraryFile) o;

        if (!file.equals(that.file)) return false;
        if (!checksum.equals(that.checksum)) return false;
        return size.equals(that.size);

    }

    public int hashCode() {
        int result = file.hashCode();
        result = 31 * result + checksum.hashCode();
        result = 31 * result + size.hashCode();
        return result;
    }
}
