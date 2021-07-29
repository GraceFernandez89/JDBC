package co.edu.utp.misiontic2022.c2.cdiaz.bookshop;

import java.sql.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBManager implements AutoCloseable {
    private Connection connection;

    public DBManager() throws SQLException {
        connect();
    }

    private void connect() throws SQLException {
        String url = "jdbc:sqlite:BookShop.db";
        connection = DriverManager.getConnection(url);
    }

    /**
     * Close the connection to the database if it is still open.
     *
     */
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
        connection = null;
    }

    /**
     * Return the number of units in stock of the given book.
     *
     * @param book The book object.
     * @return The number of units in stock, or 0 if the book does not exist in the
     *         database.
     * @throws SQLException If somthing fails with the DB.
     */
    public int getStock(Book book) throws SQLException {
        return getStock(book.getId());
    }

    /**
     * Return the number of units in stock of the given book.
     *
     * @param bookId The book identifier in the database.
     * @return The number of units in stock, or 0 if the book does not exist in the
     *         database.
     */
    public int getStock(int bookId) throws SQLException {

        int amount = 0;
        String sql = "SELECT amount FROM stock s WHERE id_book=" + bookId + "";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                amount = rs.getInt("amount");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return amount;
    }

    /**
     * Search book by ISBN.
     *
     * @param isbn The ISBN of the book.
     * @return The Book object, or null if not found.
     * @throws SQLException If somthing fails with the DB.
     */
    public Book searchBook(String isbn) throws SQLException {

        Book book = null;
        // String sql = "SELECT * FROM book b WHERE isbn='" + isbn + "';";
        String sql = "SELECT * FROM book b WHERE isbn='" + isbn + "';";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                book = new Book();
                // rs.getString("title");
                book.setId(rs.getInt("id_book"));
                book.setTitle(rs.getString("title"));
                book.setIsbn(rs.getString("isbn"));
                book.setYear(rs.getInt("year"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return book;
    }

    /**
     * Sell a book.
     *
     * @param book  The book.
     * @param units Number of units that are being sold.
     * @return True if the operation succeeds, or false otherwise (e.g. when the
     *         stock of the book is not big enough).
     * @throws SQLException If somthing fails with the DB.
     */
    public boolean sellBook(Book book, int units) throws SQLException {
        return sellBook(book.getId(), units);
    }

    /**
     * Sell a book.
     *
     * @param book  The book's identifier.
     * @param units Number of units that are being sold.
     * @return True if the operation succeeds, or false otherwise (e.g. when the
     *         stock of the book is not big enough).
     * @throws SQLException If something fails with the DB.
     */
    public boolean sellBook(int book, int units) throws SQLException {

        // Comprobar el suficiente stock

        int amount = getStock(book);
        int amountUD;

        if (amount >= units) {

            try {

                Statement stmt = connection.createStatement();
                String ins = "INSERT INTO sales (sales_date,id_book,amount)values(date('now'), " + book + ", " + units
                        + ");";

                stmt.executeUpdate(ins);

                amountUD = amount - units;
                String up = "UPDATE stock set amount = " + amountUD + " WHERE " + book + "";
                stmt.executeUpdate(up);

            } catch (Exception e) {
                // TODO: handle exception
            }
            return true;
        }

        // Comprobar la existencia del libro
        // String sql = "SELECT * FROM book b WHERE id_book='" + book + "';";

        /*
         * try { Statement stmt = connection.createStatement(); ResultSet rs =
         * stmt.executeQuery(sql);
         * 
         * while (rs.next()) { // Comprobar el suficiente stock if (getStock(book) >=
         * units) {
         * 
         * } }
         * 
         * } catch (Exception e) { //TODO: handle exception }
         */

        return false;
    }

    /**
     * Return a list with all the books in the database.
     *
     * @return List with all the books.
     * @throws SQLException If something fails with the DB.
     */
    public List<Book> listBooks() throws SQLException {
        // TODO: program this method

        List<Book> books = new ArrayList<>();

        String sql = "SELECT * FROM book";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Book book = new Book();
                // rs.getString("title");
                book.setId(rs.getInt("id_book"));
                book.setTitle(rs.getString("title"));
                book.setIsbn(rs.getString("isbn"));
                book.setYear(rs.getInt("year"));

                books.add(book);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return books;
    }
}
