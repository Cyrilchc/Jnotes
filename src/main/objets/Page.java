package main.objets;

public class Page {
    private int id;
    private String nom;
    private String content;
    private int section;

    public Page(int id, String nom, String content, int section) {
        this.id = id;
        this.nom = nom;
        this.content = content;
        this.section = section;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    @Override
    public String toString() {
        return "Page{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", content='" + content + '\'' +
                ", section=" + section +
                '}';
    }
}
