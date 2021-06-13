package Police;

import java.io.Serializable;
import javax.persistence.*;

@Entity
public class Crime implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue
    private long id;

    private String name;
    private String crime;
    private int penalty;

    public Crime() {
    }

    Crime(String name, String crime, int penalty) {
        this.name = name;
        this.crime = crime;
        this.penalty = penalty;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCrime() {
        return crime;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int p) {
        this.penalty = p;
    }

    @Override
    public String toString() {
        return "id: " + id + ", imie: " + name + ", przestepstwo: " + crime + ", kara: " + penalty;
    }
}
