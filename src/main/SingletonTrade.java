package main;

import main.objets.Section;

public class SingletonTrade {
    private static SingletonTrade instance = new SingletonTrade();
    public static SingletonTrade getInstance(){
        return instance;
    }

    private String retour;
    private Section CurrentSection;

    public Section getCurrentSection() {
        return CurrentSection;
    }

    public void setCurrentSection(Section currentSection) {
        CurrentSection = currentSection;
    }

    public String getRetour() {
        return retour;
    }

    public void setRetour(String retour) {
        this.retour = retour;
    }
}
