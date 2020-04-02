package main;

import main.objets.Section;

public class SingletonTrade {
    private static SingletonTrade instance = new SingletonTrade();
    public static SingletonTrade getInstance(){
        return instance;
    }

    private String retour;
    private Section CurrentSection;
    private boolean yesno;

    public boolean isYesno() {
        return yesno;
    }

    public void setYesno(boolean yesno) {
        this.yesno = yesno;
    }

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
