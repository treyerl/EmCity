import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

interface LineReader {
	default void eachLine(String path, BiConsumer<Integer,String> f) throws IOException{
		try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
		    String line;
		    int i = 0;
		    while ((line = br.readLine()) != null) {
		       f.accept(i++, line);
		    }
		} 
	}
	
	default String[] lineArray(String path) {
		try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
		    String line;
		    List<String> lines = new LinkedList<>();
		    while ((line = br.readLine()) != null) {
		       lines.add(line);
		    }
		    return lines.toArray(new String[lines.size()]);
		} catch (IOException e) {
//			return new String[0];
			return null;
		} 
	}
}
