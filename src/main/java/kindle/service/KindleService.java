package kindle.service;

import kindle.entity.Note;
import kindle.entity.Pair;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static kindle.constants.Constants.SEPARATOR;
import static kindle.constants.Constants.YOUR_HIGHLIGHT;

@Log4j2
@Service
public class KindleService {

    public void handle(MultipartFile mpFile) {
        Path filePath = write(mpFile, "");
        File file = filePath.toFile();

        List<String> notes = new ArrayList<>();
        readFile(file, notes);

        List<Note> parsedNotes = new ArrayList<>();

        for (int i = 0; i < notes.size(); i++) {
            if (patternExists(notes, i)) {
                String id = UUID.randomUUID().toString();
                Pair<String, String> bookAndAuthor = parseBookAndAuthor(notes.get(i + 2));
                Pair<String, String> highlightLocation = parseHighlightLocation(notes.get(i + 3));

                Note parsedNote = new Note(id, notes.get(i), bookAndAuthor.first, bookAndAuthor.second,
                        highlightLocation.first, highlightLocation.second);
                parsedNotes.add(parsedNote);
            }
        }

        parsedNotes.forEach(note -> System.out.println(note.toString()));
    }

    private Pair<String, String> parseHighlightLocation(String text) {
        String copyText = text;
        copyText.replace(YOUR_HIGHLIGHT + " ", "");

        String start = "";
        String end = "";

        int index;

        for (index = 0; index < text.length(); index++) {
            if (text.charAt(index) == '-') {
                break;
            }
            start += text.charAt(index);
        }

        for (int j = index + 1; j < text.length(); j++) {
            if (text.charAt(j) == ' ') {
                break;
            }
            end += text.charAt(j);
        }

        return new Pair<>(start, end);
    }

    private Pair<String, String> parseBookAndAuthor(String text) {
        String book = "";
        String author = "";

        // Sample string below:
        // The Mom Test: how to talk to customers and learn if your business is a good idea
        // when everybody is lying to you (Rob Fitzpatrick)
        int i;
        for (i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '(') {
                break;
            }
            book += text.charAt(i);
        }

        for (int j = i + 1; j < text.length(); j++) {
            if (text.charAt(j) == ')') {
                break;
            }
            author += text.charAt(j);
        }

        return new Pair<>(book, author);
    }

    private boolean patternExists(List<String> notes, int i) {
        if (notes.size() < i + 5) {
            return false;
        }
        if (notes.get(i + 1).contains(SEPARATOR) && notes.get(i + 2).contains("(") && notes.get(i + 2).contains(")")
                && notes.get(i + 3).contains(YOUR_HIGHLIGHT) && notes.get(i + 4).length() == 0) {
            return true;
        }

        return false;
    }

    private void readFile(File file, List<String> notes) {
        try {
            File myObj = new File(file.getName());
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                notes.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            log.info("File not found {}", e);
        }
    }

    private Path write(MultipartFile file, String dir) {
        Path filepath = Paths.get(dir, file.getOriginalFilename());

        try (OutputStream os = Files.newOutputStream(filepath)) {
            os.write(file.getBytes());
        } catch (IOException e) {
            log.info("IO Exception white converting multipartfile to file {}", e);
        }

        return filepath;
    }
}
