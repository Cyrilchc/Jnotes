package db;

import main.objets.Page;
import main.objets.Section;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class connect {

    private static Connection con = null;

    public void connect() {
        con = null;
        try {
            //Path p = new Paths("db/notes");
            // db parameters
            String url = "jdbc:sqlite:src/db/notes";
            // create a connection to the database
            con = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Obtient toutes les sections
     *
     * @return
     */
    public List<Section> getAllSections() {
        List<Section> sections = new ArrayList<>();
        if (con == null) {
            connect();
        }

        String query = "select section_id, section_nom from section;";
        try {
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                sections.add(
                        new Section(
                                resultSet.getInt("section_id"),
                                resultSet.getString("section_nom")
                        )
                );
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return sections;
    }

    /**
     * Obtient toutes les pages
     *
     * @return
     */
    public List<Page> getAllPages() {
        List<Page> pages = new ArrayList<>();
        if (con == null) {
            connect();
        }

        String query = "select page_id, page_nom, page_content, section_id from page;";
        try {
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                pages.add(
                        new Page(
                                resultSet.getInt("page_id"),
                                resultSet.getString("page_nom"),
                                resultSet.getString("page_content"),
                                resultSet.getInt("section_id")
                        )
                );
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return pages;
    }

    /**
     * Obtient la section par son id
     *
     * @param section_id
     * @return
     */
    public Section getSectionById(int section_id) {
        if (con == null) {
            connect();
        }

        String query = "select section_id, section_nom from section where section_id = ?;";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setInt(1, section_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return new Section(resultSet.getInt("section_id"), resultSet.getString("section_nom"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    /**
     * Obtient toutes les pages de la section
     *
     * @return
     */
    public List<Page> getAllPagesFromSection(int section_id) {
        List<Page> pages = new ArrayList<>();
        if (con == null) {
            connect();
        }

        String query = String.format("select page_id, page_nom, page_content, section_id from page where section_id = ?;", section_id);
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setInt(1, section_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                pages.add(
                        new Page(
                                resultSet.getInt("page_id"),
                                resultSet.getString("page_nom"),
                                resultSet.getString("page_content"),
                                resultSet.getInt("section_id")
                        )
                );
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return pages;
    }

    /**
     * Modifie le contenu d'une page
     *
     * @param page
     * @return
     */
    public boolean updateContent(Page page) {
        if (con == null) {
            connect();
        }

        String query = "update page set page_content = ? where page_id = ?;";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, page.getContent());
            preparedStatement.setInt(2, page.getId());
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return false;
    }

    /**
     * Créé une section
     *
     * @param section_nom
     * @return
     */
    public boolean createSection(String section_nom) {
        if (con == null) {
            connect();
        }

        String query = "insert into section(section_nom) values(?)";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, section_nom);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return false;
    }

    /**
     * Créé une page
     *
     * @param page_nom
     * @return
     */
    public boolean createPage(String page_nom, int section_id) {
        if (con == null) {
            connect();
        }

        String query = "insert into page(page_nom, section_id) values(?, ?)";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, page_nom);
            preparedStatement.setInt(2, section_id);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return false;
    }

    /**
     * Supprime la section en paramètres
     *
     * @param section_id
     * @return
     */
    public boolean deleteSection(int section_id) {
        if (con == null) {
            connect();
        }

        String query = "delete from section where section_id = ?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setInt(1, section_id);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return false;
    }

    /**
     * Modifie le nom de la section
     * @param section
     * @param nom
     * @return
     */
    public boolean editSection(Section section, String nom) {
        if (con == null) {
            connect();
        }

        String query = "update section set section_nom = ? where section_id = ?;";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, nom);
            preparedStatement.setInt(2, section.getId());
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return false;
    }

    /**
     * Supprime toutes les pages liées à la section en paramètre
     *
     * @param section_id
     * @return
     */
    public boolean deletePagesFromSection(int section_id) {
        if (con == null) {
            connect();
        }

        String query = "delete from page where section_id = ?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setInt(1, section_id);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return false;
    }

    /**
     * Supprime la page en paramètre
     *
     * @param page_id
     * @return
     */
    public boolean deletePage(int page_id) {
        if (con == null) {
            connect();
        }

        String query = "delete from page where page_id = ?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setInt(1, page_id);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return false;
    }

    /**
     * Modifie le nom de la page
     * @param page
     * @param nom
     * @return
     */
    public boolean editPage(Page page, String nom) {
        if (con == null) {
            connect();
        }

        String query = "update page set page_nom = ? where page_id = ?;";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, nom);
            preparedStatement.setInt(2, page.getId());
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return false;
    }
}
