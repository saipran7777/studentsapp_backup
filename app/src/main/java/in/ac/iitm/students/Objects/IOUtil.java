package in.ac.iitm.students.Objects;

import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by arunp on 31-Jan-16.
 */
public class IOUtil {
    public static byte[] readFile(Uri file) throws IOException {
        Log.d("file",String.valueOf(file));
       // getContentResolver().openInputStream(filePath);
        return readFile(new File(String.valueOf(file)));
    }

    public static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }
}
