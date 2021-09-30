import java.util.ArrayList;
import java.util.List;

public class PlannedEvent {
    public String name;
    public String description;
    public String date;
    public String month;
    public final List<String> NotifyList = new ArrayList<>();

    public PlannedEvent(String name, String description, String date, String month) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.month = month;
    }



    @Override
    public String toString() {
        return "Название мероприятия: " +
                name + '\n' +
                "Дата проведения: " + date + "\n" +
                "Описание: " + description;

    }
}
