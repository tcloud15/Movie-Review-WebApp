/*
Team name: Green
Team members: Walker LaVoy, Jason Rowland, Shakah Alhamed, Van Nguyen
Date: 05/15/2018
CSCI 367 - Spring 2018
Project: Movie review web application
Description: There are three main types of user, member, admin and visitor. Visitor can only search movie and read
  all the information related to the movie. Member can write a review to a movie, delete or edit their own reviews, and
  change their account password. Admin is a subclass of member, they have additional privileges like add movie/actor/genre/acts in/describes,
  edit/delete movie, and delete all members' review.
Deliverable 5
Instruction: Run Main in IntelliJ IDEA
Source code: Professor Wolff, class slides, class exercises
 */

import java.sql.*;

public class DbFacade implements AutoCloseable{

    private Connection conn;

    /*
    * Constructor that automatically connects to the green teams database
    * on mal.cs.plu.edu
    */
    public DbFacade() throws SQLException{
        String url = "jdbc:mariadb://mal.cs.plu.edu:3306/367_2018_green";
        String uname = "green_2018";
        String pw = "367rocks!";

        conn = DriverManager.getConnection(url, uname, pw);
    }

    /**
     * Close the connection to the database
     */
    @Override
    public void close() throws SQLException {
        if(conn != null)
            conn.close();
        conn = null;
    }

    /**
     * This method allows a review to be deleted from the database given
     * the review's key-it's ID
     * @param reviewID - the ID of the review to be deleted
     */
    public int deleteReview(String reviewID) throws SQLException{
        String sql  = "DELETE FROM Review WHERE rID =?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, reviewID);
        return pstmt.executeUpdate();
    }

    public int deleteReviewM(String reviewID, String uname) throws SQLException{
        String sql  = "DELETE FROM Review WHERE rID =? and MemberUsername=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, reviewID);
        pstmt.setString(2, uname);
        return pstmt.executeUpdate();
    }

    /*
    Return average rating of input movie name, US and international release date
     */
    public ResultSet averageRating(String movie, String US, String intl) throws SQLException {
        String sql = "select avg(mRating) from Review where mName=? and USReleaseDate=? and intlReleaseDate=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, movie);
        pstmt.setString(2, US);
        pstmt.setString(3, intl);
        ResultSet rset = pstmt.executeQuery();
        return rset;
    }

    /*
    Return the number of movies that has the input in any postion of the movie name (to check if the movies with the input characters exist in the database)
     */
    public ResultSet search(String mName) throws SQLException {
        String sql = "select  count(*)" +
                "from Movie " +
                "where upper(mName) like upper(?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, "%" + mName + "%");
        ResultSet rset = pstmt.executeQuery();
        return rset;
    }

    /*
    Return movie name, US release date, and intl release date
     */
    public ResultSet lastSearch(String mName) throws SQLException {
        String sql = "select distinct mName, USReleaseDate, intlReleaseDate " +
                "from Movie " +
                "where upper(Movie.mName) like upper(?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, "%" + mName + "%");
        ResultSet rset = pstmt.executeQuery();
        return rset;
    }

    /*
    Return the new review if successfully inserted
     */
    public ResultSet writeReview(String rID, String uName, String date, String mName, String US, String intl, String mRating, String rText) throws SQLException {
        String sql = "insert into Review values(?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, rID);
        pstmt.setString(2, uName);
        pstmt.setString(3, date);
        pstmt.setString(4, mName);
        pstmt.setString(5, US);
        pstmt.setString(6, intl);
        pstmt.setString(7, mRating);
        pstmt.setString(8, rText);
        ResultSet rset = pstmt.executeQuery();
        return rset;
    }

    /*
    Return true if both username and password match what in database, false if either username or password matches
     */
    public boolean authenticateMember(String uname, String pw) throws SQLException{
        String sql = "SELECT Username FROM SiteMember WHERE " +
                "Username = ? AND Password_Hash = SHA2(?, 256)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, uname);
        pstmt.setString(2, pw);
        ResultSet rset = pstmt.executeQuery();
        return rset.first();
    }

    /*
    Return the username if exists, else return empty table.
     */
    public boolean checkUsername(String uname) throws SQLException {
        String sql = "select Username " +
                "from SiteMember " +
                "where Username=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, uname);
        ResultSet rset = pstmt.executeQuery();
        return rset.first();
    }

    /*
    Return int = 1 when successfully sign up or int = 0
     */
    public int signup(String uname, String pass) throws SQLException {
        String sql = "insert into SiteMember values(?, SHA2(?, 256))";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, uname);
        pstmt.setString(2, pass);
        int rset = pstmt.executeUpdate();
        return rset;
    }

    /*
    Return all the movie's information and overall rating
     */
    public ResultSet movie(String mName, String USReleaseDate, String intlReleaseDate) throws SQLException{
        String sql = "select Movie.mName, Movie.USReleaseDate, Movie.intlReleaseDate, mDescription, mDirector, contentRating, mDuration, avg(mRating) " +
                "from Movie, Review " +
                "where Movie.mName=Review.mName " +
                "and Movie.USReleaseDate=Review.USReleaseDate and Movie.intlReleaseDate=Review.intlReleaseDate " +
                "and Movie.mName=? and Movie.USReleaseDate=? and Movie.intlReleaseDate=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, mName);
        pstmt.setString(2, USReleaseDate);
        pstmt.setString(3, intlReleaseDate);
        ResultSet rset= pstmt.executeQuery();
        return rset;
    }

    /*
    Return all the actors/actresses in the input movie
     */
    public ResultSet actor(String mName, String USReleaseDate, String intlReleaseDate) throws SQLException {
        String sql = "select aName " +
                "from Acts_In, Movie " +
                "where Movie.mName=Acts_In.mName and USReleaseDate=mDateUS and intlReleaseDate=mDateIntl and Movie.mName=? and USReleaseDate=? and intlReleaseDate=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, mName);
        pstmt.setString(2, USReleaseDate);
        pstmt.setString(3, intlReleaseDate);
        ResultSet rset= pstmt.executeQuery();
        return rset;
    }

    /*
    Return all the movie genre in the input movie
     */
    public ResultSet genre(String mName, String USReleaseDate, String intlReleaseDate) throws SQLException {
        String sql = "select gName " +
                "from Describes, Movie " +
                "where Movie.mName=Describes.mName and USReleaseDate=mDateUS and intlReleaseDate=mDateIntl " +
                "and Movie.mName=? and USReleaseDate=? and intlReleaseDate=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, mName);
        pstmt.setString(2, USReleaseDate);
        pstmt.setString(3, intlReleaseDate);
        ResultSet rset = pstmt.executeQuery();
        return rset;
    }

    /*
    Return all the reviews in the input movie
     */
    public ResultSet review(String mName, String USReleaseDate, String intlReleaseDate) throws SQLException {
        String sql = "select rID, DateWritten, MemberUsername, mRating, rText " +
                "from Review, Movie " +
                "where Movie.mName=Review.mName " +
                "and Movie.USReleaseDate=Review.USReleaseDate and Movie.intlReleaseDate=Review.intlReleaseDate " +
                "and Movie.mName=? and Movie.USReleaseDate=? and Movie.intlReleaseDate=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, mName);
        pstmt.setString(2, USReleaseDate);
        pstmt.setString(3, intlReleaseDate);
        ResultSet rset = pstmt.executeQuery();
        return rset;
    }

    /*
    Return int = 1 if movie is successfully inserted, int = 0 otherwise
     */
    public int addMovie(String mName, String US, String intl, String mDescription, String director, String contentRating, String mDuration, String admin) throws SQLException{
        String sql = "insert into Movie values(?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, mName);
        pstmt.setString(2, US);
        pstmt.setString(3, intl);
        pstmt.setString(4, mDescription);
        pstmt.setString(5, director);
        pstmt.setString(6, contentRating);
        pstmt.setString(7, mDuration);
        pstmt.setString(8, admin);
        int rset = pstmt.executeUpdate();
        return rset;
    }

    /*
    Return int = 1 if actor is successfully inserted, int = 0 otherwise
     */
    public int addActor(String aName, String aBD) throws SQLException{
        String sql = "insert into Actor values(?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, aName);
        pstmt.setString(2, aBD);
        int rset = pstmt.executeUpdate();
        return rset;
    }

    /*
    Return int = 1 if actor is successfully inserted into the movie, int = 0 otherwise
     */
    public int addActsIn(String mName, String US, String intl, String aName, String aBD) throws SQLException{
        String sql = "insert into Acts_In values(?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(3, mName);
        pstmt.setString(4, US);
        pstmt.setString(5, intl);
        pstmt.setString(1, aName);
        pstmt.setString(2, aBD);
        int rset = pstmt.executeUpdate();
        return rset;
    }

    /*
    Return int = 1 if genre is successfully inserted, int = 0 otherwise
     */
    public int addGenre(String gName) throws SQLException{
        String sql = "insert into Genre values(?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, gName);
        int rset = pstmt.executeUpdate();
        return rset;
    }

    /*
    Return int = 1 if genre is successfully inserted into movie, int = 0 otherwise
     */
    public int addDescribes(String mName, String US, String intl, String gName) throws SQLException{
        String sql = "insert into Describes values(?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, mName);
        pstmt.setString(2, US);
        pstmt.setString(3, intl);
        pstmt.setString(4, gName);
        int rset = pstmt.executeUpdate();
        return rset;
    }

    /*
    Return true if movie is successfully updated, false otherwise
     */
    public boolean editMovie(String mName, String US, String intl, int i, String up, String admin) throws SQLException{
        String sql;
        PreparedStatement pstmt;
        int rset;
        switch(i) {
            case 1:
                sql = "update Movie set mName=?, AdminUserName=? where mName=? and USReleaseDate=? and intlReleaseDate=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.clearParameters();
                pstmt.setString(1, up);
                pstmt.setString(3, mName);
                pstmt.setString(4, US);
                pstmt.setString(5, intl);
                pstmt.setString(2, admin);
                rset = pstmt.executeUpdate();
                if(rset > 0)
                    return true;
            case 2:
                sql = "update Movie set USReleaseDate=?, AdminUserName=? where mName=? and USReleaseDate=? and intlReleaseDate=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.clearParameters();
                pstmt.setString(1, up);
                pstmt.setString(3, mName);
                pstmt.setString(4, US);
                pstmt.setString(5, intl);
                pstmt.setString(2, admin);
                rset = pstmt.executeUpdate();
                if(rset > 0)
                    return true;
            case 3:
                sql = "update Movie set intlReleaseDate=?, AdminUserName=? where mName=? and USReleaseDate=? and intlReleaseDate=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.clearParameters();
                pstmt.setString(1, up);
                pstmt.setString(3, mName);
                pstmt.setString(4, US);
                pstmt.setString(5, intl);
                pstmt.setString(2, admin);
                rset = pstmt.executeUpdate();
                if(rset > 0)
                    return true;
            case 4:
                sql = "update Movie set mDescription=?, AdminUserName=? where mName=? and USReleaseDate=? and intlReleaseDate=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.clearParameters();
                pstmt.setString(1, up);
                pstmt.setString(3, mName);
                pstmt.setString(4, US);
                pstmt.setString(5, intl);
                pstmt.setString(2, admin);
                rset = pstmt.executeUpdate();
                if(rset > 0)
                    return true;
            case 5:
                sql = "update Movie set mDirector=?, AdminUserName=? where mName=? and USReleaseDate=? and intlReleaseDate=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.clearParameters();
                pstmt.setString(1, up);
                pstmt.setString(3, mName);
                pstmt.setString(4, US);
                pstmt.setString(5, intl);
                pstmt.setString(2, admin);
                rset = pstmt.executeUpdate();
                if(rset > 0)
                    return true;
            case 6:
                sql = "update Movie set contentRating=?, AdminUserName=? where mName=? and USReleaseDate=? and intlReleaseDate=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.clearParameters();
                pstmt.setString(1, up);
                pstmt.setString(3, mName);
                pstmt.setString(4, US);
                pstmt.setString(5, intl);
                pstmt.setString(2, admin);
                rset = pstmt.executeUpdate();
                if(rset > 0)
                    return true;
            case 7:
                sql = "update Movie set mDuration=?, AdminUserName=? where mName=? and USReleaseDate=? and intlReleaseDate=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.clearParameters();
                pstmt.setString(1, up);
                pstmt.setString(3, mName);
                pstmt.setString(4, US);
                pstmt.setString(5, intl);
                pstmt.setString(2, admin);
                rset = pstmt.executeUpdate();
                if(rset > 0)
                    return true;
            default:
                break;
        }
        return false;
    }

    /*
    Return int = 1 if movie is successfully deleted, int = 0 otherwise
     */
    public int deleteMovie(String mName, String US, String intl) throws SQLException {
        String sql = "delete from Movie where mName=? and USReleaseDate=? and intlReleaseDate=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, mName);
        pstmt.setString(2, US);
        pstmt.setString(3, intl);
        int rset = pstmt.executeUpdate();
        return rset;
    }

    /*
    Return all reviews in the database
     */
    public ResultSet displayReview() throws SQLException {
        String sql = "select * from Review";
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery(sql);
        return rset;
    }

    /*
    Return all reviews written by the input username
     */
    public ResultSet displayReview2(String uname) throws SQLException {
        String sql = "select * from Review where MemberUsername=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, uname);
        ResultSet rset = pstmt.executeQuery();
        return rset;
    }

    /*
    Return true if password matches with the username in the database, false otherwise
     */
    public boolean checkpass(String uname, String pw) throws SQLException{
        String sql = "SELECT Password_Hash FROM SiteMember WHERE " +
                "Username = ? AND Password_Hash = SHA2(?, 256)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, uname);
        pstmt.setString(2, pw);
        ResultSet rset = pstmt.executeQuery();
        return rset.first();
    }

    /*
    Return true if password is successfully updated, false otherwise
     */
    public boolean changePass(String uname, String pw, String up) throws SQLException{
        String sql = "update SiteMember set Password_Hash=SHA2(?, 256) WHERE " +
                "Username = ? AND Password_Hash = SHA2(?, 256)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(2, uname);
        pstmt.setString(3, pw);
        pstmt.setString(1, up);
        ResultSet rset = pstmt.executeQuery();
        return rset.first();
    }

    /*
    Return all movies in the database
     */
    public ResultSet displayMovie() throws SQLException {
        String sql = "select * from Movie";
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery(sql);
        return rset;
    }

    /*
    Return true if the review is successfully updated, false otherwise
     */
    public boolean editReview(String rID, int i, String up, String date) throws SQLException{
        String sql;
        PreparedStatement pstmt;
        int rset;
        switch(i) {
            case 1:
                sql = "update Review set mRating=?, DateWritten=? where rID=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.clearParameters();
                pstmt.setString(1, up);
                pstmt.setString(3, rID);
                pstmt.setString(2, date);
                rset = pstmt.executeUpdate();
                if(rset > 0)
                    return true;
            case 2:
                sql = "update Review set rText=?, DateWritten=? where rID=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.clearParameters();
                pstmt.setString(1, up);
                pstmt.setString(3, rID);
                pstmt.setString(2, date);
                rset = pstmt.executeUpdate();
                if(rset > 0)
                    return true;
            case 3:
                sql = "update Review set rID=?, DateWritten=? where rID=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.clearParameters();
                pstmt.setString(1, up);
                pstmt.setString(3, rID);
                pstmt.setString(2, date);
                rset = pstmt.executeUpdate();
                if(rset > 0)
                    return true;
            default:
                break;
        }
        return false;
    }

    /*
    Return true if the review is in the database, false otherwise
     */
    public boolean checkrID(String rID) throws SQLException {
        String sql = "select rID from Review where rID=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.clearParameters();
        pstmt.setString(1, rID);
        ResultSet rset = pstmt.executeQuery();
        return rset.first();
    }

}
