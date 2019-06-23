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

import spark.Request;
import spark.Response;
import spark.Session;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.sql.ResultSet;

import static spark.Spark.halt;

public class ExampleController {

    public ExampleController() {
    }

    //Get homepage to display
    public Object getHomepage(Request req, Response rep) {
        Session sess = req.session();
        sess.attribute("authenticated", false);
        return Main.renderTemplate(null, "homepage-form.hbs");
    }

    //Get login form to login
    public Object login(Request req, Response resp){
        return Main.renderTemplate(null, "login-form.hbs");
    }

    //Get signup form to signup
    public Object signup(Request req, Response resp) {
        return Main.renderTemplate(null, "signup.hbs");
    }

    //After clicking signup button
    public Object postSignup(Request req, Response resp) {
        String username = req.queryParams("uname");
        String password = req.queryParams("pw");
        String confirmpass = req.queryParams("confirmedpw");

        Map<String,Object> data = new HashMap<>();

        try(DbFacade db = new DbFacade()) {
            //check if the username exists
            boolean rset = db.checkUsername(username);
            //if there is no similar username or if the username is not empty
            if(rset == false && !username.equals("")) {
                //if password and confirm password match
                if(password.equals(confirmpass) && !password.equals("")) {
                    int rset1 = db.signup(username, password);
                    if(rset1 > 0)
                        data.put("successfulMsg", "You can sign in now!");
                        data.put("login", "Click here to log in!");
                } else if(password.equals("") || confirmpass.equals("")) {
                    data.put("errorMsg", "Please fill in all the texts.");
                } else {
                    data.put("errorPwMsg", "Password does not match");
                }
            } else if(username.equals("") || password.equals("")|| confirmpass.equals("")) {
                data.put("errorMsg", "Please fill in all the texts.");
            } else {
                data.put("errorUMsg", "Username is already existed.");
            }
            return Main.renderTemplate(data, "signup.hbs");
        } catch(SQLException e) {
            resp.status(500);
            System.err.println("Error checkUsername: " + e.getMessage());
            return "";
        }

    }

    //Get add movie form
    public Object addMovieForm(Request req, Response resp){

        return Main.renderTemplate(null, "add-movie.hbs");
    }

    //After clicking add movie button
    public Object addMovie(Request req, Response resp){
        String movie = req.queryParams("name");
        String usdate = req.queryParams("usdate");
        String ukdate = req.queryParams("ukdate");
        String des = req.queryParams("description");
        String dir = req.queryParams("director");
        String rate = req.queryParams("rating");
        String time = req.queryParams("time");
        Session sess = req.session();
        String user = sess.attribute("username");

        Map<String, Object> data = new HashMap<>();

        try(DbFacade db = new DbFacade()) {
            if(!movie.equals("") && !usdate.equals("") && !ukdate.equals("") && !time.equals("")) {
                int rset = db.addMovie(movie, usdate, ukdate, des, dir, rate, time, user);
                if (rset > 0) {
                    data.put("successfulMsg", "Successful Update!");
                } else {
                    data.put("errorMsg", "Error! Invalid input or that information is already in the database");
                }
            } else
                data.put("errorMsg", "Error! Movie name, US release date, intl release date, and runtime cannot be empty.");

            return Main.renderTemplate(data, "add-movie.hbs");
        } catch(SQLException e){
            resp.status(500);
            System.err.println("Error in addMovie: " + e.getMessage());
            data.put("errorMsg", "Error! Invalid input or that information is already in the database");
            return Main.renderTemplate(data, "add-movie.hbs");
        }
    }

    //Get add actor form
    public Object addActor(Request req, Response resp){

        return Main.renderTemplate(null, "add-actor.hbs");
    }

    //After clicking add actor button
    public Object actorAdded(Request req, Response resp){
        String aname = req.queryParams("aname");
        String abd = req.queryParams("abd");

        Map<String, Object> data = new HashMap<>();

        try(DbFacade db = new DbFacade()) {
            if(!aname.equals("") && !abd.equals("")) {
                int rset = db.addActor(aname, abd);
                if (rset > 0) {
                    data.put("successfulMsg", "Successful Update!");
                } else {
                    data.put("errorMsg", "Error! Invalid input or that information is already in the database");
                }
            } else
                data.put("errorMsg", "Error! Inputs cannot be empty.");

            return Main.renderTemplate(data, "add-actor.hbs");
        } catch(SQLException e){
            resp.status(500);
            System.err.println("Error in addActor: " + e.getMessage());
            data.put("errorMsg", "Error! Invalid input or that information is already in the database");
            return Main.renderTemplate(data, "add-actor.hbs");
        }
    }

    //Get add genre form
    public Object addGenre(Request req, Response resp){

        return Main.renderTemplate(null, "add-genre.hbs");
    }

    //After clicking add genre button
    public Object genreAdded(Request req, Response resp){
        String gname = req.queryParams("gname");

        Map<String, Object> data = new HashMap<>();

        try(DbFacade db = new DbFacade()) {
            if(!gname.equals("")) {
                int rset = db.addGenre(gname);
                if (rset > 0) {
                    data.put("successfulMsg", "Successful Update!");
                } else {
                    data.put("errorMsg", "Error! Invalid input or that information is already in the database");
                }
            } else
                data.put("errorMsg", "Error! Input cannot be empty.");

            return Main.renderTemplate(data, "add-genre.hbs");
        } catch(SQLException e){
            resp.status(500);
            System.err.println("Error in addGenre: " + e.getMessage());
            data.put("errorMsg", "Error! Invalid input or that information is already in the database");
            return Main.renderTemplate(data, "add-genre.hbs");
        }
    }

    //Get acts in form
    public Object addActsIn(Request req, Response resp){

        return Main.renderTemplate(null, "add-actsin.hbs");
    }

    //After clicking add acts in button
    public Object actsInAdded(Request req, Response resp){
        String aname = req.queryParams("aname");
        String abd = req.queryParams("abd");
        String mname = req.queryParams("mname");
        String US = req.queryParams("date");
        String intl = req.queryParams("ukdate");

        Map<String, Object> data = new HashMap<>();

        try(DbFacade db = new DbFacade()) {
            if(!aname.equals("") && !abd.equals("") && !mname.equals("") && !US.equals("") && !intl.equals("")) {
                int rset = db.addActsIn(mname, US, intl, aname, abd);
                if (rset > 0) {
                    data.put("successfulMsg", "Successful Update!");
                } else {
                    data.put("errorMsg", "Error! Invalid input or that information is already in the database");
                }
            } else
                data.put("errorMsg", "Error! Inputs cannot be empty.");

            return Main.renderTemplate(data, "add-actsin.hbs");
        } catch(SQLException e){
            resp.status(500);
            System.err.println("Error in addActsin: " + e.getMessage());
            data.put("errorMsg", "Error! Invalid input or that information is already in the database");
            return Main.renderTemplate(data, "add-actsin.hbs");
        }
    }

    //Get add describes form
    public Object addDescribes(Request req, Response resp){

        return Main.renderTemplate(null, "add-describes.hbs");
    }

    //After clikcing add describes button
    public Object describesAdded(Request req, Response resp){
        String gname = req.queryParams("gname");
        String mname = req.queryParams("mname");
        String US = req.queryParams("date");
        String intl = req.queryParams("ukdate");

        Map<String, Object> data = new HashMap<>();

        try(DbFacade db = new DbFacade()) {
            if(!gname.equals("") && !mname.equals("") && !US.equals("") && !intl.equals("")){
                int rset = db.addDescribes(mname, US, intl, gname);
                if (rset > 0) {
                    data.put("successfulMsg", "Successful Update!");
                } else {
                    data.put("errorMsg", "Error! Invalid input or that information is already in the database");
                }
            } else
                data.put("errorMsg", "Error! Inputs cannot be empty.");

            return Main.renderTemplate(data, "add-describes.hbs");
        } catch(SQLException e){
            resp.status(500);
            System.err.println("Error in addDescribes: " + e.getMessage());
            data.put("errorMsg", "Error! Invalid input or that information is already in the database");
            return Main.renderTemplate(data, "add-describes.hbs");
        }
    }

    //Get edit movie form
    public Object getEditMovie(Request req, Response resp){

        return Main.renderTemplate(null, "edit-movie.hbs");
    }

    //After clicking edit movie button
    public Object editMovie(Request req, Response resp){
        Map<String, Object> data = new HashMap<>();
        int options = 0;
        if(req.queryParams("num").equals("1") || req.queryParams("num").equals("2") || req.queryParams("num").equals("3") || req.queryParams("num").equals("4") || req.queryParams("num").equals("5") || req.queryParams("num").equals("6") || req.queryParams("num").equals("7"))
            options = Integer.parseInt(req.queryParams("num"));
        String up = req.queryParams("up");
        String mname = req.queryParams("name");
        String US = req.queryParams("usdate");
        String intl = req.queryParams("ukdate");
        Session sess = req.session();
        String uname = sess.attribute("username");

        try(DbFacade db = new DbFacade()) {
            if(options != 0 && !mname.equals("") && !US.equals("") && !intl.equals("") && !up.equals("")) {
                boolean rset = db.editMovie(mname, US, intl, options, up, uname);
                if (rset) {
                    data.put("successfulMsg", "Successful Update!");
                } else {
                    data.put("errorMsg", "Error! Invalid input or that information is already in the database");
                }
            } else
                data.put("errorMsg", "Error! Inputs cannot be empty or incorrect option number.");

            return Main.renderTemplate(data, "edit-movie.hbs");
        } catch(SQLException e){
            resp.status(500);
            System.err.println("Error in editMovie: " + e.getMessage());
            data.put("errorMsg", "Error! Invalid input or that information is already in the database");
            return Main.renderTemplate(data, "edit-movie.hbs");
        }
    }

    //Get delete review form and after clicking delete review button for both member and admin
    public Object deleteReview(Request req, Response resp){
        Session sess = req.session();
        String uname = sess.attribute("username");
        String id = req.queryParams("id");
        ArrayList<String> rID = new ArrayList<String>();

        Map<String,Object> data = new HashMap<>();

        try(DbFacade db = new DbFacade()) {
            //Display all reviews (for admin)
            ResultSet rset1 = db.displayReview();
            //Display logged in username's review only (for member)
            ResultSet rset2 = db.displayReview2(uname);

            ArrayList<Map<String,String>> adminReview1 = new ArrayList<>();
            ArrayList<Map<String,String>> userReview1 = new ArrayList<>();

            while(rset1.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("rID", rset1.getString(1));
                row.put("memberuname", rset1.getString(2));
                row.put("datewritten", rset1.getString(3));
                row.put("name", rset1.getString(4));
                row.put("date", rset1.getString(5));
                row.put("dateuk", rset1.getString(6));
                row.put("rating", rset1.getString(7));
                row.put("rtext", rset1.getString(8));
                adminReview1.add(row);
            }

            while(rset2.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("rID", rset2.getString(1));
                row.put("memberuname", rset2.getString(2));
                row.put("datewritten", rset2.getString(3));
                row.put("name", rset2.getString(4));
                row.put("date", rset2.getString(5));
                row.put("dateuk", rset2.getString(6));
                row.put("rating", rset2.getString(7));
                row.put("rtext", rset2.getString(8));
                rID.add(rset2.getString(1));
                userReview1.add(row);
            }

            if(id != null) {
                int rset3 = db.deleteReview(id);
                if (rset3 > 0) {
                    rset1 = db.displayReview();
                    rset2 = db.displayReview2(uname);

                    ArrayList<Map<String,String>> adminReview2 = new ArrayList<>();
                    ArrayList<Map<String,String>> userReview2 = new ArrayList<>();

                    while (rset1.next()) {
                        Map<String, String> row = new HashMap<>();
                        row.put("rID", rset1.getString(1));
                        row.put("memberuname", rset1.getString(2));
                        row.put("datewritten", rset1.getString(3));
                        row.put("name", rset1.getString(4));
                        row.put("date", rset1.getString(5));
                        row.put("dateuk", rset1.getString(6));
                        row.put("rating", rset1.getString(7));
                        row.put("rtext", rset1.getString(8));
                        adminReview2.add(row);
                    }

                    while (rset2.next()) {
                        Map<String, String> row = new HashMap<>();
                        row.put("rID", rset2.getString(1));
                        row.put("memberuname", rset2.getString(2));
                        row.put("datewritten", rset2.getString(3));
                        row.put("name", rset2.getString(4));
                        row.put("date", rset2.getString(5));
                        row.put("dateuk", rset2.getString(6));
                        row.put("rating", rset2.getString(7));
                        row.put("rtext", rset2.getString(8));
                        userReview2.add(row);
                    }
                    if(uname.equals("JasonR") || uname.equals("Shakah") || uname.equals("Van") || uname.equals("Walker")) {
                        data.put("adminReview", adminReview2);
                    } else {
                        data.put("userReview", userReview2);
                    }
                    data.put("successfulMsg", "Successfully deleted!");
                } else {
                    if (uname.equals("JasonR") || uname.equals("Shakah") || uname.equals("Van") || uname.equals("Walker"))
                        data.put("adminReview", adminReview1);
                    else
                        data.put("userReview", userReview1);
                    data.put("errorMsg", "Error! Cannot delete review.");
                }
            } else {
                if (uname.equals("JasonR") || uname.equals("Shakah") || uname.equals("Van") || uname.equals("Walker"))
                    data.put("adminReview", adminReview1);
                else
                    data.put("userReview", userReview1);
            }
            if (uname.equals("JasonR") || uname.equals("Shakah") || uname.equals("Van") || uname.equals("Walker")) {
                return Main.renderTemplate(data, "admin-delete-review.hbs");
            } else {
                return Main.renderTemplate(data, "user-delete-review.hbs");
            }

        } catch (SQLException e) {
            resp.status(500);
            System.err.println("Error deleteReviewForm: " + e.getMessage());
            return "";
        }
    }

    //Get delete movie form and after clicking delete movie button
    public Object deleteMovie(Request req, Response resp ) {
        String mName = req.queryParams("mname");
        String US = req.queryParams("usdate");
        String intl = req.queryParams("ukdate");

        Map<String,Object> data = new HashMap<>();

        try(DbFacade db = new DbFacade()) {
            ResultSet rset1 = db.displayMovie();

            ArrayList<Map<String,String>> movie1 = new ArrayList<>();

            while(rset1.next()) {
                Map<String,String> row = new HashMap<>();
                row.put("MovieName", rset1.getString(1));
                row.put("USReleaseDate", rset1.getString(2));
                row.put("IntlReleaseDate", rset1.getString(3));
                row.put("Description", rset1.getString(4));
                row.put("Director", rset1.getString(5));
                row.put("ContentRating", rset1.getString(6));
                row.put("Duration", rset1.getString(7));
                row.put("Review", rset1.getString(8));
                movie1.add(row);
            }

            if(mName != null && US != null && intl != null) {
                int rset2 = db.deleteMovie(mName, US, intl);
                if(rset2 > 0) {
                    rset1 = db.displayMovie();

                    ArrayList<Map<String,String>> movie2 = new ArrayList<>();

                    while(rset1.next()) {
                        Map<String,String> row = new HashMap<>();
                        row.put("MovieName", rset1.getString(1));
                        row.put("USReleaseDate", rset1.getString(2));
                        row.put("IntlReleaseDate", rset1.getString(3));
                        row.put("Description", rset1.getString(4));
                        row.put("Director", rset1.getString(5));
                        row.put("ContentRating", rset1.getString(6));
                        row.put("Duration", rset1.getString(7));
                        row.put("Review", rset1.getString(8));
                        movie2.add(row);
                    }
                    data.put("movie", movie2);
                    data.put("successfulMsg", "Successfully deleted!");
                } else {
                    data.put("movie", movie1);
                    data.put("errorMsg", "Invalid inputs or empty fields.");
                }
            } else
                data.put("movie", movie1);

            return Main.renderTemplate(data, "delete-movie.hbs");

        } catch(SQLException e ) {
            resp.status(500);
            System.err.println("Error deleteMovie: " + e.getMessage());
            return "";
        }
    }

    //Get change password form and after clicking change password for both admin and member
    public Object changePassword(Request req, Response resp){
        String oldpw = req.queryParams("oldpw");
        String newpw = req.queryParams("newpw");
        String cfpw = req.queryParams("confirmedpw");
        Session sess = req.session();
        String uname = sess.attribute("username");

        Map<String, Object> data = new HashMap<>();

        try(DbFacade db = new DbFacade()) {
            if(oldpw != null && newpw != null && cfpw != null) {
                boolean rset1 = db.checkpass(uname, oldpw);
                if (rset1) {
                    if (newpw.equals(cfpw)) {
                        db.changePass(uname, oldpw, newpw);
                        data.put("successfulMsg", "Password updated!");
                        data.put("login", "Please log in again");
                        sess.attribute("authenticated", false);

                    } else {
                        data.put("errorMsg", "Confirmed password does not match old password.");
                        data.put("beforeChange", "display");
                        data.put("pass", "display");
                    }
                } else {
                    data.put("errorMsg", "Invalid inputs, cannot change password.");
                    data.put("beforeChange", "display");
                    data.put("pass", "display");
                }
            } else {
                data.put("beforeChange", "display");
                data.put("pass", "display");
            }
            if (uname.equals("JasonR") || uname.equals("Shakah") || uname.equals("Van") || uname.equals("Walker"))
                return Main.renderTemplate(data, "admin-changepass.hbs");
            else
                return Main.renderTemplate(data, "user-changepass.hbs");
        } catch(SQLException e){
            resp.status(500);
            System.err.println("Error in changePassword: " + e.getMessage());
            return "";
        }
    }

    //Get edit review form for both member and admin
    public Object getEditReview(Request req, Response resp){
        Session sess = req.session();
        String uname = sess.attribute("username");

        Map<String,Object> data = new HashMap<>();

        try(DbFacade db = new DbFacade()) {
            //Display logged in username's review only
            ResultSet rset1 = db.displayReview2(uname);

            ArrayList<Map<String, String>> review1 = new ArrayList<>();

            while (rset1.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("rID", rset1.getString(1));
                row.put("memberuname", rset1.getString(2));
                row.put("datewritten", rset1.getString(3));
                row.put("name", rset1.getString(4));
                row.put("date", rset1.getString(5));
                row.put("dateuk", rset1.getString(6));
                row.put("rating", rset1.getString(7));
                row.put("rtext", rset1.getString(8));
                review1.add(row);
            }

            data.put("review", review1);

        if (uname.equals("JasonR") || uname.equals("Shakah") || uname.equals("Van") || uname.equals("Walker"))
            return Main.renderTemplate(data, "admin-edit-review.hbs");
        else
            return Main.renderTemplate(data, "user-edit-review.hbs");
        } catch (SQLException e) {
            resp.status(500);
            System.err.println("Error editReview: " + e.getMessage());
            return "";
        }
    }

    //After clicking edit review button for both admin and member
    public Object editReview(Request req, Response resp){
        Map<String,Object> data = new HashMap<>();
        Session sess = req.session();
        String uname = sess.attribute("username");
        String id = req.queryParams("id");
        int options = 0;
        if(req.queryParams("num").equals("1") || req.queryParams("num").equals("2") || req.queryParams("num").equals("3"))
            options = Integer.parseInt(req.queryParams("num"));
        else
            data.put("errorMsg", "Invalid options.");
        String up = req.queryParams("up");

        try(DbFacade db = new DbFacade()) {
            ResultSet rset1 = db.displayReview2(uname);

            ArrayList<Map<String,String>> review1 = new ArrayList<>();

            while(rset1.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("rID", rset1.getString(1));
                row.put("memberuname", rset1.getString(2));
                row.put("datewritten", rset1.getString(3));
                row.put("name", rset1.getString(4));
                row.put("date", rset1.getString(5));
                row.put("dateuk", rset1.getString(6));
                row.put("rating", rset1.getString(7));
                row.put("rtext", rset1.getString(8));
                review1.add(row);
            }

            if(options != 0 && !up.equals("") && !id.equals("")) {
                if(options == 3) {
                    boolean rset3 = db.checkrID(up);
                    if(rset3 == false) {
                            boolean rset2 = db.editReview(id, options, up, null);
                            if (rset2) {
                                data.put("successfulMsg", "Successful Update!");
                                rset1 = db.displayReview2(uname);

                                ArrayList<Map<String, String>> review2 = new ArrayList<>();

                                while (rset1.next()) {
                                    Map<String, String> row = new HashMap<>();
                                    row.put("rID", rset1.getString(1));
                                    row.put("memberuname", rset1.getString(2));
                                    row.put("datewritten", rset1.getString(3));
                                    row.put("name", rset1.getString(4));
                                    row.put("date", rset1.getString(5));
                                    row.put("dateuk", rset1.getString(6));
                                    row.put("rating", rset1.getString(7));
                                    row.put("rtext", rset1.getString(8));
                                    review2.add(row);
                                }
                                data.put("review", review2);
                            } else {
                                data.put("errorMsg", "Error! Invalid input.");
                            }
                    } else {
                        data.put("review", review1);
                        data.put("errorMsg", "Duplicate rId, please choose a different id");
                    }
                } else {
                    boolean rset2 = db.editReview(id, options, up, null);
                    if (rset2) {
                        data.put("successfulMsg", "Successful Update!");
                        rset1 = db.displayReview2(uname);

                        ArrayList<Map<String, String>> review2 = new ArrayList<>();

                        while (rset1.next()) {
                            Map<String, String> row = new HashMap<>();
                            row.put("rID", rset1.getString(1));
                            row.put("memberuname", rset1.getString(2));
                            row.put("datewritten", rset1.getString(3));
                            row.put("name", rset1.getString(4));
                            row.put("date", rset1.getString(5));
                            row.put("dateuk", rset1.getString(6));
                            row.put("rating", rset1.getString(7));
                            row.put("rtext", rset1.getString(8));
                            review2.add(row);
                        }
                        data.put("review", review2);
                    } else {
                        data.put("errorMsg", "Error! Invalid input.");
                    }
                }
            } else if(up.equals("") || id.equals("")) {
                data.put("errorMsg", "Inputs cannot be empty.");
                data.put("review", review1);
            } else
                data.put("review", review1);

            if (uname.equals("JasonR") || uname.equals("Shakah") || uname.equals("Van") || uname.equals("Walker"))
                return Main.renderTemplate(data, "admin-edit-review.hbs");
            else
                return Main.renderTemplate(data, "user-edit-review.hbs");

        } catch (SQLException e) {
            resp.status(500);
            System.err.println("Error editReview: " + e.getMessage());
            return "";
        }
    }

    //After clicking search movie button for everyone
    public Object postSearchResult(Request req, Response resp) {
        String movie = req.queryParams("mName");
        Session sess = req.session();
        String uname = sess.attribute("username");

        Map<String, Object> templatedData = new HashMap<>();

        try (DbFacade db = new DbFacade()) {
            ResultSet rset1 = db.search(movie);
            ArrayList<Map<String, String>> visitorSearch = new ArrayList<>();
            ArrayList<Map<String, String>> memberSearch = new ArrayList<>();
            int count = 0;
            while (rset1.next()) {
                count = Integer.parseInt(rset1.getString(1));
            }
            if(count > 0) {
                ResultSet rset2 = db.lastSearch(movie);
                while(rset2.next()) {
                    Map<String, String> row = new HashMap<>();
                    ResultSet rset3 = db.averageRating(rset2.getString(1), rset2.getString(2), rset2.getString(3));
                    while (rset3.next()) {
                        if(rset3.getString(1) != null) {
                            row.put("name", rset2.getString(1));
                            row.put("date", rset2.getString(2));
                            row.put("dateuk", rset2.getString(3));
                            row.put("rating", rset3.getString(1));
                            visitorSearch.add(row);
                            memberSearch.add(row);
                        } else {
                            row.put("name", rset2.getString(1));
                            row.put("date", rset2.getString(2));
                            row.put("dateuk", rset2.getString(3));
                            row.put("rating", "?");
                            visitorSearch.add(row);
                            memberSearch.add(row);
                        }
                    }
                }
            } else {
                templatedData.put("errorMsg", "Sorry, we don't have this movie :(");
            }

            if(uname == null)
                templatedData.put("visitorSearch", visitorSearch);
            else
                templatedData.put("memberSearch", memberSearch);

            if(uname == null) {
                return Main.renderTemplate(templatedData, "visitorSearchPage-post.hbs");
            } else if(uname.equals("JasonR") || uname.equals("Shakah") || uname.equals("Van") || uname.equals("Walker")) {
                return Main.renderTemplate(templatedData, "adminSearchPage-post.hbs");
            } else
                return Main.renderTemplate(templatedData, "userSearchPage-post.hbs");
        } catch (SQLException e) {
            System.err.println("Error in postSearchResult: " + e.getMessage());
            resp.status(500);
            return "";
        }
    }

    //After clicking on movie name hyperlink
    public Object visitorMovieReport(Request req, Response resp ) {
        String mName = req.params(":name");
        String USReleaseDate = req.params(":date");
        String intlReleaseDate = req.params(":dateuk");

        Map<String,Object> data = new HashMap<>();
        data.put("mName", mName);
        data.put("USReleaseDate", USReleaseDate);
        data.put("intlReleaseDate", intlReleaseDate);

        try(DbFacade db = new DbFacade()) {
            //Display movie's information and overall rating
            ResultSet rset1 = db.movie(mName, USReleaseDate, intlReleaseDate);
            //Display actor/actress in the movie visitor clicks on
            ResultSet rset2 = db.actor(mName, USReleaseDate, intlReleaseDate);
            //Display movie genre in the movie visitor clicks on
            ResultSet rset3 = db.genre(mName, USReleaseDate, intlReleaseDate);
            //Display movie review in the movie visitor clicks on
            ResultSet rset4 = db.review(mName, USReleaseDate, intlReleaseDate);

            ArrayList<Map<String,String>> movie = new ArrayList<>();
            ArrayList<Map<String,String>> actor = new ArrayList<>();
            ArrayList<Map<String,String>> genre = new ArrayList<>();
            ArrayList<Map<String,String>> review = new ArrayList<>();

            while(rset1.next()) {
                Map<String,String> row = new HashMap<>();
                row.put("MovieName", rset1.getString(1));
                row.put("USReleaseDate", rset1.getString(2));
                row.put("IntlReleaseDate", rset1.getString(3));
                row.put("Description", rset1.getString(4));
                row.put("Director", rset1.getString(5));
                row.put("ContentRating", rset1.getString(6));
                row.put("Duration", rset1.getString(7));
                row.put("Review", rset1.getString(8));
                movie.add(row);
            }

            while(rset2.next()) {
                Map<String,String> row = new HashMap<>();
                row.put("ActorName", rset2.getString(1));
                actor.add(row);
            }
            while(rset3.next()) {
                Map<String,String> row = new HashMap<>();
                row.put("Genre", rset3.getString(1));
                genre.add(row);
            }

            while(rset4.next()) {
                Map<String,String> row = new HashMap<>();
                row.put("rID", rset4.getString(1));
                row.put("DateWritten", rset4.getString(2));
                row.put("MemberUsername", rset4.getString(3));
                row.put("mRating", rset4.getString(4));
                row.put("rText", rset4.getString(5));
                review.add(row);
            }

            data.put("movie", movie);
            data.put("actor", actor);
            data.put("genre", genre);
            data.put("review", review);

            return Main.renderTemplate(data, "visitor-movie-report.hbs");

        } catch(SQLException e ) {
            resp.status(500);
            System.err.println("Error visitorMovieReport: " + e.getMessage());
            return "";
        }
    }

    //After clicking log in button for both member and admin
    public Object postLoginForm(Request req, Response resp) {
        String uname = req.queryParams("uname");
        String pwd = req.queryParams("pw");
        Session sess = req.session();

        Map<String,Object> data = new HashMap<>();
        data.put("uname", uname);

        try(DbFacade db = new DbFacade()) {

            if( db.authenticateMember(uname, pwd) ) {
                if(uname.equals("Van") || uname.equals("Walker") || uname.equals("JasonR") || uname.equals("Shakah")) {
                    sess.attribute("username", uname);
                    sess.attribute("authenticated", true);
                    return Main.renderTemplate(data, "admin-menu.hbs");
                } else {
                    sess.attribute("username", uname);
                    sess.attribute("authenticated", true);
                    return Main.renderTemplate(data, "member-menu.hbs");
                }
            }

        } catch(SQLException e) {
            resp.status(500);
            System.err.println("postLoginForm: " + e.getMessage());
            return "";
        }

        // If we got here, authentication failed.  Show login form again with error message.
        data.put("errorMsg", "Login failed!");
        return Main.renderTemplate(data, "login-form.hbs");
    }

    //Get member's main menu
    public Object getUserHome(Request req, Response resp) {
        Session sess = req.session();
        String uname = sess.attribute("username");

        Map<String, Object> templatedData = new HashMap<>();
        templatedData.put("uname", uname);

        return Main.renderTemplate(templatedData, "member-menu.hbs");
    }

    //Get admin's main menu
    public Object getAdminHome(Request req, Response resp) {
        Session sess = req.session();
        String uname = sess.attribute("username");

        Map<String, Object> templatedData = new HashMap<>();
        templatedData.put("uname", uname);

        return Main.renderTemplate(templatedData, "admin-menu.hbs");
    }

    //Check authentication for member
    public void userBefore( Request req, Response resp ) {
        Session sess = req.session();
        Boolean auth = sess.attribute("authenticated");

        if( auth == null || (!auth) )
            halt(401, "Access denied!");
    }

    //Check authentication for admin
    public void adminBefore( Request req, Response resp ) {
        Session sess = req.session();
        String uname = sess.attribute("username");
        Boolean auth = sess.attribute("authenticated");

        if( uname == null || (!uname.equals("JasonR") && !uname.equals("Shakah") && !uname.equals("Van") && !uname.equals("Walker")) ||  (!auth) || auth == null)
            halt(401, "Access denied!");
    }

    //After member or admin clicks on movie name hyperlink
    public Object memberMovieReport(Request req, Response resp ) {
        String mName = req.params(":name");
        String USReleaseDate = req.params(":date");
        String intlReleaseDate = req.params(":dateuk");
        Session sess = req.session();
        String uname = sess.attribute("username");

        String mRating = req.queryParams("mRating");
        String rText = req.queryParams("rText");

        Map<String,Object> data = new HashMap<>();
        data.put("mName", mName);
        data.put("USReleaseDate", USReleaseDate);
        data.put("intlReleaseDate", intlReleaseDate);

        try(DbFacade db = new DbFacade()) {
            ResultSet rset1 = db.movie(mName, USReleaseDate, intlReleaseDate);
            ResultSet rset2 = db.actor(mName, USReleaseDate, intlReleaseDate);
            ResultSet rset3 = db.genre(mName, USReleaseDate, intlReleaseDate);
            ResultSet rset4 = db.review(mName, USReleaseDate, intlReleaseDate);

            ArrayList<Map<String,String>> movie1 = new ArrayList<>();
            ArrayList<Map<String,String>> actor = new ArrayList<>();
            ArrayList<Map<String,String>> genre = new ArrayList<>();
            ArrayList<Map<String,String>> review = new ArrayList<>();

            while(rset1.next()) {
                Map<String,String> row = new HashMap<>();
                row.put("MovieName", rset1.getString(1));
                row.put("USReleaseDate", rset1.getString(2));
                row.put("IntlReleaseDate", rset1.getString(3));
                row.put("Description", rset1.getString(4));
                row.put("Director", rset1.getString(5));
                row.put("ContentRating", rset1.getString(6));
                row.put("Duration", rset1.getString(7));
                row.put("Review", rset1.getString(8));
                movie1.add(row);
            }

            while(rset2.next()) {
                Map<String,String> row = new HashMap<>();
                row.put("ActorName", rset2.getString(1));
                actor.add(row);
            }

            while(rset3.next()) {
                Map<String,String> row = new HashMap<>();
                row.put("Genre", rset3.getString(1));
                genre.add(row);
            }

            while(rset4.next()) {
                Map<String,String> row = new HashMap<>();
                row.put("rID", rset4.getString(1));
                row.put("DateWritten", rset4.getString(2));
                row.put("MemberUsername", rset4.getString(3));
                row.put("mRating", rset4.getString(4));
                row.put("rText", rset4.getString(5));
                review.add(row);
            }

            if(mRating != null || rText != null) {
                if(mRating.equals("1") || mRating.equals("2") || mRating.equals("3") || mRating.equals("4") || mRating.equals("5")) {
                    db.writeReview(null, uname, null, mName, USReleaseDate, intlReleaseDate, mRating, rText);
                    rset4 = db.review(mName, USReleaseDate, intlReleaseDate);
                    Map<String, String> row = new HashMap<>();
                    if (rset4.last()) {
                        row.put("rID", rset4.getString(1));
                        row.put("DateWritten", rset4.getString(2));
                        row.put("MemberUsername", rset4.getString(3));
                        row.put("mRating", rset4.getString(4));
                        row.put("rText", rset4.getString(5));
                        review.add(row);
                    }
                    ArrayList<Map<String, String>> movie2 = new ArrayList<>();
                    rset1 = db.movie(mName, USReleaseDate, intlReleaseDate);
                    while (rset1.next()) {
                        Map<String, String> row1 = new HashMap<>();
                        row1.put("MovieName", rset1.getString(1));
                        row1.put("USReleaseDate", rset1.getString(2));
                        row1.put("IntlReleaseDate", rset1.getString(3));
                        row1.put("Description", rset1.getString(4));
                        row1.put("Director", rset1.getString(5));
                        row1.put("ContentRating", rset1.getString(6));
                        row1.put("Duration", rset1.getString(7));
                        row1.put("Review", rset1.getString(8));
                        movie2.add(row1);
                    }
                    data.put("movie", movie2);
                } else {
                    data.put("movie", movie1);
                    data.put("errorMsg", "Please rate movie from 1 to 5 only");
                }
            } else {
                data.put("movie", movie1);
            }

            data.put("actor", actor);
            data.put("genre", genre);
            data.put("review", review);

            if(uname.equals("JasonR") || uname.equals("Shakah") || uname.equals("Van") || uname.equals("Walker"))
                return Main.renderTemplate(data, "admin-movie-report.hbs");
            else
                return Main.renderTemplate(data, "user-movie-report.hbs");

        } catch(SQLException e ) {
            resp.status(500);
            System.err.println("Error memberMovieReport: " + e.getMessage());
            return "";
        }
    }
}
