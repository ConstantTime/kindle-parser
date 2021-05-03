package kindle.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Note {

    private String id;
    private String note;
    private String book;
    private String author;
    private String startingPoint;
    private String endingPoint;

    public String toString() {
        return "Note is " + note + "\n" + "Author is " + author + "\n" + "Book is " + book + "\n" +
                "Starting point is " + startingPoint + "\n" + "Ending point is " + endingPoint + "\n";
    }
}
